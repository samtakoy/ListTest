package ru.samtakoy.listtest.presentation.list

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.samtakoy.listtest.domain.model.Employee

interface ListView : MvpView{


    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(messageId: Int)
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(data: List<Employee>)


    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = "LOADING")
    fun showDataLoading()
    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = "LOADING")
    fun hideDataLoading()

    //@StateStrategyType(OneExecutionStateStrategy::class)
    //fun navigateToSettings()
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun navigateToEmployeeDetails(employeeId: Int)
}