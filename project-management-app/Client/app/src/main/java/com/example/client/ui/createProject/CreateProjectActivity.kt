package com.example.client.ui.createProject

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.client.R
import com.example.client.data.model.ProjectCreation
import com.example.client.databinding.ActivityCreateProjectBinding
import com.example.client.ui.projectList.ProjectListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import androidx.lifecycle.Observer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateProjectActivity : AppCompatActivity() {

    lateinit var editNewProjectName: EditText
    lateinit var editNewProjectDescription: EditText
    lateinit var editNewProjectKeywords: EditText
    lateinit var toggleButtonSetType: Switch
    lateinit var buttonFinalizeProject: Button
    lateinit var buttonSelectCollaborators: Button
    lateinit var buttonSeletcDeadline: Button
    lateinit var textViewResult: TextView
    lateinit var badge: ImageView
    lateinit var textViewDate: TextView

    private lateinit var viewModel: CreateProjectViewModel  //////

    var currentPhotoPath: String = " "
    var cal = Calendar.getInstance()

    companion object {
        const val LOG = "createProjectActivity"
        const val EXTRA_CollaboratorID = "EXTRA_CollaboratorID"
        const val EXTRA_CollaboratorName = "EXTRA_CollaboratorName"
        const val PICK_IMAGE_REQUEST = 71
        const val PICK_COLLABORATORS = 3
        const val CAMERA = 2
        private var filePath: Uri? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)

        viewModel = ViewModelProviders.of(this).get(CreateProjectViewModel::class.java) //////

        val binding: ActivityCreateProjectBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_create_project)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        textViewDate = findViewById(R.id.editSelectDeadline)
        editNewProjectName = findViewById(R.id.editNewProjectName)
        editNewProjectDescription = findViewById(R.id.editNewProjectDescription)
        editNewProjectKeywords = findViewById(R.id.editNewProjectKeywords)
        toggleButtonSetType = findViewById(R.id.toggle_button_set_type)
        buttonSelectCollaborators = findViewById(R.id.button_select_collaborators)
        buttonFinalizeProject = findViewById(R.id.button_finalize_project)
        buttonSeletcDeadline = findViewById(R.id.selectDDL)
        textViewResult = findViewById(R.id.resultView)
        badge = findViewById(R.id.imageView)

        badge.setOnClickListener {
            Log.d(LOG, "Select a badge")
            //withItems(it)
            uploadImage()
        }

        toggleButtonSetType.setOnCheckedChangeListener { el, isChecked ->
            Log.d("ha","pleaaaaaaase")
            el.text = if (isChecked) "Personal" else "Group"
            buttonSelectCollaborators.visibility = if (isChecked) View.INVISIBLE else View.VISIBLE
        }

        buttonSelectCollaborators.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            Log.d(LOG, "Change into ListUserActivity")
            startActivityForResult(intent, 2, savedInstanceState)
        }

        buttonFinalizeProject.setOnClickListener {
            Log.d(LOG, "Try final create")
            Log.d(LOG, "isPersonal: ${viewModel.isPersonal}")
            createNewProject(it.context)
        }


        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val value = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)

            viewModel.deadline = value
            textViewDate.text = value
        }

        buttonSeletcDeadline.setOnClickListener {
            DatePickerDialog(this@CreateProjectActivity, dateSetListener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val context = this
        viewModel.getProject().observe(this, Observer {

            val intentBack = Intent(context, ProjectListActivity::class.java)
            Log.d(LOG, "Change into ProjectListActivity")

            intent.putExtra("projectCreation", "Create Successfully")
            startActivity(intentBack)
            Log.d(LOG, "Create Successfully")

        })

        viewModel.getError().observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            Log.d(LOG, "Create error")
        })


    }

    private fun createNewProject(context: Context) {

        if(viewModel.name == null || viewModel.description == null ){
            Toast.makeText(context, "Missing name/description", Toast.LENGTH_SHORT).show()
            return
        }

        val name = viewModel.name!!
        val description = viewModel.description!!
        val keywords = viewModel.keywords
        val isPersonal = viewModel.isPersonal
        var deadline = viewModel.deadline

        deadline = if(deadline == null) null else {
            LocalDateTime.parse("$deadline 00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                .format( DateTimeFormatter.ISO_DATE_TIME)
        }

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        currentUser!!.getIdToken(true)

        Log.d(LOG, "Name is: $name")
        Log.d(LOG, "Description is: $description")
        Log.d(LOG, "CurrentUser is $currentUser")
        Log.d(LOG, "Deadline is $deadline")

        val isFavorite = HashMap<String, Boolean>()
        isFavorite[currentUser.uid] = false

        Log.d(LOG, "Parameter3 is:$isFavorite")

        val collaboratorIds = viewModel.collaborators

        if (!isPersonal) {
            collaboratorIds.forEach  {
                isFavorite[it] = false
            }
        }

        val project = ProjectCreation(
            administrator = currentUser.uid,
            isFavorite = isFavorite,
            description = description,
            name = name,
            isPersonal = isPersonal,
            contributors = if (isPersonal) null else collaboratorIds,
            keywords = keywords,
            deadline = deadline
        )

        val test = project.deadline
        Log.d(LOG, "Deadline of project obj $test")

        viewModel.postProject(project)

    }




//    private fun withItems(view: View) {
//
//        val items = arrayOf("Camera", "Gallery", "Cancel")
//        val builder = AlertDialog.Builder(this)
//        with(builder)
//        {
//            setTitle("Select Options")
//            setItems(items) { dialog, which ->
//                val selected = items[which]
//                if(selected=="Gallery"){
//                    uploadImage()
//                }else if(selected=="Camera"){
//
//                    takephoto()
//
//                }
//                // Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
//            }
//            // items[0].setOnClickListener()
//
//
////            setPositiveButton("OK", positiveButtonClick)
//            show()
//        }
//    }
//    private fun takephoto() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    filePath = FileProvider.getUriForFile(
//                        this,
//                        "com.example.rotateimage.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)
//                    startActivityForResult(takePictureIntent, CAMERA)
//                }
//            }
//        }
//    }


    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView = findViewById<ImageView>(R.id.imageView)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data

            try {
                imageView.setImageURI(data?.data)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            val bitmap2 =
                BitmapFactory.decodeStream(getContentResolver().openInputStream(filePath!!))
            imageView!!.setImageBitmap(bitmap2)
            galleryAddPic()
        }
        else if(requestCode == PICK_COLLABORATORS){
            Log.d(LOG, intent.getStringArrayListExtra(EXTRA_CollaboratorID).joinToString ())
            viewModel.collaborators = intent.getStringArrayListExtra(EXTRA_CollaboratorID)
        }


    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    //
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)

        }
        Toast.makeText(this, "Update image.", Toast.LENGTH_SHORT).show()
    }


}
