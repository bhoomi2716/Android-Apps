package com.example.projectsignupandsigninandcrud

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class ProductAdapter(var context: Context, var productList: MutableList<ProductModel>) : RecyclerView.Adapter<ProductViewHolder>()
{
    var id = 0
    var apiinterface : ApiInterface = ApiClient().getapiclient().create(ApiInterface::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder
    {
        var layout = LayoutInflater.from(parent.context)
        var layoutview = layout.inflate(R.layout.view_design,parent,false)
        return ProductViewHolder(layoutview)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int)
    {
        id = productList.get(position).pid
        holder.productname.setText(productList.get(position).pname)
        holder.productdesc.setText(productList.get(position).pdesc)
        holder.productprice.setText(productList.get(position).pprice)
        Glide
            .with(holder.itemView.context)
            .load(productList.get(position).pimage)
            .centerCrop()
            .into(holder.productimg)

        holder.activebtn.setOnClickListener {

            var alertDialog = AlertDialog.Builder(holder.itemView.context)
            alertDialog.setTitle("Which Operation Do You Perform ?? ")
            alertDialog.setPositiveButton("UPDATE", { dialogInterface: DialogInterface, i: Int ->

                var i = Intent(context,UpdateProduct::class.java)
                i.putExtra("pname",productList.get(position).pname)
                i.putExtra("pprice",productList.get(position).pprice)
                i.putExtra("pdes",productList.get(position).pdesc)
                i.putExtra("pstatus",productList.get(position).pstatus)
                i.putExtra("pid",productList.get(position).pid)
                i.putExtra("pimg",productList.get(position).pimage)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            })
            alertDialog.setNegativeButton("DELETE", { dialogInterface: DialogInterface, i: Int ->

                var call: Call<Void> = apiinterface.DeleteProduct(productList.get(position).pid)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>)
                    {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()

                        var i = Intent(context,ViewProduct::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(i)
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable)
                    {
                        Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
                    }

                })

            })

            alertDialog.show()
        }
    }

    override fun getItemCount(): Int
    {
        return productList.size
    }

    fun filterList(filterlist: ArrayList<ProductModel>) {
        // below line is to add our filtered
        // list in our course array list.
        productList = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
}

class ProductViewHolder(layoutview: View) : RecyclerView.ViewHolder(layoutview) {
    var productimg : ImageView = layoutview.findViewById(R.id.showimg)
    var productname : TextView = layoutview.findViewById(R.id.showName)
    var productdesc : TextView = layoutview.findViewById(R.id.showDesc)
    var productprice : TextView = layoutview.findViewById(R.id.showPrice)
    var activebtn : Button = layoutview.findViewById(R.id.activeOP)
//    var update : Button = layoutview.findViewById(R.id.updateproduct)
//    var delete : ImageView = layoutview.findViewById(R.id.delteproduct)
}
