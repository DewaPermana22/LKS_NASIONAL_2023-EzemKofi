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

class Adapter_coffee (val data : JSONArray, val onItemClick : (JSONObject) -> Unit ) : RecyclerView.Adapter<Adapter_coffee.Coffee_holder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Coffee_holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coffee, parent, false)
        return Coffee_holder(view)
    }

    override fun onBindViewHolder(
        holder: Coffee_holder,
        position: Int
    ) {
        val jsonobject : JSONObject = data.getJSONObject(position)
        var name = jsonobject.getString("name")
        if (name.length > 20){
            name = name.substring(0, 20) + " ..."
        }
        holder.name_coffee.text = name
        holder.rate_coffee.text = jsonobject.getDouble("rating").toString()
        holder.price_coffee.text = jsonobject.getDouble("price").toString()
        val imagePath = jsonobject.getString("imagePath")
        val imageUrl = "http://10.0.2.2:5000/images/$imagePath"
        Glide.with(holder.itemView.context)
            .load(imageUrl).placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.icon_coffee).into(holder.image_coffee)
        holder.itemView.setOnClickListener{
            onItemClick(jsonobject)
        }
    }


    override fun getItemCount(): Int {
        return data.length()
    }

    class Coffee_holder (val view: View) : RecyclerView.ViewHolder(view.rootView){
        val name_coffee = view.findViewById<TextView>(R.id.coffee_name)
        val rate_coffee = view.findViewById<TextView>(R.id.rate_coffee)
        val price_coffee = view.findViewById<TextView>(R.id.price_coffee)
        val image_coffee = view.findViewById<ImageView>(R.id.image_coffee)
    }
}