package com.example.client.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import kotlin.math.log

class FirebaseSource {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    fun login(email: String, password: String) = Completable.create { emitter ->

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful){
                    println("IT WORKS")
                    emitter.onComplete()

                }
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun register(email: String, password: String, name: String) = Completable.create { emitter ->

        val payload = JSONObject()
        payload.put("email", email)
        payload.put("password", password)
        payload.put("name", name)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = payload.toString().toRequestBody(mediaType)
        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
            .method("POST", requestBody)
            .url("http://mcc-fall-2019-g25.appspot.com/user")
            .build()


        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            println(response.body!!.string())
        }

    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser

    fun resetPassword(newPassword: String) = Completable.create { emitter ->

        val user  = firebaseAuth.currentUser

        var urlUP = "http://mcc-fall-2019-g25.appspot.com/user/updpsw/"

        if (user != null) {
            Log.i("mcc1",user.uid)
            urlUP = urlUP + user.uid
            logout()
        } else {
            println("not login")

        }



        val payload = JSONObject()
        payload.put("password", newPassword)


        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = payload.toString().toRequestBody(mediaType)
        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
            .method("PUT", requestBody)
            .url(urlUP)
            .build()


        okHttpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful){
            }
            else if (!response.isSuccessful) throw IOException("Unexpected code $response")

            println(response.body!!.string())
        }
    }

}