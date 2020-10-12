package ru.samtakoy.listtest.presentation.details_pager.inner

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.samtakoy.listtest.presentation.details.DetailsFragment
import java.lang.ref.WeakReference

class PagerAdapter (
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity){

    var idData: List<Int> = arrayListOf()
        set(value){
            field = value
            // TODO diffUtil
            notifyDataSetChanged()
        }

    val fragments:HashMap<Int, WeakReference<DetailsFragment>> = hashMapOf()

    override fun getItemCount(): Int = idData.size

    override fun createFragment(position: Int): Fragment =
        DetailsFragment.create(idData[position]).apply {
            fragments[position] = WeakReference(this)
        }

    fun getExistsFragment(position: Int) = fragments[position]?.get()
}