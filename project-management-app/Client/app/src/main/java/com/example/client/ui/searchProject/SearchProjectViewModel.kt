package com.example.client.ui.searchProject


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.client.data.model.Project
import com.example.client.data.repositories.APIResponse
import com.example.client.data.repositories.ProjectRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchProjectViewModel: ViewModel() {

    private val completableJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

    val LOG = "SearchProjectViewModel"

    private val projects: MutableLiveData<List<Project>?> by lazy {
        MutableLiveData<List<Project>?>()
    }

    private val error: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }

    fun getError(): LiveData<String?> {
        return error
    }

    fun getProjects(): LiveData<List<Project>?> {
        return projects
    }

    fun loadProjects(by: String, q: String) {

        coroutineScope.launch {

            val token: String =  FirebaseAuth.getInstance().getAccessToken(true).await().token!! //TODO method for that, and on fail redirect to login string

            ProjectRepository.retrofitService.searchProject(by, q, token).enqueue(object: Callback<APIResponse<List<Project>>> {

                override fun onFailure(call: Call<APIResponse<List<Project>>>, t: Throwable) {
                    Log.d(LOG, t.message)
                    projects.postValue(null)
                }

                override fun onResponse(call: Call<APIResponse<List<Project>>>, response: Response<APIResponse<List<Project>>>) {

                    if(response.isSuccessful){
                        val responseBody = response.body()!!
                        Log.d(LOG, responseBody.message)
                        Log.d(LOG, response.body()!!.data!!.size.toString())
                        projects.postValue(response.body()!!.data)
                    }
                    else{
                        Log.d(LOG, response.message())
                        Log.d(LOG, response.toString())
                        projects.postValue(null)

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

