package com.example.khawajatakeaway
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({

            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // User is logged in, now check their role
                checkUserRoleAndRedirect(currentUser.uid)
            } else {
                // User is not logged in
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 3000) // 3-second splash screen
    }

    private fun checkUserRoleAndRedirect(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    if (role == "admin") {
                        // Redirect to Admin HomeActivity
                        val intent = Intent(this, AdminActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Redirect to regular HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                } else {
                    // If no user document found, redirect to Login
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener {
                // Handle failure to fetch role
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
    }
}
