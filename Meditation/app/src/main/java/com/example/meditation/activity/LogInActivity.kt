package com.example.meditation.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import com.example.meditation.R
import com.example.meditation.databinding.ActivityLogInBinding
import com.dev.meditation.`object`.Utils
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LogInActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener
{
    private lateinit var binding: ActivityLogInBinding
    private var isPasswordVisible = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAuth : FirebaseAuth
    private val signInNum = 1001
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var googleSignInOptions: GoogleSignInOptions

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        addListener()
    }

    private fun logInWithGoogle() {
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Sign out any existing account to force account picker
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, signInNum)
        }
    }


    private fun showForgotPasswordDialog(userId : String, email: String) {
        val passDialog = Dialog(this)
        passDialog.setCancelable(false)
        passDialog.setContentView(R.layout.design_forgot_password)
        passDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        passDialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val edtNewPass = passDialog.findViewById<EditText>(R.id.edtNewPassForgotPassword)
        val edtRePass = passDialog.findViewById<EditText>(R.id.edtRePassForgotPassword)
        val btnSavePass = passDialog.findViewById<AppCompatButton>(R.id.btnSavePassForgotPassword)
        val ivCancelDialog = passDialog.findViewById<ImageView>(R.id.ivCancelDialogForgotPass)

        toggleEyePassword(passDialog.findViewById(R.id.edtNewPassForgotPassword))
        toggleEyePassword(passDialog.findViewById(R.id.edtRePassForgotPassword))

        ivCancelDialog.setOnClickListener { passDialog.dismiss() }

        btnSavePass.setOnClickListener {
            val newPass = edtNewPass.text.toString().trim()
            val rePass = edtRePass.text.toString().trim()

            if (!isStrongPassword(newPass)){
                Toast.makeText(this,
                    "Password must be 6–12 characters, include upper & lower case, digit, special character, and no spaces.",
                    Toast.LENGTH_SHORT).show()
            }
            else if (newPass.isEmpty() || rePass.isEmpty() ) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else if (newPass != rePass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                val databaseRef = FirebaseDatabase.getInstance().getReference("SignUp_Record").child(userId)
                databaseRef.child("password").setValue(newPass)
                Toast.makeText(this, "Password Updated..!!", Toast.LENGTH_LONG).show()
                passDialog.dismiss()
            }
        }

        passDialog.show()
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == signInNum) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val username = user?.displayName ?: "Guest"
                    val email = user?.email ?: ""

                    // Save to SharedPreferences
                    sharedPreferences.edit()
                        .putBoolean("Authentication", true)
                        .putString("username", username)
                        .putString("email", email)
                        .apply()

                    Toast.makeText(this, "LogIn Successful...!!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, UserWelcomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun init()
    {
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.twenty_five_top_margin)

            val layoutParams = binding.btnBackLogIn.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.btnBackLogIn.layoutParams = layoutParams

            insets
        }

        Utils.setStatusBarColor(this,false)

        sharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE)

        if (sharedPreferences.getBoolean("Authentication", false)) {
            startActivity(Intent(this, UserWelcomeActivity::class.java))
            finish()
        }

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
            .requestEmail()
            .build()

        firebaseAuth = FirebaseAuth.getInstance()

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build()
    }

    @Suppress("LABEL_NAME_CLASH")
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    private fun addListener() {
        binding.btnBackLogIn.setOnClickListener {
            startActivity(Intent(applicationContext,WelcomeActivity::class.java))
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(applicationContext,SignUpActivity::class.java))
        }

        toggleEyePassword(binding.edtPasswordLogIn)

        binding.btnLogin.setOnClickListener {
//            val email = binding.edtEmailLogIn.text.toString().trim()
//            val password = binding.edtPasswordLogIn.text.toString().trim()
            logInUser()
        }

        binding.btnGoogleLogIn.setOnClickListener {
            logInWithGoogle()
        }

        binding.edtEmailLogIn.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                val email = binding.edtEmailLogIn.text.toString().trim()
                if (isValidEmail(email) && email.isNotEmpty() ) {
                    binding.edtEmailLogIn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.valid_fields_mark, 0)
                } else {
                    binding.edtEmailLogIn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }

        binding.tvForgotPass.setOnClickListener {
            val emailDialog = Dialog(this)
            emailDialog.setCancelable(false)
            emailDialog.setContentView(R.layout.item_confirm_email_forgot_password) // create a new layout with one EditText and two buttons
            emailDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            emailDialog.window?.setBackgroundDrawableResource(R.color.transparent)

            val edtEmail = emailDialog.findViewById<EditText>(R.id.edtConfirmEmail)
            val btnConfirm = emailDialog.findViewById<AppCompatButton>(R.id.btnConfirm)
            val btnCancel = emailDialog.findViewById<AppCompatImageView>(R.id.ivCancelDialogConfirmEmail)

            btnCancel.setOnClickListener { emailDialog.dismiss() }

            btnConfirm.setOnClickListener {
                val email = edtEmail.text.toString().trim()
                if (email.isEmpty()) {
                    Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val databaseRef = FirebaseDatabase.getInstance().getReference("SignUp_Record")
                databaseRef.get().addOnSuccessListener { snapshot ->
                    var matchedUserId: String? = null
                    for (child in snapshot.children) {
                        val storedEmail = child.child("email").value.toString()
                        if (storedEmail.equals(email, ignoreCase = true)) {
                            matchedUserId = child.key
                            break
                        }
                    }

                    if (matchedUserId != null) {
                        emailDialog.dismiss()
                        Toast.makeText(this, "Email Matched..!!", Toast.LENGTH_SHORT).show()
                        showForgotPasswordDialog(matchedUserId, email)
                    } else {
                        Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error accessing database", Toast.LENGTH_SHORT).show()
                }
            }

            emailDialog.show()
        }
    }

    private fun logInUser() {
        val inputEmail = binding.edtEmailLogIn.text.toString().trim()
        val inputPassword = binding.edtPasswordLogIn.text.toString().trim()

        if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseRef = FirebaseDatabase.getInstance().getReference("SignUp_Record")
        databaseRef.get().addOnSuccessListener { dataSnapshot ->
            var matched = false
            for (userSnapshot in dataSnapshot.children) {
                val email = userSnapshot.child("email").value?.toString() ?: continue
                val password = userSnapshot.child("password").value?.toString() ?: continue

                if (email == inputEmail && password == inputPassword) {
                    val username = userSnapshot.child("username").value?.toString() ?: "Unknown"

                    val editor = sharedPreferences.edit()
                    editor.putBoolean("Authentication", true)
                    editor.putString("username", username)
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.apply()

                    Toast.makeText(applicationContext, "LogIn Successful...!!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, UserWelcomeActivity::class.java))
                    finish()
                    matched = true
                    break
                }
            }

            if (!matched) {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to access database", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun toggleEyePassword(editText: EditText) {
        editText.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Right drawable
                editText.compoundDrawables[drawableEnd]?.let {
                    val bounds = it.bounds
                    val drawableWidth = bounds.width()
                    val touchAreaStart = editText.width - editText.paddingEnd - drawableWidth

                    if (motionEvent.x >= touchAreaStart) {
                        // Toggle password visibility
                        isPasswordVisible = !isPasswordVisible

                        val inputType = if (isPasswordVisible) {
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        } else {
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }

                        editText.inputType = inputType
                        val typeface = ResourcesCompat.getFont(this, R.font.helvetica_neue_light) // replace with your font
                        editText.typeface = typeface
                        editText.setSelection(editText.text.length)

                        // Update drawable
                        val icon = if (isPasswordVisible) R.drawable.pass_eye_open else R.drawable.pass_eye_close
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)

                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    private fun isStrongPassword(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!])(?=\\S+$).{6,12}$")
        return password.matches(passwordPattern)
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
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