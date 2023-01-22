package com.hiccup.hiccup_client

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val userCollection = Firebase.firestore.collection("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        val mGoogleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton.setOnClickListener {
            googleSignInResultLauncher.launch(mGoogleSignInClient.signInIntent)
        }
    }

    private var googleSignInResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        .getResult(ApiException::class.java)!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    Firebase.auth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                checkData(Firebase.auth.currentUser!!)
                            } else Toast.makeText(
                                this, "Authentication failed.", Toast.LENGTH_SHORT
                            ).show()
                        }
                } catch (ignored: ApiException) {
                }
            }
        }

    private fun checkData(firebaseUser: FirebaseUser) {
        userCollection.document(firebaseUser.uid).get().addOnSuccessListener {
            if (!it.exists()) {
                createData(firebaseUser)
            }
        }
        openHome()
    }

    private fun createData(firebaseUser: FirebaseUser) {
        val user = hashMapOf(
            "email" to firebaseUser.email,
            "name" to firebaseUser.displayName,
        )

        userCollection.document(firebaseUser.uid).set(user).addOnSuccessListener {
            Toast.makeText(this, "Account Created", Toast.LENGTH_LONG).show()
        }
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}