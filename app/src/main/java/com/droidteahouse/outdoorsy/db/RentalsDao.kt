/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.example.android.codelabs.paging.db

import androidx.paging.DataSource
import androidx.room.*
import com.droidteahouse.outdoorsy.vo.Included
import com.droidteahouse.outdoorsy.vo.Rental


@Dao
interface RentalsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentals(rentals: List<Rental>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncluded(included: List<Included>)

    @Query("SELECT * FROM rentals WHERE " +
            "attr_name LIKE :queryString OR attr_description LIKE :queryString " +
            "ORDER BY indexInResponse ASC")
    fun rentalsByKeyword(queryString: String): DataSource.Factory<Int, Rental>

    @Query("SELECT MAX(indexInResponse) + 1 FROM rentals")
    fun getNextIndexInCategory(): Int


    @Query("DELETE FROM rentals")
    suspend fun clearRepos()

    @Transaction
    @Update
    suspend fun updateRentals(data: List<Rental>): Int

}