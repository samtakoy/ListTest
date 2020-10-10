package ru.samtakoy.listtest.presentation.list

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_employee_list.*
import kotlinx.android.synthetic.main.fragment_employee_list.view.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.app.Di
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.presentation.list.inner.EmployeeListAdapter
import ru.samtakoy.listtest.presentation.list.inner.EmployeeViewHolder
import ru.samtakoy.listtest.presentation.list.inner.InfiniteScrollListener
import ru.samtakoy.listtest.presentation.list.inner.SwipeItemHelper
import ru.samtakoy.listtest.presentation.transitionPair
import javax.inject.Inject
import javax.inject.Provider


class ListFragment : MvpAppCompatFragment(), ListView, SwipeItemHelper.SwipeListener{

    private val TAG = "ListFragment"

    @Inject
    lateinit var presenterProvider: Provider<ListPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    private lateinit var recyclerViewAdapter: EmployeeListAdapter
    private lateinit var recyclerLayoutManager: LinearLayoutManager
    private lateinit var swipeItemHelper: SwipeItemHelper

    private val recyclerViewPreDrawListener: ViewTreeObserver.OnPreDrawListener = ViewTreeObserver.OnPreDrawListener {
        tryScrollOneItemDown()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Di.appComponent.inject(this)
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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

    override fun onStart() {
        super.onStart()

        swipeItemHelper.attachToRecyclerView(recyclerView)
        // for test
        presenter.onUiCheckCacheStatus()
    }

    override fun onStop() {

        swipeItemHelper.detachToRecyclerView()

        super.onStop()
    }

    private fun setupRecyclerView(view: View, employeeListAdapter: EmployeeListAdapter) {

        recyclerLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        view.recyclerView.run{

            layoutManager = recyclerLayoutManager
            adapter = employeeListAdapter

            addOnScrollListener(createInfiniteScrollListener(layoutManager as LinearLayoutManager))

            swipeItemHelper = SwipeItemHelper(requireContext(), this@ListFragment)

            // for shared element back transition
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder) {
        // do nothing yet
    }

    private fun createInfiniteScrollListener(linearLayoutManager: LinearLayoutManager): RecyclerView.OnScrollListener {

        return object: InfiniteScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                presenter.onUiGetMoreEmployees()
            }
            override fun isLoading(): Boolean = false
        }
    }

    private var clickedView: View? = null

    private fun createAdapter(): EmployeeListAdapter {
        return EmployeeListAdapter{ view, employee ->
            clickedView = view
            presenter.onUiEmployeeClick(employee.id)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        /*
        when(item.itemId){
            R.id.menu_item_settings -> presenter.onUiSettingsClick()
        }*/

        return item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(item)
    }

    /*
    override fun navigateToSettings() {
        findNavController().navigate(
            ListFragmentDirections.toSettings()
        )
    }*/

    override fun navigateToEmployeeDetails(employeeId: Int) {
        var holder: EmployeeViewHolder? = null

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            holder = getRecyclerItemViewByEmployeeId(employeeId) as EmployeeViewHolder

            if(holder != null){
                //sharedElementView.is
                if(clickedView == holder.itemView){
                    Log.d("tag", "1")
                }else{
                    Log.d("tag", "2")
                }
                val extras = FragmentNavigatorExtras(
                    holder.avatarTrView.transitionPair(),
                    holder.firstNameTrView.transitionPair(),
                    holder.lastNameTrView.transitionPair(),
                    holder.itemView.transitionPair()
                )
                findNavController().navigate(ListFragmentDirections.toDetails(employeeId), extras)
                return
            }
        }

        // usual navigation
        findNavController().navigate(ListFragmentDirections.toDetails(employeeId))
    }

    private fun getRecyclerItemViewByEmployeeId(employeeId: Int): EmployeeViewHolder?{

        for(i in 0 until recyclerLayoutManager.childCount){
            var view = recyclerLayoutManager.getChildAt(i)
            if(view != null) {
                var viewHolder = recyclerView.getChildViewHolder(view) as EmployeeViewHolder
                if(viewHolder.employeeId == employeeId){
                    return viewHolder
                }
            }
        }
        return null
    }

}