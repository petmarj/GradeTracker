package ch.example.gradetracker.ui.settings


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ApiConnectionCard(
    state: SettingsUiState,
    onTestConnection: () -> Unit,
    onLogin: () -> Unit,
    onLogout: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Absenzensystem ",
                    style = MaterialTheme.typography.titleLarge
                )
                ConnectionStatusLed(connectionState = state.connectionState)
            }
            HorizontalDivider(thickness = 2.dp)
            Text(
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                text = when (val connection = state.connectionState) {
                    ConnectionState.NotTested -> "Bitte Testen"

                    ConnectionState.Testing -> "Am testen.."

                    ConnectionState.Connected -> "Angemeldet als: " + state.user?.firstname + " " + state.user?.lastname

                    ConnectionState.NotLoggedIn -> "Bitte Anmelden"

                    is ConnectionState.Failed -> connection.message
                }
            )


            Row() {
                if (!state.loggedIn) {
                    Button(
                        onClick = onLogin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Im Absenzensystem anmelden",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Abmelden",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
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

        ConnectionState.NotLoggedIn ->
            Color(0xFFFF9900)

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