package com.example.cx61.ppo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import androidx.appcompat.app.AppCompatActivity
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
class LoginActivity : AppCompatActivity() {
    private val LOGIN_ACTION = 0
    private val REGISTER_ACTION = 1
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptAction(LOGIN_ACTION)
                return@OnEditorActionListener true
            }
            false
        })
        email_sign_in_button.setOnClickListener { attemptAction(LOGIN_ACTION) }

        email_sign_up_button.setOnClickListener { attemptAction(REGISTER_ACTION) }
    }
    private fun attemptAction(action: Int) {
        email.error = null
        password.error = null
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()
        var cancel = false
        var focusView: View? = null
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)
            if (action == LOGIN_ACTION)
                mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this) {
                    showProgress(false)
                    if (it.isSuccessful) {
                        finish()
                    } else {
                        password.error = it.exception.toString()
                        password.requestFocus()
                    }
                }
            else
                mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this) {
                    showProgress(false)
                    if (it.isSuccessful) {
                        finish()
                    } else {
                        password.error = it.exception.toString()
                        password.requestFocus()
                    }
                }
        }
    }
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }
}