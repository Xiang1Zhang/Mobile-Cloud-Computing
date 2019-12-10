package com.example.client.ui.searchProject

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.client.R
import com.example.client.data.model.Project

class FindProjectListAdapter(val context: Context, val list: List<Project>?) : BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = LayoutInflater.from(context).inflate(R.layout.find_project_row_layout, parent, false)
        // val userID = view.findViewById(R.id.userlistID) as AppCompatTextView
        val projectName: TextView = view.findViewById(R.id.projectlistName)
        val showProjectDetail: Button = view.findViewById(R.id.showProjectDetail)

        val project: Project? = getItem(position)

        if(project != null){
            projectName.text = project.name

            showProjectDetail.setOnClickListener {
                val sendInfo = Intent(context, ProjectInfoShowActivity::class.java)
                sendInfo.putExtra(ProjectInfoShowActivity.EXTRA_ProjectName, project.name)
                sendInfo.putExtra(ProjectInfoShowActivity.EXTRA_ProjectDescription, project.description)
                sendInfo.putExtra(ProjectInfoShowActivity.EXTRA_ProjectIsPersonal, project.isPersonal)
                context.startActivity(sendInfo)
            }
        }

        return view

    }

    override fun getItem(position: Int): Project? {
        return if(list != null) list[position] else null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list?.size ?: 0
    }

}