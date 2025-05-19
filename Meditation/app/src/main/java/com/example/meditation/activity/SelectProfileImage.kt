package com.example.meditation.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.dev.meditation.adapter.AdapterViewPager
import com.dev.meditation.fragment.ImageTabFragment
import com.dev.meditation.`object`.Utils
import com.dev.meditation.`object`.Utils.getImageFoldersWithPaths
import com.example.meditation.R
import com.example.meditation.databinding.ActivitySelectProfileImageBinding
import java.io.File

class SelectProfileImage : AppCompatActivity() {

    private lateinit var binding: ActivitySelectProfileImageBinding
    private var photoUri: Uri? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectProfileImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        addListeners()
        initCameraLauncher()
    }

    private fun init() {
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            val layoutParams = binding.toolbarSelectImg.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight
            binding.toolbarSelectImg.layoutParams = layoutParams
            insets
        }

        Utils.setStatusBarColor(this, false)
        setSupportActionBar(binding.toolbarSelectImg)
        setUpViewPager()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.camera_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.openCamera -> {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 1001)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun captureImageFromCamera() {
        val photoFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", photoFile)
        photoUri = uri
        cameraLauncher.launch(uri)
    }

    private fun addListeners() {
        binding.toolbarSelectImg.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureImageFromCamera()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCameraLauncher() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri != null) {
                val returnIntent = Intent().apply {
                    putExtra("selectedImageUri", photoUri.toString())
                }
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
    }

    private fun setUpViewPager() {
        val folders = getImageFoldersWithPaths(this)
        val adapter = AdapterViewPager(supportFragmentManager)
        for ((folderName, folderPath) in folders) {
            val fragment = ImageTabFragment.newInstance(folderPath)
            adapter.setData(fragment, folderName)
        }
        binding.vpSelectImg.adapter = adapter
        binding.tabLytSelectImg.setupWithViewPager(binding.vpSelectImg)
    }
}
