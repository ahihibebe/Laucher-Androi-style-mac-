package com.example.model

data class CoffeePrice(
    val province: String,
    val priceText: String, // ví dụ: "121,500 đ/kg"
    val changeText: String, // ví dụ: "+500 đ/kg"
    val isUp: Boolean
)

data class GoldPrice(
    val brand: String, // "Vàng Nhẫn 9999"
    val buyPrice: String, // "88.200.000 đ"
    val sellPrice: String, // "89.500.000 đ"
    val changeText: String // "+300.000 đ/lượng"
)

data class MarketPriceReport(
    val coffeePrices: List<CoffeePrice>,
    val averageCoffeePrice: String,
    val goldPrices: List<GoldPrice>,
    val lastUpdated: String
)
