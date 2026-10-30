package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GoldPrice
import com.example.model.MarketPriceReport
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FullGoldPriceDialog(
    marketPrices: MarketPriceReport?,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Tất cả") }
    var searchQuery by remember { mutableStateOf("") }

    val currentTimestamp = remember {
        SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", Locale("vi", "VN")).format(Date())
    }

    val categories = listOf("Tất cả", "Miếng SJC", "Nhẫn 9999", "Nữ trang")

    val goldItems: List<GoldPrice> = marketPrices?.goldPrices ?: listOf(
        GoldPrice("Vàng SJC Miếng 1L - 10L", "Miếng SJC", "138.700.000 đ", "142.000.000 đ", "+500.000"),
        GoldPrice("Vàng Nhẫn SJC 99,99% (1-5 chỉ)", "Nhẫn 9999", "135.000.000 đ", "139.000.000 đ", "+400.000"),
        GoldPrice("Nhẫn Tròn DOJI 9999", "Nhẫn 9999", "137.300.000 đ", "141.300.000 đ", "+350.000"),
        GoldPrice("Vàng Nhẫn Trơn PNJ 24K", "Nhẫn 9999", "137.600.000 đ", "140.600.000 đ", "+300.000")
    )

    val filteredList = goldItems.filter { item ->
        val matchCategory = (selectedCategory == "Tất cả") || item.type.equals(selectedCategory, ignoreCase = true)
        val matchSearch = searchQuery.isBlank() || item.brand.contains(searchQuery, ignoreCase = true)
        matchCategory && matchSearch
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF1E1E1E),
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .fillMaxHeight(0.90f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // prevent dismiss when clicking content
                    )
                    .shadow(28.dp, RoundedCornerShape(26.dp))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.60f))
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .testTag("full_gold_price_dialog")
            ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // macOS Title Bar with Traffic Lights
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFF282828))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Traffic lights
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5F56))
                                .clickable { onDismiss() }
                        )
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFBD2E))
                                .clickable { onDismiss() }
                        )
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF27C93F))
                        )
                    }

                    // Window Title
                    Text(
                        text = "Bảng Giá Vàng Trực Tuyến Toàn Quốc",
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Làm mới",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Subheader: Timestamp according to current date/time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF242424))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399))
                        )
                        Text(
                            text = "Cập nhật lúc: ${marketPrices?.lastUpdated ?: currentTimestamp}",
                            color = Color(0xFFE5E7EB),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "ĐVT: VNĐ / lượng",
                        color = Color(0xFFFBBF24),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Search & Filter Categories
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Tìm thương hiệu (SJC, DOJI, PNJ, 24K...)", fontSize = 11.5.sp, color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color(0xFF383838),
                            focusedContainerColor = Color(0xFF252525),
                            unfocusedContainerColor = Color(0xFF252525),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    )

                    // Categories chip row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = (selectedCategory == cat)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFF007AFF) else Color(0xFF282828),
                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383838)) else null,
                                modifier = Modifier.clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF333333))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF252525))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Loại vàng / Thương hiệu", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                    Text("Mua vào", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                    Text("Bán ra", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                }

                // Table Items List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredList) { item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF242424),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1.3f)) {
                                    Text(
                                        text = item.brand,
                                        color = Color.White,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = Color(0xFF34D399),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = item.changeText,
                                            color = Color(0xFF34D399),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(0.9f)) {
                                    Text(
                                        text = item.buyPrice,
                                        color = Color(0xFF60A5FA),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("Mua", color = Color.Gray, fontSize = 9.sp)
                                }

                                Column(modifier = Modifier.weight(0.9f)) {
                                    Text(
                                        text = item.sellPrice,
                                        color = Color(0xFF4ADE80),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("Bán", color = Color.Gray, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }

                // Footer note
                Surface(
                    color = Color(0xFF222222),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nguồn: Giá thị trường vàng Việt Nam (SJC, DOJI, PNJ)",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${filteredList.size} loại",
                            color = Color(0xFF007AFF),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
}
