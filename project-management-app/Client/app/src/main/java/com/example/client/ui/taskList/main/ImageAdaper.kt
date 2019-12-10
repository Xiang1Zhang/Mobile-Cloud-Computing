package com.example.client.ui.taskList.main

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import com.example.client.R


class ImageAdapter(private val context: Context, private val imageIdList: ArrayList<String>)
    : BaseAdapter() {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.activity_image_list,null)
        val imageView: ImageView = rowView.findViewById(R.id.image_item)
        val path = imageIdList[p0]
        val buttonShow: Button = rowView.findViewById(R.id.show_image)



        Log.d(ContentValues.TAG, "Image Url Adapter: $path")

        GlideApp.with(context)
            .load(path)
            .override(250,250)
            .centerCrop()
            .into(imageView)

        buttonShow.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val content_url = Uri.parse(path)
            intent.data = content_url
            context.startActivity(Intent.createChooser(intent, "choose browser"))

        }

        Log.d(ContentValues.TAG, "Image Url Adapter: $path")

        return rowView
    }
    override fun getItem(p0: Int): Any {
        return imageIdList.get(p0)
    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
    override fun getCount(): Int {
        return imageIdList.size
    }
}