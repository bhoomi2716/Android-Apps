package com.example.meditation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.meditation.R
import com.dev.meditation.dataClass.SignUpUsers
import com.example.meditation.databinding.ActivitySignUpBinding
import com.dev.meditation.`object`.Utils
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    private lateinit var binding : ActivitySignUpBinding
    private var isPasswordVisible = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signUpDb: FirebaseDatabase
    private val signInNum = 1001
    private lateinit var googleApiClient: GoogleApiClient

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        addListener()
    }

    private fun signUpWithGoogle() {
        GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
            .signOut()
            .addOnCompleteListener {
                val i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
                startActivityForResult(i, signInNum)
            }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==signInNum) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {

        val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

        Log.d("Email",account.email.toString())
        Log.d("DisplayName",account.displayName.toString())
        Log.d("FamilyName",account.familyName.toString())
        Log.d("Account",account.account.toString())
        Log.d("Id",account.id.toString())
        Log.d("Photo",account.photoUrl.toString())

        val id = account.id ?: return
        val username = account.displayName ?: "Unknown"
        val email = account.email ?: "NoEmail"
        val password = "GoogleSignIn" // Placeholder to indicate this is a Google account

        val signUpUsers = SignUpUsers(username, email, password)

        signUpDb.getReference("SignUp_Record")
            .child(id)
            .setValue(signUpUsers)
            .addOnSuccessListener {
                val editor = sharedPreferences.edit()
                editor.putString("username", username)
                editor.putString("email", email)
                editor.putString("password", password)
                editor.putBoolean("Authentication", true)
                editor.apply()

                Toast.makeText(applicationContext,"SignUp SuccessFull...!!",Toast.LENGTH_LONG).show()
                startActivity(Intent(this, UserWelcomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Failed to save Google account info", Toast.LENGTH_SHORT).show()
            }
    }

    private fun init() {
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.twenty_five_top_margin)

            val layoutParams = binding.btnBackSignUp.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.btnBackSignUp.layoutParams = layoutParams

            insets
        }

        Utils.setStatusBarColor(this,false)

        sharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE)

        if (sharedPreferences.getBoolean("Authentication", false)) {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }

        firebaseAuth = FirebaseAuth.getInstance()
        signUpDb = FirebaseDatabase.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(applicationContext)
            .enableAutoManage(this,this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addListener() {
        binding.edtUserNameSignUp.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                val username = binding.edtUserNameSignUp.text.toString().trim()
                if (isValidUsername(username) && username.isNotEmpty()) {
                    binding.edtUserNameSignUp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.valid_fields_mark, 0)
                } else {
                    binding.edtUserNameSignUp.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }

        binding.edtEmailSignUp.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                val email = binding.edtEmailSignUp.text.toString().trim()
                if (isValidEmail(email) && email.isNotEmpty() ) {
                    binding.edtEmailSignUp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.valid_fields_mark, 0)
                } else {
                    binding.edtEmailSignUp.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }

        binding.edtPasswordSignUp.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Right drawable
                binding.edtPasswordSignUp.compoundDrawables[drawableEnd]?.let {
                    val bounds = it.bounds
                    val drawableWidth = bounds.width()
                    val touchAreaStart = binding.edtPasswordSignUp.width - binding.edtPasswordSignUp.paddingEnd - drawableWidth

                    if (motionEvent.x >= touchAreaStart) {
                        isPasswordVisible = !isPasswordVisible

                        val inputType = if (isPasswordVisible) {
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        } else {
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }

                        binding.edtPasswordSignUp.inputType = inputType
                        val typeface = ResourcesCompat.getFont(this, R.font.helvetica_neue_light) // replace with your font
                        binding.edtPasswordSignUp.typeface = typeface
                        binding.edtPasswordSignUp.setSelection(binding.edtPasswordSignUp.text.length)

                        val icon = if (isPasswordVisible) R.drawable.pass_eye_open else R.drawable.pass_eye_close
                        binding.edtPasswordSignUp.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)

                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        binding.btnGetStartedSignUp.setOnClickListener{
            val username = binding.edtUserNameSignUp.text.toString().trim()
            val email = binding.edtEmailSignUp.text.toString().trim()
            val password = binding.edtPasswordSignUp.text.toString().trim()

            if (!isValidUsername(username)) {
                Toast.makeText(applicationContext, "Invalid username: only letters allowed, max 30 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
                Toast.makeText(applicationContext, "Please Enter Valid Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password) || !isStrongPassword(password)) {
                Toast.makeText(applicationContext, "Password must be 6–12 characters, include upper & lower case, digit, special character, and no spaces.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!binding.cbPolicySignUp.isChecked) {
                Toast.makeText(applicationContext, "Please accept the Privacy Policy to continue", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUpUser(username,email,password)
        }

        binding.btnBackSignUp.setOnClickListener {
            startActivity(Intent(applicationContext,WelcomeActivity::class.java))
        }

        binding.btnGoogleSignUp.setOnClickListener {
            signUpWithGoogle()
        }
    }

    private fun signUpUser(username : String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val id = firebaseAuth.currentUser?.uid
                    val signUpUsers = SignUpUsers(username,email,password)

                    signUpDb.getReference("SignUp_Record")
                        .child(id!!)
                        .setValue(signUpUsers)
                        .addOnSuccessListener {
                            val editor = sharedPreferences.edit()
                            editor.putString("username", username)
                            editor.putString("email", email)
                            editor.putString("password", password)
                            editor.apply()

                            val loginIntent = Intent(this, LogInActivity::class.java)
                            loginIntent.putExtra("email", email)
                            loginIntent.putExtra("password", password)
                            startActivity(loginIntent)
                            finish()

                            Toast.makeText(applicationContext, "SignUp SuccessFull..!!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "SignUp Failed..!! Please Check Network Connection", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidUsername(username: String?): Boolean {
        if (username.isNullOrBlank()) return false
        if (username.length > 30) return false

        val regex = "^[A-Za-z\\s]{1,30}$".toRegex()
        return regex.matches(username)
    }

    private fun isStrongPassword(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!])(?=\\S+$).{6,12}$")
        return password.matches(passwordPattern)
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(applicationContext, "Connection Failed Error", Toast.LENGTH_SHORT).show()
    }
}