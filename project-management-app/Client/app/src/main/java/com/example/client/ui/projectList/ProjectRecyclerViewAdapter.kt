package com.example.client.ui.projectList

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.client.R
import com.example.client.data.model.Project
import com.google.firebase.auth.FirebaseAuth


class ProjectRecyclerViewAdapter(private val mValues: List<Project>,
                                 private val mListener: OnListFragmentInteractionListener?,
                                 private val viewModel: ProjectViewModel)

    : RecyclerView.Adapter<ProjectFragment>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val project = v.tag as Project
            mListener?.onListFragmentInteraction(project)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectFragment {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_project, parent, false)
        return ProjectFragment(view)
    }

    override fun onBindViewHolder(holder: ProjectFragment, position: Int) {
        val project = mValues[position]
        holder.projectNameTextView.text = project.name
        holder.modificationDateTextView.text = if(project.modificationDate != null) project.modificationDate.toString() else "Never Modified"
        holder.deadlineTextView.text = if(project.deadline != null) project.deadline.toString() else "No deadline set"


        val uid: String =  FirebaseAuth.getInstance().currentUser!!.uid
        holder.isFavoriteButton.text = (if(project.isFavorite.getValue(uid)) "Unfavorite" else "Favorite")

        with(holder.mView) {
            tag = project
            setOnClickListener(mOnClickListener)
        }

        if(project.administrator == uid){
            holder.deleteProjectButton.setOnClickListener { v ->
                viewModel.deleteProject(project.id)
            }
        }
        else{
            holder.deleteProjectButton.visibility = View.GONE
        }

        holder.isFavoriteButton.setOnClickListener { v ->
            viewModel.updateIsFavoriteProject(project.id, !project.isFavorite.getValue(uid))
        }

    }

    override fun getItemCount(): Int = mValues.size

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Project?)
    }
}

