package com.example.ui_slicing_lks2023

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.webkit.JsResult
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class Cart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val rc_chart = findViewById<RecyclerView>(R.id.rc_cart_coffee)
        val checkout = findViewById<Button>(R.id.btn_checkOut)
        val token = getSharedPreferences("User_Token", MODE_PRIVATE).getString("Token", null)
        if ( token == null){
            Toast.makeText(this@Cart, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@Cart, Login::class.java))
        }
        val sharepref = getSharedPreferences("Cart_Coffe", MODE_PRIVATE)
        val items = JSONArray(sharepref.getString("cart_items", "[]"))
        rc_chart.layoutManager = LinearLayoutManager(this@Cart)
        rc_chart.adapter = chart_adapter(items, sharepref)
        val back = findViewById<ImageView>(R.id.back_button).setOnClickListener{
            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        checkout.setOnClickListener{
            if (items.length() == 0){
                Toast.makeText(this@Cart, "Cart is empty. Add items before checkout!", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch{
                    val conn = URL("http://10.0.2.2:5000/api/checkout").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.setRequestProperty("Authorization", "Bearer $token")
                    conn.doOutput = true

                    val req = JSONArray()
                    for ( i in 0 until items.length()){
                        val item = items.getJSONObject(i)
                        val reqObj = JSONObject()
                        reqObj.put("coffeeId", item.getInt("id"))
                        reqObj.put("size", item.getString("size"))
                        reqObj.put("qty", item.getInt("qty"))
                        req.put(reqObj)
                    }
                    conn.outputStream.write(req.toString().toByteArray())
                    conn.outputStream.flush()
                    conn.outputStream.close()

                    val res_code = conn.responseCode
                    if (res_code == HttpURLConnection.HTTP_OK){
                        withContext(Dispatchers.Main){
                            getSharedPreferences("Cart_Coffe", MODE_PRIVATE).edit().remove("cart_items").apply()
                            val dataJson = JSONArray()
                            rc_chart.adapter = chart_adapter(dataJson, sharepref)
                            rc_chart.adapter?.notifyDataSetChanged()
                            Toast.makeText(this@Cart, "Checkout successful and Transaction created!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Cart, "Transaction failed! Response code: $res_code", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}