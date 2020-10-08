package ru.samtakoy.listtest.presentation.settings

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SettingsView : MvpView{

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(messageId: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun clearGlideCaches()

}