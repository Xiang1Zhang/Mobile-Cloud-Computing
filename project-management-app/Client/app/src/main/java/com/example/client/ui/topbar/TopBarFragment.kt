package com.example.client.ui.topbar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.client.R
import com.example.client.ui.searchProject.SearchProjectActivity
import com.example.client.ui.auth.UserProfileActivity
import com.example.client.ui.createProject.CreateProjectActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.client.utils.startLoginActivity

class TopBarFragment : Fragment() {

    private lateinit var userProfileButton: Button
    private lateinit var logoutButton: Button
    private lateinit var createProjectButton: Button
    private lateinit var searchProjectButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userProfileButton = view.findViewById(R.id.userProfile)
        userProfileButton.setOnClickListener {
            val intent = Intent(view.context, UserProfileActivity::class.java)
            startActivity(intent)
        }

        createProjectButton = view.findViewById(R.id.create_project_button)
        createProjectButton.setOnClickListener{
            val intent = Intent(view.context, CreateProjectActivity::class.java)
            startActivity(intent)
        }

        logoutButton = view.findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            it.context.startLoginActivity()
        }

        searchProjectButton = view.findViewById(R.id.searchProject)
        searchProjectButton.setOnClickListener{
            val intent = Intent(view.context, SearchProjectActivity::class.java)
            Log.d("MainActivity","Change into SearchProjectActivity")
            startActivity(intent)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreate(savedInstanceState)
        return inflater.inflate(R.layout.top_bar_fragment, container, false)
    }

}
