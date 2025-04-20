package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextMobile: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var buttonSave: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        editTextName = findViewById(R.id.editTextName)
        editTextMobile = findViewById(R.id.editTextMobile)
        editTextAddress = findViewById(R.id.editTextAddress)
        buttonSave = findViewById(R.id.btnUpdateProfile)

        fetchUserProfile()

        buttonSave.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        editTextName.setText(document.getString("name") ?: "")
                        editTextMobile.setText(document.getString("mobile") ?: "")
                        editTextAddress.setText(document.getString("address") ?: "")
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val updatedData = mapOf(
                "name" to editTextName.text.toString().trim(),
                "mobile" to editTextMobile.text.toString().trim(),
                "address" to editTextAddress.text.toString().trim()
            )

            db.collection("users").document(userId)
                .update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
