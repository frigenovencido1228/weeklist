package com.example.weeklist

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.weeklist.classes.User
import com.example.weeklist.commons.Commons
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class RegisterActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirm: EditText
    lateinit var etName: EditText
    lateinit var btnRegister: MaterialButton
    lateinit var tvLogin: TextView
    lateinit var database: FirebaseDatabase
    lateinit var dbUsers: DatabaseReference
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var btnDemo: MaterialButton
    lateinit var loadingDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        loadingDialog=Commons.loadingDialog(this)

        database = Firebase.database
        dbUsers = database.getReference("users")
        firebaseAuth = Firebase.auth

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirm = findViewById(R.id.etConfirm)
        etName = findViewById(R.id.etName)
        btnRegister = findViewById(R.id.btnRegister)
        btnDemo = findViewById(R.id.btnDemo)
        tvLogin = findViewById(R.id.tvLogin)

        tvLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })

        btnDemo.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.putExtra("demo", "demo")
            startActivity(intent)
        })

        btnRegister.setOnClickListener(View.OnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm = etConfirm.text.toString().trim()
            val name = etName.text.toString().trim()

            if (email.isEmpty()) {
                Commons.showToast(applicationContext,"Please enter your email address.")
                return@OnClickListener
            }
            if (password.isEmpty()) {
                Commons.showToast(applicationContext,"Please enter your password.")
                return@OnClickListener
            }
            if (password != confirm) {
                Commons.showToast(applicationContext,"Passwords do not match.")
                return@OnClickListener
            }
            if (name.isEmpty()) {
                Commons.showToast(applicationContext,"Please enter your name.")
                return@OnClickListener
            }

            loadingDialog.show()

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {

                            val id = firebaseAuth.currentUser?.uid
                            val user = User(email, id, name)
                            dbUsers.child(id.toString()).setValue(user)

                                .addOnCompleteListener(
                                    OnCompleteListener {
                                        Commons.showToast(applicationContext,"User created.")
                                        loadingDialog.dismiss()
                                    })

                                .addOnFailureListener(OnFailureListener {
                                    Commons.showToast(applicationContext,"Error creating user. Error: ${it.message}")
                                    loadingDialog.dismiss()
                                })
                        }
                    })

                .addOnFailureListener(OnFailureListener {
                    Commons.showToast(applicationContext,"Error: ${it.message}")
                    loadingDialog.dismiss()
                })
        })
    }
}