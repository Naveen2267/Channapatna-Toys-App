package com.example.channapatnatoys.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.channapatnatoys.R
import com.example.channapatnatoys.ui.theme.CreamWhite
import com.example.channapatnatoys.ui.theme.DarkBg
import com.example.channapatnatoys.ui.theme.Poppins
import com.example.channapatnatoys.ui.theme.TurmericYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannapatnaTopBar(
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Spinning top logo
                Image(
                    painter = painterResource(id = R.drawable.ic_spinning_top),
                    contentDescription = "Spinning Top Logo",
                    modifier = Modifier.size(52.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "Channapatna",
                        color = CreamWhite,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp,
                        lineHeight = 25.sp
                    )
                    Text(
                        text = "Toys",
                        color = TurmericYellow,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CreamWhite
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBg
        )
    )
}
