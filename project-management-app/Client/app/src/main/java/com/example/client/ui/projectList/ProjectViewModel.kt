package com.example.client.ui.projectList

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.client.data.model.Project
import com.example.client.data.model.ProjectCreation
import com.example.client.data.repositories.APIResponse
import com.example.client.data.repositories.IsFavorite
import com.example.client.data.repositories.ProjectRepository
import com.example.client.ui.createProject.CreateProjectActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProjectViewModel(private val kind: String): ViewModel() {

    private val completableJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

    private val LOG = "ProjectViewModel"
    
    private val projects: MutableLiveData<List<Project>?> by lazy {
        MutableLiveData<List<Project>?>().also {
            loadProjects()
        }
    }

    fun getProjects(): LiveData<List<Project>?> {
        return projects
    }

    fun loadProjects() {

        coroutineScope.launch {

            val token: String =  FirebaseAuth.getInstance().getAccessToken(true).await().token!! //TODO method for that, and on fail redirect to login string

            ProjectRepository.retrofitService.getProjects(token, kind).enqueue(object: Callback<APIResponse<List<Project>>> {

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
                        Log.d(LOG, response.errorBody()!!.string())
                        projects.postValue(null)
                    }

                }
            })

        }

    }

    fun deleteProject(id: String) {

        coroutineScope.launch {

            val token: String =  FirebaseAuth.getInstance().getAccessToken(true).await().token!!

            ProjectRepository.retrofitService.deleteProject(id, token).enqueue(object: Callback<APIResponse<String>> {

                override fun onFailure(call: Call<APIResponse<String>>, t: Throwable) {
                    Log.d(LOG, t.message)
                }

                override fun onResponse(call: Call<APIResponse<String>>, response: Response<APIResponse<String>>) {

                    if(response.isSuccessful){
                        val responseBody = response.body()!!
                        Log.d(LOG, responseBody.message)
                        projects.postValue(projects.value!!.filter { it.id != id })
                    }
                    else{
                        Log.d(LOG, response.message())
                        Log.d(LOG, response.toString())
                        Log.d(LOG, response.errorBody()!!.string())
                    }

                }

            })

        }

    }




    fun updateIsFavoriteProject(id: String, isFavorite: Boolean) {

        coroutineScope.launch {

            val token: String =  FirebaseAuth.getInstance().getAccessToken(true).await().token!!

            ProjectRepository.retrofitService.updateIsFavoriteProject(IsFavorite( isFavorite), id, token).enqueue(object: Callback<APIResponse<String>> {

                override fun onFailure(call: Call<APIResponse<String>>, t: Throwable) {
                    Log.d(LOG, t.message)
                }

                override fun onResponse(call: Call<APIResponse<String>>, response: Response<APIResponse<String>>) {

                    if(response.isSuccessful){
                        val responseBody = response.body()!!
                        Log.d(LOG, responseBody.message)

                        val update = projects.value!!.map {

                            if(it.id == id) {
                                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                                it.isFavorite[uid] = isFavorite
                            }

                            it
                        }

                        projects.postValue(update)

                    }
                    else{
                        Log.d(LOG, response.message())
                        Log.d(LOG, response.toString())
                        Log.d(LOG, response.errorBody()!!.string())
                    }

                }

            })
        }

    }


    /*private fun getProject(projectId: String) {
        ProjectRepository.retrofitService.getProject(projectId).enqueue(object: Callback<APIResponse<Project>> {

            override fun onFailure(call: Call<APIResponse<Project>>, t: Throwable) {
                Log.d(LOG, t.message)
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<APIResponse<Project>>, response: retrofit2.Response<APIResponse<Project>>) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }*/

}

