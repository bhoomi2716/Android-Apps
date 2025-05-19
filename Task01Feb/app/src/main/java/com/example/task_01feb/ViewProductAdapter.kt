package com.example.task_01feb

import android.app.Activity
import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.task_01feb.ViewProductAdapter.Companion.selectedPosition

class ViewProductAdapter (var context: Context , var productList : MutableList<ViewProductModel>) :  RecyclerView.Adapter<ProductView>()
{

    companion object {
        var selectedPosition: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductView
    {
        var productlayout = LayoutInflater.from(parent.context)
        var view = productlayout.inflate(R.layout.cardview_design, parent, false)
        return ProductView(view)
    }

    override fun getItemCount(): Int
    {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductView, position: Int)
    {
        holder.name.setText(productList.get(position).product_name)
        holder.price.setText(productList.get(position).product_price)
        holder.descpription.setText(productList.get(position).product_description)
    }
}

class ProductView ( productDetail : View) : RecyclerView.ViewHolder(productDetail), View.OnCreateContextMenuListener
{
    lateinit var name : TextView
    lateinit var price : TextView
    lateinit var descpription : TextView

    init {

        name = productDetail.findViewById(R.id.productname)
        price = productDetail.findViewById(R.id.productprice)
        descpription = productDetail.findViewById(R.id.productdesc)
        productDetail.setOnCreateContextMenuListener(this)
        productDetail.setOnLongClickListener {

            selectedPosition = adapterPosition
            false
        }
    }

    override fun onCreateContextMenu(p0: ContextMenu?, p1: View?, p2: ContextMenu.ContextMenuInfo?)
    {
        var productinflater = (p1!!.context as Activity).menuInflater
        productinflater.inflate(R.menu.menu,p0)
    }

}