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

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.droidteahouse.outdoorsy.api.RentalSearchResponse
import com.droidteahouse.outdoorsy.api.RentalService
import com.droidteahouse.outdoorsy.repository.NetworkState
import com.droidteahouse.outdoorsy.util.createStatusLiveData
import com.droidteahouse.outdoorsy.vo.Rental

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.concurrent.Executor
import kotlin.reflect.KSuspendFunction1

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class BoundaryCallback(
        private val webservice: RentalService,
        private val ioExecutor: Executor,
        private val networkPageSize: Int)
    : PagedList.BoundaryCallback<Rental>() {
    lateinit var scope: CoroutineScope
    lateinit var handleResponse: KSuspendFunction1<@ParameterName(name = "body") RentalSearchResponse?, Unit>
    val helper = PagingRequestHelper(ioExecutor)
    var networkState = helper.createStatusLiveData() as MutableLiveData


    companion object {
        const val DEFAULT_NETWORK_PAGE_SIZE = 30
    }

    var query: String = ""
    var page = 1
        get() = field

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        resetStart()
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            scope.launch {
                try {
                    val response = RentalService.safeApiCall(it, networkState) { webservice.rentals(query = query, offset = 0, itemsPerPage = DEFAULT_NETWORK_PAGE_SIZE) }

                    if (response != null) {
                        if (response.isSuccessful) withContext(Dispatchers.IO) { insertItemsIntoDb(response, it) } else it.recordFailure(Throwable(response.errorBody().toString()))
                    }
                } catch (e: Exception) {
                    networkState.value = (NetworkState.error(e.message ?: "unknown err"))
                    it.recordFailure(e)
                }

            }
        }
    }

    /**
     *
     */
    @MainThread
    override fun onItemAtFrontLoaded(itemAtFront: Rental) {
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Rental) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            scope.launch {
                try {
                    val response = RentalService.safeApiCall(it, networkState) { webservice.rentals(query = "", offset = page * DEFAULT_NETWORK_PAGE_SIZE, itemsPerPage = DEFAULT_NETWORK_PAGE_SIZE) }

                    if (response != null) {
                        if (response.isSuccessful) withContext(Dispatchers.IO) { insertItemsIntoDb(response, it) } else it.recordFailure(Throwable(response.errorBody().toString()))
                    }
                } catch (e: Exception) {
                    networkState.value = (NetworkState.error(e.message ?: "unknown err"))
                    it.recordFailure(e)
                }

            }
        }


    }


    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private suspend fun insertItemsIntoDb(
            response: Response<RentalSearchResponse>,
            it: PagingRequestHelper.Request.Callback) {

        try {
            handleResponse?.invoke(response.body())
            it.recordSuccess()
        } catch (e: Exception) {
            it.recordFailure(e)
        }

    }

    fun incrementStart() {
        page++
    }

    fun resetStart() {
        page = 0
    }

}