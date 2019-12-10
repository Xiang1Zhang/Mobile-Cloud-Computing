package com.example.client.ui.taskList.main

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.client.R
import com.example.client.ui.createProject.CreateProjectActivity
import com.example.client.ui.taskList.CREATE_TASK_CODE
import com.example.client.ui.taskList.EXTRA_PROJECT_ID
import com.example.client.ui.taskList.TaskListActivity
import com.example.client.ui.taskList.ThirdFragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_create_task.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreateTaskActivity : AppCompatActivity() {

    private var buttonDate: Button? = null
    private var textviewDate: TextView? = null
    var cal = Calendar.getInstance()
    private var buttonSave: Button? = null
    lateinit var textView: TextView
    lateinit var imageView: ImageView
    val LOG = "createTaskActivity"

    lateinit var projectId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        projectId = intent.getStringExtra(EXTRA_PROJECT_ID)

        // get the references from layout file
        textviewDate = this.editText2
        buttonDate = this.button_date_1
        buttonSave = this.button_save

        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.editText)

        textviewDate!!.text = "----------"

        // OnClickListener for uploading pictures and detecting text
        val buttonUpload: Button = findViewById(R.id.upload)
        buttonUpload.setOnClickListener { selectImage() }

        val buttonDetect: Button = findViewById(R.id.detect)
        buttonDetect.setOnClickListener { startRecognizing() }

        // create an OnDateSetListener
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        buttonDate!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@CreateTaskActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        // when you click on the button, save the data
        buttonSave!!.setOnClickListener { view ->
            //addNewItem()
            createNewTask()
            val intent = Intent(this, TaskListActivity::class.java)
            intent.putExtra(EXTRA_PROJECT_ID, projectId)
            setResult(CREATE_TASK_CODE, intent)
            finish()
        }

    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textviewDate!!.text = sdf.format(cal.getTime())
    }

    private fun createNewTask() {

        val editText = findViewById<EditText>(R.id.editText)
        val editText2 = findViewById<EditText>(R.id.editText2)

        val status: String? = "pending"

        val description = editText.text.toString()
        val deadTime = editText2.text.toString()


        Log.d(LOG, "Description is:$description")
        Log.d(LOG, "Deadline is :$deadTime")

        val newTask = JSONObject()
        newTask.put("description", description)
        newTask.put("deadline", deadTime)
        newTask.put("projectID", projectId)
        newTask.put("status", status)
        Log.d(LOG, "newProject is$newTask")


        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = newTask.toString().toRequestBody(mediaType)

        val okHttpClient1 = OkHttpClient()
        val request1 = Request.Builder()
            .method("POST", requestBody)
            .url("http://mcc-fall-2019-g25.appspot.com/task")
            .build()
        Log.d(LOG, "Request build successfully")
        val call = okHttpClient1.newCall(request1)
        call.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.d(LOG, "Request build failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseHTTP = response.body.toString()
                    Log.d("GET Back from HTTP", "RESPONSE IS :$responseHTTP")
                }
            }
        })

        Toast.makeText(this,"Create a Task Successfully",Toast.LENGTH_LONG).show()
    }


        /*private fun addNewItem() {

            val editText = findViewById<EditText>(R.id.editText)
            val editText2 = findViewById<EditText>(R.id.editText2)

            val description = editText.text.toString()
            val deadTime = editText2.text.toString()

            val todoItem = ToDoItem.create()
            todoItem.itemText = description
            todoItem.done = false
            todoItem.deadline = deadTime

            //We first make a push so that a new item is made with a unique ID
            val newItem = mDatabase.child(TaskListActivity.Constants.FIREBASE_ITEM).push()
            todoItem.objectId = newItem.key

            //then, we used the reference to set the value on that ID
            newItem.setValue(todoItem)

            Toast.makeText(this, "Item saved with ID " + todoItem.objectId, Toast.LENGTH_SHORT).show()
        }*/

    // recognize description from a picture
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data!!.data)

        }
    }

    private fun startRecognizing() {
        if (imageView.drawable != null) {
            textView.text = ""
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    processResultText(firebaseVisionText)
                }
                .addOnFailureListener {
                    textView.text = "Failed"
                }
        } else {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
        }

    }

    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            textView.text = "No Text Found"
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text
            textView.append(blockText + "\n")
        }
    }

}

