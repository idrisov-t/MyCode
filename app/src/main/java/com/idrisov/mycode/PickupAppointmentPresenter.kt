

import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import javax.inject.Inject

/**
 * Created by Tagir Idrisov on 09.07.2021
 */

@InjectViewState
class PickupAppointmentPresenter @Inject constructor(
    private val router: CustomRouter,
    private val errorHandler: UiErrorHandler,
    private val interactor: OrdersInteractor
) : BasePresenter<PickupAppointmentView>() {

    fun applyWarehouseData(
        serviceId: String,
        timeId: String,
        date: String,
        time: String,
        fullName: String,
        carNumber: String,
        carBrand: String,
        carModel: String,
        hasTrailer: Boolean,
        region: Int,
        specificCarNumber: Boolean
    ) {
        compositeDisposable += interactor.setDataOnPickupWarehouse(
            serviceId = serviceId,
            serviceType = SERVICE_TYPE,
            timeId = timeId,
            date = date,
            time = time,
            fullName = fullName,
            carNumber = carNumber,
            carBrand = carBrand,
            carModel = carModel,
            hasTrailer = hasTrailer,
            region = region,
            specificCarNumber = specificCarNumber
        )
            .doOnSubscribe { viewState.showProgress() }
            .doAfterTerminate { viewState.hideProgress() }
            .subscribeBy(
                onComplete = {
                    viewState.showSuccess()
                    onBackPressed()
                },
                onError = { throwable ->
                    errorHandler.proceed(
                        error = throwable,
                        messageListener = { msg -> viewState.showError(msg) },
                        recaptchaAppCallback = {
                            applyWarehouseData(serviceId, timeId, date, time, fullName, carNumber, carBrand, carModel, hasTrailer, region, specificCarNumber)
                        }
                    )
                })
    }

    override fun onBackPressed() = router.exit()

    companion object {
        private const val SERVICE_TYPE = "SelfDelivery"
    }
}
