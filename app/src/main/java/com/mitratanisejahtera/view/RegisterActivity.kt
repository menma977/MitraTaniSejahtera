package com.mitratanisejahtera.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.mitratanisejahtera.NavigationActivity
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.UserController
import com.mitratanisejahtera.model.User
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

class RegisterActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var name: EditText
  private lateinit var username: EditText
  private lateinit var email: EditText
  private lateinit var password: EditText
  private lateinit var validationPassword: EditText
  private lateinit var bank: EditText
  private lateinit var pinBank: EditText
  private lateinit var ktpNumber: EditText
  private lateinit var phone: EditText
  private lateinit var address: EditText
  private lateinit var register: Button
  private lateinit var response: JSONObject
  private lateinit var goTo: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_register)

    user = User(this)
    loading = Loading(this)
    loading.openDialog()

    name = findViewById(R.id.nameEditText)
    username = findViewById(R.id.usernameEditText)
    email = findViewById(R.id.emailEditText)
    password = findViewById(R.id.passwordEditText)
    validationPassword = findViewById(R.id.validationPasswordEditText)
    bank = findViewById(R.id.bankEditText)
    pinBank = findViewById(R.id.pinBankEditText)
    ktpNumber = findViewById(R.id.accountIdEditText)
    phone = findViewById(R.id.phoneEditText)
    address = findViewById(R.id.addressEditText)
    register = findViewById(R.id.registerButton)

    register.setOnClickListener {
      loading.openDialog()
      Timer().schedule(100) {
        val massage: String
        val body = java.util.HashMap<String, String>()
        body["name"] = name.text.toString()
        body["username"] = username.text.toString()
        body["email"] = email.text.toString()
        body["password"] = password.text.toString()
        body["c_password"] = validationPassword.text.toString()
        body["id_identity_card"] = ktpNumber.text.toString()
        body["phone"] = phone.text.toString()
        body["bank"] = bank.text.toString()
        body["pin_bank"] = pinBank.text.toString()
        body["address"] = address.text.toString()
        response = UserController.Register(user.token, body).execute().get()
        if (response["code"] == 200) {
          runOnUiThread {
            loading.closeDialog()
            goTo = Intent(applicationContext, NavigationActivity::class.java)
            startActivity(goTo)
          }
        } else {
          when {
            response["response"].toString() in "name" -> {
              massage = response["response"].toString().replace(response["response"].toString(), "Nama")
            }
            response["response"].toString() in "username" -> {
              massage = response["response"].toString().replace(response["response"].toString(), "Username")
            }
            response["response"].toString() in "email" -> {
              massage = response["response"].toString().replace(response["response"].toString(), "Email")
            }
            response["response"].toString() in "password" -> {
              massage = response["response"].toString().replace(response["response"].toString(), "Password")
            }
            response["response"].toString() in "c_password" -> {
              massage = response["response"].toString().replace(response["response"].toString(), "Konfrimasi Password")
            }
            response["response"].toString() in "pin_bank" -> {
              massage = response["response"].toString().replace(response["response"].toString(), "Pin Bank")
            }
            response["response"].toString() in "ktp_number" -> {
              massage = response["response"].toString().replace(response["response"].toString(), "Nomor KTP")
            }
            response["response"].toString() in "phone" -> {
              massage = response["response"].toString().replace(response["response"].toString(), "Nomor Telfon")
            }
            response["response"].toString() in "address" -> {
              massage =
                response["response"].toString().replace(response["response"].toString(), "Keterangan Alamat Lengkap")
            }
            else -> {
              massage = response["response"].toString()
            }
          }
          runOnUiThread {
            loading.closeDialog()
            Toast.makeText(
              applicationContext, massage, Toast.LENGTH_SHORT
            ).show()
          }
        }
      }
    }
    loading.closeDialog()
  }
}
