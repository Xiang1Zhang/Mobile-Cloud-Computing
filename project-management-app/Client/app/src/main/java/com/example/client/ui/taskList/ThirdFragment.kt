package com.example.client.ui.taskList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.client.R
import com.example.client.ui.createProject.CreateProjectActivity
import com.example.client.ui.taskList.main.FileListAdapter
import com.example.client.ui.taskList.main.UploadFile
import com.example.client.ui.taskList.main.showFileListArray
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */
class ThirdFragment(private val projectId: String) : Fragment() {

    companion object {
        const val EXTRA_File_Name = "EXTRA_File_Name"
        const val EXTRA_File_Url = "EXTRA_File_Url"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_third, container, false)

        val fab3 = view.findViewById<FloatingActionButton>(R.id.fab3)

        fab3?.setOnClickListener { _ ->
            val intent = Intent(activity, UploadFile::class.java)
            intent.putExtra(EXTRA_PROJECT_ID, projectId)
            activity?.startActivity(intent)
        }

        val adapter = FileListAdapter(requireContext(), showFileListArray)
        val fileListView: ListView = view.findViewById(R.id.fileListView)
        fileListView.adapter = adapter

        return view

    }
}