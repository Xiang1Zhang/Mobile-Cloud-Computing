package com.example.client.ui.createProject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.client.R
import com.example.client.data.model.User


class UserListAdapter (val context:Context, val list: List<User>, val viewModel: UserSearchViewModel):BaseAdapter(){

    var nameArray: ArrayList<String> = ArrayList()
    var idArray: ArrayList<String> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view:View = if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            inflater.inflate(R.layout.row_layout, parent, false)
        } else convertView

        val username: TextView = view.findViewById(R.id.userlistName)
        val addUser: Button = view.findViewById(R.id.addUser)

        username.text = list[position].name

        addUser.setOnClickListener{
            viewModel.chosenCollaboratorsIds.add(list[position].id)
            viewModel.chosenCollaboratorsNames.add(list[position].name)
        }

        return view
    }

    override fun getItem(position: Int): Any {
       return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}