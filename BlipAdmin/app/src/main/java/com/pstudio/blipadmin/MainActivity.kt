package com.pstudio.blipadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pstudio.blipadmin.ui.theme.BlipAdminTheme
import com.pstudio.blipadmin.viewmodels.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        

        enableEdgeToEdge()
        setContent {
            BlipAdminTheme {

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFlaggedMessagesScreen(viewModel: AdminViewModel) {
    val flaggedMessages = viewModel.flaggedMessages

    Scaffold(topBar = {
        TopAppBar(title = { Text("Flagged Messages") })
    }) {
        LazyColumn(
            contentPadding = PaddingValues(it.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(flaggedMessages) { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Message: ${message.message}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "From: ${message.senderUsername}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "To: ${message.receiverUsername}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "At: ${
                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
                                    Date(message.timestamp)
                                )
                            }",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { viewModel.acceptMessage(message) }) {
                                Text("Accept")
                            }
                            Button(onClick = { viewModel.declineMessage(message) }) {
                                Text("Decline")
                            }
                        }
                    }
                }
            }
    }
}