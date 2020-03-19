package com.mitratanisejahtera.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Loading
import com.mitratanisejahtera.controller.UserController
import com.mitratanisejahtera.controller.WithdrawController
import com.mitratanisejahtera.model.User
import java.util.*
import kotlin.concurrent.schedule

class WithdrawActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var balance: TextView
  private lateinit var inputBalance: EditText
  private lateinit var submit: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_withdraw)

    loading = Loading(this)
    loading.openDialog()
    user = User(this)
    balance = findViewById(R.id.balanceTextView)
    inputBalance = findViewById(R.id.balanceEditText)
    submit = findViewById(R.id.submitButton)

    validateWithdraw()

    submit.setOnClickListener {
      loading.openDialog()
      withdrawRequest()
    }
  }

  private fun withdrawRequest() {
    Timer().schedule(100) {
      val body = HashMap<String, String>()
      body["nominal"] = inputBalance.text.toString()
      val response = WithdrawController.Post(user.token, body).execute().get()
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

  private fun validateWithdraw() {
    Timer().schedule(100) {
      val response = WithdrawController.Validate(user.token).execute().get()
      if (response["code"] == 200) {
        if (response["response"] != 0) {
          runOnUiThread {
            Toast.makeText(applicationContext, getString(R.string.withdraw_limit), Toast.LENGTH_SHORT).show()
            loading.closeDialog()
            finishAndRemoveTask()
          }
        } else {
          runOnUiThread {
            getBalance()
          }
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, response["response"].toString(), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          finishAndRemoveTask()
        }
      }
    }
  }

  private fun getBalance() {
    Timer().schedule(100) {
      val response = UserController.Balance(user.token).execute().get()
      if (response["code"] == 200) {
        runOnUiThread {
          balance.text = response["balance"].toString()
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
}
