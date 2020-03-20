package com.mitratanisejahtera.view.barcode

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.TreeController
import com.mitratanisejahtera.model.User
import java.util.*
import kotlin.concurrent.schedule

class ResultQrActivity : AppCompatActivity() {
  private lateinit var loading: Loading
  private lateinit var user: User
  private lateinit var username: TextView
  private lateinit var qrCode: TextView
  private lateinit var getDate: TextView
  private lateinit var location: Button
  private lateinit var goTo: Intent
  private lateinit var longitude: String
  private lateinit var latitude: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_result_qr)

    username = findViewById(R.id.usernameTextView)
    qrCode = findViewById(R.id.qrCodeTextView)
    getDate = findViewById(R.id.getDateTextView)
    location = findViewById(R.id.buttonLocation)

    loading = Loading(this)
    loading.openDialog()
    user = User(this)
    val qrResponse = intent.getSerializableExtra("qr").toString()

    Timer().schedule(100) {
      val body = HashMap<String, String>()
      body["qr"] = qrResponse
      val response = TreeController.Get(user.token, body).execute().get()
      if (response["code"] == 200) {
        runOnUiThread {
          username.text = response.getJSONObject("response")["user"].toString()
          qrCode.text = response.getJSONObject("response")["code"].toString()
          getDate.text = response.getJSONObject("response")["start"].toString()
          longitude = response.getJSONObject("response")["x_fild"].toString()
          latitude = response.getJSONObject("response")["y_fild"].toString()
          if (longitude.isEmpty() && latitude.isEmpty()) {
            location.isEnabled = false
          }
          loading.closeDialog()
        }
      } else {
        runOnUiThread {
          loading.closeDialog()
          Toast.makeText(applicationContext, response["response"].toString(), Toast.LENGTH_SHORT).show()
        }
      }
    }

    location.setOnClickListener {
      val urlMap = "geo:$longitude,$latitude?q=$longitude,$latitude"
      goTo = Intent(Intent.ACTION_VIEW, Uri.parse(urlMap))
      goTo.setPackage("com.google.android.apps.maps")
      startActivity(goTo)
    }
  }
}
