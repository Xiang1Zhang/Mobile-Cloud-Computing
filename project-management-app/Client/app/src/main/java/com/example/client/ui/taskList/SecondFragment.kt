package com.example.client.ui.taskList

import android.content.ContentValues
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.client.R
import com.example.client.ui.taskList.main.GlideApp
import com.example.client.ui.taskList.main.ImageAdapter
import com.example.client.ui.taskList.main.UploadImage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_second.*

/**
 * A simple [Fragment] subclass.
 */
@Suppress("DEPRECATION")
class SecondFragment(private val projectId : String) : Fragment() {

    private val imageList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_second, container, false)

        val fab2 = view.findViewById<FloatingActionButton>(R.id.fab2)

        val storageReference = FirebaseStorage.getInstance().reference

        val linearLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)
        val listView = ListView(requireContext())
        val imageAdapter = ImageAdapter(requireContext(), imageList)
        listView.adapter = imageAdapter
        linearLayout.addView(listView)

        //val imageView = view.findViewById<ImageView>(R.id.imageView)

        db.collection("picUrl")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    val url = document.getString("imageUrl")
                    imageList.add(url!!)
                    imageAdapter.notifyDataSetChanged()

                    Log.d(ContentValues.TAG, "Image Url: $imageList")

                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }



        fab2?.setOnClickListener {

            val intent = Intent(activity, UploadImage::class.java)
            activity?.startActivity(intent)
        }

        return view

    }


}