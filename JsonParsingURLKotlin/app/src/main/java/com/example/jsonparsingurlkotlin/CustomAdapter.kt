package com.example.jsonparsingurlkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import java.util.ArrayList


class CustomeAdapter(private val context: Context, private val playersModelArrayList: ArrayList<PlayersModel>) :
    BaseAdapter() {

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return playersModelArrayList.size
    }

    override fun getItem(position: Int): Any {
        return playersModelArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.lv_item, null, true)

            holder.iv = convertView!!.findViewById(R.id.iv) as ImageView
            holder.tvname = convertView!!.findViewById(R.id.name) as TextView


            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        Picasso.get().load(playersModelArrayList[position].getURL()).into(holder.iv)
        holder.tvname!!.text = playersModelArrayList[position].getNames()


        return convertView
    }

    inner class ViewHolder {

         var iv: ImageView? = null
         var tvname: TextView? = null

    }

}