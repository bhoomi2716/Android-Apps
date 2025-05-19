package com.dev.meditation.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.meditation.adapter.AdapterImage
import com.example.meditation.R
import java.io.File

class ImageTabFragment : Fragment() {

    private lateinit var folderPath: String
    private lateinit var recyclerView: RecyclerView
    private val imagePaths = mutableListOf<String>()

    companion object {
        private const val ARG_FOLDER_PATH = "folder_path"

        fun newInstance(folderPath: String): ImageTabFragment {
            val fragment = ImageTabFragment()
            val args = Bundle()
            args.putString(ARG_FOLDER_PATH, folderPath)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        folderPath = arguments?.getString(ARG_FOLDER_PATH) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_image_tab, container, false)
        recyclerView = view.findViewById(R.id.rvImages)
        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        imagePaths.clear()

        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            val files = folder.listFiles { file ->
                file.isFile && (file.name.endsWith(".jpg", true)
                        || file.name.endsWith(".jpeg", true)
                        || file.name.endsWith(".png", true))
            }?.sortedByDescending { it.lastModified() }

            files?.forEach { imagePaths.add(it.absolutePath) }
        }

        val adapter = AdapterImage(imagePaths) { selectedImagePath ->
            val intent = Intent()
            intent.putExtra("selectedImagePath", selectedImagePath)
            requireActivity().setResult(android.app.Activity.RESULT_OK, intent)
            requireActivity().finish()
        }

        recyclerView.adapter = adapter
    }
}
