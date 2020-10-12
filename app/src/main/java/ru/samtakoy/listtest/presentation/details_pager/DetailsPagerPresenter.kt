package ru.samtakoy.listtest.presentation.details_pager

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import ru.samtakoy.listtest.domain.model.cache.CacheModel
import javax.inject.Inject

@InjectViewState
class DetailsPagerPresenter (
    private val cacheModel: CacheModel,
    private var currentUserId: Int
): MvpPresenter<DetailsPagerView>(){

    class Factory @Inject constructor(
        val cacheModel: CacheModel
    ){
        fun create(currentUserId: Int) = DetailsPagerPresenter(cacheModel, currentUserId)
    }

    private var currentUserPosition: Int = -1

    init{
        observeData()
    }

    private fun observeData() {
        presenterScope.launch {
            cacheModel.observeEmployeeIds()
                .collect {
                    viewState.setData(it)
                    defineCurrentUserPos(it)
                }
        }
    }

    private fun defineCurrentUserPos(idList: List<Int>) {
        currentUserPosition = idList.indexOf(currentUserId)
        viewState.setCurrentEmployeePosition(currentUserPosition)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}