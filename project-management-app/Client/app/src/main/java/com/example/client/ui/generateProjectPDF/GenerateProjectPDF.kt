package com.example.client.ui.generateProjectPDF

import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.client.R
import kotlinx.android.synthetic.main.activity_generate_project_pdf.*


class GenerateProjectPDF : AppCompatActivity() {
    private val STORAGE_CODE: Int = 100;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_project_pdf)

        generatePDF.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                    //permission is not granted, request it
                    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissions,STORAGE_CODE)
                }
                else{
                    //permission already granted, call savePdf() method
                    savePdf()
                }
            }
            else{
                //system OS < marshmallow, call savePdf() method
                savePdf()

            }
        }



    }
    private fun savePdf(){

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults){
//            when(requestCode){
//                STORAGE_CODE -> {
//                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                        //permission from popup was granted, call savePdf() method
//                    }
//                    else{
//                        //permission from popup was denied, show error message
//                        Toast.makeText(this,"Pemission denied ...!",Toast.LENGTH_LONG).show()
//                    }
//
//                }
//            }
//        }
    }
}
