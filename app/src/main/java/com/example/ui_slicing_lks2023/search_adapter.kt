package com.example.ui_slicing_lks2023

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.net.URL

class search_adapter(val data : JSONArray, val onitemClick : (JSONObject) -> Unit) : RecyclerView.Adapter<search_adapter.search_holder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): search_holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top_picks, parent, false)
        return search_holder(view)
    }

    override fun onBindViewHolder(
        holder: search_holder,
        position: Int
    ) {
        val jsonbject : JSONObject = data.getJSONObject(position)
        holder.name_coffe.text = jsonbject.getString("name").toString()
        holder.category_coffe.text = jsonbject.getString("category").toString()
        holder.price_coffee.text = jsonbject.getDouble("price").toString()
        holder.rate_coffee.text = jsonbject.getDouble("rating").toString()
        val nameImage = jsonbject.getString("imagePath")
        val imagePath = URL("http://10.0.2.2:5000/images/$nameImage")
        Glide.with(holder.itemView.context).load(imagePath).placeholder(R.drawable.icon_coffee).error(R.drawable.ic_launcher_background)
            .into(holder.images)
        holder.itemView.setOnClickListener{
            onitemClick(jsonbject)
        }
    }

    override fun getItemCount(): Int {
        return  data.length()
    }

    class search_holder(view: View) : RecyclerView.ViewHolder(view.rootView){
        val name_coffe = view.findViewById<TextView>(R.id.name_top_coffee)
        val category_coffe = view.findViewById<TextView>(R.id.category_top_pick)
        val price_coffee = view.findViewById<TextView>(R.id.price_top_pick)
        val rate_coffee = view.findViewById<TextView>(R.id.rate_top_pick)
        val images = view.findViewById<ImageView>(R.id.image_top_pick)
    }
}