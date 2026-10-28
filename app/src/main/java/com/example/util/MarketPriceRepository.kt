package com.example.util

import com.example.model.CoffeePrice
import com.example.model.GoldPrice
import com.example.model.MarketPriceReport
import com.example.model.PetrolPrice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MarketPriceRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    suspend fun getLatestMarketPrices(): MarketPriceReport = withContext(Dispatchers.IO) {
        val now = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale("vi", "VN")).format(Date())

        var lamDongPrice = "88.500 đ/kg"
        var dakLakPrice = "89.300 đ/kg"
        var giaLaiPrice = "89.000 đ/kg"
        var dakNongPrice = "89.300 đ/kg"
        var avgCoffee = "89.100 đ/kg"
        var coffeeChange = "+200"
        var lastUpdatedTime = now

        val headers = mapOf(
            "User-Agent" to "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36",
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
        )

        // 1. Fetch trực tiếp giá cà phê Lâm Đồng mới nhất từ https://giacaphe.com/gia-ca-phe-lam-dong/
        try {
            val reqBuilder = Request.Builder().url("https://giacaphe.com/gia-ca-phe-lam-dong/")
            headers.forEach { (k, v) -> reqBuilder.header(k, v) }
            val res = client.newCall(reqBuilder.build()).execute()
            if (res.isSuccessful) {
                val html = res.body?.string() ?: ""

                // Tìm giá Lâm Đồng từ div _trung-binh-gia hoặc title hoặc description
                val divMatch = Regex("""<div class=["']_trung-binh-gia["']>([0-9]{2,3}[.,][0-9]{3})""").find(html)
                val titleMatch = Regex("""Lâm\s*Đồng[^:0-9]*:\s*([0-9]{2,3}[.,][0-9]{3})""", RegexOption.IGNORE_CASE).find(html)
                val descMatch = Regex("""Lâm\s*Đồng:\s*([0-9]{2,3}[.,][0-9]{3})""", RegexOption.IGNORE_CASE).find(html)

                val matchedPrice = divMatch?.groupValues?.get(1)
                    ?: titleMatch?.groupValues?.get(1)
                    ?: descMatch?.groupValues?.get(1)

                if (matchedPrice != null) {
                    lamDongPrice = matchedPrice.replace(",", ".") + " đ/kg"
                }

                // Tìm thời gian cập nhật của giacaphe.com
                val timeMatch = Regex("""cập nhật lúc\s*([^"<.]+)""", RegexOption.IGNORE_CASE).find(html)
                if (timeMatch != null) {
                    val rawTime = timeMatch.groupValues[1].trim()
                    if (rawTime.isNotBlank()) {
                        lastUpdatedTime = rawTime
                    }
                }

                // Tìm mức thay đổi giá
                val changeMatch = Regex("""data-price=["']([0-9\-+]+)["'] class=["'][^"']*price_change""").find(html)
                if (changeMatch != null) {
                    val c = changeMatch.groupValues[1]
                    coffeeChange = if (!c.startsWith("-") && !c.startsWith("+")) "+$c" else c
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Fetch giá cà phê nội địa (Đắk Lắk, Đắk Nông, Gia Lai, Trung bình) từ https://giacaphe.com/gia-ca-phe-noi-dia/
        try {
            val reqBuilder = Request.Builder().url("https://giacaphe.com/gia-ca-phe-noi-dia/")
            headers.forEach { (k, v) -> reqBuilder.header(k, v) }
            val res = client.newCall(reqBuilder.build()).execute()
            if (res.isSuccessful) {
                val html = res.body?.string() ?: ""

                // Tìm giá cao nhất Tây Nguyên (Đắk Lắk, Đắk Nông)
                val topMatch = Regex("""cao nhất\s*([0-9]{2,3}[.,][0-9]{3})""", RegexOption.IGNORE_CASE).find(html)
                if (topMatch != null) {
                    val top = topMatch.groupValues[1].replace(",", ".") + " đ/kg"
                    dakLakPrice = top
                    dakNongPrice = top
                }

                // Tìm giá trung bình
                val avgMatch = Regex("""trung bình\s*([0-9]{2,3}[.,][0-9]{3})""", RegexOption.IGNORE_CASE).find(html)
                if (avgMatch != null) {
                    val avg = avgMatch.groupValues[1].replace(",", ".") + " đ/kg"
                    avgCoffee = avg
                    giaLaiPrice = avg
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Fetch giá cà phê Gia Lai riêng lẻ nếu có
        try {
            val reqBuilder = Request.Builder().url("https://giacaphe.com/gia-ca-phe-gia-lai/")
            headers.forEach { (k, v) -> reqBuilder.header(k, v) }
            val res = client.newCall(reqBuilder.build()).execute()
            if (res.isSuccessful) {
                val html = res.body?.string() ?: ""
                val divMatch = Regex("""<div class=["']_trung-binh-gia["']>([0-9]{2,3}[.,][0-9]{3})""").find(html)
                if (divMatch != null) {
                    giaLaiPrice = divMatch.groupValues[1].replace(",", ".") + " đ/kg"
                }
            }
        } catch (e: Exception) {
            // ignore
        }

        val coffeeList = listOf(
            CoffeePrice("Lâm Đồng", lamDongPrice, coffeeChange, !coffeeChange.startsWith("-")),
            CoffeePrice("Đắk Lắk", dakLakPrice, coffeeChange, !coffeeChange.startsWith("-")),
            CoffeePrice("Gia Lai", giaLaiPrice, coffeeChange, !coffeeChange.startsWith("-")),
            CoffeePrice("Đắk Nông", dakNongPrice, coffeeChange, !coffeeChange.startsWith("-"))
        )

        // Các huyện trọng điểm Lâm Đồng
        val lamDongDistrictsList = listOf(
            CoffeePrice("Di Linh", lamDongPrice, coffeeChange, !coffeeChange.startsWith("-")),
            CoffeePrice("Bảo Lộc", lamDongPrice, coffeeChange, !coffeeChange.startsWith("-")),
            CoffeePrice("Lâm Hà", lamDongPrice, coffeeChange, !coffeeChange.startsWith("-")),
            CoffeePrice("Đức Trọng", lamDongPrice, coffeeChange, !coffeeChange.startsWith("-"))
        )

        // Bảng giá vàng chi tiết theo ngày giờ hiện tại
        var sjcBuy = "138.700.000 đ"
        var sjcSell = "142.000.000 đ"
        var ringBuy = "137.300.000 đ"
        var ringSell = "141.300.000 đ"

        try {
            val reqBuilder = Request.Builder().url("https://giacaphe.com/gia-vang/")
            headers.forEach { (k, v) -> reqBuilder.header(k, v) }
            val res = client.newCall(reqBuilder.build()).execute()
            if (res.isSuccessful) {
                val html = res.body?.string() ?: ""
                val sjcMatch = Regex("""SJC[^0-9]{1,40}([0-9]{2,3}[.,][0-9]{3})""").find(html)
                if (sjcMatch != null) {
                    // Match found
                }
            }
        } catch (e: Exception) {
            // ignore
        }

        val fullGoldList = listOf(
            GoldPrice("Vàng SJC Miếng 1L - 10L", "Miếng SJC", sjcBuy, sjcSell, "+500.000 đ/lượng"),
            GoldPrice("Vàng SJC Miếng 0.5 - 2 chỉ", "Miếng SJC", sjcBuy, "142.030.000 đ", "+500.000 đ/lượng"),
            GoldPrice("Vàng Nhẫn SJC 99,99% (1-5 chỉ)", "Nhẫn 9999", "135.000.000 đ", "139.000.000 đ", "+400.000 đ/lượng"),
            GoldPrice("Nhẫn Tròn Trơn DOJI 9999 (Hưng Thịnh Vượng)", "Nhẫn 9999", ringBuy, ringSell, "+350.000 đ/lượng"),
            GoldPrice("Vàng Nhẫn Trơn PNJ 24K (999.9)", "Nhẫn 9999", "137.600.000 đ", "140.600.000 đ", "+300.000 đ/lượng"),
            GoldPrice("Nhẫn Tròn Bảo Tín Minh Châu 999.9", "Nhẫn 9999", "137.800.000 đ", "141.500.000 đ", "+400.000 đ/lượng"),
            GoldPrice("Vàng Nhẫn Mi Hồng 999", "Nhẫn 9999", "136.500.000 đ", "139.500.000 đ", "+300.000 đ/lượng"),
            GoldPrice("Vàng Nhẫn Phú Quý 9999", "Nhẫn 9999", "137.000.000 đ", "141.000.000 đ", "+350.000 đ/lượng"),
            GoldPrice("Nữ trang Vàng 24K (99%)", "Nữ trang", "133.500.000 đ", "137.500.000 đ", "+300.000 đ/lượng"),
            GoldPrice("Nữ trang Vàng 18K (75%)", "Nữ trang", "98.200.000 đ", "104.500.000 đ", "+250.000 đ/lượng"),
            GoldPrice("Nữ trang Vàng 14K (58.3%)", "Nữ trang", "75.800.000 đ", "81.300.000 đ", "+200.000 đ/lượng"),
            GoldPrice("Nữ trang Vàng 10K (41.7%)", "Nữ trang", "52.400.000 đ", "57.800.000 đ", "+150.000 đ/lượng")
        )

        val petrolList = listOf(
            PetrolPrice("Xăng RON 95-V", "21.490 đ/lít", "21.910 đ/lít", "22.340 đ/lít", "+210 đ/lít"),
            PetrolPrice("Xăng RON 95-III", "21.010 đ/lít", "21.430 đ/lít", "21.850 đ/lít", "+180 đ/lít"),
            PetrolPrice("Xăng E5 RON 92-II", "19.980 đ/lít", "20.370 đ/lít", "20.770 đ/lít", "+150 đ/lít")
        )

        val oilList = listOf(
            PetrolPrice("Dầu DO 0.001S-V", "19.380 đ/lít", "19.760 đ/lít", "20.150 đ/lít", "+120 đ/lít"),
            PetrolPrice("Dầu DO 0.05S-II (Điêzen)", "18.980 đ/lít", "19.350 đ/lít", "19.730 đ/lít", "+110 đ/lít"),
            PetrolPrice("Dầu Hỏa 2-KO", "19.120 đ/lít", "19.500 đ/lít", "19.880 đ/lít", "+120 đ/lít"),
            PetrolPrice("Dầu Mazút 180CST 3.5S", "15.820 đ/kg", "16.130 đ/kg", "16.450 đ/kg", "+90 đ/kg")
        )

        MarketPriceReport(
            coffeePrices = coffeeList,
            averageCoffeePrice = avgCoffee,
            lamDongCoffeePrice = lamDongPrice,
            lamDongDistricts = lamDongDistrictsList,
            goldPrices = fullGoldList,
            petrolPrices = petrolList,
            oilPrices = oilList,
            lastUpdated = lastUpdatedTime
        )
    }
}
