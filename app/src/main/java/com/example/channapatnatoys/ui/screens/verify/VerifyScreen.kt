package com.example.channapatnatoys.ui.screens.verify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.channapatnatoys.ui.UiState
import com.example.channapatnatoys.ui.theme.*

@Composable
fun VerifyScreen(
    toyIdArg: String?,
    viewModel: VerifyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var otpText by remember { mutableStateOf(toyIdArg ?: "") }
    
    LaunchedEffect(toyIdArg) {
        if (!toyIdArg.isNullOrBlank() && uiState is UiState.Idle) {
            viewModel.verifyToy(toyIdArg)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamWhite)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter Toy ID",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkBg,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = otpText,
                    onValueChange = { if (it.length <= 10) otpText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        letterSpacing = 8.sp
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { viewModel.verifyToy(otpText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaRed),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Verify", color = CreamWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(color = DarkBg)
            }
            is UiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = state.message,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            is UiState.Success -> {
                val data = state.data
                
                // Show basic toy details
                data.toy?.let { toy ->
                    // Toy Image
                    if (toy.imageUrl.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            coil.compose.AsyncImage(
                                model = toy.imageUrl,
                                contentDescription = toy.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Toy Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LightCream)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Toy Verified: ${toy.name}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Artisan: ${toy.artisanName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkBg
                            )
                            Text(
                                text = "Location: ${toy.workshopAddress}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkBg
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (data.storyEnglish.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LightCream)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Story of This Toy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaRed
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = data.storyEnglish,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkBg
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (data.storyKannada.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // light green
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "ಈ ಆಟಿಕೆಯ ಕಥೆ",
                                fontFamily = NotoSansKannada,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = data.storyKannada,
                                fontFamily = NotoSansKannada,
                                color = DarkBg,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            UiState.Idle -> {}
        }
    }
}
