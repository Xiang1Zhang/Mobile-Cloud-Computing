package com.example.exercise01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val upperLowerButton: Button = findViewById(R.id.upperLowerButton)
        upperLowerButton.setOnClickListener { upperLower() }

        val charCount: Button = findViewById(R.id.charCount)
        charCount.setOnClickListener { charCount() }
    }
    var count = 0
    private fun upperLower() {
        // Do something in response to button
        val editText = findViewById<EditText>(R.id.editText)
        var resultText1: TextView = findViewById(R.id.textUpperLower)
        val message = editText.text.toString()

        if (count==0){
            resultText1.text = message.toUpperCase()
            count = count + 1
        } else {
            resultText1.text = message.toLowerCase()
            count = count - 1
        }



    }

    private fun charCount() {
        val editText = findViewById<EditText>(R.id.editText)
        var message = editText.text.toString()
        message = message.replace("\\s".toRegex(), "")

        countChar.text = message.length.toString()
    }
}
