package com.droidteahouse.outdoorsy.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.droidteahouse.GlideApp
import com.droidteahouse.GlideRequests
import com.droidteahouse.outdoorsy.R
import com.droidteahouse.outdoorsy.ServiceLocator
import com.droidteahouse.outdoorsy.repository.NetworkState
import kotlinx.android.synthetic.main.fragment_rental.*

open class BaseFragment : Fragment() {
    var network: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    lateinit var glide: GlideRequests
    lateinit var pg: View

    val model: RentalViewModel by activityViewModels { (ServiceLocator.instance(activity!!)).provideViewModel(activity!!.application, this) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        glide = GlideApp.with(context!!)
    }

    //@todo refactor to network callback
    fun checkNetwork(): Boolean {
        var result = false
        val cm = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            val activeNetworkInfo = cm.getActiveNetworkInfo()
            result = activeNetworkInfo != null && activeNetworkInfo.isConnected
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork
            val capabilities = cm
                    .getNetworkCapabilities(network)
            result = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        network.value = result
        if (result == false) swipe_refresh.isRefreshing = false
        return result
    }

    fun initSwipeToRefresh() {
        swipe_refresh.setColorSchemeResources(
                R.color.primary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = (it == NetworkState.LOADING)
            checkNetwork()
        })

        swipe_refresh.setOnRefreshListener {
            if (checkNetwork()) {
                model.refresh("")
            }
        }
    }


}
