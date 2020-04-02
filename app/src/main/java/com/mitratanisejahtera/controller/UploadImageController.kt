package com.mitratanisejahtera.controller

import android.os.AsyncTask
import com.mitratanisejahtera.R
import com.mitratanisejahtera.model.Url
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

class UploadImageController(private val file: String, private var name: String, private val token: String) :
    AsyncTask<Void, Void, JSONObject>() {
  override fun doInBackground(vararg params: Void?): JSONObject {
    try {
      val client = OkHttpClient()
      val sendBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart(name, file, File(file).asRequestBody("application/octet-stream".toMediaTypeOrNull())).build()
      val request: Request = Request.Builder().url("${Url.get()}/user/update/profile/image").method("POST", sendBody)
        .addHeader("X-Requested-With", "XMLHttpRequest").addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("Authorization", "Bearer $token").build()
      val response: Response = client.newCall(request).execute()
      val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
      val inputData: String = input.readLine()
      val convertJSON = JSONObject(inputData)
      input.close()
      return if (response.isSuccessful) {
        JSONObject().put("code", response.code).put("data", convertJSON["response"])
      } else {
        JSONObject().put("code", response.code).put(
          "data", convertJSON.getJSONObject("errors").getJSONArray(
            convertJSON.getJSONObject("errors").names()[0].toString()
          )[0]
        )
        JSONObject().put("code", response.code).put("data", convertJSON["errors"].toString())
      }
    } catch (e: Exception) {
      e.printStackTrace()
      return JSONObject().put("code", 500).put("data", R.string.code_500)
    }
  }
}