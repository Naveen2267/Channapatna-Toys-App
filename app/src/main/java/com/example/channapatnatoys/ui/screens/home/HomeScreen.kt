package com.example.channapatnatoys.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.channapatnatoys.R
import com.example.channapatnatoys.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToVerify: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        // ── Spinning Top Logo ──────────────────────────────────
        Image(
            painter = painterResource(id = R.drawable.ic_spinning_top),
            contentDescription = "Channapatna Spinning Top",
            modifier = Modifier.size(160.dp)
        )

        // ── Title ──────────────────────────────────────────────
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Channapatna",
                style = MaterialTheme.typography.displaySmall,
                color = CreamWhite,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Toys",
                style = MaterialTheme.typography.displaySmall,
                color = TurmericYellow,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ಚನ್ನಪಟ್ಟಣ ಆಟಿಕೆಗಳು",
                fontFamily = NotoSansKannada,
                fontSize = 18.sp,
                color = LightCream,
                fontWeight = FontWeight.Normal
            )
        }

        // ── Verify Button ──────────────────────────────────────
        Button(
            onClick = onNavigateToVerify,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CreamWhite),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Verify My Toy",
                color = DarkBg,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // ── Feature Cards Row ──────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                icon = Icons.Filled.Star,
                iconTint = ForestGreen,
                label = "How Made",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = Icons.Filled.LocationOn,
                iconTint = TerracottaRed,
                label = "Meet Maker",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = Icons.Filled.List,
                iconTint = TurmericYellow,
                label = "Catalog",
                modifier = Modifier.weight(1f)
            )
        }

        // ── Tagline ────────────────────────────────────────────
        Text(
            text = "Authentic · GI Tagged · Heritage Craft",
            fontSize = 12.sp,
            color = LightCream.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkBg,
                textAlign = TextAlign.Center
            )
        }
    }
}
