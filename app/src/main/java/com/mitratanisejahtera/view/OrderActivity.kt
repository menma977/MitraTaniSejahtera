package com.mitratanisejahtera.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.TreeController
import com.mitratanisejahtera.controller.UploadTransferController
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
  private lateinit var inputTree: EditText
  private lateinit var submit: Button
  private lateinit var contentData: LinearLayout
  private lateinit var agentValidation: CheckBox
  private val localeID = Locale("in", "ID")
  private val numberFormatToIDR = NumberFormat.getCurrencyInstance(localeID)
  private lateinit var indexCode: String

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
    inputTree = findViewById(R.id.totalEditText)
    submit = findViewById(R.id.submitButton)
    contentData = findViewById(R.id.contentDataLinearLayout)
    agentValidation = findViewById(R.id.agentCheckBox)
    agentValidation.visibility = CheckBox.INVISIBLE
    getDataUser()

    inputTree.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(text: Editable?) {
        if (text.toString().isNotEmpty()) {
          if (User(applicationContext).type.toInt() != 1 && User(applicationContext).type.toInt() != 4) {
            if (text.toString().toInt() >= 2) {
              agentValidation.visibility = CheckBox.VISIBLE
            } else {
              agentValidation.visibility = CheckBox.INVISIBLE
            }
          }
        }
      }

      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
      }

      override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
      }
    })

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
      body["total"] = inputTree.text.toString()
      if (agentValidation.isChecked) {
        body["agentMode"] = "1"
      } else {
        body["agentMode"] = "0"
      }
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
      linearLayout.setOnClickListener {
        pickImageFromGallery()
        indexCode = dataJsonArray.getJSONObject(i)["id"].toString()
      }
      linearLayout.orientation = LinearLayout.VERTICAL
      linearLayout.elevation = 50F
      linearLayout.layoutParams = mainLinear
      val titleValue = "Total Request Pohon : " + dataJsonArray.getJSONObject(i)["total"]
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
      if (dataJsonArray.getJSONObject(i)["status"].toString().toInt() == 99) {
        count += 300000
      }
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

  private fun pickImageFromGallery() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    startActivityForResult(intent, 1000)
  }

  @SuppressLint("Recycle")
  private fun getRealPathFromImageURI(contentUri: Uri?): String {
    val data: Array<String> = Array(100) { MediaStore.Images.Media.DATA }
    val cursor = this.contentResolver.query(contentUri, data, null, null, null)!!
    val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    return cursor.getString(columnIndex)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      1001 -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          pickImageFromGallery()
        } else {
          Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  @SuppressLint("MissingSuperCall")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
      loading.openDialog()
      Timer().schedule(100) {
        val response =
          UploadTransferController(user.token, indexCode, getRealPathFromImageURI(data?.data)).execute().get()
        runOnUiThread {
          if (response["code"] == 200) {
            Toast.makeText(applicationContext, response["data"].toString(), Toast.LENGTH_LONG).show()
          } else {
            Toast.makeText(applicationContext, "Ada maslah saat mengirim gambar/coba login kembali untuk mengupload ulang", Toast.LENGTH_LONG).show()
          }
          loading.closeDialog()
        }
      }
    }
  }
}
