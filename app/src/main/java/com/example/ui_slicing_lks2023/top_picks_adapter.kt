package com.example.ui_slicing_lks2023

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject

class top_picks_adapter (val data : JSONArray) : RecyclerView.Adapter<top_picks_adapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top_picks, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val jsonObject : JSONObject = data.getJSONObject(position)
        holder.tp_name.text = jsonObject.getString("name")
        holder.tp_category.text = jsonObject.getString("category")
        holder.tp_rate.text = jsonObject.getString("rating")
        holder.tp_price.text = jsonObject.getString("price")
        val imagePath = jsonObject.getString("imagePath")
        val imageUrl = "http://10.0.2.2:5000/images/$imagePath"
        Glide.with(holder.itemView.context)
            .load(imageUrl).placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.icon_coffee).into(holder.image_tp)
    }

    override fun getItemCount(): Int {
        return data.length()
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val tp_name = view.findViewById<TextView>(R.id.name_top_coffee)
        val tp_category = view.findViewById<TextView>(R.id.category_top_pick)
        val tp_price = view.findViewById<TextView>(R.id.price_top_pick)
        val tp_rate = view.findViewById<TextView>(R.id.rate_top_pick)
        val image_tp = view.findViewById<ImageView>(R.id.image_top_pick)
    }
}