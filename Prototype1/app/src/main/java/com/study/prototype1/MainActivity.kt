package com.study.prototype1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.prototype1.R
import com.study.prototype1.downloadmanagerguide.AndroidDownloader
import com.study.prototype1.downloadmanagerguide.DownloadCompletedReceiver
import com.study.prototype1.ui.theme.Prototype1Theme
import com.study.prototype1.viewModels.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel = MainViewModel("Images")

    private val downloadCompleted = DownloadCompletedReceiver()

    private val context: Context = this

    companion object {
        const val RC_SIGN_IN = 100
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(
            downloadCompleted,
            IntentFilter(
                "android.intent.action.DOWNLOAD_COMPLETE"
            )
        )

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val downloader = AndroidDownloader(this)

        setContent {
            Prototype1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Navigation(
                        { logOut() },
                        mAuth,
                        { signIn() },
                        viewModel,
                        "splash_screen",
                        downloader,
                        context
                    )
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET),
                1
            )
        }

    }
    /******************************** SignIn starts ***************************/
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: Exception) {
                    Log.d("SignIn", "Google SignIn Failed")
                }
            } else {
                Log.d("SignIn", exception.toString())
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val downloader = AndroidDownloader(this)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "SignIn Successful", Toast.LENGTH_SHORT).show()
                    setContent{
                        Prototype1Theme {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Navigation(
                                    signOutClicked = { logOut() },
                                    mAuth = mAuth,
                                    signInClicked = { signIn() },
                                    viewModel = viewModel,
                                    startDest = "year",
                                    downloader = downloader,
                                    context = context
                                )
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun logOut() {
        val googleSignInClient: GoogleSignInClient

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth.signOut()
        googleSignInClient.signOut().addOnSuccessListener {
            Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show()
            val downloader = AndroidDownloader(this)
            setContent {
                Prototype1Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Navigation(
                            signOutClicked = { logOut() },
                            mAuth = mAuth,
                            signInClicked = { signIn() },
                            viewModel = viewModel,
                            startDest = "sign_in",
                            downloader = downloader,
                            context = context
                        )
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Sign Out Failed", Toast.LENGTH_SHORT).show()
        }

    }
    /******************************** SignIn ends ******************************/

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadCompleted)
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Prototype1Theme {
    }
}