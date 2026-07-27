package com.example.gradetracker.ui.settings


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ApiConnectionCard(
    state: SettingsUiState,
    onTestConnection: () -> Unit,
    onAddToken: () -> Unit,
    onDeleteToken: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Lerbermatt API  ",
                    style = MaterialTheme.typography.titleLarge
                )
                ConnectionStatusLed(connectionState = state.connectionState)
            }
            HorizontalDivider(thickness = 2.dp)
            when (val connection = state.connectionState) {
                ConnectionState.NotTested ->
                    if (!state.tokenConfigured) {
                        Text("Kein Token hinzugefügt")
                    }

                ConnectionState.Testing -> {}

                ConnectionState.Connected ->{}

                ConnectionState.MissingToken -> Text("Token nicht vorhanden")

                is ConnectionState.Failed -> Text(connection.message)
            }

            Row() {
                Button(
                    onClick = onTestConnection,
                    enabled = state.connectionState != ConnectionState.Testing
                ) {
                    if (state.connectionState == ConnectionState.Testing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )

                        Spacer(Modifier.width(8.dp))
                        Text("Testen...")
                    }

                    Text("Verbindung testen")
                }
                OutlinedButton(
                    onClick = onAddToken
                ) {
                    Text(
                        text = if(state.tokenConfigured){
                                "Token ändern"
                            } else{
                                "Token hinzufügen"
                            },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if(state.tokenConfigured){
                    IconButton(
                        onClick = onDeleteToken
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteForever,
                            contentDescription = "Delete",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectionStatusLed(
    connectionState: ConnectionState
) {
    val targetColor = when (connectionState) {
        ConnectionState.NotTested ->
            MaterialTheme.colorScheme.outline

        ConnectionState.Testing ->
            MaterialTheme.colorScheme.tertiary

        ConnectionState.Connected ->
            Color(0xFF4CAF50)

        ConnectionState.MissingToken ->
            Color(0xFFFF0000)

        is ConnectionState.Failed ->
            Color(0xFFFF0000)
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        label = "connectionStatusColor"
    )

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(
                color = animatedColor,
                shape = CircleShape
            )
    )
}