package com.example.client.ui.taskList.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.client.R

class FileListAdapter (val context: Context,val list:ArrayList<FileList>):BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View = LayoutInflater.from(context).inflate(R.layout.filelist_row_layout,parent,false)
        val fileName:TextView = view.findViewById(R.id.oneFileName)
        fileName.text = list[position].name
        val fileUrl:String = list[position].url
        val fileUri:Uri =Uri.parse(fileUrl)
        val buttonShow: Button = view.findViewById(R.id.fileShow)
        buttonShow.setOnClickListener {
            val intent:Intent = Intent()
            intent.action = (Intent.ACTION_VIEW)
            intent.data = fileUri
            context.startActivity(Intent.createChooser(intent,"Please select a Browser"))

        }

        return view
    }
    override fun getCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }


}