package ru.samtakoy.listtest.presentation.details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.transition.TransitionInflater
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
import ru.samtakoy.listtest.presentation.getContainerTransitionName
import ru.samtakoy.listtest.presentation.getFirstNameTransitionName
import ru.samtakoy.listtest.presentation.getLastNameTransitionName
import java.text.MessageFormat
import javax.inject.Inject
import javax.inject.Provider

class DetailsFragment : MvpAppCompatFragment(), DetailsView{

    @Inject
    lateinit var factoryProvider: Provider<DetailsPresenter.Factory>
    private val presenter by moxyPresenter {
        val employeeId = requireArguments().getInt("employeeId")
        factoryProvider.get().create(employeeId)
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
        val view: View = inflater.inflate(R.layout.fragment_details, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementTransitionOnEnter()
            postponeEnterTransition()
        }
    }

    override fun onDestroyView() {

        cancelAnimations()

        super.onDestroyView()
    }

    private fun cancelAnimations() {
        Glide.with(requireContext())
            .clear(avatar)
    }

    override fun showEmployee(employee: Employee) {

        firstName.text = employee.getVisibleFirstName()
        lastName.text = employee.getVisibleLastName()

        val phoneString = requireContext().resources.getString(R.string.details_phone_text)
        phone.text = MessageFormat.format(phoneString, employee.phone)

        ViewCompat.setTransitionName(avatar, employee.getAvatarTransitionName())
        ViewCompat.setTransitionName(firstName, employee.getFirstNameTransitionName())
        ViewCompat.setTransitionName(lastName, employee.getLastNameTransitionName())
        ViewCompat.setTransitionName(container, employee.getContainerTransitionName())
        startEnterTransitionAfterLoadingImage(employee.avatar, avatar)
    }

    override fun showError(errorId: Int) {
        Toast.makeText(requireContext().applicationContext, errorId, Toast.LENGTH_SHORT).show()
    }

    override fun updateToolbarTitle(title: String) {
        val activity: AppCompatActivity = requireActivity() as AppCompatActivity;
        activity.supportActionBar!!.title = title
    }


    private fun setSharedElementTransitionOnEnter(){
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_element_transition)
    }

    private fun startEnterTransitionAfterLoadingImage(imageUrl: String, imageView: ImageView){
        Glide.with(requireContext())
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_person_gray_24dp)
            .error(R.drawable.ic_person_err_gray_24dp)
            .dontAnimate()
            .listener(object: RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            }
            )
            .into(imageView)
    }

}