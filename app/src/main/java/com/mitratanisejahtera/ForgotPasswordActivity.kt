package com.mitratanisejahtera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.UserController
import java.util.*
import kotlin.concurrent.schedule

class ForgotPasswordActivity : AppCompatActivity() {

  private lateinit var email: EditText
  private lateinit var sendEmail: Button
  private lateinit var loading: Loading

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_forgot_password)

    loading = Loading(this)
    loading.openDialog()

    email = findViewById(R.id.emailEditText)
    sendEmail = findViewById(R.id.sendEmailButton)

    sendEmail.setOnClickListener {
      loading.openDialog()
      Timer().schedule(100) {
        val body = HashMap<String, String>()
        body["email"] = email.text.toString()
        val response = UserController.SendEmail(body).execute().get()
        runOnUiThread {
          if (response["code"] == 200) {
            Toast.makeText(applicationContext, response["response"].toString(), Toast.LENGTH_SHORT).show()
            loading.closeDialog()
            finishAndRemoveTask()
          } else {
            Toast.makeText(applicationContext, response["response"].toString(), Toast.LENGTH_SHORT).show()
            loading.closeDialog()
          }
        }
      }
    }

    loading.closeDialog()
  }
}
