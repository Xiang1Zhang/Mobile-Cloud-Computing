package com.example.client.ui.projectList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.client.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class ProjectViewModelFactory(val kind: String) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectViewModel(kind) as T
    }

}