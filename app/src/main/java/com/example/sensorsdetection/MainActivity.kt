package com.example.sensorsdetection

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    var button: Button? = null


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.btn)
        checkBiometricSupport()
        button!!.setOnClickListener {
            if (this.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                checkFingerPrint()
            } else
                Toast.makeText(this, "Devoid dos not have finger print scanner", Toast.LENGTH_SHORT)
                    .show()
        }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkFingerPrint() {
        val biometricPrompt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricPrompt.Builder(this@MainActivity)
                .setTitle("Title of Prompt")
                .setSubtitle("Subtitle")
                .setDescription("Uses FP")
                //TO Make Only Selected Authentication(if you want all Authentication eay do not pass this)
                //.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)//For Both Face Finger Print
                //.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)//For  Finger Print
                .setNegativeButton(
                    "Cancel",
                    this.mainExecutor
                ) { dialog, which ->
                    Toast.makeText(
                        this,
                        "Authentication Cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                }.build()
        } else {
            TODO("VERSION.SDK_INT < R")
        }

        // start the authenticationCallback in mainExecutor
        biometricPrompt.authenticate(
            getCancellationSignal(),
            mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                // here we need to implement two methods
                // onAuthenticationError and onAuthenticationSucceeded
                // If the fingerprint is not recognized by the app it will call
                // onAuthenticationError and show a toast
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication Error : $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // If the fingerprint is recognized by the app then it will call
                // onAuthenticationSucceeded and show a toast that Authentication has Succeed
                // Here you can also start a new activity after that
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication Succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                    // or start a new Activity

                }
            }
        )

    }

    private fun getCancellationSignal(): CancellationSignal {
        val cancellationSignal: CancellationSignal?
        cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
            Toast.makeText(
                this,
                "Authentication Cancelled",
                Toast.LENGTH_SHORT
            ).show()
        }


        return cancellationSignal
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        Log.e("FINGER_PRINT", keyguardManager.isDeviceSecure.toString())
        Log.e("FINGER_PRINT", keyguardManager.isKeyguardLocked.toString())
        Log.e("FINGER_PRINT", keyguardManager.isKeyguardSecure.toString())
        if (!keyguardManager.isDeviceSecure) {
            Toast.makeText(
                this,
                "Fingerprint authentication has not been enabled in settings",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                this,
                "Fingerprint Authentication Permission is not enabled",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }
}
