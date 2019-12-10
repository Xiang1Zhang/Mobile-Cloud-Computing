package com.example.client.ui.projectList

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_project.view.*


class ProjectFragment(val mView: View) : RecyclerView.ViewHolder(mView) {

    val projectNameTextView: TextView = mView.project_name_value
    val modificationDateTextView: TextView = mView.modification_date_value
    val deadlineTextView: TextView = mView.deadline_value
    val isFavoriteButton: Button = mView.is_favorite_button
    val deleteProjectButton: Button = mView.delete_button

    override fun toString(): String {
        return super.toString() + " '" + projectNameTextView.text + "'"
    }
}