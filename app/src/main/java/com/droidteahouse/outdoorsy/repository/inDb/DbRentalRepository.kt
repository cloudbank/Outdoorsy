/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.droidteahouse.outdoorsy.repository.inDb


import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.toLiveData
import androidx.room.withTransaction
import com.droidteahouse.outdoorsy.api.RentalSearchResponse
import com.droidteahouse.outdoorsy.api.RentalService
import com.droidteahouse.outdoorsy.repository.Listing
import com.droidteahouse.outdoorsy.repository.NetworkState
import com.droidteahouse.outdoorsy.repository.RentalRepository
import com.droidteahouse.outdoorsy.vo.Rental
import com.example.android.codelabs.paging.db.RentalsDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Repository implementation that uses a database PagedList + a boundary callback to return a
 * listing that loads in pages.
 */
class DbRentalRepository(
        val db: RentalsDatabase,
        private val webService: RentalService,
        private var boundaryCallback: BoundaryCallback
) : RentalRepository {

    init {
        boundaryCallback.handleResponse = this::insertResultIntoDb
    }

    companion object {

        private val TAG = DbRentalRepository::class.java.canonicalName
    }

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private suspend fun updateResult(body: RentalSearchResponse?) {
        var num = 0
        body?.let { it ->
            //@Transaction
            num = db.rentalsDao().updateRentals(it.rentals)
            Log.d(TAG, "Update on ${num} rows successful")
            boundaryCallback.incrementStart()
        }
    }

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private suspend fun insertResultIntoDb(body: RentalSearchResponse?) {
        body?.let { it ->
            db.withTransaction {
                try {
                    val start = db.rentalsDao().getNextIndexInCategory()
                    val included = it.included
                    val items = it.rentals.mapIndexed { index, child ->
                        child.indexInResponse = start + index
                        val id = child.relationships?.primary_image?.data?.id
                        val imageArray = included.filter { it.id == id }
                        val uri = if (imageArray.size > 0) imageArray[0].attributes.url else " "
                        child.url = uri!!
                        child
                    }

                    db.rentalsDao().insertRentals(items)
                    db.rentalsDao().insertIncluded(included)
                } catch (e: Exception) {
                    Log.e(TAG, e.message)
                }
            }
            boundaryCallback.incrementStart()
        }
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    override fun refresh(mainScope: CoroutineScope, query: String): LiveData<NetworkState> {
        //does this make sense?  check rate limiting/ throttling for this
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        boundaryCallback.resetStart()

        mainScope.launch {
            try {
                val response = RentalService.safeApiCall(null, networkState) { webService.rentals(query = query, offset = boundaryCallback.page, itemsPerPage = BoundaryCallback.DEFAULT_NETWORK_PAGE_SIZE) }

                if (response != null) {
                    if (response.isSuccessful) {
                        withContext(Dispatchers.IO) { updateResult(response.body()) }
                        networkState.value = (NetworkState.LOADED)
                    } else {
                        networkState.value = (NetworkState.error(response.errorBody().toString()))
                    }
                }
            } catch (e: Exception) {
                networkState.value = (NetworkState.error(e.message))
            }
        }
        return networkState
    }

    /**
     * Returns a Listing for the given data
     *
     */
    @MainThread
    override suspend fun rentals(pageSize: Int, ctx: CoroutineContext, scope: CoroutineScope, query: String): Listing<Rental> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        boundaryCallback.query = query
        boundaryCallback.scope = scope
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = refreshTrigger.switchMap {
            refresh(scope, query)
        }
        val dbQuery = "%${query.replace(' ', '%')}%"
        return Listing(

                pagedList = db.rentalsDao().rentalsByKeyword(dbQuery).toLiveData(pageSize = pageSize, boundaryCallback = boundaryCallback),
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.helper.retryAllFailed()
                },
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState
        )
    }


}

