package com.example.ui_slicing_lks2023

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class coffee_detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coffee_detail)
        val id_coffee = intent.getIntExtra("id", -1)
        val btn_m = findViewById<Button>(R.id.buton_m)
        val btn_s = findViewById<Button>(R.id.buton_s)
        val btn_l = findViewById<Button>(R.id.buton_l)
        val tv_price = findViewById<TextView>(R.id.text_price)
        val to_chart = findViewById<Button>(R.id.btn_add_to_chart)

        val sharedPreferences = getSharedPreferences("Cart_Coffe", MODE_PRIVATE).edit()

        var selected_size = "M"
        var price : Double = intent.getDoubleExtra("price", 0.0)
        logic_btn(btn_s, true)
        tv_price.text = price.toString()

        btn_s.setOnClickListener{
            logic_btn(btn_s, true)
            logic_btn(btn_m, false)
            logic_btn(btn_l, false)
            selected_size = "M"
            animate_coffee("M")
            tv_price.text = price.toString()
        }
        btn_l.setOnClickListener{
            logic_btn(btn_l, true)
            logic_btn(btn_s, false)
            logic_btn(btn_m, false)
            selected_size = "L"
            animate_coffee("L")
            val updt_price = price * 1.15
            tv_price.text = "%.2f".format(updt_price)
        }
        btn_m.setOnClickListener{
            logic_btn(btn_m, true)
            logic_btn(btn_s, false)
            logic_btn(btn_l, false)
            selected_size = "S"
            animate_coffee("S")
            val updt_price = price * 0.85
            tv_price.text = "%.2f".format(updt_price)
        }
        var qty = 1
        val tv_qty = findViewById<TextView>(R.id.tv_quantity)
        val minus = findViewById<Button>(R.id.btn_minus).setOnClickListener{
            if (qty > 1){
                qty--
                tv_qty.text = qty.toString();
            }
        }
       val plus = findViewById<Button>(R.id.btn_plus).setOnClickListener{
           qty++
           tv_qty.text = qty.toString()
       }

        to_chart.setOnClickListener{
            val id = id_coffee
            val name = findViewById<TextView>(R.id.name_of_coffee).text.toString()
            val size = selected_size
            val qty = qty.toString().toInt()
            val price = tv_price.text.toString().toDouble()
            val category = intent.getStringExtra("category").toString()
            val imageName = intent.getStringExtra("imagePath").toString()
            add_cart(id, name, category, qty, size, price, imageName)
            val intent = Intent(this, Cart::class.java)
            startActivity(intent)
        }
        val back = findViewById<ImageView>(R.id.to_home).setOnClickListener{
            val intent = Intent(this@coffee_detail, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        val back_home = findViewById<CardView>(R.id.back_home).setOnClickListener{
            val intent = Intent(this@coffee_detail, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        fetch_by_id(id_coffee)
    }

    fun fetch_by_id(id : Int){
        CoroutineScope(Dispatchers.IO).launch{
            val koneksi = URL("http://10.0.2.2:5000/api/coffee/$id").openStream().bufferedReader().readText()
            val response = JSONObject(koneksi)
            withContext(Dispatchers.Main){
                findViewById<TextView>(R.id.name_of_coffee).text = response.getString("name")
                findViewById<TextView>(R.id.Desc_Coffe).text = response.getString("description")
                findViewById<TextView>(R.id.tv_rate).text = response.getDouble("rating").toString()

            }
            val images_url = "http://10.0.2.2:5000/images/" + response.getString("imagePath")
            val url_fetch = URL(images_url).openStream()
            val image = BitmapFactory.decodeStream(url_fetch)
            withContext(Dispatchers.Main){
                val view_image = findViewById<ImageView>(R.id.image_coffee)
                view_image.setImageBitmap(image)
            }
        }
    }

    fun logic_btn(button : Button, active : Boolean){
        if (active){
            button.setBackgroundResource(R.drawable.active_btn_sml)
            button.setTextColor(Color.WHITE)
        }else{
            button.setTextColor(ContextCompat.getColor(this, R.color.ezem_green))
            button.setBackgroundResource(R.drawable.inactive_btn)
        }
    }

    fun animate_coffee(size : String){
        val image_coffee = findViewById<ImageView>(R.id.image_coffee)
        val rotation = ObjectAnimator.ofFloat(image_coffee, "rotation", 0f, 360f).apply {
            duration = 500
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        val scale_x = when(size) {
            "S" -> 0.85f
            "L" -> 1.15f
            else -> 1f
        }
        val scale_y = scale_x
        val scale_Y = ObjectAnimator.ofFloat(image_coffee, "scaleY", 1f, scale_y)
        val scale_X = ObjectAnimator.ofFloat(image_coffee, "scaleX", 1f, scale_x)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotation, scale_X, scale_Y)
        animatorSet.start()
    }

    fun add_cart(id: Int, name_coffe: String, Category: String, Qty: Int, Size: String, Price: Double, ImageName : String){
        val existing = getSharedPreferences("Cart_Coffe", MODE_PRIVATE).getString("cart_items", "[]")
        val array = JSONArray(existing)
        val add_cart = JSONObject()
            add_cart.put("id", id)
            add_cart.put("name", name_coffe)
            add_cart.put("qty", Qty)
            add_cart.put("category", Category)
            add_cart.put("size", Size)
            add_cart.put("price", Price)
        add_cart.put("imageName", ImageName)
        array.put(add_cart)
        getSharedPreferences("Cart_Coffe", MODE_PRIVATE).edit().putString("cart_items", array.toString()).apply()
    }
}