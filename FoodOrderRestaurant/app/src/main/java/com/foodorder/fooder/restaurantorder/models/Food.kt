package com.foodorder.fooder.restaurantorder.models

import com.foodorder.fooder.restaurantorder.db.FoodEntity
import com.foodorder.fooder.restaurantorder.db.OrderFoodEntity
import com.foodorder.fooder.restaurantorder.models.base.BaseModel
import com.foodorder.fooder.restaurantorder.utils.Constants
import com.foodorder.fooder.restaurantorder.utils.Utils
import kotlinx.android.parcel.Parcelize
import java.text.NumberFormat
import java.util.*

@Parcelize
data class Food(
	val id: Long = 0,
	val name: String = "",
	val imagePath: String = "",
	val price: String = "",
	val currencyCode: String = Constants.CURRENCY_CODE_DEFAULT,
	val status: String = "",
	val quantity: Int = 0,
) : BaseModel()

fun List<Food>.calculateTotalAmount(): String {
	var totalAmount = 0f
	forEach { food ->
		totalAmount += food.quantity * Utils.convertAmountToLong(food.price)
	}
	val first = firstOrNull() ?: return "0"
	val currency = Currency.getInstance(first.currencyCode)
	val format = NumberFormat.getCurrencyInstance()
	format.currency = currency
	format.maximumFractionDigits = currency.defaultFractionDigits

//	val amount = if (currency.defaultFractionDigits == 0)
//		totalAmount.toLong().toString() else totalAmount.toString()
//	val formatAmount = amount.
	return format.format(totalAmount)
}

fun Food.convertOrderFoodEntity(idOrder: Long) = OrderFoodEntity(
	id = id,
	idOrder = idOrder,
	nameFood = name,
	imageFoodPath = imagePath,
	priceFood = price,
	currencyCode = currencyCode,
	status = status,
	quantity = quantity,
)

fun Food.convertFoodEntity() = FoodEntity(
	id = id,
	name = name,
	imagePath = imagePath,
	price = price,
	currencyCode = currencyCode,
	status = status
)

fun FoodEntity.convertFood() = Food(
	id = id,
	name = name,
	imagePath = imagePath,
	price = price,
	currencyCode = currencyCode,
	status = status
)

fun OrderFoodEntity.convertFood() = Food(
	id = id,
	name = nameFood,
	imagePath = imageFoodPath,
	price = priceFood,
	currencyCode = currencyCode,
	status = status,
	quantity = quantity,
)

fun List<FoodEntity>.convertFoodList() = map {
	it.convertFood()
}

fun List<OrderFoodEntity>.convertOrderFoodList() = map {
	it.convertFood()
}

