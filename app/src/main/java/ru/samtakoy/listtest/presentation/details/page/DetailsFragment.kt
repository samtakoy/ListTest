package ru.samtakoy.listtest.presentation.details.page

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_details.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.app.Di
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.presentation.getAvatarTransitionName
import ru.samtakoy.listtest.presentation.getFirstNameTransitionName
import ru.samtakoy.listtest.presentation.getLastNameTransitionName
import ru.samtakoy.listtest.presentation.shared.SharedEmployeeViewModel
import java.text.MessageFormat
import javax.inject.Inject
import javax.inject.Provider
import kotlin.collections.set


private const val ARG_EMPLOYEE_ID = "employeeId"

class DetailsFragment : MvpAppCompatFragment(), DetailsView {

    companion object{
        fun create(employeeId: Int): DetailsFragment = DetailsFragment().apply {
            arguments = bundleOf(
                ARG_EMPLOYEE_ID to employeeId
            )
        }
    }

    @Inject
    lateinit var factoryProvider: Provider<DetailsPresenter.Factory>
    private val presenter by moxyPresenter {
        val employeeId = requireArguments().getInt(ARG_EMPLOYEE_ID)
        factoryProvider.get().create(employeeId)
    }

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
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onDestroyView() {
        cancelAnimations()
        super.onDestroyView()
    }

    private fun cancelAnimations() {
        if(activity != null && !requireActivity().isDestroyed) {
            // ?
            //Glide.with(requireContext()).pauseRequests()
            Glide.with(requireContext())
                .clear(avatar)
        }
    }

    override fun showEmployee(employee: Employee) {

        firstName.text = employee.getVisibleFirstName()
        lastName.text = employee.getVisibleLastName()

        val phoneString = requireContext().resources.getString(R.string.details_phone_text)
        phone.text = MessageFormat.format(phoneString, employee.phone)

        ViewCompat.setTransitionName(avatar, employee.getAvatarTransitionName())
        ViewCompat.setTransitionName(firstName, employee.getFirstNameTransitionName())
        ViewCompat.setTransitionName(lastName, employee.getLastNameTransitionName())
        startEnterTransitionAfterLoadingImage(employee.avatar, avatar)
    }

    fun onMapSharedElements(
        names: MutableList<String>,
        sharedElements: MutableMap<String, View>
    ) {
        mapOneSharedElements(names, sharedElements, avatar)
        mapOneSharedElements(names, sharedElements, firstName)
        mapOneSharedElements(names, sharedElements, lastName)
    }

    private fun mapOneSharedElements(
        names: MutableList<String>,
        sharedElements: MutableMap<String, View>,
        view: View
    ) {
        val transitionName = ViewCompat.getTransitionName(view)
        if(transitionName != null) {

            val namePrefix: String = transitionName.substringBefore(":")

            for(name in names){
                if(name.startsWith(namePrefix)){
                    sharedElements[name] = view
                    return
                }
            }
        }
    }

    override fun showError(errorId: Int) {
        Toast.makeText(requireContext().applicationContext, errorId, Toast.LENGTH_SHORT).show()
    }

    override fun updateToolbarTitle(title: String) {
        val activity: AppCompatActivity = requireActivity() as AppCompatActivity;
        activity.supportActionBar!!.title = title
    }

    private fun startEnterTransitionAfterLoadingImage(imageUrl: String, imageView: ImageView){

        val loadingAllowed = currentEmployeeSharedModel.bigPictureLoadingAllowed.value!!

        if(!loadingAllowed){
            loadImageOnTransitionFinish(imageUrl, imageView)
        }

        loadImage(imageUrl, imageView, !loadingAllowed){
            currentEmployeeSharedModel.onImageReady(requireArguments().getInt(ARG_EMPLOYEE_ID))
        }
    }

    private fun loadImageOnTransitionFinish(imageUrl: String, imageView: ImageView) {
        currentEmployeeSharedModel.bigPictureLoadingAllowed.observe(
            viewLifecycleOwner, {
                if (it) {
                    currentEmployeeSharedModel.bigPictureLoadingAllowed.removeObservers(viewLifecycleOwner)
                    loadImage(imageUrl, imageView, false){}
                }
            }
        )
    }

    private fun loadImage(
        imageUrl: String,
        imageView: ImageView,
        onlyRetrieveFromCache: Boolean,
        onFinish: ()->Unit
    ): Unit {
        Glide.with(requireContext())
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_person_gray_24dp)
            .error(R.drawable.ic_person_err_gray_24dp)
            .dontAnimate()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onFinish()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onFinish()
                    return false
                }
            })
            .onlyRetrieveFromCache(onlyRetrieveFromCache)
            .into(imageView)
    }



}