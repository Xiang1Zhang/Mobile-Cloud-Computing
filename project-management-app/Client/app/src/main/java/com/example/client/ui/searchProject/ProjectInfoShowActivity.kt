package com.example.client.ui.searchProject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.client.R

class ProjectInfoShowActivity : AppCompatActivity() {

    val LOG = "ProjectInfoActivity"

    companion object{
        const val EXTRA_ProjectName = "EXTRA_ProjectName"
        const val EXTRA_ProjectDescription = "EXTRA_Description"
        const val EXTRA_ProjectIsPersonal = "EXTRA_IsPersonal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_info_show)

        val proName = intent.getStringExtra(EXTRA_ProjectName)
        Log.d(LOG, "proName is :$proName")

        val proDescription = intent.getStringExtra(EXTRA_ProjectDescription)
        Log.d(LOG, "proDescription is :$proDescription")
        //val proIsPersonal:Boolean =intent.getBooleanExtra(ProjectInfoShow.EXTRA_ProjectIsPersonal)

        val projectNAME: TextView = findViewById(R.id.projectInfoName)
        projectNAME.text = proName

        val projectDes: TextView = findViewById(R.id.projectInfoDescription)
        projectDes.text = proDescription

        val backButton:Button = findViewById(R.id.backTo)
        backButton.setOnClickListener {
            val searchProject = Intent(this, SearchProjectActivity::class.java)
            startActivity(searchProject)
        }

        //val addUser: Button =view.findViewById(R.id.addUser)
        //userNAME.text = list[position].name.toString()
    }
}
