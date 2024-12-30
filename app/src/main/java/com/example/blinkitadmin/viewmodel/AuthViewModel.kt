package com.example.blinkitadmin.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blinkitadmin.Firebase
import com.example.blinkitadmin.model.Admin
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {
    private var _verificationId : String? = null

    private val _otpSent =  MutableLiveData(false)
    val otpSent : LiveData<Boolean> = _otpSent

    private val _isLoginSuccessful =  MutableStateFlow<Boolean?>(null)
    val isLoginSuccessful = _isLoginSuccessful


    fun sendOtp(phoneNumber : String, activity : Activity) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _verificationId = verificationId
                _otpSent.value = true
            }
        }

        val options = PhoneAuthOptions.newBuilder(Firebase.getAuthInstance())
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(otp: String, admin: Admin) {
        // verificationId can be null if we enter OTP before even receiving it.
        val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(_verificationId!!, otp)

        Firebase.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                admin.id = Firebase.getCurrentUserId()
                if (task.isSuccessful) {
                    _isLoginSuccessful.value = true
                    Firebase.getDatabaseInstance()
                        .getReference("Admins")
                        .child("AdminInfo")
                        .child(admin.id!!)
                        .setValue(admin)

                } else {
                    _isLoginSuccessful.value = false
                }
            }
    }
}