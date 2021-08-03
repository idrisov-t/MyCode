package com.idrisov.mycode

import ABClearBasketVariation
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by Tagir Idrisov on 03.08.2021
 */

class BasketClearABTest {

    private fun initAbTest() {
        FirebaseAbTesting.fetchTestingGroup<String>(type = ABTestType.ClearBasket) { itemType ->
            viewState.setupToolbarMenuItems(ABClearBasketVariation.valueOf(itemType))
        }
    }

    override fun setupToolbarMenuItems(menuItemType: ABClearBasketVariation) {

        with(viewBinding.toolbar) {

            setupToolbar(title = getString(R.string.basket_toolbar_title), menuEnabled = false)

            if (menu.size() <= 0) {
                inflateMenu(R.menu.clear_basket)

                menu.findItem(R.id.clearBasket).actionView.apply {

                    menuIconView = when (menuItemType) {
                        ABClearBasketVariation.IMAGE -> find<AppCompatImageView>(R.id.iconItemMenuAppTextView)
                        ABClearBasketVariation.TEXT -> find<AppCompatTextView>(R.id.titleItemMenuAppTextView)
                    }

                    menuIconView.setOnClickListener {
                        showDialogClearBasket(menuItemType.name)
                    }
                }
            }
        }
    }
}