package com.mitratanisejahtera.controller

import android.os.AsyncTask
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Converter
import com.mitratanisejahtera.model.Url
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginController(private var body: HashMap<String, String>) : AsyncTask<Void, Void, JSONObject>() {
  override fun doInBackground(vararg params: Void?): JSONObject {
    return try {
      val client = OkHttpClient()
      val mediaType: MediaType = "application/x-www-form-urlencoded".toMediaType()
      val sendBody = Converter().map(body).toRequestBody(mediaType)
      val request: Request =
        Request.Builder().url("${Url.get()}/login").post(sendBody).addHeader("X-Requested-With", "XMLHttpRequest")
          .build()
      val response: Response = client.newCall(request).execute()
      val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
      val inputData: String = input.readLine()
      val convertJSON = JSONObject(inputData)
      input.close()
      return if (response.isSuccessful) {
        JSONObject().put("code", response.code).put("response", convertJSON["response"])
          .put("username", convertJSON["username"]).put("status", convertJSON["status"])
          .put("role", convertJSON["role"]).put("img", convertJSON["img"])
      } else {
        JSONObject().put("code", response.code).put(
          "response",
          convertJSON.getJSONObject("errors").getJSONArray(convertJSON.getJSONObject("errors").names()[0].toString())[0]
        )
      }
    } catch (e: Exception) {
      e.printStackTrace()
      JSONObject().put("code", 500).put("response", "Koneksi tidak setabil/Response Tidak di temukan")
    }
  }
}