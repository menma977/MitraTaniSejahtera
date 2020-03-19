package com.mitratanisejahtera.controller

import android.os.AsyncTask
import com.mitratanisejahtera.R
import com.mitratanisejahtera.model.Url
import okhttp3.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class AuthController(private var token: String) : AsyncTask<Void, Void, JSONObject>() {
  override fun doInBackground(vararg params: Void?): JSONObject {
    try {
      val client = OkHttpClient()
      val request: Request =
        Request.Builder().url("${Url.get()}/verification").addHeader("X-Requested-With", "XMLHttpRequest")
          .addHeader("Authorization", "Bearer $token").build()
      val response: Response = client.newCall(request).execute()
      val input = BufferedReader(InputStreamReader(response.body?.byteStream()))
      val inputData: String = input.readLine()
      val convertJSON = JSONObject(inputData)
      input.close()
      return if (response.isSuccessful) {
        JSONObject().put("code", response.code).put("response", convertJSON["response"])
      } else {
        JSONObject().put("code", response.code).put("response", convertJSON["message"])
      }
    } catch (e: Exception) {
      return JSONObject().put("code", 500).put("response", R.string.code_500)
    }
  }
}