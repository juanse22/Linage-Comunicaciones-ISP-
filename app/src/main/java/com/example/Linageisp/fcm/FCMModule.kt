package com.example.Linageisp.fcm

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 🔧 Módulo Hilt para dependencias de Firebase Cloud Messaging
 * 
 * Proporciona:
 * - Instancias de Firebase singleton
 * - FCMRepository configurado
 * - LinageNotificationManager
 * - FirebaseAnalytics para tracking
 */
@Module
@InstallIn(SingletonComponent::class)
object FCMModule {

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return FirebaseFunctions.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideLinageNotificationManager(
        @ApplicationContext context: Context,
        firebaseAnalytics: FirebaseAnalytics
    ): LinageNotificationManager {
        return LinageNotificationManager(context, firebaseAnalytics)
    }

    @Provides
    @Singleton
    fun provideFCMRepository(
        @ApplicationContext context: Context,
        firebaseAuth: FirebaseAuth,
        firebaseFunctions: FirebaseFunctions,
        firebaseMessaging: FirebaseMessaging
    ): FCMRepository {
        return FCMRepository(context, firebaseAuth, firebaseFunctions, firebaseMessaging)
    }
}