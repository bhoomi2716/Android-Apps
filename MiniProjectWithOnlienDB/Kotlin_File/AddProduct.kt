package com.example.projectsignupandsigninandcrud

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddProduct : AppCompatActivity()
{
    lateinit var addname : EditText
    lateinit var addprice : EditText
    lateinit var adddesc : EditText
    lateinit var addstatus : EditText
    lateinit var addimg : ImageView
    lateinit var addproductBtn : Button
    private var filepath: Uri ? = null
    lateinit var bitmap: Bitmap
    lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)

        addname = findViewById(R.id.addproductname)
        addprice = findViewById(R.id.addproductprice)
        adddesc = findViewById(R.id.addproductdesc)
        addstatus = findViewById(R.id.addproductstatus)
        addimg = findViewById(R.id.addproductimg)
        addproductBtn = findViewById(R.id.addproductBtn)
        apiInterface = ApiClient().getapiclient().create(ApiInterface::class.java)
        permissions()

        addimg.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 101)
        }

        addproductBtn.setOnClickListener {

            if (filepath != null) {
                val filePath = getpath(filepath!!)
                if (filePath != null) {
                    uploadImage(filePath)
                } else {
                    Toast.makeText(this, "Failed to get image path", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }


        }

    }

    private fun permissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(READ_MEDIA_IMAGES), 100)
        } else {
            Toast.makeText(applicationContext, "Permission already granted", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage(filePath: String)
    {
            var pname = addname.text.toString()
            var pprice = addprice.text.toString()
            var pdes = adddesc.text.toString()
            var pstatus = addstatus.text.toString()

        val name: RequestBody = RequestBody.create(MultipartBody.FORM, pname)
        val price: RequestBody = RequestBody.create(MultipartBody.FORM, pprice)
        val desc: RequestBody = RequestBody.create(MultipartBody.FORM, pdes)
        val status: RequestBody = RequestBody.create(MultipartBody.FORM, pstatus)

        val file = File(filePath)

        val requestFile = file.asRequestBody(contentResolver.getType(filepath!!)!!.toMediaTypeOrNull())

        val imgbody: MultipartBody.Part = MultipartBody.Part.createFormData("pimage", file.name, requestFile)

        val call: Call<ResponseBody> = apiInterface.InsertProduct(name,price,desc,status,imgbody)

        call.enqueue(object : Callback<ResponseBody>
        {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Upload Success", Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext,ViewProduct::class.java))
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Upload Failed: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "Upload Failed Details", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getpath(uri: Uri): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, filePathColumn, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
        }
        return uri.path
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            filepath = data.data
            addimg.setImageURI(filepath)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext,ViewProduct::class.java))
        super.onBackPressed()
    }
}