package com.example.finalproject.Admin.Activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivityAddCategoryBinding
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream

class AddCategoryActivity : AppCompatActivity()
{
    lateinit var binding: ActivityAddCategoryBinding
    lateinit var imageUri : Uri
    private val contract = registerForActivityResult(ActivityResultContracts.GetContent())
    {
        imageUri = it!!
        binding.imageViewAddCategory.setImageURI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Set Statusbar Colour
        if (Build.VERSION.SDK_INT >= 21)
        {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.statusbarColour)
        }

        binding.toolbarAddCategory.setTitle("Add Category")
        setSupportActionBar(binding.toolbarAddCategory)

        binding.imageViewAddCategory.setOnClickListener {

            contract.launch("image/*")
        }

        binding.btnSubmitAddCategory.setOnClickListener {


        }

    }

    private fun upload()
    {
        val filesDir = applicationContext.filesDir
        val file = File(filesDir,"image.png")
        val inputstream = contentResolver.openInputStream(imageUri)
        val outputstream = FileOutputStream(file)
        inputstream!!.copyTo(outputstream)

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("url",file.name,requestBody)
        val name1:RequestBody = RequestBody.Companion.create(MultipartBody.FORM,edt1.text.toString())


        val retrofit = Retrofit.Builder().baseUrl("https://prakrutitech.buzz/AndroidAPI/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Apiinterface::class.java)


        CoroutineScope(Dispatchers.IO).launch {

            val response = retrofit.uploadImage(part,name1)
            Log.d("Cheezycoder",response.toString())

        }
    }
}