package com.example.jsonparsingurlkotlin

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pic_detail.*

class PicDetailActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pic_detail)

        //var image: ImageView = findViewById(R.id.showImage)
        var name: TextView = findViewById(R.id.showAuthor)

        var picture = intent.extras.getString("imageShow")
        var author = intent.extras.getString("nameShow")

        name.text = author
        Picasso.get().load(picture).into(showImage)



    }
    companion object {
        fun newIntent(context: Context, recipe: PlayersModel): Intent {
            val detailIntent = Intent(context, PicDetailActivity::class.java)

            detailIntent.putExtra("imageShow", recipe.imgURL)
            detailIntent.putExtra("nameShow", recipe.name)

            return detailIntent
        }
    }
}
