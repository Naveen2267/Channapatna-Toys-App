package com.example.channapatnatoys.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.channapatnatoys.data.model.WorkshopLocation
import com.example.channapatnatoys.ui.theme.LightCream

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.platform.LocalContext

@Composable
fun WorkshopCard(
    workshop: WorkshopLocation,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = LightCream)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // A simple placeholder icon for the workshop
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = workshop.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workshop.name.ifBlank { "Channapatna Workshop" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (workshop.address.isNotBlank()) {
                    Text(
                        text = workshop.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        // Open in Google Maps app or fallback to browser
                        val uriStr = "geo:${workshop.lat},${workshop.lng}?q=${workshop.lat},${workshop.lng}(${Uri.encode(workshop.name)})"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr))
                        intent.setPackage("com.google.android.apps.maps")
                        
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val webUri = "https://www.google.com/maps/search/?api=1&query=${workshop.lat},${workshop.lng}"
                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUri))
                            context.startActivity(webIntent)
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Map",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Go to Maps", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
