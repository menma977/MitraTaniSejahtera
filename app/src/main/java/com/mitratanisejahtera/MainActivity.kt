package com.mitratanisejahtera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mitratanisejahtera.controller.LoginController
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.AuthController
import com.mitratanisejahtera.model.User
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
  private lateinit var username: EditText
  private lateinit var password: EditText
  private lateinit var login: Button
  private lateinit var forgotPassword: Button
  private lateinit var goTo: Intent
  private lateinit var loading: Loading

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    username = findViewById(R.id.username)
    password = findViewById(R.id.password)
    login = findViewById(R.id.login)
    forgotPassword = findViewById(R.id.ForgotPassword)

    doRequestPermission()

    loading = Loading(this)
    loading.openDialog()

    forgotPassword.setOnClickListener {
      goTo = Intent(applicationContext, ForgotPasswordActivity::class.java)
      startActivity(goTo)
    }

    Timer().schedule(100) {
      if (User(applicationContext).token.isNotEmpty()) {
        val response = AuthController(User(applicationContext).token).execute().get()
        if (response["code"] == 200) {
          goTo = Intent(applicationContext, NavigationActivity::class.java)
          finishAndRemoveTask()
          loading.closeDialog()
          startActivity(goTo)
        }
      }

      loading.closeDialog()
    }

    login.setOnClickListener {
      loading.openDialog()
      Timer().schedule(100) {
        val body = HashMap<String, String>()
        body["username"] = username.text.toString()
        body["password"] = password.text.toString()
        val response = LoginController(body).execute().get()
        runOnUiThread {
          if (response["code"] == 200) {
            val json = JSONObject()
            json.put("token", response["response"].toString())
            json.put("username", response["username"].toString())
            json.put("image", response["img"].toString())
            json.put("status", response["status"].toString().toInt())
            json.put("type", response["role"].toString())
            User(applicationContext).set(json.toString())
            goTo = Intent(applicationContext, NavigationActivity::class.java)
            loading.closeDialog()
            finishAndRemoveTask()
            startActivity(goTo)
          } else {
            loading.closeDialog()
            try {
              Toast.makeText(
                applicationContext, getString(response["response"].toString().toInt()), Toast.LENGTH_SHORT
              ).show()
            } catch (e: Exception) {
              Toast.makeText(applicationContext, response["response"].toString(), Toast.LENGTH_SHORT).show()
            }
          }
        }
      }
    }
  }

  private fun doRequestPermission() {
    if (ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
      ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
      ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this, Manifest.permission.READ_EXTERNAL_STORAGE
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions(
          arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
          ), 100
        )
      }
    }
  }
}
