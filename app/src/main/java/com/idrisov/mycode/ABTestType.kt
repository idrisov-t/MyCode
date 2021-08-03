
/**
 * Created by Tagir Idrisov on 09.07.2021
 */

sealed class ABTestType(val key: String) {
    object ShowPriceInBasket : ABTestType(key = "show_price_basket")
    object VariationBonusCard: ABTestType(key = "bonus_card_variation")
}