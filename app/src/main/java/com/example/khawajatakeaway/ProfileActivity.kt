package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var textViewName: TextView
    private lateinit var textViewMobile: TextView
    private lateinit var textViewAddress: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewRole: TextView
    private lateinit var buttonEditProfile: Button
    private lateinit var buttonLogout: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        textViewName = findViewById(R.id.textViewName)
        textViewMobile = findViewById(R.id.textViewMobile)
        textViewAddress = findViewById(R.id.textViewAddress)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewRole = findViewById(R.id.textViewRole)
        buttonEditProfile = findViewById(R.id.buttonEditProfile)
        buttonLogout = findViewById(R.id.buttonLogout)

        fetchUserProfile()

        buttonEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        buttonLogout.setOnClickListener{
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        textViewName.text = "Name: ${document.getString("name") ?: "N/A"}"
                        textViewMobile.text = "Mobile: ${document.getString("mobile") ?: "N/A"}"
                        textViewAddress.text = "Address: ${document.getString("address") ?: "N/A"}"
                        textViewEmail.text = "Email: ${document.getString("email") ?: "N/A"}"
                        textViewRole.text = "Role: ${document.getString("role") ?: "N/A"}"
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
