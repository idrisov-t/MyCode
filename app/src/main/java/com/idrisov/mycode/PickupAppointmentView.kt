
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

/**
 * Created by Tagir Idrisov on 09.07.2021
 */

@StateStrategyType(AddToEndSingleStrategy::class)
interface PickupAppointmentView : BaseView {

    fun showError(errorMessage: String)

    fun showProgress()
    fun hideProgress()

    fun showSuccess()
}
