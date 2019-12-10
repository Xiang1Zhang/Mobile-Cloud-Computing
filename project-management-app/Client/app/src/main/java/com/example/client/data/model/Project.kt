package com.example.client.data.model

import android.util.Log
import android.view.View
import com.example.client.ui.createProject.CreateProjectActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class Project(val deadline: LocalDateTime?, val isFavorite: HashMap<String, Boolean>,
                   val administrator: String, val creationDate: LocalDateTime,
                   val description: String, val id: String, val modificationDate: LocalDateTime?,
                   val name: String, val isPersonal: Boolean, val contributors: List<String>?){

    override fun equals(other: Any?): Boolean {
        return if(other == null || other !is Project) false else (id == other.id)
    }

}


data class ProjectCreation(val isFavorite: HashMap<String, Boolean>, val administrator: String,
                           val description: String, val name: String, val isPersonal: Boolean,
                           val contributors: List<String>?, val keywords: String?, val deadline: String?)

class ProjectCreationJsonAdapter {

    val gson: Gson = Gson()

    @ToJson
    fun projectToJson(project: ProjectCreation): Map<*, *> {
        val moshi = Moshi.Builder().build()
        return moshi.adapter<Map<*, *>>(MutableMap::class.java).fromJson(gson.toJson(project))!!
    }

    @FromJson
    fun projectFromJson(map: Map<String, Any>): ProjectCreation?{
        return null
    }

}

class ProjectJsonAdapter {

    val gson = Gson()

    @FromJson
    fun projectFromJson(map: Map<String, Any>): Project {

        val moshi = Moshi.Builder().build()
        val str = moshi.adapter<Map<*, *>>(MutableMap::class.java).toJson(map)

        val obj: JsonObject = gson.fromJson(str, JsonObject::class.java)

        val isFavoriteType = object : TypeToken<HashMap<String, Boolean>>() {}.type
        val contributorsType= object : TypeToken<HashMap<String, Boolean>>() {}.type

        return Project(
            administrator = obj.getAsJsonPrimitive("administrator").asString,
            isFavorite = gson.fromJson(obj.getAsJsonObject("isFavorite").toString(), isFavoriteType),
            creationDate = LocalDateTime.parse(obj.getAsJsonPrimitive("creationDate").asString,
                DateTimeFormatter.ISO_DATE_TIME),
            description = obj.getAsJsonPrimitive("description").asString,
            id = obj.getAsJsonPrimitive("id").asString,
            modificationDate = if (!obj.has("modificationDate")) null else LocalDateTime.parse(
                obj.getAsJsonPrimitive("modificationDate").asString,
                DateTimeFormatter.ISO_DATE_TIME),
            name = obj.getAsJsonPrimitive("name").asString,
            isPersonal = obj.getAsJsonPrimitive("isPersonal").asBoolean,
            deadline = if (!obj.has("deadline")) null else LocalDateTime.parse(
                obj.getAsJsonPrimitive("deadline").asString,
                DateTimeFormatter.ISO_DATE_TIME) ,
            contributors = if (!obj.has("contributors")) null
            else gson.fromJson(obj.getAsJsonArray("contributors").toString(), contributorsType)
        )

    }

    @ToJson
    fun projectToJson(project: Project): String {
        return gson.toJson(project)

        /*name = editNewProjectName.text.toString()
        editNewProjectName.visibility = View.VISIBLE
        description = editNewProjectDescription.text.toString()
        editNewProjectDescription.visibility = View.VISIBLE

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        currentUser!!.getIdToken(true)

        Log.d(CreateProjectActivity.LOG, "Name is: $name")
        Log.d(CreateProjectActivity.LOG, "Description is: $description")
        Log.d(CreateProjectActivity.LOG, "CurrentUser is $currentUser")


        val isFavorite = JSONObject()
        isFavorite.put(currentUser.uid, false)


        Log.d(CreateProjectActivity.LOG, "Parameter3 is:$isFavorite")

        val newProject = JsonObject()
        newProject.add("description", description)
        newProject.add("isPersonal", isPersonal)
        newProject.put("name", name)

        if(!isPersonal) {

            newProject.put("collaborators", collaboratorIds)

            for (i in 0 until collaboratorIds!!.size) {
                isFavorite.put(collaboratorIds[i], false)
            }

        }

        newProject.put("isFavorite", isFavorite)

        Log.d(CreateProjectActivity.LOG, "newProject is$newProject")

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = newProject.toString().toRequestBody(mediaType)
        val token = currentUser.getIdToken(true).await().token!!

        return HashMap<String, Any>()*/

    }
}
