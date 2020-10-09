package ru.samtakoy.listtest.presentation.details

import android.util.Log
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.domain.reps.EmployeeCacheRepository
import javax.inject.Inject

private const val TAG = "DetailsPresenter"

@InjectViewState
class DetailsPresenter(
    val employeeCacheRepository: EmployeeCacheRepository,
    val employeeId: Int
) : MvpPresenter<DetailsView>(){

    class Factory @Inject constructor(
        val employeeCacheRepository: EmployeeCacheRepository
    ){
        fun create(
            employeeId: Int

        ) = DetailsPresenter(employeeCacheRepository, employeeId)
    }

    init{
        viewState.updateToolbarTitle("")
        observeEmployee()
    }

    private fun observeEmployee() {

        presenterScope.launch {
            employeeCacheRepository.getEmployee(employeeId)
                .collect{
                    if(it.isNotEmpty()){
                        val employee = it[0]
                        viewState.showEmployee(employee)
                        viewState.updateToolbarTitle(employee.getVisibleFirstName())
                    } else {
                        onGetUserError(null)
                    }
                }
        }

    }

    private fun onGetUserError(throwable: Throwable?) {
        Log.e(TAG, "Error on user from db getting\n${throwable?.stackTraceToString()}")
        viewState.showError(R.string.details_user_getting_err)
    }

    override fun onDestroy() {

        super.onDestroy()
    }
}