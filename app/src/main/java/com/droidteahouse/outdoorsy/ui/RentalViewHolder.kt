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

package com.droidteahouse.outdoorsy.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.droidteahouse.GlideRequests
import com.droidteahouse.outdoorsy.R
import com.droidteahouse.outdoorsy.vo.Rental


/**
 * View Holder for a RecyclerView list item.
 */
class RentalViewHolder(view: View, private val glide: GlideRequests) : RecyclerView.ViewHolder(view) {

    private val name: TextView = view.findViewById(R.id.name)
    private val thumbnail: ImageView = view.findViewById(R.id.thumbnail)


    init {
        view.setOnClickListener {

        }
    }

    fun bind(rental: Rental?) {
        if (rental == null) {
            val resources = itemView.resources
            name.text = resources.getString(R.string.loading)
            thumbnail.visibility = View.GONE

        } else {
            showRentalData(rental)
        }
    }

    private fun showRentalData(rental: Rental) {

        name.text = rental.attributes?.name
        if (rental.url != null) {
            glide.load(rental.url?.toUri())
                    .fitCenter()
                    .placeholder(R.drawable.camper)
                    .into(thumbnail)
        }

    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): RentalViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rental_view_item, parent, false)
            return RentalViewHolder(view, glide)
        }
    }
}
