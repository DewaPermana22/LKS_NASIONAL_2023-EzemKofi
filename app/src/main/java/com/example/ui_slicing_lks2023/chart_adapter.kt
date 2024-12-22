package com.example.ui_slicing_lks2023

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.badge.BadgeUtils
import org.json.JSONArray
import org.json.JSONObject

class chart_adapter (val data : JSONArray, val sharedPreferences: SharedPreferences) : RecyclerView.Adapter<chart_adapter.chart_holder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): chart_holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return chart_holder(view)
    }

    override fun onBindViewHolder(
        holder: chart_holder,
        position: Int
    ) {
        val datajsonobj : JSONObject = data.getJSONObject(position)
        val price_in_cart = datajsonobj.getDouble("price")
        val qty_in_cart = datajsonobj.getInt("qty")
        val totalprice = price_in_cart * qty_in_cart
        holder.name_coffee.text = datajsonobj.getString("name")
        holder.price_coffee.text = String.format("%.2f", totalprice)
        holder.qty_coffee.text = qty_in_cart.toString()
        holder.category_coffee.text = datajsonobj.getString("category")
        holder.size.text = datajsonobj.getString("size")
        val image = datajsonobj.getString("imageName")
        val url_image = "http://10.0.2.2:5000/images/$image"
        Glide.with(holder.itemView.context).load(url_image).placeholder(R.drawable.icon_coffee)
            .error(R.drawable.icon_coffee).into(holder.image_view)

        holder.ples.setOnClickListener{
            val newQty = qty_in_cart + 1
            datajsonobj.put("qty", newQty)
            sharedPreferences.edit().putString("cart_items", data.toString()).apply()
            notifyItemChanged(position)
        }

        holder.min.setOnClickListener{
            val newQty = qty_in_cart - 1
            if ( newQty > 0){
                datajsonobj.put("qty", newQty)
                sharedPreferences.edit().putString("cart_items", data.toString()).apply()
                notifyItemChanged(position)
            } else {
                data.remove(position)
                sharedPreferences.edit().putString("cart_items", data.toString()).apply()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, data.length())
            }
        }
    }

    override fun getItemCount(): Int {
        return data.length()
    }
    class chart_holder(view: View, ) : RecyclerView.ViewHolder(view.rootView){
        val name_coffee = view.findViewById<TextView>(R.id.name_coffee_chart)
        val price_coffee = view.findViewById<TextView>(R.id.price_chart)
        val qty_coffee = view.findViewById<TextView>(R.id.tv_quantity_cart)
        val category_coffee = view.findViewById<TextView>(R.id.category_chart)
        val image_view = view.findViewById<ImageView>(R.id.image_chart)
        val size = view.findViewById<TextView>(R.id.size_chart)
        val ples = view.findViewById<Button>(R.id.btn_plus_cart)
        val min = view.findViewById<Button>(R.id.btn_minus_cart)
    }
}