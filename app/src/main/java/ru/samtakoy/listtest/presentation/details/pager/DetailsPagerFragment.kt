package ru.samtakoy.listtest.presentation.details.pager

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import kotlinx.android.synthetic.main.fragment_details_pager.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.app.Di
import ru.samtakoy.listtest.domain.TEST_TAG
import ru.samtakoy.listtest.presentation.details.pager.inner.PagerAdapter
import ru.samtakoy.listtest.presentation.shared.SharedEmployeeViewModel
import javax.inject.Inject
import javax.inject.Provider

private const val ARG_EMPLOYEE_ID = "employeeId"

class DetailsPagerFragment : MvpAppCompatFragment(), DetailsPagerView {


    @Inject
    lateinit var factoryProvider: Provider<DetailsPagerPresenter.Factory>
    val presenter: DetailsPagerPresenter by moxyPresenter{
        val employeeId = requireArguments().getInt(ARG_EMPLOYEE_ID)
        factoryProvider.get().create(employeeId)
    }

    private lateinit var adapter: PagerAdapter
    private val currentEmployeeSharedModel: SharedEmployeeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SharedEmployeeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Di.appComponent.inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_details_pager, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        proceedSharedAnimsOnEnter()
    }

    private fun proceedSharedAnimsOnEnter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementTransitionOnEnter()
            postponeEnterTransition()
            observeImageReady()
            prepareTransitions()
        }
    }

    private fun observeImageReady() {
        currentEmployeeSharedModel.resetReady()
        currentEmployeeSharedModel?.getImageReadyEmployeeId().observe(viewLifecycleOwner, {
            val employeeId = requireArguments().getInt(ARG_EMPLOYEE_ID)
            if (it == employeeId) {
                currentEmployeeSharedModel.getImageReadyEmployeeId().removeObservers(viewLifecycleOwner)
                startPostponedEnterTransition()
            }
        })
    }

    private fun setSharedElementTransitionOnEnter(){
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_element_transition)
    }

    private fun setupAdapter() {
        adapter = createAdapter()
        viewPager.adapter = adapter
    }

    private fun createAdapter() = PagerAdapter(requireActivity()!!)

    override fun setData(idList: List<Int>) {
        adapter.idData = idList
    }

    override fun setCurrentEmployeePosition(positionIdx: Int) {
        currentEmployeeSharedModel.currentEmployeeId = adapter.idData[positionIdx]
        viewPager.setCurrentItem(positionIdx, false)
    }

    override fun showError(errorId: Int) {
        Toast.makeText(requireContext().applicationContext, errorId, Toast.LENGTH_SHORT).show()
    }

    private fun prepareTransitions() {
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                Log.w(TEST_TAG, "..Details: -- mapSharedElements --")

                val position = viewPager.currentItem
                val currentPage = adapter.getExistsFragment(position)
                currentPage?.onMapSharedElements(names, sharedElements)
            }
        })
    }
}