package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey("AIzaSyDKCk7QXyRNkYVb0NVCXxZwvoCEYuqg9M0")
                    .setApplicationId("1:321270886611:web:a12d19f3bc61ccbe37a426")
                    .setProjectId("magic-game-9f8c0")
                    .setStorageBucket("magic-game-9f8c0.firebasestorage.app")
                    .setGcmSenderId("321270886611")
                    .build()
                FirebaseApp.initializeApp(context, options)
                Log.d(TAG, "Firebase initialized with custom options")
            } else {
                Log.d(TAG, "Firebase already initialized by google-services")
            }
            isInitialized = true
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization error: ${e.message}", e)
        }
    }

    val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseAuth error: ${e.message}")
            null
        }

    val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseFirestore error: ${e.message}")
            null
        }
}
