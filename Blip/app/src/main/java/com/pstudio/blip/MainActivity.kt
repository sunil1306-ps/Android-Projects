package com.pstudio.blip

import android.app.Activity
import com.onesignal.OneSignal
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.FirebaseApp
import com.onesignal.debug.LogLevel
import com.pstudio.blip.ui.theme.BlipTheme
import com.pstudio.blip.ui.theme.navigation.BlipApp
import com.pstudio.blip.ui.theme.screens.ChatScreen
import com.pstudio.blip.ui.theme.screens.HomeScreen
import com.pstudio.blip.ui.theme.screens.LogInScreen
import com.pstudio.blip.ui.theme.screens.SignUpScreen
import com.pstudio.blip.ui.theme.screens.SplashScreen
import com.pstudio.blip.utilclasses.hasStoragePermission
import com.pstudio.blip.utilclasses.requestLegacyStoragePermissions
import com.pstudio.blip.utilclasses.requestManageAllFilesPermission
import com.pstudio.blip.viewmodels.AuthViewModel
import com.pstudio.blip.viewmodels.ChatViewModel
import com.pstudio.blip.viewmodels.UserViewModel

@RequiresApi(api = 26)
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private val STORAGE_PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasStoragePermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestManageAllFilesPermission(this)
            } else {
                (this as? Activity)?.let { requestLegacyStoragePermissions(it, STORAGE_PERMISSION_REQUEST_CODE) }
            }
        } else {
            // Safe to write to /storage/emulated/0/Blip/...
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "chat_channel",
                "Chat Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            val config = HashMap<String, String>()
            config["cloud_name"] = "dktkdph2n"
            config["upload_preset"] = "blip_preset"

            MediaManager.init(this, config)
            channel.description = "Notifications for new messages"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // Initialize OneSignal with your app context
        OneSignal.initWithContext(this, "f236a802-d418-4f09-a430-d491f1888955")

        WindowCompat.setDecorFitsSystemWindows(window, false)
        FirebaseApp.initializeApp(this)
        setContent {

            val authViewModel: AuthViewModel = viewModel()
            val userViewModel: UserViewModel = viewModel()
            val chatViewModel: ChatViewModel = viewModel()

            val intent = (LocalActivity.current as? Activity)?.intent
            LaunchedEffect(intent) {
                val shouldNavigate = intent?.getStringExtra("navigate_to") == "chatscreen"
                val friendId = intent?.getStringExtra("friendId")
                val friendUserName = intent?.getStringExtra("friendUserName")
                if (shouldNavigate && friendId != null) {
                    navController.navigate("chatScreen/$friendId/$friendUserName")
                }
            }

            navController = rememberNavController()
            SetStatusBarColor(Color.Black)
            BlipTheme {
                BlipApp(
                    navController,
                    authViewModel,
                    userViewModel,
                    chatViewModel
                )
            }
        }
    }
}

@Composable
fun SetStatusBarColor(color: Color) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(color)
    }
}