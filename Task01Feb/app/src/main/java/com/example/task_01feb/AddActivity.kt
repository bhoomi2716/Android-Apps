package com.example.task_01feb

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class AddActivity : AppCompatActivity() {
    lateinit var productName : EditText
    lateinit var productPrice : EditText
    lateinit var productDesc : EditText
    lateinit var insertBtn : Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        productName = findViewById(R.id.productname)
        productPrice = findViewById(R.id.productprice)
        productDesc = findViewById(R.id.productdescription)
        insertBtn = findViewById(R.id.insertbtn)

        insertBtn.setOnClickListener {

            var name = productName.text.toString()
            var price = productPrice.text.toString()
            var desc = productDesc.text.toString()


            var stringRequest = object : StringRequest(Request.Method.POST,"https://prakrutitech.buzz/Fluttertestapi/productinsert.php",object : Response.Listener<String>
            {
                override fun onResponse(response: String?)
                {
                    Toast.makeText(applicationContext, "Product Inserted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext,MainActivity::class.java))
                }
            }, object : Response.ErrorListener
            {
                override fun onErrorResponse(error: VolleyError?)
                {
                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            })
            {
                override fun getParams(): MutableMap<String, String>?
                {
                    var addMap = HashMap<String,String>()
                    addMap["product_name"] = name
                    addMap["product_price"] = price
                    addMap["product_description"] = desc
                    return addMap
                }
            }

            var queue : RequestQueue = Volley.newRequestQueue(this)
            queue.add(stringRequest)

        }
    }

}