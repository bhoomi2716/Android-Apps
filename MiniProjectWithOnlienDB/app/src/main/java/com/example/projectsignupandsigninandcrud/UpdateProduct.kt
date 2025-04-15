package com.example.projectsignupandsigninandcrud

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateProduct : AppCompatActivity()
{
    lateinit var updatename : EditText
    lateinit var updateprice : EditText
    lateinit var updatedesc : EditText
    lateinit var updatestatus : EditText
    lateinit var updatebtn : Button
    lateinit var updateimg : ImageView
    lateinit var apiInterface: ApiInterface
    private var filepath: Uri ? = null
    lateinit var bitmap: Bitmap

    var id = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_product)

        updatename = findViewById(R.id.updateproductname)
        updateprice = findViewById(R.id.updateproductprice)
        updatedesc = findViewById(R.id.updateproductdesc)
        updatestatus = findViewById(R.id.updateproductstatus)
        updateimg = findViewById(R.id.updateproductimg)
        updatebtn = findViewById(R.id.updateproductBtn)
        apiInterface = ApiClient().getapiclient().create(ApiInterface::class.java)

        var i = intent
        id = i.getIntExtra("pid",101)
        var pname = i.getStringExtra("pname")
        var pprice = i.getStringExtra("pprice")
        var pdesc = i.getStringExtra("pdes")
        var pstatus = i.getStringExtra("pstatus")
        var pimg = i.getStringExtra("pimg")

        updatename.setText(pname)
        updateprice.setText(pprice)
        updatedesc.setText(pdesc)
        updatestatus.setText(pstatus)

        if (!pimg.isNullOrEmpty()) {
            Glide.with(this)
                .load(pimg)
                .placeholder(R.drawable.addimg)
                .into(updateimg)
        }

        updateimg.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 101)
        }

        updatebtn.setOnClickListener {

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

    private fun uploadImage(filePath: String)
    {
        var pname = updatename.text.toString()
        var pprice = updatename.text.toString()
        var pdes = updatedesc.text.toString()
        var pstatus = updatestatus.text.toString()

        val name: RequestBody = RequestBody.create(MultipartBody.FORM, pname)
        val price: RequestBody = RequestBody.create(MultipartBody.FORM, pprice)
        val desc: RequestBody = RequestBody.create(MultipartBody.FORM, pdes)
        val status: RequestBody = RequestBody.create(MultipartBody.FORM, pstatus)

        val file = File(filePath)

        val requestFile = file.asRequestBody(contentResolver.getType(filepath!!)!!.toMediaTypeOrNull())

        val imgbody: MultipartBody.Part = MultipartBody.Part.createFormData("pimage", file.name, requestFile)

        val call: Call<ResponseBody> = apiInterface.UpdateProduct(id,name,price,desc,status,imgbody)

        call.enqueue(object : Callback<ResponseBody>
        {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Update Success", Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext,ViewProduct::class.java))
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Update Failed: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "Update Failed Details", Toast.LENGTH_LONG).show()
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
            updateimg.setImageURI(filepath)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext,ViewProduct::class.java))
        super.onBackPressed()
    }

}