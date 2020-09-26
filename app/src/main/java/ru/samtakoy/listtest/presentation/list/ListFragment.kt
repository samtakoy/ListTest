package ru.samtakoy.listtest.presentation.list

import android.os.AsyncTask
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_employee_list.*
import kotlinx.android.synthetic.main.fragment_employee_list.view.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.app.Di
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.presentation.list.inner.EmployeeListAdapter
import ru.samtakoy.listtest.presentation.list.inner.InfiniteScrollListener
import javax.inject.Inject
import javax.inject.Provider


class ListFragment : MvpAppCompatFragment(), ListView{

    private val TAG = "ListFragment"

    @Inject
    lateinit var presenterProvider: Provider<ListPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    private lateinit var recyclerViewAdapter: EmployeeListAdapter
    private lateinit var recyclerLayoutManager: LinearLayoutManager

    private val recyclerViewPreDrawListener: ViewTreeObserver.OnPreDrawListener = ViewTreeObserver.OnPreDrawListener {
        tryScrollOneItemDown()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Di.appComponent.inject(this)
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            clearGlideCache()
        }
    }

    private fun clearGlideCache() {
        object:AsyncTask<Void, Void, Void> (){
            override fun doInBackground(vararg params: Void?): Void? {
                Glide
                    .get(requireContext().applicationContext)
                    .clearDiskCache();
                return null
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_employee_list, container, false);

        recyclerViewAdapter = createAdapter()
        setupRecyclerView(view, recyclerViewAdapter)

        return view
    }

    private fun setupRecyclerView(view: View, employeeListAdapter: EmployeeListAdapter) {

        recyclerLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        view.recyclerView.run{

            layoutManager = recyclerLayoutManager
            adapter = employeeListAdapter

            addOnScrollListener(createInfiniteScrollListener(layoutManager as LinearLayoutManager))

            // TODO transition
        }
    }

    private fun createInfiniteScrollListener(linearLayoutManager: LinearLayoutManager): RecyclerView.OnScrollListener {

        return object: InfiniteScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                presenter.onUiGetMoreEmployees()
            }
            override fun isLoading(): Boolean = false
        }
    }

    private fun createAdapter(): EmployeeListAdapter {
        return EmployeeListAdapter{ view, employee ->
            //tryScrollOneItemDown()
        }
    }

    override fun showMessage(messageId: Int) {
        Snackbar.make(requireActivity().window.decorView, messageId, Snackbar.LENGTH_SHORT).show()
    }

    override fun setData(data: List<Employee>) {

        val isScrollNeeded = recyclerViewAdapter.itemCount > 0
                && recyclerViewAdapter.itemCount < data.size

        recyclerViewAdapter.submitList(data)

        if(isScrollNeeded){
            recyclerView.getViewTreeObserver().addOnPreDrawListener (recyclerViewPreDrawListener)
        }
    }

    override fun showDataLoading() {
        //loadingProgress.show()
        loadingProgress.visibility = View.VISIBLE
    }

    override fun hideDataLoading() {

        if(loadingProgress.visibility == View.VISIBLE) {
            //loadingProgress.hide()
            loadingProgress.visibility = View.GONE
        }
    }

    private fun tryScrollOneItemDown() {

        recyclerView.getViewTreeObserver().removeOnPreDrawListener (recyclerViewPreDrawListener)
        if(recyclerLayoutManager.findLastVisibleItemPosition() < recyclerViewAdapter.itemCount){
            recyclerView.post {
                recyclerView.layoutManager!!.startSmoothScroll(
                    createSmoothScrollerToPosition(recyclerLayoutManager.findLastVisibleItemPosition()+1)
                )
            }
        }
    }

    private fun createSmoothScrollerToPosition(targetPos: Int): LinearSmoothScroller{
        val smoothScroller = object: LinearSmoothScroller(context){
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return super.calculateSpeedPerPixel(displayMetrics)*10
            }
        }
        smoothScroller.setTargetPosition(targetPos)
        return smoothScroller
    }

}