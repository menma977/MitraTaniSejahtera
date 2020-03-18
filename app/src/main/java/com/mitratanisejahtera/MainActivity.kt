package com.mitratanisejahtera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
  private lateinit var username: EditText
  private lateinit var password: EditText
  private lateinit var login: Button
  private lateinit var goTo: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    username = findViewById(R.id.username)
    password = findViewById(R.id.password)
    login = findViewById(R.id.login)

    login.setOnClickListener {
      goTo = Intent(this, NavigationActivity::class.java)
      finishAndRemoveTask()
      startActivity(goTo)
    }
  }
}
