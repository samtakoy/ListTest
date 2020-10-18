package ru.samtakoy.listtest.presentation.details.pager

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.ChangeClipBounds
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_details_pager.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.app.Di
import ru.samtakoy.listtest.presentation.details.pager.inner.PagerAdapter
import ru.samtakoy.listtest.presentation.shared.SharedEmployeeViewModel
import ru.samtakoy.listtest.utils.extensions.doOnEndOnce
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
        return inflater.inflate(R.layout.fragment_details_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentEmployeeSharedModel.bigPictureLoadingAllowed.value = false

        listenPagerChanges()
        setupAdapter()
        proceedSharedAnimsOnEnter()
    }

    private fun listenPagerChanges() {
        viewPager.registerOnPageChangeCallback(
            object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    onEmployeeIdxSelect(position)
                }
            }
        )
    }

    private fun proceedSharedAnimsOnEnter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementTransitionOnEnter()
            postponeEnterTransition()
            observeImageReady()
            prepareTransitions()
        } else {
            currentEmployeeSharedModel.bigPictureLoadingAllowed.value = true
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

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setSharedElementTransitionOnEnter(){

        sharedElementEnterTransition = TransitionSet()
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(ChangeClipBounds())
            .addTransition(ChangeTransform())
            .addTransition(ChangeBounds())
            .doOnEndOnce{
                currentEmployeeSharedModel.bigPictureLoadingAllowed.value = true
            }
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
        onEmployeeIdxSelect(positionIdx)
        viewPager.setCurrentItem(positionIdx, false)
    }

    private fun onEmployeeIdxSelect(positionIdx: Int) {
        currentEmployeeSharedModel.currentEmployeeId = adapter.idData[positionIdx]
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
                val position = viewPager.currentItem
                val currentPage = adapter.getExistsFragment(position)
                currentPage?.onMapSharedElements(names, sharedElements)
            }
        })
    }
}