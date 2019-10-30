package com.example.jsonparsingurlkotlin

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONException
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import java.util.ArrayList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import org.json.JSONArray
import android.support.annotation.RequiresApi as RequiresApi1
import android.R.attr.data
import android.content.Intent
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.github.kittinunf.fuel.httpGet


class MainActivity : AppCompatActivity() {


    private val jsoncode = 1
    private var listView: ListView? = null
    private var playersModelArrayList: ArrayList<PlayersModel>? = null
    private var customeAdapter: CustomeAdapter? = null

    @RequiresApi1(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById<ListView>(R.id.lv)
        val loadInfo: Button = findViewById(R.id.button)
        loadInfo.setOnClickListener { sampleKo() }

    }


    @RequiresApi1(Build.VERSION_CODES.KITKAT)
    private fun sampleKo() {

        try {
            val editText = findViewById<EditText>(R.id.editText)
            val url = editText.text.toString()

            url.httpGet().responseJson { request, response, result ->
                    Log.d("plzzzzz", result.get().content)
                    onTaskCompleted(result.get().content)
                }
        } catch (e: Exception) {

        } finally {

        }
    }

    @RequiresApi1(Build.VERSION_CODES.KITKAT)
    fun onTaskCompleted(response: String) {
        Log.d("responsejson", response)

        playersModelArrayList = getInfo(response)
        listView!!.adapter = CustomeAdapter(this, playersModelArrayList!!)

        listView!!.setOnItemClickListener{ parent, view, position, id ->

            //val intent = Intent(this@MainActivity, PicDetailActivity::class.java)

            //startActivity(intent)
            val selectedRecipe = playersModelArrayList!![position]

            val detailIntent = PicDetailActivity.newIntent(this, selectedRecipe)

            startActivity(detailIntent)

            if (position==0){
                Toast.makeText(this@MainActivity, "Item One",   Toast.LENGTH_SHORT).show()
            }
            if (position==1){
                Toast.makeText(this@MainActivity, "Item Two",   Toast.LENGTH_SHORT).show()
            }
            if (position==2){
                Toast.makeText(this@MainActivity, "Item Three", Toast.LENGTH_SHORT).show()
            }
            if (position==3){
                Toast.makeText(this@MainActivity, "Item Four",  Toast.LENGTH_SHORT).show()
            }
            if (position==4){
                Toast.makeText(this@MainActivity, "Item Five",  Toast.LENGTH_SHORT).show()
            }
        }

    }

    @RequiresApi1(Build.VERSION_CODES.KITKAT)
    fun getInfo(response: String): ArrayList<PlayersModel> {
        val playersModelArrayList = ArrayList<PlayersModel>()
        try {

            Log.d("A", "A")

            val dataArray = JSONArray(response)
            Log.d("C", "B")

                for (i in 0 until dataArray.length()) {
                    Log.d("AAA", String.format("%d", i))
                    val playersModel = PlayersModel()
                    Log.d("AAA", dataArray[i].toString())
                    val dataobj = dataArray.getJSONObject(i)


                    playersModel.setNames(dataobj.getString("author"))
                    playersModel.setURL(dataobj.getString("photo"))
                    playersModelArrayList.add(playersModel)
                }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return playersModelArrayList
    }

}


