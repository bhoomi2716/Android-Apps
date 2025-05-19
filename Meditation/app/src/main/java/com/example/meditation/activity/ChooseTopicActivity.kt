package com.example.meditation.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dev.meditation.adapter.AdapterChooseTopic
import com.dev.meditation.model.ModelChooseTopic
import com.dev.meditation.`object`.Utils
import com.example.meditation.databinding.ActivityChooseTopicBinding
import com.example.meditation.R

class ChooseTopicActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityChooseTopicBinding
    private lateinit var itemChooseTopic : MutableList<ModelChooseTopic>

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseTopicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        addRvItems()
    }

    private fun init(){
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop

            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.thirty_top_margin)

            val layoutParams = binding.tvWhatBrings.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.tvWhatBrings.layoutParams = layoutParams

            insets
        }
        Utils.setStatusBarColor(this,false)
        itemChooseTopic = mutableListOf()
    }

    private fun addRvItems() {
        itemChooseTopic.add(ModelChooseTopic(R.drawable.reduce_stress_img,resources.getString(R.string.reduce_stress),R.color.heading_color_light))
        itemChooseTopic.add(ModelChooseTopic(R.drawable.improve_performance_img,resources.getString(R.string.improve_performance),R.color.card_txt_color))
        itemChooseTopic.add(ModelChooseTopic(R.drawable.reduce_anxiety_img,resources.getString(R.string.reduce_anxiety),R.color.heading_colour_primary))
        itemChooseTopic.add(ModelChooseTopic(R.drawable.increase_happiness_img,resources.getString(R.string.increase_happiness),R.color.heading_colour_primary))
        itemChooseTopic.add(ModelChooseTopic(R.drawable.better_sleep_img,resources.getString(R.string.better_sleep),R.color.light_btn_bg_color))
        itemChooseTopic.add(ModelChooseTopic(R.drawable.personal_growth_img,resources.getString(R.string.personal_growth),R.color.heading_color_light))
        itemChooseTopic.add(ModelChooseTopic(R.drawable.personal_grouth_bottom,resources.getString(R.string.personal_growth),R.color.heading_colour_primary))
        itemChooseTopic.add(ModelChooseTopic(R.drawable.reduse_stress_bottom,resources.getString(R.string.reduce_stress),R.color.heading_color_light))

        val layoutManager : RecyclerView.LayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvChooseTopic.layoutManager = layoutManager
        val adapter = AdapterChooseTopic(applicationContext,itemChooseTopic)
        binding.rvChooseTopic.adapter = adapter
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        val exitDialog = Dialog(this)
        exitDialog.setCancelable(false)
        exitDialog.setContentView(R.layout.item_confirm_dialog)
        exitDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        exitDialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val btnYes = exitDialog.findViewById<AppCompatButton>(R.id.btnYes)
        val btnNo = exitDialog.findViewById<AppCompatButton>(R.id.btnNo)

        btnYes.setOnClickListener {
            super.onBackPressed()
            finishAffinity()
        }
        btnNo.setOnClickListener { exitDialog.dismiss() }

        exitDialog.show()
    }
}