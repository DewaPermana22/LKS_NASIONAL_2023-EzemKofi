package com.example.ui_slicing_lks2023

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)
        val login = findViewById<TextView>(R.id.to_login).setOnClickListener{
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            val username : EditText = findViewById(R.id.username_new)
            val fullname : EditText = findViewById(R.id.fullname_new)
            val email : EditText = findViewById(R.id.email_new)
            val pasword : EditText = findViewById(R.id.password_new)
            val conf_pasword : EditText = findViewById(R.id.confirm_password_new)
            val daftar : Button = findViewById(R.id.sign_up)
            daftar.setOnClickListener{
                    if (pasword.text.toString() == conf_pasword.text.toString()){
                        register(username.text.toString(),
                            fullname.text.toString(),
                            email.text.toString(),
                            pasword.text.toString()
                        ){
                            rg ->
                            if (rg == true){
                                startActivity(Intent(this@Register, Login::class.java))
                                Toast.makeText(this@Register, "Berhasil daftar akun!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@Register, "Gagal mendaftar akun! Coba lagi.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@Register, "Password Tidak Cocok", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    fun register(Username: String, Fullname: String, Email: String, Password: String, oke: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://10.0.2.2:5000/api/register")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                }

                val request = JSONObject().apply {
                    put("username", Username)
                    put("fullname", Fullname)
                    put("email", Email)
                    put("password", Password)
                }

                connection.outputStream.write(request.toString().toByteArray())
                val resCode = connection.responseCode

                if (resCode in 200..299) {
                    val responseBody = connection.inputStream.use { it.reader().readText() }
                    CoroutineScope(Dispatchers.Main).launch { oke(true) }
                } else {
                    val errorBody = connection.errorStream?.use { it.reader().readText() } ?: "No error body"
                    CoroutineScope(Dispatchers.Main).launch { oke(false) }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { oke(false) }
            }
        }
    }
}
