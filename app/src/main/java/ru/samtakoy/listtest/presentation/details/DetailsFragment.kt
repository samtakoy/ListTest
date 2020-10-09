package ru.samtakoy.listtest.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_details.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.app.Di
import ru.samtakoy.listtest.domain.model.Employee
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

    override fun showEmployee(employee: Employee) {

        firstName.text = employee.firstName
        lastName.text = employee.lastName

        val phoneString = requireContext().resources.getString(R.string.details_phone_text)
        phone.text = MessageFormat.format(phoneString, employee.phone)

        Glide.with(requireContext())
            .load(employee.avatar)
            .centerCrop()
            .placeholder(R.drawable.ic_person_gray_24dp)
            .error(R.drawable.ic_person_err_gray_24dp)
            .into(avatar)
    }

    override fun showError(errorId: Int) {
        Toast.makeText(requireContext().applicationContext, errorId, Toast.LENGTH_SHORT).show()
    }

    override fun updateToolbarTitle(title: String) {
        val activity: AppCompatActivity = requireActivity() as AppCompatActivity;
        activity.supportActionBar!!.title = title
    }

}