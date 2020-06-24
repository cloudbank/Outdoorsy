package com.droidteahouse.outdoorsy.repository

import androidx.lifecycle.LiveData
import com.droidteahouse.outdoorsy.vo.Rental

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext


interface RentalRepository {

    suspend fun rentals(pageSize: Int, ctx: CoroutineContext, scope: CoroutineScope, query: String): Listing<Rental>
    fun refresh(mainScope: CoroutineScope, query: String): LiveData<NetworkState>

}