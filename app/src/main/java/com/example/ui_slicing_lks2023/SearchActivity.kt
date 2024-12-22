package com.example.ui_slicing_lks2023

import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

       val et_search = findViewById<EditText>(R.id.search_et)
        et_search.doOnTextChanged { editable, _,_,_ ->
            search(editable.toString())
        }
        findViewById<ImageView>(R.id.back_home_button).setOnClickListener{
            val intet = Intent(this@SearchActivity, Home::class.java)
            intet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intet)
        }
    }

    fun search(keywords : String){
        try {
            CoroutineScope(Dispatchers.IO).launch{
                val koneksi = URL("http://10.0.2.2:5000/api/coffee?search=$keywords").openStream().bufferedReader().readText()
                val res_json = JSONArray(koneksi)
                withContext(Dispatchers.Main){
                    val rc = findViewById<RecyclerView>(R.id.rc_search)
                    rc.layoutManager = LinearLayoutManager(this@SearchActivity)
                    rc.adapter = search_adapter(res_json){ json ->
                        val intent = Intent(this@SearchActivity, coffee_detail::class.java).apply {
                            putExtra("id", json.getInt("id"))
                            putExtra("category", json.getString("category"))
                            putExtra("price", json.getDouble("price"))
                            putExtra("imagePath", json.getString("imagePath"))
                        }
                        startActivity(intent)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Eror Bos", "Erronya Gini ${e.message}")
        }
    }
}