package com.example.client.ui.projectList

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.client.R
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein


class ProjectListActivity: AppCompatActivity(), KodeinAware {

    override val kodein by kodein()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_project_list)

        if(intent.hasExtra("projectCreation")){
            Toast.makeText(this, intent.getStringExtra("projectCreation"), Toast.LENGTH_SHORT).show()
        }
    }

}

