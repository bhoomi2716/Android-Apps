package com.example.task_01feb

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class ViewActivity : AppCompatActivity()
{
    lateinit var productList : RecyclerView
    lateinit var productItems : MutableList<ViewProductModel>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        productList = findViewById(R.id.itemRecyclerView)
        productItems = ArrayList()

        registerForContextMenu(productList)

        var stringrequest = StringRequest(Request.Method.GET,"https://prakrutitech.buzz/Fluttertestapi/productview.php",
            object : Response.Listener<String>
        {
            override fun onResponse(response: String?)
            {

                var jsonArray = JSONArray(response)
                for(data in 0 until  jsonArray.length())
                {
                    var jsonObject = jsonArray.getJSONObject(data)

                    var id = jsonObject.getInt("id")
                    var ProductName = jsonObject.getString("product_name")
                    var ProductPrice = jsonObject.getString("product_price")
                    var ProductDesc = jsonObject.getString("product_description")

                    var product = ViewProductModel()
                    product.id = id
                    product.product_name = ProductName
                    product.product_price = ProductPrice
                    product.product_description = ProductDesc

                    productItems.add(product)
                }

                var productadapter = ViewProductAdapter(applicationContext,productItems)
                productList.adapter = productadapter

                var layoutmanager : RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
                productList.layoutManager = layoutmanager

            }
        },object : Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?)
            {
                Toast.makeText(applicationContext,"No Internet Connection",Toast.LENGTH_SHORT).show()
            }
        })

        var queue : RequestQueue = Volley.newRequestQueue(this)
        queue.add(stringrequest)


    }

    override fun onContextItemSelected(item: MenuItem): Boolean
    {
        var position = ViewProductAdapter.selectedPosition
        if (position == -1) return super.onContextItemSelected(item)
        var selectedProduct = productItems[position]

        when(item.itemId)
        {
            R.id.update->
            {
                var updateintent = Intent(applicationContext,UpdateActivity::class.java)
                updateintent.putExtra("id",selectedProduct.id)
                updateintent.putExtra("product_name",selectedProduct.product_name)
                updateintent.putExtra("product_price",selectedProduct.product_price)
                updateintent.putExtra("product_description",selectedProduct.product_description)
                startActivity(updateintent)
            }
        }

        when(item.itemId)
        {
            R.id.delete->
            {
                var deletedialog = AlertDialog.Builder(this)
                deletedialog.setTitle("Are Sure Want To Delete This Product ?")
                deletedialog.setPositiveButton("YES", { dialogInterface: DialogInterface, i: Int ->

                    var stringRequest :StringRequest = object: StringRequest(Request.Method.POST,"https://prakrutitech.buzz/Fluttertestapi/productdelete.php",
                        Response.Listener
                        {
                        Toast.makeText(applicationContext,"Product Deleted",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext,ViewActivity::class.java))
                        },
                        Response.ErrorListener {
                        Toast.makeText(applicationContext,"Error in Deletion",Toast.LENGTH_SHORT).show()
                        })
                         {
                            override fun getParams() : MutableMap<String,String>
                            {
                                var deleteMap = HashMap<String,String>()
                                deleteMap["id"] = selectedProduct.id.toString()

                                return deleteMap
                            }
                         }
                    var queue:RequestQueue = Volley.newRequestQueue(this)
                    queue.add(stringRequest)


                })

                deletedialog.setNegativeButton("NO", { dialogInterface: DialogInterface, i: Int ->

                    dialogInterface.cancel()

                })
                deletedialog.show()
            }
        }
        return super.onContextItemSelected(item)
    }
    override fun onBackPressed() {
        startActivity(Intent(applicationContext,MainActivity::class.java))
        super.onBackPressed()
    }
}