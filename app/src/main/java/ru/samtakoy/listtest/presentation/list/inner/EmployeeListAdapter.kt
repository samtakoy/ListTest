package ru.samtakoy.listtest.presentation.list.inner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.employee_list_item.view.*
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.presentation.getAvatarTransitionName
import ru.samtakoy.listtest.presentation.getContainerTransitionName
import ru.samtakoy.listtest.presentation.getFirstNameTransitionName
import ru.samtakoy.listtest.presentation.getLastNameTransitionName


class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    var employeeId: Int = 0
        private set
    val avatarTrView: View = itemView.icon
    val firstNameTrView: View = itemView.firstName
    val lastNameTrView: View = itemView.lastName

    fun bind(item: Employee, itemClickListener: ((view: View, empl: Employee) -> Unit)) {

        itemView.setOnClickListener {
            itemClickListener.invoke(it, item)
        }

        itemView.firstName.text = item.getVisibleFirstName()
        itemView.lastName.text = item.getVisibleLastName()

        employeeId = item.id
        ViewCompat.setTransitionName(avatarTrView, item.getAvatarTransitionName())
        ViewCompat.setTransitionName(firstNameTrView, item.getFirstNameTransitionName())
        ViewCompat.setTransitionName(lastNameTrView, item.getLastNameTransitionName())
        ViewCompat.setTransitionName(itemView, item.getContainerTransitionName())

        Glide.with(itemView.context)
            .load(item.avatar)
            .placeholder(R.drawable.ic_person_gray_24dp)
            .error(R.drawable.ic_person_gray_24dp)
            .into(itemView.icon)
    }

    fun unbind() {
        ViewCompat.setTransitionName(avatarTrView, "")
        ViewCompat.setTransitionName(firstNameTrView, "")
        ViewCompat.setTransitionName(lastNameTrView, "")
        ViewCompat.setTransitionName(itemView, "")
        Glide.with(itemView.context)
            .clear(itemView.icon)
    }


}

class EmployeeListAdapter(
    private val itemClickListener:((view: View, empl: Employee) -> Unit)
) : ListAdapter<Employee, EmployeeViewHolder>(ITEM_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EmployeeViewHolder(
            inflater.inflate(R.layout.employee_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val item: Employee = getItem(position)
        holder.bind(item, itemClickListener)
    }

    override fun onViewRecycled(holder: EmployeeViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }
}


private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Employee>() {
    override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem == newItem
    }
}