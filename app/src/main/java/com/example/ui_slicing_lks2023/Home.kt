package com.example.ui_slicing_lks2023

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class Home : AppCompatActivity() {
    val categories = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        val token = intent.getStringExtra("Token") ?: getSharedPreferences("User_Token", MODE_PRIVATE).getString("Token", null)
        if (token == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<EditText>(R.id.search_et).setOnClickListener{
            startActivity(Intent(this@Home, SearchActivity::class.java))
        }
        val to_cart = findViewById<ImageView>(R.id.to_cart).setOnClickListener{
            val intent = Intent(this, Cart::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        fetch_profile_user(token)
        fetch()
        fetch_top_menu()
    }
    fun fetch() {
        CoroutineScope(Dispatchers.IO).launch {
            val categories = JSONArray(URL("http://10.0.2.2:5000/api/coffee-category").openStream().bufferedReader().readText())
            val coffeeData = JSONArray(URL("http://10.0.2.2:5000/api/coffee?").openStream().bufferedReader().readText())
            val defaultCategory = "Americano"

            withContext(Dispatchers.Main) {
                val categoryView = findViewById<RecyclerView>(R.id.category_pager)
                val coffeeView = findViewById<RecyclerView>(R.id.pager_menu_coffe)

                categoryView.layoutManager = LinearLayoutManager(this@Home, LinearLayoutManager.HORIZONTAL, false)
                categoryView.adapter = adapter_category(categories) { position ->
                    val selectedCategory = categories.getJSONObject(position).getString("name")
                    display(coffeeView, filter_coffee(selectedCategory, coffeeData))
                }
                display(coffeeView, filter_coffee(defaultCategory, coffeeData))
            }
        }
    }

    fun fetch_top_menu(){
        CoroutineScope(Dispatchers.IO).launch{
            val koneksi = URL("http://10.0.2.2:5000/api/coffee/top-picks").openConnection() as HttpURLConnection
            val response = koneksi.inputStream.bufferedReader().readText()
            val data_coffee = JSONArray(response)
            withContext(Dispatchers.Main){
                val view_coffee = findViewById<RecyclerView>(R.id.rc_top_picks)
                view_coffee.layoutManager = LinearLayoutManager(this@Home, LinearLayoutManager.VERTICAL, false)
                view_coffee.adapter = top_picks_adapter(data_coffee)
            }
        }
    }

    fun fetch_profile_user(token : String?){
        CoroutineScope(Dispatchers.IO).launch{
            val conn = URL("http://10.0.2.2:5000/api/me").openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $token")
            val res_code = conn.responseCode
            if (res_code == HttpURLConnection.HTTP_OK){
                val res = conn.inputStream.bufferedReader().use { it.readText() }
                val json_respon = JSONObject(res)
                withContext(Dispatchers.Main){
                    findViewById<TextView>(R.id.name_user).text = json_respon.getString("fullName")
                }
            }
        }
    }

    fun filter_coffee(category : String, data_json : JSONArray) : JSONArray {
        return JSONArray((0 until data_json.length())
            .map { data_json.getJSONObject(it) }
            .filter { it.getString("category") == category })
    }

    fun display(view: RecyclerView, data : JSONArray){
        view.layoutManager = LinearLayoutManager(this@Home, LinearLayoutManager.HORIZONTAL, false)
        view.adapter = Adapter_coffee(data) { jsonobject ->
            val intent = Intent(this@Home, coffee_detail::class.java).apply {
                putExtra("id", jsonobject.getInt("id"))
                putExtra("category", jsonobject.getString("category"))
                putExtra("price", jsonobject.getDouble("price"))
                putExtra("imagePath", jsonobject.getString("imagePath"))
            }
            startActivity(intent)}
    }
}