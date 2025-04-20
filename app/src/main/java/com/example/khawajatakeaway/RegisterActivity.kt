package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val mobileEditText = findViewById<EditText>(R.id.editTextMobile)
        val addressEditText = findViewById<EditText>(R.id.editTextAddress)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Dropdown for user role selection
        val roles = arrayOf("customer", "admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        roleSpinner.adapter = adapter

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val mobile = mobileEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()

            if (name.isNotEmpty() && mobile.isNotEmpty() && address.isNotEmpty() &&
                email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(name, mobile, address, email, password, role)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(name: String, mobile: String, address: String, email: String, password: String, role: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser!!.uid
                    saveUserToFirestore(userId, name, mobile, address, email, role)
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirestore(userId: String, name: String, mobile: String, address: String, email: String, role: String) {
        val user = hashMapOf(
            "name" to name,
            "mobile" to mobile,
            "address" to address,
            "email" to email,
            "role" to role
        )

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "User saved successfully in Firestore")
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving user: ${e.message}")
                Toast.makeText(this, "Error saving user: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
