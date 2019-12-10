package com.example.client.ui.searchProject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.client.R
import com.example.client.data.model.Project
import com.example.client.utils.startMainActivity

class SearchProjectActivity : AppCompatActivity() {

    val LOG = "SearchProject"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_project)

        val viewModel = ViewModelProviders.of(this).get<SearchProjectViewModel>(SearchProjectViewModel::class.java)

        val listView: ListView = findViewById(R.id.findProjectListView)
        val editProjectName = findViewById<EditText>(R.id.projectFindByname)
        val editProjectKey=findViewById<EditText>(R.id.projectFindByKey)

        var projectName: String = editProjectName.toString()
        var projectKey: String = editProjectKey.toString()

        editProjectName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                projectName = s.toString()
            }

        })

        editProjectKey.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                projectKey = s.toString()
            }

        })

        val back2MainButton:Button = findViewById(R.id.buttonBack2Main)

        back2MainButton.setOnClickListener {
            startMainActivity()
        }


        val projectFindByNameButton: Button = findViewById(R.id.buttonProjectFindByName)

        projectFindByNameButton.setOnClickListener {
           viewModel.loadProjects("name", projectName)
        }

        val projectFindByKeyButton: Button = findViewById(R.id.buttonProjectFindByKey)

        projectFindByKeyButton.setOnClickListener {
            viewModel.loadProjects("keywords", projectKey)
        }


        viewModel.getProjects().observe(this, Observer {
            listView.adapter = FindProjectListAdapter(this, viewModel.getProjects().value)
            Log.d(LOG, "Searched Successfully")
        })

        viewModel.getError().observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            Log.d(LOG, "Search error")
        })

    }



}
