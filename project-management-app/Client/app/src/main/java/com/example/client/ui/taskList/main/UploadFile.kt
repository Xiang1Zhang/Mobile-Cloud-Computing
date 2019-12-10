package com.example.client.ui.taskList.main

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.client.R
import com.example.client.ui.taskList.EXTRA_PROJECT_ID
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_upload_file.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


var showFileListArray = ArrayList<FileList>()
private const val BASE_URL = "http://mcc-fall-2019-g25.appspot.com/"


class UploadFile : AppCompatActivity() {


    val PDF : Int = 0
    val DOCX : Int = 1
    val AUDIO : Int = 2
    val VIDEO : Int = 3
    lateinit var uri : Uri
    lateinit var mStorage : StorageReference
    lateinit var projectId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_file)

        val pdfBtn = findViewById<View>(R.id.pdfBtn) as Button
        val docxBtn = findViewById<View>(R.id.docxBtn) as Button
        val musicBtn = findViewById<View>(R.id.musicBtn) as Button
        val videoBtn = findViewById<View>(R.id.videoBtn) as Button

        projectId  = intent.getStringExtra(EXTRA_PROJECT_ID)
        mStorage = FirebaseStorage.getInstance().getReference("Uploads")

        pdfBtn.setOnClickListener { view: View? -> val intent = Intent()
            intent.setType ("application/pdf")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF)
        }

        docxBtn.setOnClickListener { view: View? -> val intent = Intent()
            intent.setType ("*/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select DOCX"), DOCX)
        }

        musicBtn.setOnClickListener { view: View? -> val intent = Intent()
            intent.setType ("audio/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Audio"), AUDIO)
        }

        videoBtn.setOnClickListener { view: View? -> val intent = Intent()
            intent.setType ("video/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val uriTxt = findViewById<View>(R.id.uriTxt) as TextView
        if (resultCode == RESULT_OK) {
            if (requestCode == PDF) {
                uri = data!!.data!!
                Log.d("Add attachments","PDF is : " + uri.toString())

                uriTxt.text = uri.toString()
                var uriName:String = uriTxt.text.split("%2F").toTypedArray().last()
                Log.i("mcc5",uriTxt.text.split("%2F").toTypedArray().last())
                upload ()
                fileListShow()
            }else if (requestCode == DOCX) {
                uri = data!!.data!!

                uriTxt.text = uri.toString()
                var uriName:String = uriTxt.text.split("%2F").toTypedArray().last()
                upload ()
                fileListShow()
            }else if (requestCode == AUDIO) {
                uri = data!!.data!!
                uriTxt.text = uri.toString()
                var uriName:String = uriTxt.text.split("%2F").toTypedArray().last()
                upload ()
                fileListShow()
            }else if (requestCode == VIDEO) {
                uri = data!!.data!!
                uriTxt.text = uri.toString()
                var uriName:String = uriTxt.text.split("%2F").toTypedArray().last()
                upload ()
                fileListShow()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upload() {
        Log.d("Upload","Change to upload file !")
        var downloadUri:Uri?
        var mReference = mStorage.child(uri.lastPathSegment!!)
        try {

            mReference.putFile(uri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                mReference.downloadUrl

            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    downloadUri = task.result
                    Log.i("mcc4", downloadUri.toString())

                    Thread {
                        //Do some Network Request
                        val payload = JSONObject()
                        payload.put("docsUrl", downloadUri)
                        payload.put("projectID", projectId)

                        val mediaType = "application/json; charset=utf-8".toMediaType()
                        val requestBody = payload.toString().toRequestBody(mediaType)
                        val okHttpClient = OkHttpClient()

                        val request = Request.Builder()
                            .method("PUT", requestBody)
                            .url(BASE_URL +"files/docsU")
                            .build()

                        okHttpClient.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            println(response.body!!.string())
                        }

                    }.start()

                } else {
                    // Handle failures
                    // ...
                }
            }.addOnSuccessListener {
                Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }

    }

    private fun fileListShow(){
        Log.d("Upload show","Change to show file list")

        uploadFile.setOnClickListener {
            var projectID = BASE_URL + "docs/" + projectId
            AsyncTaskHandleJson().execute(projectID)

        }


    }
    inner class AsyncTaskHandleJson : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg url: String?): String {
            val text: String
            val connection = URL(url[0]).openConnection() as HttpURLConnection
            try {
                connection.connect()
                text =
                    connection.inputStream.use { it.reader().use { reader -> reader.readText() } }

            } finally {
                connection.disconnect()
            }
            return text
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleJson(result)
        }


        private fun handleJson(responseData: String?) {
            Log.d("Find project", "response is: $responseData")
            var jsonOject1 =JSONObject(responseData)
            var jsonArray = jsonOject1.getJSONArray("docs")

            var x = 0
            while (x < jsonArray.length()) {
                val baseUrl = jsonArray[x]
                val baseUrl2:String = baseUrl.toString()
                val fileName:String = baseUrl2.split("%2F").toTypedArray().last()
                val fileUrl:String =baseUrl2

                showFileListArray.add(FileList(fileName, fileUrl))

                x++
            }

        }

    }





}
