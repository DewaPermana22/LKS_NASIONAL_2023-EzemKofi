package com.example.ui_slicing_lks2023

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class adapter_category(val data : JSONArray, val onItemClick : (Int) -> Unit) : RecyclerView.Adapter<adapter_category.ViewHolder>(){

    var selection : Int = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val jsonobject : JSONObject = data.getJSONObject(position)
       holder.item_categories.text = jsonobject.getString("name").toString()
        if (position == selection){
            holder.layouts_categories.setBackgroundResource(R.drawable.bg_category_active)
            holder.item_categories.setTextColor(holder.itemView.context.getColor(R.color.ezem_white))
        } else{
            holder.layouts_categories.setBackgroundResource(R.drawable.bg_category_not_active)
            holder.item_categories.setTextColor(holder.itemView.context.getColor(R.color.ezem_gray))
        }
        holder.itemView.setOnClickListener{
            selection = position
            notifyDataSetChanged()
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return data.length()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view.rootView){
        val layouts_categories = view.findViewById<LinearLayout>(R.id.layouts_categories)
        val item_categories = view.findViewById<TextView>(R.id.items_Categories)
    }
}
