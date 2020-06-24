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

package com.droidteahouse.outdoorsy.ui


import androidx.lifecycle.*
import com.droidteahouse.outdoorsy.repository.RentalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RentalViewModel(
        private val repository: RentalRepository,
        private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val uiContext = viewModelScope.coroutineContext + Dispatchers.Main
    private val uiScope = CoroutineScope(uiContext)


    companion object {

        const val DEFAULT_CATEGORY = ""
        const val CATEGORY_KEY = ""
    }

    init {
        if (!savedStateHandle.contains(CATEGORY_KEY)) {
            savedStateHandle.set(CATEGORY_KEY, DEFAULT_CATEGORY)
        }
    }

    private val repoResult = savedStateHandle.getLiveData<String>(CATEGORY_KEY).switchMap {
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(repository.rentals(30, uiContext, uiScope, it))
        }
    }

    val resultList = repoResult.switchMap { it.pagedList }
    val networkState = repoResult.switchMap { it.networkState }
    val refreshState = repoResult.switchMap { it.refreshState }


    fun changeCategory(category: String): Boolean {
        if (savedStateHandle.get<String>(CATEGORY_KEY) == category) {
            return false
        }
        savedStateHandle.set(CATEGORY_KEY, category)
        refresh(category)
        return true
    }

    fun refresh(query: String) {
        viewModelScope.launch {

            repoResult.value?.refresh?.invoke()
        }
    }

    fun retry() {
        repoResult.value?.retry?.invoke()
    }


}

