package com.idrisov.mycode

import ABBonusCard.*

/**
 * Created by Tagir Idrisov on 03.08.2021
 */

class BonusCardAbTestFragment {

    private var firstVariationBonusCard = false
    private var secondVariationBonusCard = false
    private var thirdVariationBonusCard = false

    fun init() {
        FirebaseAbTesting.fetchTestingGroup<String>(type = ABTestType.VariationBonusCard) { variation ->

            when (variation) {
                FIRST.value -> firstVariationBonusCard = true
                SECOND.value -> secondVariationBonusCard = true
                THIRD.value -> thirdVariationBonusCard = true
            }
        }
    }

    private fun setupBonusCardData(
        bonusCard: OrderRegistrationModel.BonusCard?,
        isLogged: Boolean, bonusCardChecked: Boolean, haveCard: Boolean
    ) {

        val cardData = bonusCard?.data

        val havePromotionalProduct = presenter.orderRegistrationModel.cart
            ?.any { it.isBestPrice } ?: false

        val allProductsIsPromotional = presenter.orderRegistrationModel.cart
            ?.all { it.isBestPrice } ?: false

        with(viewBinding) {

            bonusCardTitle.text = cardData?.run {
                makeCardNumberHidden(cardNumber)
            } ?: getString(R.string.order_registration_bonus_card_title)

            writeOffTitle.isVisible(cardData?.bonusesForPay ?: 0 > 0)
            writeOffValue.isVisible(cardData?.bonusesForPay ?: 0 > 0)

            when {
                allProductsIsPromotional -> bonusInfoText.text = getString(R.string.bonus_card_all_products_promotions)
                havePromotionalProduct -> bonusInfoText.text = getString(R.string.bonus_card_have_promotions_products)
                else -> bonusInfoText.makeGone()
            }

            titleBonusAction.isVisible(thirdVariationBonusCard || ChangeTextActionBonus.changeTextOnAction)
            getBonusCardAction.isVisible(secondVariationBonusCard && !titleBonusAction.isVisible)
            dividerGetBonusCard.isVisible(getBonusCardAction.isVisible)

            titleBonusAction.text = getString(
                when {
                    ChangeTextActionBonus.changeTextOnAction || allProductsIsPromotional -> R.string.bonus_card_change
                    else -> R.string.bonus_card_write_off
                }
            )

            getBonusCardAction.text = getString(
                when {
                    thirdVariationBonusCard || !cardData?.cardNumber.isNullOrBlank() -> R.string.bonus_card_write_off
                    else -> R.string.bonus_card_have_card
                }
            )

            accrualValue.isVisible(cardData?.bonusesForBuy ?: 0 > 0)
            accrualTitle.isVisible(cardData?.bonusesForBuy ?: 0 > 0)

            // Для открытия ссылок
            privacyPolicyTextView.movementMethod = LinkMovementMethod.getInstance()
            combinedBonusTextView.movementMethod = LinkMovementMethod.getInstance()

            switchContainer.isVisible(
                cardData?.bonusesForBuy ?: 0 == 0L
                        && cardData?.bonusesForPay ?: 0 == 0L
                        && bonusCard?.singlePolicy == false
            )

            combinedSwitchContainer.isVisible(
                cardData?.bonusesForBuy ?: 0 == 0L
                        && cardData?.bonusesForPay ?: 0 == 0L
                        && bonusCard?.singlePolicy == true
            )


            accrualValue.text = resources.getQuantityString(
                R.plurals.order_registration_accrual_value,
                cardData?.bonusesForBuy?.toInt() ?: 0,
                cardData?.bonusesForBuy?.toInt() ?: 0
            )

            writeOffValue.text = resources.getQuantityString(
                R.plurals.order_registration_writeOff_value,
                cardData?.bonusesForPay?.toInt() ?: 0,
                cardData?.bonusesForPay?.toInt() ?: 0
            )

            if (cardData?.bonusesForBuy ?: 0 == 0L && cardData?.bonusesForPay ?: 0 == 0L && (!haveCard || !isLogged)) {
                bonusCardContainerNew.setPadding(0, 0, 0, 0)
            } else bonusCardContainerNew.setPadding(0, 0, 0, (12 * DP).toInt())

            setSelectPrivacyPolicy(bonusCardChecked)

            getBonusCardSwitch.onCheckedChange { _, _ ->
                presenter.onGetBonusCardClicked(if (bonusCard?.singlePolicy == true) combinedSwitch.isChecked else getBonusCardSwitch.isChecked)
            }
        }
    }
}