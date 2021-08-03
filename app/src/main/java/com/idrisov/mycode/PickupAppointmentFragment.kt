package com.idrisov.mycode

import PickupAppointmentPresenter
import PickupAppointmentView
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.delay
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

/**
 * Created by Tagir Idrisov on 09.07.2021
 */

class PickupAppointmentFragment : BaseFragment(), PickupAppointmentView, TextWatcher {

    private var _binding: FragmentPickupAppointmentBinding? = null

    private val viewBinding
        get() = _binding!!

    private var beginTransaction = false

    private var rxBindingDisposable: Disposable? = null

    private var timeId = ""
    private var date = ""
    private var time = ""

    private val autoTransition by lazy(LazyThreadSafetyMode.NONE) {
        AutoTransition()
    }

    private val serviceId by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString(ARG_SERVICE_ID).orEmpty()
    }

    private val orderNumber by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString(ARG_ORDER_NUMBER).orEmpty()
    }

    @Inject
    @InjectPresenter
    lateinit var presenter: PickupAppointmentPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun onCreate(savedInstanceState: Bundle?) {

        App.appComponent
            .pickupAppointmentBuilder()
            .router(getRouter())
            .build()
            .inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPickupAppointmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun init() {

        initToolbar()
        initRxBinding()
        initListeners()
    }

    override fun showError(errorMessage: String) {

        with(viewBinding) {

            TransitionManager.beginDelayedTransition(pickupAppointmentContainer, autoTransition)
            warningLayout.warningMessage.text = errorMessage
            warningLayout.root.makeVisible()

            lifecycleScope.launchWhenStarted {
                delay(3000)
                TransitionManager.beginDelayedTransition(pickupAppointmentContainer, autoTransition)
                warningLayout.root.makeGone()
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        if (s?.length == 6 && (before == 5 || start == 5))
            viewBinding.numberCarEditText.apply {
                setText("$s ")
                setSelection(text.length)
            }
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun showProgress() {
        with(viewBinding) {
            applyButton.makeGone()
            progressInBtn.makeVisible()
        }
    }

    override fun hideProgress() {
        with(viewBinding) {
            applyButton.makeVisible()
            progressInBtn.makeGone()
        }
    }

    override fun showSuccess() {
        toast(getString(R.string.warehouse_data_updated))
    }

    private fun initToolbar() {

        viewBinding.toolbar.setupToolbar(
            title = orderNumber,
            backButtonListener = ::onBackPressed
        )
    }

    private fun initRxBinding() {

        with(viewBinding) {
            rxBindingDisposable = Observables.combineLatest(
                RxTextView.textChanges(fullNameEditText),
                RxTextView.textChanges(numberCarEditText),
                RxTextView.textChanges(brandCarEditText),
                RxTextView.textChanges(modelCarEditText)
            ) { fullName, numberCar, brandCar, modelCar ->

                fullName.isNotBlank()
                    && numberCar.isNotBlank()
                    && brandCar.isNotBlank()
                    && modelCar.isNotBlank()
                    && date.isNotBlank()
                    && time.isNotBlank()
            }
                .subscribeBy {

                    beginTransaction = applyButton.accessibleWithTransitionEnabled(
                        access = it,
                        btnTransitionIsStarted = beginTransaction
                    )
                }
        }
    }

    private fun initListeners() {

        with(viewBinding) {

            titlePickupDate.setOnClickListener { showDataPickerBottomSheet() }
            applyButton.setOnClickListener { clickApplyButton() }
            changeEnteredDate.setOnClickListener { showDataPickerBottomSheet() }

            numberCarEditText.addTextChangedListener(this@PickupAppointmentFragment)

            differentFormatCheckBox.setOnCheckedChangeListener { _, isChecked ->

                when (isChecked) {
                    true -> numberCarEditText.apply {
                        filters = arrayOf(InputFilter.LengthFilter(LENGTH_CHAR_WHEN_MASK_OFF))
                        removeTextChangedListener(this@PickupAppointmentFragment)
                    }
                    false -> numberCarEditText.apply {
                        filters = arrayOf(InputFilter.LengthFilter(LENGTH_CHAR_WHEN_MASK_ON))
                        addTextChangedListener(this@PickupAppointmentFragment)
                    }
                }
            }
        }
    }

    private fun clickApplyButton() {

        with(viewBinding) {

            val listChars = numberCarEditText.text.toString().split(" ")
            val numberCar = if (differentFormatCheckBox.isChecked) numberCarEditText.text.toString() else listChars.take(1).joinToString("")
            val regionCar = numberCarEditText.text.toString().split(" ").takeLast(1).joinToString("").toInt()

            when {
                fullNameEditText.text.isNullOrEmpty() -> {
                    fullNameEditText.requestFocus()
                    showKeyboard()
                    showError(requireContext().getString(R.string.write_us_user_data_name_error))
                }

                numberCarEditText.text.isNullOrEmpty() -> {
                    numberCarEditText.requestFocus()
                    showKeyboard()
                    showError(requireContext().getString(R.string.write_us_user_data_name_error))
                }

                brandCarEditText.text.isNullOrEmpty() -> {
                    brandCarEditText.requestFocus()
                    showKeyboard()
                    showError(requireContext().getString(R.string.write_us_user_data_name_error))
                }

                modelCarEditText.text.isNullOrEmpty() -> {
                    modelCarEditText.requestFocus()
                    showKeyboard()
                    showError(requireContext().getString(R.string.write_us_user_data_name_error))
                }

                date.isBlank() || time.isBlank() -> {
                    showError(requireContext().getString(R.string.registration_date_or_time_to_warehouse))
                    scrollView.scrollTo(0, scrollView.top)
                }

                else -> {
                    presenter.applyWarehouseData(
                        serviceId = serviceId,
                        timeId = timeId,
                        date = date,
                        time = time,
                        fullName = fullNameEditText.text.toString(),
                        carNumber = numberCar,
                        carBrand = brandCarEditText.text.toString(),
                        carModel = modelCarEditText.text.toString(),
                        hasTrailer = availabilityTrailerCheckBox.isChecked,
                        region = regionCar,
                        specificCarNumber = differentFormatCheckBox.isChecked)
                }
            }
        }
    }

    private fun showDataPickerBottomSheet() {

        DateFilterBottomSheetFragment.newInstance(
            serviceId = serviceId,
            serviceType = SERVICE_TYPE,
            onServiceChangedSuccessful = null,
            onEmptyAvailableServices = {
                showError("К сожалению, к данной услуге нет доступных временных отрезков.")
            },
            returnDate = ::updateData
        ).show(requireFragmentManager(), PickupAppointmentFragment::class.simpleName)
    }

    private fun updateData(date: String, time: String, timeId: String) {

        this.timeId = timeId

        setSelectedDate(date, time)
    }

    private fun setSelectedDate(date: String, time: String) {

        if (date.isNotBlank() && time.isNotBlank()) {

            with(viewBinding) {
                titlePickupDate.makeGone()

                dateTextView.makeVisible()
                dateTextView.text = date
                timeTextView.makeVisible()
                timeTextView.text = time

                changeEnteredDate.makeVisible()

                initRxBinding()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        rxBindingDisposable?.dispose()
    }

    override fun onBackPressed() = presenter.onBackPressed()

    companion object {

        fun newInstance(serviceId: String, orderNumber: String, date: String, time: String) =
            PickupAppointmentFragment().withArgs {
                putString(ARG_SERVICE_ID, serviceId)
                putString(ARG_ORDER_NUMBER, orderNumber)
                putString(ARG_ORDER_DATE, date)
                putString(ARG_ORDER_TIME, time)
            }

        private const val ARG_SERVICE_ID = "ARG_NUMBER_ORDER"
        private const val ARG_ORDER_NUMBER = "ARG_ORDER_NUMBER"
        private const val ARG_ORDER_DATE = "ARG_ORDER_DATE"
        private const val ARG_ORDER_TIME = "ARG_ORDER_TIME"
        private const val SERVICE_TYPE = "SelfDelivery"
        private const val LENGTH_CHAR_WHEN_MASK_OFF = 12
        private const val LENGTH_CHAR_WHEN_MASK_ON = 10
    }
}
