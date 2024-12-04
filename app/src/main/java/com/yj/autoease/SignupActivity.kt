package com.yj.autoease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yj.autoease.models.User

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val signupButton = findViewById<MaterialButton>(R.id.signupButton)

        signupButton.setOnClickListener {
            if(passwordEditText.text.toString() != confirmPasswordEditText.text.toString()) {
                Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User("", emailEditText.text.toString())

            signup(user, passwordEditText.text.toString())
        }
    }

    private fun signup(user: User, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(user.email, password).addOnCompleteListener(this) { task ->
            if(task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if(userId != null) {
                    user.id = userId
                    saveUser(user)
                }
            } else {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveUser(user: User) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User data successfully written!")

                // 메인 화면으로 이동
                val intent = Intent(this, SearchCarActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("SignUpActivity", "Error writing document", e)
            }
    }
}