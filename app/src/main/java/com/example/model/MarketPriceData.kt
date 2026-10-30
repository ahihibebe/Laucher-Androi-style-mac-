package com.example.model

data class CoffeePrice(
    val province: String,
    val priceText: String, // ví dụ: "89.000 đ/kg"
    val changeText: String, // ví dụ: "+500 đ/kg"
    val isUp: Boolean = true
)

data class GoldPrice(
    val brand: String, // "Vàng Nhẫn 9999 SJC"
    val type: String = "Vàng Nhẫn",
    val buyPrice: String, // "135.000.000 đ"
    val sellPrice: String, // "139.000.000 đ"
    val changeText: String = "+400.000 đ/lượng"
)

data class PetrolPrice(
    val fuelType: String, // ví dụ: "Xăng RON 95-III"
    val region1Price: String, // "21.010 đ/lít"
    val region2Price: String, // "21.430 đ/lít"
    val region3Price: String, // "21.850 đ/lít"
    val changeText: String = "+150 đ/lít"
)

data class MarketPriceReport(
    val coffeePrices: List<CoffeePrice> = emptyList(),
    val averageCoffeePrice: String = "89.000 đ/kg",
    val lamDongCoffeePrice: String = "88.500 đ/kg",
    val lamDongDistricts: List<CoffeePrice> = emptyList(),
    val pepperPrice: String = "137.500 đ/kg",
    val pepperChange: String = "-1.000 đ/kg",
    val goldPrices: List<GoldPrice> = emptyList(),
    val petrolPrices: List<PetrolPrice> = emptyList(),
    val oilPrices: List<PetrolPrice> = emptyList(),
    val lastUpdated: String = "Thời gian thực"
)
