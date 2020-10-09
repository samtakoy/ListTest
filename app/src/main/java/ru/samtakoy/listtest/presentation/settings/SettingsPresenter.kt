package ru.samtakoy.listtest.presentation.settings

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.domain.model.cache.CacheModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import ru.samtakoy.listtest.domain.model.cache.RequestResult

private const val TAG = "SettingsPresenter"

@InjectViewState
class SettingsPresenter @Inject constructor(
    val cache: CacheModel
) : MvpPresenter<SettingsView>(){

    init{
        presenterScope.launch {
            cache.observeErrors()
                .collect{
                    viewState.showMessage(it.errorTextId)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun onUiInvalidateDbCache() {
        cache.invalidateDbCache()
    }

    fun onUiClearDbCache() {
        presenterScope.launch {
            val result = cache.clearDbCache()
            if(result.await() == RequestResult.SUCCESS){
                viewState.showMessage(R.string.msg_settings_apply_success)
            }
        }
    }

    fun onUiClearGlideCaches() {
        viewState.clearGlideCaches()
    }
}
