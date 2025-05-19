package com.example.task_01feb

import android.content.Intent
import android.os.Bundle
import android.view.View
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

class UpdateActivity : AppCompatActivity()
{
    lateinit var name : EditText
    lateinit var price : EditText
    lateinit var desc : EditText
    lateinit var updateBtn : Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        name = findViewById(R.id.update_productname)
        price = findViewById(R.id.update_productprice)
        desc = findViewById(R.id.update_productdescription)
        updateBtn = findViewById(R.id.updatebtn)

        var updateintent = intent
        var productID = updateintent.getStringExtra("id")
        var productName = updateintent.getStringExtra("product_name")
        var productPrice = updateintent.getStringExtra("product_price")
        var productDesc = updateintent.getStringExtra("product_description")

        name.setText(productName)
        price.setText(productPrice)
        desc.setText(productDesc)

        updateBtn.setOnClickListener {

            var pname = name.text.toString()
            var pprice = price.text.toString()
            var pdesc = desc.text.toString()

            var stringRequest = object : StringRequest(Request.Method.POST,"https://prakrutitech.buzz/Fluttertestapi/productupdate.php",
                object : Response.Listener<String>
                {
                override fun onResponse(response: String?)
                {
                    Toast.makeText(applicationContext, "Product Updated", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext,ViewActivity::class.java))                }
                 },
                object : Response.ErrorListener
                {
                    override fun onErrorResponse(error: VolleyError?) {
                        Toast.makeText(applicationContext, "No Internet", Toast.LENGTH_SHORT).show()
                    }
                })

            {
                override fun getParams(): MutableMap<String, String>?
                {
                    var updatemap = HashMap<String,String>()
                    updatemap["id"] = productID.toString()
                    updatemap["product_name"] = pname
                    updatemap["product_price"] = pprice
                    updatemap["product_description"] = pdesc
                    return updatemap
                }
            }

            var queue: RequestQueue = Volley.newRequestQueue(this)
            queue.add(stringRequest)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext,ViewActivity::class.java))
        super.onBackPressed()
    }
}