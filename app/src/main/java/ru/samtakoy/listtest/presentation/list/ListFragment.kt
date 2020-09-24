package ru.samtakoy.listtest.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.presentation.list.inner.EmployeeListAdapter
import ru.samtakoy.listtest.presentation.list.inner.InfiniteScrollListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_employee_list.view.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.samtakoy.listtest.app.Di
import javax.inject.Inject
import javax.inject.Provider

class ListFragment : MvpAppCompatFragment(), ListView{

    @Inject
    lateinit var presenterProvider: Provider<ListPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    private lateinit var recyclerViewAdapter: EmployeeListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {

        Di.appComponent.inject(this)
        super.onCreate(savedInstanceState)

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
        view.recyclerView.run{
            adapter = employeeListAdapter

            addOnScrollListener(createInfiniteScrollListener(layoutManager as LinearLayoutManager))

            // TODO transition
        }
    }

    private fun createInfiniteScrollListener(linearLayoutManager: LinearLayoutManager): RecyclerView.OnScrollListener {

        return object: InfiniteScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                presenter.getMoreEmployees()
            }
            override fun isLoading(): Boolean = false
        }
    }

    private fun createAdapter(): EmployeeListAdapter {
        return EmployeeListAdapter{ view, employee ->
            // goto Employee DetailsScreen
        }
    }

    override fun showMessage(messageId: Int) {
        Snackbar.make(requireActivity().window.decorView, messageId, Snackbar.LENGTH_SHORT).show()
    }

    override fun setData(data: List<Employee>) {
        recyclerViewAdapter.submitList(data)

    }

    override fun showDataLoading() {
        //TODO("Not yet implemented")
    }

    override fun hideDataLoading() {
        //TODO("Not yet implemented")
    }
}