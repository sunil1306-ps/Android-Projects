package com.example.googlesignin1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.googlesignin1.ui.theme.GoogleSignIn1Theme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : ComponentActivity() {

    companion object {
        const val RC_SIGN_IN = 100
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            GoogleSignIn1Theme {

                if (mAuth.currentUser == null) {
                    GoogleSignInButton(
                        signInClicked = { signIn() }
                    )
                } else {
                    val user: FirebaseUser = mAuth.currentUser!!
                    ProfileScreen(
                        profileImage = user.photoUrl!!,
                        name = user.displayName!!,
                        email = user.email!!,
                        signOutClicked = { signOut() }
                    )
                }
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

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
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "SignIn Successful", Toast.LENGTH_SHORT).show()
                    setContent{
                        GoogleSignIn1Theme {
                            val user: FirebaseUser = mAuth.currentUser!!
                            ProfileScreen(
                                profileImage = user.photoUrl!!,
                                name = user.displayName!!,
                                email = user.email!!,
                                signOutClicked = { signOut() }
                            )
                        }
                    }
                } else {
                    Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signOut() {
        val googleSignInClient: GoogleSignInClient

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth.signOut()
        googleSignInClient.signOut().addOnSuccessListener {
            Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show()
            setContent {

                GoogleSignIn1Theme {
                    GoogleSignInButton(signInClicked = { signIn() })
                }

            }
        }.addOnFailureListener {
            Toast.makeText(this, "Sign Out Failed", Toast.LENGTH_SHORT).show()
        }

    }

}

@Composable
fun GoogleSignInButton(
    signInClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = modifier.size(150.dp),
            shape = RoundedCornerShape(100.dp),
            onClick = { signInClicked() }
        ) {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileImage: Uri,
    name: String,
    email: String,
    signOutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .size(150.dp)
                .fillMaxHeight(0.4f),
            shape = RoundedCornerShape(125.dp)
        ) {
            AsyncImage(
                model = profileImage,
                contentDescription = "profile_photo",
                contentScale = ContentScale.FillBounds
            )
        }

        Column(
            modifier = modifier
                .fillMaxHeight(0.6f)
                .padding(top = 60.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {  },
                readOnly = true,
                label = {
                    Text(text = "Name")
                }
            )

            OutlinedTextField(
                modifier = modifier.padding(top = 20.dp),
                value = email,
                onValueChange = {  },
                readOnly = true,
                label = {
                    Text(text = "Email")
                }
            )
            Button(
                modifier = modifier
                    .align(CenterHorizontally)
                    .padding(top = 100.dp),
                onClick = { signOutClicked() }
            ) {
                Text(text = "LogOut")
            }

        }

    }

}