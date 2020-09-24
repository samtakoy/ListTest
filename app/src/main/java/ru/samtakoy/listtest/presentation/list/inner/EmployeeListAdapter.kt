package ru.samtakoy.listtest.presentation.list.inner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.employee_list_item.view.*
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.domain.model.Employee


class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){



    fun bind(item: Employee, itemClickListener: ((view: View, empl: Employee) -> Unit)?) {

        itemView.firstName.text = item.firstName
        itemView.lastName.text = item.lastName
    }



}

class EmployeeListAdapter(
    private val itemClickListener:((view: View, empl: Employee) -> Unit)? = null
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


}


private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Employee>() {
    override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem == newItem
    }
}