package com.example.client.ui.createProject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.client.data.model.User
import com.example.client.data.repositories.APIResponse
import com.example.client.data.repositories.UserSearchRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSearchViewModel : ViewModel(){

    private val completableJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

    private val LOG = "UserViewModel"

    private val users: MutableLiveData<List<User>?> by lazy {
        MutableLiveData<List<User>?>()
    }

    private val error: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }

    fun getError(): LiveData<String?> {
        return error
    }

    fun getUsers(): LiveData<List<User>?> {
        return users
    }


    val chosenCollaboratorsIds = ArrayList<String>()
    var chosenCollaboratorsNames = ArrayList<String>()

    fun loadUsers(query: String) {

        coroutineScope.launch {

            val token: String =  FirebaseAuth.getInstance().getAccessToken(true).await().token!! //TODO method for that, and on fail redirect to login string

            UserSearchRepository.retrofitService.searchUsers(query, token).enqueue(object: Callback<APIResponse<List<User>>> {

                override fun onFailure(call: Call<APIResponse<List<User>>>, t: Throwable) {
                    Log.d(LOG, t.message)
                    error.postValue(t.message)
                }

                override fun onResponse(call: Call<APIResponse<List<User>>>, response: Response<APIResponse<List<User>>>) {

                    if(response.isSuccessful){
                        val responseBody = response.body()!!
                        Log.d(LOG, responseBody.message)
                        Log.d(LOG, response.body()!!.data!!.size.toString())
                        users.postValue(response.body()!!.data)
                    }
                    else{
                        Log.d(LOG, response.message())
                        Log.d(LOG, response.toString())
                        Log.d(LOG, response.errorBody()!!.string())

                        val msg = if (response.errorBody() != null)
                            JSONObject(response.errorBody()!!.string()).getString("err")
                        else response.message()

                        error.postValue(msg)
                    }

                }
            })

        }

    }
}