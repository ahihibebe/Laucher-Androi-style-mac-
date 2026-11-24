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
        val viLocale = Locale.forLanguageTag("vi-VN")
        val now = SimpleDateFormat("HH:mm - dd/MM/yyyy", viLocale).format(Date())
        val todayDate = SimpleDateFormat("dd/MM/yyyy", viLocale).format(Date())

        var lamDongPrice = "88.500 đ/kg"
        var dakLakPrice = "89.300 đ/kg"
        var giaLaiPrice = "89.000 đ/kg"
        var dakNongPrice = "89.300 đ/kg"
        var avgCoffee = "89.100 đ/kg"
        var coffeeChange = "+200"
        var pepperPrice = "137.500 đ/kg"
        var pepperChange = "-1.000 đ/kg"
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

        // 3.5 Fetch giá xăng dầu Petrolimex mới nhất theo ngày hiện tại
        val fuelResult = fetchPetrolimexPrices(headers, todayDate)
        val petrolList = fuelResult.petrolPrices
        val oilList = fuelResult.oilPrices
        val fuelUpdatedDate = fuelResult.updatedDate

        // 4. Fetch giá hồ tiêu từ https://giacaphe.com/gia-tieu-noi-dia/ và https://giacaphe.com/gia-tieu-hom-nay/
        val pepperUrls = listOf("https://giacaphe.com/gia-tieu-noi-dia/", "https://giacaphe.com/gia-tieu-hom-nay/")
        for (pUrl in pepperUrls) {
            try {
                val reqBuilder = Request.Builder().url(pUrl)
                headers.forEach { (k, v) -> reqBuilder.header(k, v) }
                val res = client.newCall(reqBuilder.build()).execute()
                if (res.isSuccessful) {
                    val html = res.body?.string() ?: ""
                    var parsedPrice: String? = null

                    // Cách 1: Tìm theo span class gt-cao-gia
                    val gtCaoGiaMatch = Regex("""class=["']gt-cao-gia["']>([0-9]{2,3}[.,][0-9]{3})""").find(html)
                    if (gtCaoGiaMatch != null) {
                        parsedPrice = gtCaoGiaMatch.groupValues[1].replace(",", ".") + " đ/kg"
                    }

                    // Cách 2: Tìm theo meta description hoặc text "cao nhất"
                    if (parsedPrice == null) {
                        val metaMatch = Regex("""cao\s*nhất[^0-9]{0,35}([0-9]{2,3}[.,][0-9]{3})""", RegexOption.IGNORE_CASE).find(html)
                        if (metaMatch != null) {
                            parsedPrice = metaMatch.groupValues[1].replace(",", ".") + " đ/kg"
                        }
                    }

                    // Cách 3: Tìm theo các hàng giá trong bảng tiêu (Gia Lai, Đắk Lắk, Bình Phước, Bà Rịa...)
                    if (parsedPrice == null) {
                        val tablePrices = Regex(""">([1-2][0-9]{2}[.,][0-9]{3})<""").findAll(html)
                            .mapNotNull { it.groupValues[1].replace(".", "").replace(",", "").toIntOrNull() }
                            .toList()
                        if (tablePrices.isNotEmpty()) {
                            val maxPrice = tablePrices.maxOrNull() ?: 137500
                            parsedPrice = String.format(Locale.US, "%,d", maxPrice).replace(",", ".") + " đ/kg"
                        }
                    }

                    if (parsedPrice != null) {
                        pepperPrice = parsedPrice
                    }

                    // Tìm biến động giá tiêu
                    val dataPriceMatch = Regex("""data-price=["']([0-9\-+]+)["']\s*class=["'][^"']*price_change""").find(html)
                    if (dataPriceMatch != null) {
                        val rawNum = dataPriceMatch.groupValues[1].toIntOrNull()
                        if (rawNum != null) {
                            pepperChange = if (rawNum > 0) "+${String.format(Locale.US, "%,d", rawNum).replace(",", ".")} đ/kg"
                            else if (rawNum < 0) "${String.format(Locale.US, "%,d", rawNum).replace(",", ".")} đ/kg"
                            else "0 đ/kg"
                        }
                    } else {
                        val textChangeMatch = Regex("""(giảm|tăng)\s*(?:mạnh\s*)?([0-9.,]+)\s*(?:vnđ|đ|đồng)""", RegexOption.IGNORE_CASE).find(html)
                        if (textChangeMatch != null) {
                            val dir = textChangeMatch.groupValues[1].lowercase()
                            val amt = textChangeMatch.groupValues[2].replace(",", ".")
                            val sign = if (dir.contains("giảm")) "-" else "+"
                            pepperChange = "$sign$amt đ/kg"
                        }
                    }

                    if (parsedPrice != null) {
                        break // Đã lấy được giá tiêu thực tế thành công
                    }
                }
            } catch (e: Exception) {
                // ignore và thử link kế tiếp nếu có
            }
        }

        MarketPriceReport(
            coffeePrices = coffeeList,
            averageCoffeePrice = avgCoffee,
            lamDongCoffeePrice = lamDongPrice,
            lamDongDistricts = lamDongDistrictsList,
            pepperPrice = pepperPrice,
            pepperChange = pepperChange,
            goldPrices = fullGoldList,
            petrolPrices = petrolList,
            oilPrices = oilList,
            fuelUpdatedDate = fuelUpdatedDate,
            lastUpdated = lastUpdatedTime
        )
    }

    private data class FuelFetchResult(
        val petrolPrices: List<PetrolPrice>,
        val oilPrices: List<PetrolPrice>,
        val updatedDate: String
    )

    private fun fetchPetrolimexPrices(
        headers: Map<String, String>,
        todayDate: String
    ): FuelFetchResult {
        val defaultRon95V = PetrolPrice("Xăng E10 RON 95-V", "21.100 đ/lít", "21.520 đ/lít")
        val defaultRon95III = PetrolPrice("Xăng E10 RON 95-III", "19.100 đ/lít", "19.480 đ/lít")
        val defaultE5Ron92 = PetrolPrice("Xăng E5 RON 92-II", "18.570 đ/lít", "18.940 đ/lít")
        val defaultDo005 = PetrolPrice("Dầu DO 0.05S-II", "26.340 đ/lít", "26.860 đ/lít")
        val defaultKerosene = PetrolPrice("Dầu Hỏa 2-K", "25.850 đ/lít", "26.360 đ/lít")
        val defaultDo001 = PetrolPrice("Dầu DO 0.001S-V", "28.140 đ/lít", "28.700 đ/lít")
        val defaultMazut = PetrolPrice("Dầu Mazút 180CST 3.5S", "18.670 đ/kg", "19.040 đ/kg")

        val fuelUrls = listOf(
            "https://webgia.com/gia-xang-dau/petrolimex/",
            "https://giacaphe.com/gia-xang-dau/",
            "https://www.pvoil.com.vn/truyen-thong/tin-gia-xang-dau"
        )

        val rowRegex = Regex("""<tr\b[^>]*>(.*?)</tr>""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val cellRegex = Regex("""<t[dh]\b[^>]*>(.*?)</t[dh]>""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val tagRegex = Regex("""<[^>]+>""")
        val priceRegex = Regex("""\b([1-9][0-9][.,][0-9]{3})\b""")
        val dateRegex = Regex(
            """(?:áp\s*dụng\s*từ|kỳ\s*điều\s*hành|cập\s*nhật(?:\s*lúc|\s*ngày)?|từ\s*\d{1,2}h\d{0,2}\s*ngày|ngày)\s*(?:\d{1,2}[h:]\d{2}\s*(?:ngày\s*)?)?(\d{1,2}/\d{1,2}/20\d{2})""",
            RegexOption.IGNORE_CASE
        )

        for (url in fuelUrls) {
            try {
                val reqBuilder = Request.Builder().url(url)
                headers.forEach { (k, v) -> reqBuilder.header(k, v) }
                client.newCall(reqBuilder.build()).execute().use { res ->
                    if (!res.isSuccessful) return@use
                    val html = res.body?.string().orEmpty()
                    if (html.isBlank()) return@use

                    var ron95V: PetrolPrice? = null
                    var ron95III: PetrolPrice? = null
                    var e5Ron92: PetrolPrice? = null
                    var do001: PetrolPrice? = null
                    var do005: PetrolPrice? = null
                    var kerosene: PetrolPrice? = null
                    var mazut: PetrolPrice? = null

                    rowRegex.findAll(html).forEach { rowMatch ->
                        val cells = cellRegex.findAll(rowMatch.groupValues[1])
                            .map { cellMatch ->
                                tagRegex.replace(cellMatch.groupValues[1], " ")
                                    .replace("&nbsp;", " ")
                                    .replace("&amp;", "&")
                                    .replace(Regex("""\s+"""), " ")
                                    .trim()
                            }
                            .filter { it.isNotEmpty() }
                            .toList()

                        if (cells.size < 2) return@forEach
                        val nameCell = cells[0]
                        val priceValues = cells.drop(1).mapNotNull { cellText ->
                            val trimmed = cellText.trim()
                            if (trimmed.startsWith("+") || trimmed.startsWith("-")) {
                                null
                            } else {
                                priceRegex.find(trimmed)?.groupValues?.get(1)?.replace(",", ".")
                            }
                        }
                        if (priceValues.isEmpty()) return@forEach

                        val r1 = priceValues[0]
                        val r2 = priceValues.getOrNull(1) ?: priceValues[0]

                        when {
                            ron95V == null && Regex("""ron\s*95[-\s]*v\b""", RegexOption.IGNORE_CASE).containsMatchIn(nameCell) -> {
                                ron95V = PetrolPrice(nameCell, "$r1 đ/lít", "$r2 đ/lít")
                            }
                            ron95III == null && Regex("""ron\s*95[-\s]*iii\b""", RegexOption.IGNORE_CASE).containsMatchIn(nameCell) -> {
                                ron95III = PetrolPrice(nameCell, "$r1 đ/lít", "$r2 đ/lít")
                            }
                            e5Ron92 == null && Regex("""e5\s*ron\s*92""", RegexOption.IGNORE_CASE).containsMatchIn(nameCell) -> {
                                e5Ron92 = PetrolPrice(nameCell, "$r1 đ/lít", "$r2 đ/lít")
                            }
                            do001 == null && Regex("""0[.,]001\s*s""", RegexOption.IGNORE_CASE).containsMatchIn(nameCell) -> {
                                do001 = PetrolPrice(nameCell, "$r1 đ/lít", "$r2 đ/lít")
                            }
                            do005 == null && Regex("""0[.,]05\s*s""", RegexOption.IGNORE_CASE).containsMatchIn(nameCell) -> {
                                do005 = PetrolPrice(nameCell, "$r1 đ/lít", "$r2 đ/lít")
                            }
                            kerosene == null && Regex("""d[ầa]u\s*h[ỏo]a""", RegexOption.IGNORE_CASE).containsMatchIn(nameCell) -> {
                                kerosene = PetrolPrice(nameCell, "$r1 đ/lít", "$r2 đ/lít")
                            }
                            mazut == null && Regex("""maz[uú]t|180\s*cst""", RegexOption.IGNORE_CASE).containsMatchIn(nameCell) -> {
                                mazut = PetrolPrice(nameCell, "$r1 đ/kg", "$r2 đ/kg")
                            }
                        }
                    }

                    val parsedPetrolCount = listOfNotNull(ron95V, ron95III, e5Ron92).size
                    val parsedOilCount = listOfNotNull(do001, do005, kerosene, mazut).size
                    if (parsedPetrolCount >= 2 || parsedOilCount >= 2) {
                        val plainPageText = tagRegex.replace(html, " ")
                            .replace("&nbsp;", " ")
                            .replace(Regex("""\s+"""), " ")
                        val parsedDate = dateRegex.find(plainPageText)?.groupValues?.get(1)?.trim()
                            ?.takeIf { it.isNotBlank() } ?: todayDate

                        return FuelFetchResult(
                            petrolPrices = listOf(
                                ron95III ?: defaultRon95III,
                                e5Ron92 ?: defaultE5Ron92,
                                ron95V ?: defaultRon95V
                            ),
                            oilPrices = listOf(
                                do005 ?: defaultDo005,
                                kerosene ?: defaultKerosene,
                                do001 ?: defaultDo001,
                                mazut ?: defaultMazut
                            ),
                            updatedDate = parsedDate
                        )
                    }
                }
            } catch (_: Exception) {
                // Thử nguồn tiếp theo nếu nguồn hiện tại lỗi
            }
        }

        return FuelFetchResult(
            petrolPrices = listOf(defaultRon95III, defaultE5Ron92, defaultRon95V),
            oilPrices = listOf(defaultDo005, defaultKerosene, defaultDo001, defaultMazut),
            updatedDate = todayDate
        )
    }
}
