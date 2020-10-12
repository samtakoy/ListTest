package ru.samtakoy.listtest.presentation.details.pager

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface DetailsPagerView : MvpView{

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(idList: List<Int>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCurrentEmployeePosition(positionIdx: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showError(errorId: Int)

}