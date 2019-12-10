package com.example.client.ui.taskList.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.client.R
import com.example.client.ui.taskList.SecondFragment
import com.example.client.ui.taskList.TaskListActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_second.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UploadImage : AppCompatActivity() {

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null


    private var imageview: ImageView? = null
    private val FROM_GALLERY = 1
    private val FROM_CAMERA = 2
    private var imageUri : Uri ? = null
    private var PERMISSION_CODE = 100

    private var select: Button? = null
    private var upload: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        imageview = findViewById(R.id.imageView)
        select = findViewById<View>(R.id.select) as Button
        upload = findViewById<View>(R.id.upload) as Button

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        select!!.setOnClickListener { showDialog() }
        upload!!.setOnClickListener {
            uploadImage()
            //val intent = Intent(this, TaskListActivity::class.java)

            //startActivity(intent)
        }

    }

    private fun showDialog() {

        val items = arrayOf("Camera", "Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Select Options")
            setItems(items) { dialog, which ->
                actions(items[which])
            }

//            setPositiveButton("OK", positiveButtonClick)
            show()
        }
    }



    private fun actions(item : String){
        when(item){
            "Camera"->{
                //Toast.makeText(applicationContext, item + " is clicked", Toast.LENGTH_SHORT).show()
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    takePic()
                }
                else {
                    val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ActivityCompat.requestPermissions(this,permission,PERMISSION_CODE)

                }
            }
            "Gallery"->{
                //Toast.makeText(applicationContext, item + " is clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, FROM_GALLERY)
                }
            }
            else->{


            }
        }
    }

    private fun takePic(){
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFile = File(externalCacheDir, "$timeStamp.jpg")
        imageFile.createNewFile()
        imageUri = FileProvider.getUriForFile(this, "com.example.client.ui.taskList.main",imageFile)
        val imageIntent = Intent("android.media.action.IMAGE_CAPTURE")
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(imageIntent, FROM_CAMERA)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FROM_GALLERY->{
                if (resultCode == Activity.RESULT_OK) {
                    imageview!!.setRotation(0.0f)
                    imageview!!.setImageURI(data?.data)
                }
            }
            FROM_CAMERA->{
                //Log.i("MCC","a")
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri!!))
                    val matrix = Matrix()
                    matrix.postRotate(90.0f)
                    val scaledBitmap = createScaledBitmap(
                        bitmap,
                        bitmap.width,
                        bitmap.height,
                        true
                    )
                    val bitmap1 = createBitmap(scaledBitmap,
                        0,
                        0,
                        scaledBitmap.width,
                        scaledBitmap.height,
                        matrix,
                        true)

                    imageview!!.setRotation(0.0f)
                    imageview!!.setImageBitmap(bitmap1)
                }
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        when(requestCode){
            PERMISSION_CODE->{
                if(grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    takePic()
                }else{
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage(){
        if(imageUri != null){
            val ref = storageReference?.child("pics/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(imageUri!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    addUploadRecordToDb(downloadUri.toString())
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUploadRecordToDb(uri: String){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        db.collection("picUrl")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"

    }
}
