package com.example.projectsignupandsigninandcrud

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode.Callback
import retrofit2.Call
import retrofit2.Response

class SignInActivity : AppCompatActivity()
{
    lateinit var emailsingnin : EditText
    lateinit var passwordsignin : EditText
    lateinit var signinBtn : Button
    lateinit var apiInterface: ApiInterface
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sigupTxt : TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_activity)

        emailsingnin = findViewById(R.id.emailSiginIn)
        passwordsignin = findViewById(R.id.passwordSiginIn)
        signinBtn = findViewById(R.id.signinBtn)
        sigupTxt = findViewById(R.id.signuptxt)
        apiInterface = ApiClient().getapiclient().create(ApiInterface::class.java)
        sharedPreferences = getSharedPreferences("MYSESSION", Context.MODE_PRIVATE)

        if(sharedPreferences.getBoolean("MYSESSION",false) && !sharedPreferences.getString("e1","")!!.isEmpty())
        {
            var i = Intent(applicationContext,ViewProduct::class.java)
            startActivity(i)
            finish()
        }

        sigupTxt.setOnClickListener {

            startActivity(Intent(applicationContext,SignUpActivity::class.java))
        }

        signinBtn.setOnClickListener {

            var email = emailsingnin.text.toString()
            var password = passwordsignin.text.toString()

            var call = apiInterface.SignIn(email,password)


            call.enqueue(object : retrofit2.Callback<Model>
            {
                override fun onResponse(call: Call<Model>, response: Response<Model>)
                {
                    var xyz:SharedPreferences.Editor = sharedPreferences.edit()


                    Intent(applicationContext,ViewProduct::class.java)
                    xyz.putBoolean("MYSESSION",true)
                    xyz.putString("e1",email)
                    xyz.putString("p1",password)
                    xyz.commit()

                    Toast.makeText(applicationContext, "Login Done", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, ViewProduct::class.java))
                    finish()
                }

                override fun onFailure(call: Call<Model>, t: Throwable)
                {
                    Toast.makeText(applicationContext, "Login Fail", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    override fun onBackPressed()
    {
        finishAffinity()
        super.onBackPressed()
    }
}