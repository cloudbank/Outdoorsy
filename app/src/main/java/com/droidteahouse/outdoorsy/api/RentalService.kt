/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.droidteahouse.outdoorsy.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingRequestHelper
import com.droidteahouse.outdoorsy.repository.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit

import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Github API communication setup via Retrofit.
 */
interface RentalService {
    /**
     * Get repos ordered by stars.
     */
    @GET("rentals")
    suspend fun rentals(
            @Query("filter[keyword]") query: String,
            @Query("page[offset]") offset: Int,
            @Query("page[limit]") itemsPerPage: Int
    ): Response<RentalSearchResponse>

    companion object {
        private const val BASE_URL = "https://search.outdoorsy.co/"

        fun create(): RentalService {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BASIC

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(RentalService::class.java)
        }


        @JvmStatic
        suspend inline fun <T> safeApiCall(prh: PagingRequestHelper.Request.Callback?, networkState: LiveData<NetworkState>, crossinline responseFunction: suspend () -> T): T? {
            return try {
                val response = withContext(Dispatchers.IO) { responseFunction.invoke() }
                response
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    Log.e(RentalService.javaClass.canonicalName, "Call error: ${e.localizedMessage}", e.cause)
                    (networkState as MutableLiveData).postValue(NetworkState.error("Network error, please check your connection and try again"))
                    prh?.recordFailure(e)
                }
                null
            }
        }
    }
}