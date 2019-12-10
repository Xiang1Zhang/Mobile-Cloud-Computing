package com.example.client.ui.createProject

import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.InverseMethod
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.client.data.model.Project
import com.example.client.data.model.ProjectCreation
import com.example.client.data.repositories.APIResponse
import com.example.client.data.repositories.ProjectRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateProjectViewModel : ViewModel(), Observable {

    private val completableJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

    private val LOG = "CreateProjectViewModel"

    private val project: MutableLiveData<Project?> by lazy {
        MutableLiveData<Project?>()
    }

    private val error = MutableLiveData<String?>()

    fun getProject(): LiveData<Project?> {
        return project
    }

    fun getError(): LiveData<String?> {
        return error
    }

    var name: String? = null
    var description: String? = null
    var keywords: String? = null
    var deadline: String? = null
    var isPersonal: Boolean = true

    var collaborators: List<String> = ArrayList()

    @Bindable
    fun getIsPersonal(): Boolean {
        return isPersonal
    }

    fun setIsPersonal(value: Boolean){
        Log.d("createProjectActivity", if(value) "personal" else "group")
        isPersonal = value
        addCollaboratorVisible = if(!isPersonal) View.VISIBLE else View.INVISIBLE
    }

    var addCollaboratorVisible: Int = if(!isPersonal) View.VISIBLE else View.INVISIBLE //TODO make it work


    fun postProject(projectValue: ProjectCreation){

        coroutineScope.launch {

            val token: String = FirebaseAuth.getInstance().getAccessToken(true).await().token!!

            ProjectRepository.retrofitService.createProject(projectValue, token).enqueue(object : Callback<APIResponse<Project>> {

                override fun onFailure(call: Call<APIResponse<Project>>, t: Throwable) {
                    Log.d(CreateProjectActivity.LOG, "Create fail callback")
                    Log.d(CreateProjectActivity.LOG, t.message)
                    error.postValue(t.message)
                }

                override fun onResponse(call: Call<APIResponse<Project>>, response: Response<APIResponse<Project>>) {

                    Log.d(CreateProjectActivity.LOG, "Create response fallback")

                    if (response.isSuccessful) {

                        Log.d(CreateProjectActivity.LOG, "Response is successful")
                        val responseBody = response.body()!!
                        Log.d(CreateProjectActivity.LOG, responseBody.message)

                        project.postValue(responseBody.data!!)

                    } else {
                        Log.d(CreateProjectActivity.LOG, "Response not is successful")
                        Log.d(CreateProjectActivity.LOG, response.message())
                        Log.d(CreateProjectActivity.LOG, response.toString())

                        val msg = if (response.errorBody() != null)
                            JSONObject(response.errorBody()!!.string()).getString("err")
                        else response.message()

                        error.postValue(msg)
                    }

                }

            })

        }

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}


}