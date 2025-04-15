package com.example.finalproject.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivitySignInBinding
import com.example.finalproject.databinding.ActivitySignUpBinding
import com.example.finalproject.databinding.ActivitySplashScreenBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener
{
    //Declare Variables
    private lateinit var binding: ActivitySignUpBinding
    var Gender = ""
    lateinit var apiInterface : ApiInterface
    var type = "User"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Connection Of ApiInterface With ApiClient
        apiInterface = ApiClient().Connection().create(ApiInterface::class.java)

        //Set Statusbar Colour
        if (Build.VERSION.SDK_INT >= 21)
        {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.statusbarColour)
        }

        //Redirect SignIn Activity
        binding.txtLogin.setOnClickListener {

            startActivity(Intent(applicationContext,SignInActivity::class.java))
        }

        //Set Method For Gender RadioGroup
        binding.rbMale.setOnCheckedChangeListener(this)
        binding.rbFemale.setOnCheckedChangeListener(this)
        binding.radioGroupGender.setOnCheckedChangeListener(this)

        //Pass Data In Database From ApiInterface While User Click On Register Button
        binding.btnRegister.setOnClickListener {

            var fname = binding.edtFirstName.text.toString()
            var lname = binding.edtLastName.text.toString()
            var phonenum = binding.edtMobileNumber.text.toString()
            var gender = Gender
            var email = binding.edtEmail.text.toString()
            var password = binding.edtPassword.text.toString()

            var call : Call<Void> = apiInterface.SignUp(fname,lname,phonenum,gender,email,password,type)

            call.enqueue(object : Callback<Void>
            {
                //If Response Is SuccessFull Activity Redirect On SignIn Screen
                override fun onResponse(call: Call<Void>, response: Response<Void>)
                {
                    startActivity(Intent(applicationContext,SignInActivity::class.java))
                }

                //If Response Is Fail Display Toast Message Of Internet Connection Issue
                override fun onFailure(call: Call<Void>, t: Throwable)
                {
                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    //When User Click On Back, App Will Close
    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

    //RadioGroup Take One Value From 2 RadioButton
    override fun onCheckedChanged(p0: RadioGroup?, p1: Int)
    {
        if (binding.rbMale.isChecked)
        {
            Gender = "Male"
            Toast.makeText(applicationContext, "You Select MALE", Toast.LENGTH_SHORT).show()
        }
        if (binding.rbFemale.isChecked)
        {
            Gender = "Female"
            Toast.makeText(applicationContext, "You Select FEMALE", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean)
    {

    }
}