package com.droidteahouse.outdoorsy.ui

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.droidteahouse.backdrop.BackDropIconClickListener
import com.droidteahouse.outdoorsy.R
import com.droidteahouse.outdoorsy.vo.Rental
import kotlinx.android.synthetic.main.fragment_rental.*


/**
 *
 */
class RentalFragment : BaseFragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rental, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        pg = (activity as AppCompatActivity?)?.findViewById<LinearLayout>(R.id.product_grid) as LinearLayout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pg?.background = ContextCompat.getDrawable(activity!!, R.drawable.shr_product_grid_background_shape)
        }
        initSearch("")
        initAdapter()
        val pg = (activity as AppCompatActivity?)?.findViewById<LinearLayout>(R.id.product_grid)
        val backdropListener = BackDropIconClickListener(
                activity!!,
                pg!!,
                ContextCompat.getDrawable(activity!!, R.drawable.shr_branded_menu), // Menu open icon
                ContextCompat.getDrawable(activity!!, R.drawable.shr_close_menu))
        val toolbar = (activity as AppCompatActivity?)?.findViewById<Toolbar>(R.id.app_bar)
        toolbar?.setNavigationIcon(R.drawable.shr_branded_menu)
        toolbar?.setNavigationOnClickListener(backdropListener) // Menu close icon
        pg?.setOnTouchListener(backdropListener)
        toolbar?.invalidate()
        toolbar?.requestLayout()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //initSwipeToRefresh()
        checkNetwork()
        network.observe(viewLifecycleOwner, Observer {
            no_network.visibility = if (it == true) View.GONE else View.VISIBLE
            no_network.invalidate()
        })


    }

    private fun initAdapter() {

        val adapter = RentalAdapter(glide) {
            if (checkNetwork()) {
                model.retry()

            }
        }
        list.adapter = adapter

        list.setHasFixedSize(true);
        list.setItemAnimator(DefaultItemAnimator())



        model.resultList.observe(viewLifecycleOwner, Observer<PagedList<Rental>> {
            adapter.submitList(it) {
                // Workaround for an issue where RecyclerView incorrectly uses the loading / spinner
                // item added to the end of the list as an anchor during initial load.
                val layoutManager = (list.layoutManager as LinearLayoutManager)
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    list.scrollToPosition(position)
                }
            }
        })
        model.networkState.observe(viewLifecycleOwner, Observer {
            adapter.setNetworkState(it)
        })

    }

    private fun initSearch(query: String) {
        search_repo.setText(query)

        search_repo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                changeCategory(search_repo.text.trim().toString())
                true
            } else {
                false
            }
        }
        search_repo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                changeCategory(search_repo.text.trim().toString())
                true
            } else {
                false
            }
        }

        list.scrollToPosition(0)

    }


    fun changeCategory(query: String) {
        if (model.changeCategory(query)) {

            list.scrollToPosition(0)
            (list.adapter as? RentalAdapter)?.submitList(null)
        }
    }

}
