package com.example.projectsignupandsigninandcrud

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Response

class SignUpActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    lateinit var namesignup: EditText
    lateinit var surnamesignup: EditText
    lateinit var emailsignup: EditText
    lateinit var femalegender: RadioButton
    lateinit var malegender: RadioButton
    lateinit var mobilesignup: EditText
    lateinit var passwordsignup: EditText
    lateinit var signupBtn: Button
    var Gender = ""
    lateinit var apiInterface: ApiInterface
    lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        namesignup = findViewById(R.id.namesignup)
        surnamesignup = findViewById(R.id.surnamesignup)
        emailsignup = findViewById(R.id.emailsignup)
        femalegender = findViewById(R.id.femaleRB)
        malegender = findViewById(R.id.maleRB)
        mobilesignup = findViewById(R.id.mobilesignup)
        passwordsignup = findViewById(R.id.passwordsignup)
        signupBtn = findViewById(R.id.signupBtn)

        apiInterface = ApiClient().getapiclient().create(ApiInterface::class.java)
        sharedPreferences = getSharedPreferences("MYSESSION", Context.MODE_PRIVATE)

        if(sharedPreferences.getBoolean("MYSESSION",false) && !sharedPreferences.getString("n1","")!!.isEmpty())
        {
            var i = Intent(applicationContext,SignInActivity::class.java)
            startActivity(i)
            finish()
        }

        femalegender.setOnCheckedChangeListener(this)
        malegender.setOnCheckedChangeListener(this)


        signupBtn.setOnClickListener {
            val name = namesignup.text.toString()
            val surname = surnamesignup.text.toString()
            val email = emailsignup.text.toString()
            val gender = Gender
            val mobile = mobilesignup.text.toString()
            val password = passwordsignup.text.toString()

            if (name.isNotEmpty() && surname.isNotEmpty()
                && email.isNotEmpty() && gender.isNotEmpty()
                && mobile.isNotEmpty() && password.isNotEmpty())
            {
                val call: Call<Void> = apiInterface.SignUp(name, surname, email, gender, mobile, password)

                call.enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            var xyz:SharedPreferences.Editor = sharedPreferences.edit()


                            var i = Intent(applicationContext,SignInActivity::class.java)
                            xyz.putBoolean("MYSESSION",true)
                            xyz.putString("n1",name)
                            xyz.putString("p1",password)
                            xyz.commit()

                            Toast.makeText(applicationContext, "SignUp Done", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, SignInActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "SignUp Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(applicationContext, "SignUp Fail: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(applicationContext, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean)
    {
        if (femalegender.isChecked)
        {
            Gender = "Female"
        }
        if(malegender.isChecked)
        {
            Gender = "Male"
        }
    }
}
