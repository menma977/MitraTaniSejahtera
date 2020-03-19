package com.mitratanisejahtera.view.barcode

import android.os.Bundle
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
  private lateinit var location: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_result_qr)

    username = findViewById(R.id.usernameTextView)
    qrCode = findViewById(R.id.qrCodeTextView)
    getDate = findViewById(R.id.getDateTextView)
    location = findViewById(R.id.locationTextView)

    loading = Loading(this)
    loading.openDialog()
    user = User(this)
    val qrResponse = intent.getSerializableExtra("qr").toString()

    Timer().schedule(100) {
      val body = HashMap<String, String>()
      body["qr"] = qrResponse
      val responseBee = TreeController.Get(user.token, body).execute().get()
      if (responseBee["code"] == 200) {
        runOnUiThread {
          username.text = responseBee.getJSONObject("response")["user"].toString()
          qrCode.text = responseBee.getJSONObject("response")["code"].toString()
          getDate.text = responseBee.getJSONObject("response")["start"].toString()
          location.text = responseBee.getJSONObject("response")["location"].toString()
          loading.closeDialog()
        }
      } else {
        runOnUiThread {
          loading.closeDialog()
          Toast.makeText(applicationContext, responseBee["response"].toString(), Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
}
