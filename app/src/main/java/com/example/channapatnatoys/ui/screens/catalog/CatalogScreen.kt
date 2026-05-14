package com.example.channapatnatoys.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.channapatnatoys.data.model.Toy
import com.example.channapatnatoys.ui.UiState
import com.example.channapatnatoys.ui.components.ToyCard
import com.example.channapatnatoys.ui.theme.*
import coil.request.ImageRequest
import coil.size.Size
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onNavigateToVerify: (String) -> Unit, // kept for compatibility but unused
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Track which toy the user tapped — null means no bottom sheet shown
    var selectedToy by remember { mutableStateOf<Toy?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamWhite)
            .padding(16.dp)
    ) {

        // Grid
        when (val state = uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkBg)
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is UiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.data) { toy ->
                        ToyCard(
                            toy = toy,
                            onClick = { selectedToy = toy } // open bottom sheet
                        )
                    }
                }
            }
            UiState.Idle -> {}
        }
    }

    // Bottom Sheet — pops up when a toy is tapped
    selectedToy?.let { toy ->
        ModalBottomSheet(
            onDismissRequest = { selectedToy = null },
            containerColor = CreamWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Toy Image
                if (toy.imageUrl.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(toy.imageUrl)
                                .size(Size.ORIGINAL)
                                .crossfade(true)
                                .build(),
                            contentDescription = toy.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Toy Name & Category
                Text(
                    text = toy.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkBg
                )
                Text(
                    text = toy.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = ForestGreen,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(12.dp))

                // Details
                ToyDetailRow(label = "Artisan", value = toy.artisanName)
                ToyDetailRow(label = "Workshop", value = toy.workshopAddress)
                ToyDetailRow(label = "Lac Colors", value = toy.lacColor)
                if (toy.madeYear > 0) {
                    ToyDetailRow(label = "Year Made", value = toy.madeYear.toString())
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ToyDetailRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkBg,
            modifier = Modifier.weight(0.6f)
        )
    }
}
