package com.example.weeklist

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
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

class LoginActivity : AppCompatActivity() {
    lateinit var etEmail: TextView
    lateinit var etPassword: TextView
    lateinit var tvForgot: TextView
    lateinit var tvRegister: TextView
    lateinit var btnLogin: MaterialButton
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var userDb: DatabaseReference
    lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadingDialog = Commons.loadingDialog(this)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tvForgot = findViewById(R.id.tvForgot)
        tvRegister = findViewById(R.id.tvRegister)
        btnLogin = findViewById(R.id.btnLogin)

        firebaseAuth = Firebase.auth

        database = Firebase.database
        userDb = database.getReference("users")

        btnLogin.setOnClickListener(View.OnClickListener {
            login()
        })
        tvRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        })
        tvForgot.setOnClickListener(View.OnClickListener {
            showForgotDialog()
        })
    }

    private fun setDialogAttributes(dialog: Dialog) {
        //set dialog attributes
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun showForgotDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_forgot)
        dialog.show()

        setDialogAttributes(dialog)

        val etEmail = dialog.findViewById<EditText>(R.id.etEmail)
        val btnReset = dialog.findViewById<MaterialButton>(R.id.btnConfirm)
        btnReset.text = "Reset"
        btnReset.setOnClickListener(View.OnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Commons.showToast(applicationContext, "Enter your email address.")
                return@OnClickListener
            }

            loadingDialog.show()
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful) {
                    Commons.showToast(
                        applicationContext,
                        "Password reset email sent. Please check your email."
                    )
                    loadingDialog.dismiss()
                }
            }).addOnFailureListener(OnFailureListener {
                Commons.showToast(applicationContext, "Error: ${it.message}")
                loadingDialog.dismiss()
            })
        })
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        btnCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
    }

    private fun login() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            Commons.showToast(applicationContext, "Please enter your email address.")
            return
        }
        if (password.isEmpty()) {
            Commons.showToast(applicationContext, "Please enter your password.")
            return
        }

        loadingDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                loadingDialog.dismiss()
                val intent = Intent(this, ItemsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            loadingDialog.dismiss()
            Commons.showToast(applicationContext, "Login error. Error: ${it.message}")
        }


    }


}