package ru.samtakoy.listtest.presentation.details.page

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.samtakoy.listtest.domain.model.Employee

interface DetailsView : MvpView{

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmployee(employee: Employee)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showError(errorId: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun updateToolbarTitle(title: String)

}