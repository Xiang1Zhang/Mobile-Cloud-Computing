package com.example.client.ui.createProject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.client.R


class UserListActivity : AppCompatActivity() {

    val LOG = "UserListActivity"

    private lateinit var viewModel: UserSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val editUserName = findViewById<EditText>(R.id.userFindbyName)
        val userFindByNameButton: Button = findViewById(R.id.findByName)
        val listView: ListView = findViewById(R.id.userlistView)

        var username = ""


        viewModel =  ViewModelProviders.of(this).get<UserSearchViewModel>(UserSearchViewModel::class.java)


        editUserName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                username = s.toString()
            }

        })

        userFindByNameButton.setOnClickListener {
            viewModel.loadUsers(username)
        }


        viewModel.getUsers().observe(this, Observer {

            if (it == null) {
                Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show()
            } else {
                listView.adapter = UserListAdapter(this, it, viewModel)
                Log.d(LOG, "Searched Successfully")
            }

        })

        viewModel.getError().observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            Log.d(LOG, "Search error")
        })

        val finishButton: Button = findViewById(R.id.finish)

        finishButton.setOnClickListener {

            val sendCo = Intent(this, CreateProjectActivity::class.java)
            sendCo.putExtra(CreateProjectActivity.EXTRA_CollaboratorID, viewModel.chosenCollaboratorsIds)
            sendCo.putExtra(CreateProjectActivity.EXTRA_CollaboratorName,viewModel.chosenCollaboratorsNames)
            setResult(CreateProjectActivity.PICK_COLLABORATORS, sendCo)
            finish()

            Log.d("SelectActivity", "Select successfully")
        }

    }

}



