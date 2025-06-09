package com.example.videoplayerappdemo

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoplayerappdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var folderAdapter: VideoFolderAdapter

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            videoViewModel.loadVideos()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoViewModel = ViewModelProvider(this)[VideoViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        checkAndRequestPermissions()
    }

    private fun setupRecyclerView() {
        folderAdapter = VideoFolderAdapter(
            onVideoClick = { video ->
                playVideo(video.uri)
            },
            onMoreOptionsClick = { video, anchorView ->
                showVideoOptionsMenu(video, anchorView)
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = folderAdapter
        }
    }

    private fun observeViewModel() {
        videoViewModel.videoFolders.observe(this) { folders ->
            folderAdapter.submitList(folders)
            binding.statusText.visibility = if (folders.isEmpty()) View.VISIBLE else View.GONE
            binding.statusText.text = if (folders.isEmpty()) "No videos found." else ""
        }

        videoViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.statusText.visibility = if (isLoading || folderAdapter.itemCount == 0) View.VISIBLE else View.GONE
            binding.statusText.text = if (isLoading) "Loading videos..." else if (folderAdapter.itemCount == 0) "No videos found." else ""
        }

        videoViewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                binding.statusText.text = it
                binding.statusText.visibility = View.VISIBLE
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
            }
        } else { // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            // WRITE_EXTERNAL_STORAGE is also needed for older Android versions for direct file operations
            // However, for rename, we are using MediaStore for modern Android, and direct File for older.
            // If you need broad write access on older versions, uncomment this:
            // if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) { // Android 9 and below
            //    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //        permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //    }
            // }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            videoViewModel.loadVideos()
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("This app needs storage permissions to access videos. Please grant them in settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun playVideo(videoUri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, videoUri)
            intent.setDataAndType(videoUri, "video/*")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot play video: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showVideoOptionsMenu(video: Video, anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(R.menu.video_options_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_rename -> {
                    showRenameDialog(video)
                    true
                }
                R.id.action_copy -> {
                    Toast.makeText(this, "Copy not implemented yet for: ${video.title}", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_cut -> {
                    Toast.makeText(this, "Cut not implemented yet for: ${video.title}", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_delete -> {
                    Toast.makeText(this, "Delete not implemented yet for: ${video.title}", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showRenameDialog(video: Video) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rename, null)
        val editText = dialogView.findViewById<EditText>(R.id.editTextNewName)
        editText.setText(video.title)

        AlertDialog.Builder(this)
            .setTitle("Rename Video")
            .setView(dialogView)
            .setPositiveButton("Rename") { dialog, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty() && newName != video.title) {
                    videoViewModel.renameVideo(video, newName)
                } else if (newName.isEmpty()) {
                    Toast.makeText(this, "New name cannot be empty.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}