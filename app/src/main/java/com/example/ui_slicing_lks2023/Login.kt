package com.example.ui_slicing_lks2023

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.ezem_green)
        setContentView(R.layout.activity_main)
        val saherePref = getSharedPreferences("User_Token", MODE_PRIVATE)
        val token = saherePref.getString("Token", null)
        //getSharedPreferences("User_Token", MODE_PRIVATE).edit().remove("Token").apply()
        if (token != null){
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
        val usrnm = findViewById<EditText>(R.id.et_usrnme)
        val passwrd = findViewById<EditText>(R.id.usr_pw)

        val register = findViewById<TextView>(R.id.register_account).setOnClickListener{
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }
        val to_home = findViewById<Button>(R.id.button_login).setOnClickListener{
            if (usrnm.text.isNotEmpty() && passwrd.text.isNotEmpty()){
                login(usrnm.text.toString(), passwrd.text.toString()) { suc, res ->
                    CoroutineScope(Dispatchers.Main).launch{
                        if (suc){
                            with(saherePref.edit()){
                                putString("Token", res)
                                apply()}
                            val intent = Intent(this@Login, Home::class.java)
                            intent.putExtra("Token", res)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@Login, "Login Gagal!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    fun login(Username : String, Password : String, callback : (Boolean, String?) -> Unit){
        CoroutineScope(Dispatchers.IO).launch{
            val connection = URL("http://10.0.2.2:5000/api/auth").openConnection() as HttpURLConnection
            connection.setRequestProperty("Content-Type", "application/json")
            connection.requestMethod = "POST"
            connection.doOutput = true
            val request = """
                {
                    "username" : "$Username",
                    "password" : "$Password"
                }
            """
            connection.outputStream.use { os ->
                os.write(request.toByteArray())
            }
            val res_code = connection.responseCode
            if (res_code in 200..299){
                val res = InputStreamReader(connection.inputStream).use { r ->
                    r.readText()
                }
                if (res.isNotEmpty()){
                    callback(true, res)
                } else {
                    callback(false, null)
                }
            } else{
                callback(false, null)
            }
        }
    }
}