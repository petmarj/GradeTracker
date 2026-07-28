package com.example.gradetracker.ui.student


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gradetracker.R

@Composable
fun PictureCard(
    state: StudentUiState
){
    val student = state.student
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(10.dp),

        ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.student),
                contentDescription = "Profilbild",
                contentScale = ContentScale.Crop,
                alignment = BiasAlignment(
                    horizontalBias = 0f,
                    verticalBias = -0.4f
                ),
                modifier = Modifier
                    .size(120.dp)
            )
        }
    }
}