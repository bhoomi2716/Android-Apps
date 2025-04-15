package com.example.finalproject.Activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivityPaymentBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity(), PaymentResultListener
{
    private lateinit var binding: ActivityPaymentBinding
    lateinit var apiInterface : ApiInterface
    lateinit var sharedPreferences: SharedPreferences
    var mobNum = ""
    var paymentName = ""
    var paymentPrice = ""
    var paymentDesc = ""
    var paymentImage = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Set Statusbar Colour
        if (Build.VERSION.SDK_INT >= 21)
        {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.statusbarColour)
        }

        binding.toolbarPayment.setTitle("Payment")
        setSupportActionBar(binding.toolbarPayment)

        var intent = intent
        paymentName = intent.getStringExtra("name").toString()
        paymentPrice = intent.getStringExtra("price").toString()
        paymentDesc = intent.getStringExtra("desc").toString()
        paymentImage = intent.getStringExtra("image").toString()

        sharedPreferences = getSharedPreferences("FinalProject", Context.MODE_PRIVATE)

        mobNum = sharedPreferences.getString("PhoneNumber","").toString()

        Picasso.get().load(paymentImage).into(binding.imageViewPayment)
        binding.txtItemNamePayment.text = paymentName
        binding.txtItemPricePayment.text = paymentPrice
        binding.txtItemDescPayment.text = paymentDesc

        binding.btnProceedToPayment.setOnClickListener{

            val price = Integer.parseInt(paymentPrice) * 100

            val checkout = Checkout()
            checkout.setKeyID("rzp_test_7PX7ejznhsbYwJ")
            val obj = JSONObject()
            try
            {
                obj.put("name", paymentName)
                obj.put("description", "Test Payment")
                obj.put("theme.color", "")
                obj.put("currency", "INR")
                obj.put("amount", price)
                obj.put("prefill.contact", "8849817263")
                obj.put("prefill.email", "submitdata123@gmail.com")
                checkout.open(this, obj)
            }
            catch (e: JSONException)
            {
                e.printStackTrace()
            }

        }
    }

    override fun onPaymentSuccess(p0: String?)
    {
        var call : Call<Void> = apiInterface.proceedPayment(paymentName,paymentPrice,paymentDesc,paymentImage,mobNum)
        call.enqueue(object : Callback<Void>
        {
            override fun onResponse(call: Call<Void>, response: Response<Void>)
            {
                Toast.makeText(applicationContext, "Payment Success", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Void>, t: Throwable)
            {
                Toast.makeText(applicationContext, "Payment Fail", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onPaymentError(p0: Int, p1: String?)
    {
        Toast.makeText(this, "Payment Failed :-  $p0 : $p1", Toast.LENGTH_SHORT).show()
    }
}