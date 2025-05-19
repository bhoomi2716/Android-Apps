package com.dev.meditation.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.meditation.R
import com.example.meditation.activity.LogInActivity
import com.example.meditation.databinding.FragmentProfileBinding
import com.dev.meditation.interfaces.onBackPress
import com.dev.meditation.`object`.Utils
import com.example.meditation.activity.SelectProfileImage
import com.google.android.material.textview.MaterialTextView
import java.io.File

class ProfileFragment : Fragment(), onBackPress {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageUriString = result.data?.getStringExtra("selectedImageUri")
            val imagePath = result.data?.getStringExtra("selectedImagePath")

            val uri = when {
                !imageUriString.isNullOrEmpty() -> Uri.parse(imageUriString)
                !imagePath.isNullOrEmpty() -> Uri.fromFile(File(imagePath))
                else -> null
            }

            if (uri != null) {
                sharedPreferences.edit().putString("profileImageUri", uri.toString()).apply()
                binding.ivProfile.setImageURI(uri)
            } else {
                Toast.makeText(requireContext(), "Image selection failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(requireContext(), SelectProfileImage::class.java)
            selectImageLauncher.launch(intent)
        } else {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else
                Manifest.permission.READ_EXTERNAL_STORAGE

            if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                showPermissionDeniedDialog()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater)
        init()
        addListener()
        return binding.root
    }

    private fun init() {
        Utils.setStatusBarColor(requireActivity(), true)
        sharedPreferences = requireContext().getSharedPreferences("Authentication", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "Unknown User")
        val email = sharedPreferences.getString("email", "No Email")
        binding.tvUserNameProfile.text = username
        binding.tvEmailProfile.text = email

        val imageUriString = sharedPreferences.getString("profileImageUri", null)
        if (!imageUriString.isNullOrEmpty()) {
            binding.ivProfile.setImageURI(Uri.parse(imageUriString))
        } else {
            binding.ivProfile.setImageResource(R.drawable.img_profile)
        }
    }

    private fun addListener() {
        binding.btnLogOut.setOnClickListener {
            val logoutDialog = Dialog(requireActivity())
            logoutDialog.setCancelable(false)
            logoutDialog.setContentView(R.layout.item_confirm_dialog)
            logoutDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            logoutDialog.window?.setBackgroundDrawableResource(R.color.transparent)

            val btnYes = logoutDialog.findViewById<AppCompatButton>(R.id.btnYes)
            val btnNo = logoutDialog.findViewById<AppCompatButton>(R.id.btnNo)
            val tvTitle = logoutDialog.findViewById<MaterialTextView>(R.id.tvTitle)

            tvTitle.text = resources.getString(R.string.sure_to_logout)
            btnYes.setOnClickListener {
                sharedPreferences.edit().putBoolean("Authentication", false).apply()
                val intent = Intent(requireContext(), LogInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
            btnNo.setOnClickListener { logoutDialog.dismiss() }
            logoutDialog.show()
        }

        binding.ivProfile.setOnClickListener {
            checkPermissionAndLoadImages()
        }
    }

    private fun checkPermissionAndLoadImages() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(requireContext(), SelectProfileImage::class.java)
            selectImageLauncher.launch(intent)
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun showPermissionDeniedDialog() {
        val permissionDialog = Dialog(requireActivity())
        permissionDialog.setContentView(R.layout.item_permisson_dialog)
        permissionDialog.setCancelable(false)
        permissionDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        permissionDialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val btnSetting = permissionDialog.findViewById<AppCompatButton>(R.id.btnSetting)
        btnSetting.setOnClickListener {
            permissionDialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }

        permissionDialog.show()
    }

    override fun onBackPressed(): Boolean {
        Log.d("ProfileFragment", "Back pressed handled in fragment")
        val layout = requireActivity().findViewById<LinearLayout>(R.id.navHome)
        layout.setBackgroundColor(resources.getColor(R.color.white))
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLytHomeActivity, HomeFragment())
            .addToBackStack(null)
            .commit()
        return true
    }
}
