package com.droidteahouse.outdoorsy.ui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.droidteahouse.GlideRequests
import com.droidteahouse.outdoorsy.R
import com.droidteahouse.outdoorsy.repository.NetworkState
import com.droidteahouse.outdoorsy.vo.Rental


/**
 * A paged list adapter implementation for a Rental
 */
class RentalAdapter(
        private val glide: GlideRequests,
        private val retryCallback: () -> Unit)
    : PagedListAdapter<Rental, RecyclerView.ViewHolder>(DIFF_COMPARATOR) {
    private var networkState: NetworkState? = null


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.rental_view_item -> (holder as RentalViewHolder).bind(getItem(position))
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
                    networkState)
        }
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            //(holder as RentalViewHolder).updateCases(item)
        } else {
            onBindViewHolder(holder, position)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.rental_view_item -> RentalViewHolder.create(parent, glide)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.rental_view_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        private val PAYLOAD_SCORE = Any()
        val DIFF_COMPARATOR = object : DiffUtil.ItemCallback<Rental>() {
            override fun areContentsTheSame(oldItem: Rental, newItem: Rental): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: Rental, newItem: Rental): Boolean =
                    oldItem.id == newItem.id

            override fun getChangePayload(oldItem: Rental, newItem: Rental) {

            }
        }

    }
}


