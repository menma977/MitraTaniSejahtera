package com.mitratanisejahtera.view

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.TreeController
import com.mitratanisejahtera.controller.UserController
import com.mitratanisejahtera.model.User
import org.json.JSONArray
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class OrderActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var bankToTransfer: LinearLayout
  private lateinit var name: TextView
  private lateinit var bank: TextView
  private lateinit var pinBank: TextView
  private lateinit var inputStup: EditText
  private lateinit var submit: Button
  private lateinit var contentData: LinearLayout
  private val localeID = Locale("in", "ID")
  private val numberFormatToIDR = NumberFormat.getCurrencyInstance(localeID)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_order)

    loading = Loading(this)
    loading.openDialog()
    user = User(this)
    bankToTransfer = findViewById(R.id.bankToTransferLinearLayout)
    name = findViewById(R.id.nameTextView)
    bank = findViewById(R.id.bankTextView)
    pinBank = findViewById(R.id.pinBankTextView)
    inputStup = findViewById(R.id.totalEditText)
    submit = findViewById(R.id.submitButton)
    contentData = findViewById(R.id.contentDataLinearLayout)
    getDataUser()

    submit.setOnClickListener {
      loading.openDialog()
      order()
    }
  }

  private fun getDataUser() {
    Timer().schedule(100) {
      val response = UserController.Balance(user.token).execute().get()
      if (response["code"] == 200) {
        runOnUiThread {
          val nominal = response["nominal"].toString().toInt()
          val dataArrayTotalRequest = response.getJSONArray("data")
          if (dataArrayTotalRequest.length() > 0) {
            bankToTransfer.visibility = LinearLayout.VISIBLE
            name.text = response.getJSONObject("admin")["name"].toString()
            bank.text = response.getJSONObject("admin")["bank"].toString()
            pinBank.text = response.getJSONObject("admin")["pin_bank"].toString()
            setViewInLinearLayout(dataArrayTotalRequest, nominal)
          } else {
            bankToTransfer.visibility = LinearLayout.GONE
          }
          loading.closeDialog()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, getString(R.string.code_500), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          finishAndRemoveTask()
        }
      }
    }
  }

  private fun order() {
    Timer().schedule(100) {
      val body = HashMap<String, String>()
      body["total"] = inputStup.text.toString()
      val response = TreeController.Post(user.token, body).execute().get()
      if (response["code"] == 200) {
        runOnUiThread {
          Toast.makeText(applicationContext, response["response"].toString(), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          finishAndRemoveTask()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, response["response"].toString(), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
        }
      }
    }
  }

  private fun setViewInLinearLayout(dataJsonArray: JSONArray, nominal: Int) {
    val mainLinear = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    mainLinear.setMargins(0, 20, 0, 20)
    val titleLinear = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    titleLinear.setMargins(20, 10, 20, 5)
    val valueLinear = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    valueLinear.setMargins(45, 5, 25, 20)
    for (i in 0 until dataJsonArray.length()) {
      val linearLayout = LinearLayout(this)
      linearLayout.setBackgroundResource(R.drawable.card_secondary)
      linearLayout.orientation = LinearLayout.VERTICAL
      linearLayout.elevation = 50F
      linearLayout.layoutParams = mainLinear
      val titleValue = "Total Request Stup : " + dataJsonArray.getJSONObject(i)["total"]
      val title = TextView(this)
      title.text = titleValue
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        title.setTextColor(getColor(R.color.textPrimary))
      } else {
        title.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
      }
      title.layoutParams = titleLinear
      linearLayout.addView(title)
      var count = nominal * dataJsonArray.getJSONObject(i)["total"].toString().toInt()
      count += dataJsonArray.getJSONObject(i)["code"].toString().toInt()
      val nominalDecimalFormat = numberFormatToIDR.format(count).toString()
      val value = TextView(this)
      value.text = nominalDecimalFormat
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        value.setTextColor(getColor(R.color.textPrimary))
      } else {
        value.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
      }
      value.layoutParams = valueLinear
      linearLayout.addView(value)

      contentData.addView(linearLayout)
    }
  }
}
