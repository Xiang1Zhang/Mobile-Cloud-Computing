package com.example.client.utils

import android.content.Context
import android.content.Intent
import com.example.client.ui.auth.LoginActivity
import com.example.client.ui.projectList.ProjectListActivity


fun Context.startMainActivity() =
    Intent(this, ProjectListActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun Context.startLoginActivity() =
    Intent(this, LoginActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

