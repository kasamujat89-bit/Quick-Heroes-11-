package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.CricketViewModel

data class StoreProduct(
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val rating: String,
    val color: Long = 0xFFDC2626
)

@Composable
fun StoreScreen(
    viewModel: CricketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products = listOf(
        StoreProduct("sp1", "Pro Sublimation Jersey", "Custom Jerseys", "₹499", "4.9", 0xFFE11D48),
        StoreProduct("sp2", "Kashmir Willow Bat Grade 1", "Cricket Bats", "₹1,899", "4.8", 0xFFF59E0B),
        StoreProduct("sp3", "Heavy Tennis Cricket Ball (Pack of 6)", "Balls", "₹450", "4.7", 0xFF10B981),
        StoreProduct("sp4", "Leather Match Ball (Alum Tanned)", "Balls", "₹799", "4.9", 0xFFB91C1C),
        StoreProduct("sp5", "Cricket Batting Pads Pro", "Protective Gear", "₹1,499", "4.8", 0xFF3B82F6),
        StoreProduct("sp6", "Full Cricket Kit Bag with Wheels", "Kit Bags", "₹2,499", "4.9", 0xFF8B5CF6)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("store_screen")
    ) {
        // App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("store_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Quick Heroes 11 Store",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Authentic cricket kits, custom jerseys & gear",
                        fontSize = 12.sp,
                        color = CricTextSecondary
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.id }) { product ->
                StoreProductCard(product = product)
            }
        }
    }
}

@Composable
fun StoreProductCard(product: StoreProduct) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(product.color).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = Color(product.color),
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.category.uppercase(),
                    fontSize = 10.sp,
                    color = CricTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.price,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = CricTeal
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = CricGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(product.rating, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Add to cart */ },
                    colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                ) {
                    Text("Order Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
