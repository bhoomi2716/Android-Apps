package com.example.finalproject.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalproject.Admin.Activity.AdminDashboardActivity
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.Model.SignInModel
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivitySignInBinding
import com.example.finalproject.databinding.ActivitySplashScreenBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity()
{
    //Declare Variables
    private lateinit var binding: ActivitySignInBinding
    lateinit var apiInterface: ApiInterface
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Set SharedPreferences For LogIn Until User LogOut
        sharedPreferences = getSharedPreferences("FinalProject",Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("FinalProject",false) && !sharedPreferences.getString("PhoneNumber","")!!.isEmpty())
        {
            var dashboardIntent = Intent(applicationContext,DashBoardActivity::class.java)
            startActivity(dashboardIntent)
            finish()
        }

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

        //Redirect SignUp Activity
        binding.txtRegister.setOnClickListener {

            startActivity(Intent(applicationContext,SignUpActivity::class.java))
        }

        //Pass Data In Database From ApiInterface While User Click On Register Button
        binding.btnSignIn.setOnClickListener {

            var phnum = binding.edtMobileNumber.text.toString()
            var password = binding.edtPassword.text.toString()

            if(phnum.equals("0987654321") && password.equals("123456"))
            {
                //Pass Mobile Number In Admin DashBoard Activity By SharedPreference
                var signInSharedPreferences : SharedPreferences.Editor = sharedPreferences.edit()
                var signInIntent = Intent(applicationContext,AdminDashboardActivity::class.java)
                signInSharedPreferences.putBoolean("FinalProject",true)
                signInSharedPreferences.putString("PhoneNumber",phnum)
                signInSharedPreferences.commit()
                startActivity(signInIntent)
            }

            else
            {
                val call : Call<SignInModel> = apiInterface.SignIn(phnum,password)
                call.enqueue(object : Callback<SignInModel>
                {
                    //If Response Is SuccessFull Activity Redirect On Home Screen
                    override fun onResponse(call: Call<SignInModel>, response: Response<SignInModel>)
                    {
                        Toast.makeText(applicationContext, "SignIn Success", Toast.LENGTH_SHORT).show()

                        //Pass Mobile Number In DashBoard Activity By SharedPreference
                        var signInSharedPreferences : SharedPreferences.Editor = sharedPreferences.edit()
                        var signInIntent = Intent(applicationContext,DashBoardActivity::class.java)
                        signInSharedPreferences.putBoolean("FinalProject",true)
                        signInSharedPreferences.putString("PhoneNumber",phnum)
                        signInSharedPreferences.commit()
                        startActivity(signInIntent)
                    }

                    //If Response Is Fail Display Toast Message Of Fail SignIn
                    override fun onFailure(call: Call<SignInModel>, t: Throwable)
                    {
                        Toast.makeText(applicationContext, "SignIn Failed Enter Correct Fields", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        }
    }

    //When User Click On Back, App Will Close
    override fun onBackPressed()
    {
        finishAffinity()
        super.onBackPressed()
    }
}