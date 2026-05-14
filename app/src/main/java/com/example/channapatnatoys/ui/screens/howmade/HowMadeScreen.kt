package com.example.channapatnatoys.ui.screens.howmade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.channapatnatoys.ui.theme.*

data class Step(val title: String, val description: String)

val steps = listOf(
    Step("Selecting Hale Wood", "The process begins with acquiring 'Aale mara' (Ivory-wood), known for its soft texture and fine grain, perfect for carving."),
    Step("Shaping on the Lathe", "The wood is cut, seasoned, and then turned on a lathe. Skilled artisans use hand tools to shape the rapidly spinning wood into beautiful forms."),
    Step("Applying Lac Dye", "Natural dyes mixed with lac (a natural resin) are applied to the spinning wood. The friction melts the lac, creating a vibrant, seamless colored layer."),
    Step("Polishing and Finishing", "The final polish is achieved using palm leaves (Tale Gari), which gives Channapatna toys their signature glossy, non-toxic finish.")
)

@Composable
fun HowMadeScreen(
    viewModel: HowMadeViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamWhite)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "The Crafting Process",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBg,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        itemsIndexed(steps) { index, step ->
            val isEven = index % 2 == 0
            val cardColor = if (isEven) LightCream else Color(0xFFE8F5E9)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TerracottaRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkBg
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}
