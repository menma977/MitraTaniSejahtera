package com.mitratanisejahtera.controller

import android.os.AsyncTask
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.Converter
import com.mitratanisejahtera.model.Url
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class UserController {
  class Get(private val token: String) : AsyncTask<Void, Void, JSONObject>() {
    override fun doInBackground(vararg params: Void?): JSONObject {
      try {
        val client = OkHttpClient()
        val request: Request =
          Request.Builder().url("${Url.get()}/user/show").addHeader("X-Requested-With", "XMLHttpRequest")
            .addHeader("Authorization", "Bearer $token").build()
        val response: Response = client.newCall(request).execute()
        val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
        val inputData: String = input.readLine()
        val convertJSON = JSONObject(inputData)
        input.close()
        return if (response.isSuccessful) {
          JSONObject().put("code", response.code).put("response", convertJSON["response"])
        } else {
          JSONObject().put("code", response.code).put("response", R.string.code_425)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        return JSONObject().put("code", 500).put("response", "Koneksi tidak setabil/Response Tidak di temukan")
      }
    }
  }

  class Post(private val token: String, private val body: HashMap<String, String>) :
      AsyncTask<Void, Void, JSONObject>() {
    override fun doInBackground(vararg params: Void?): JSONObject {
      return try {
        val client = OkHttpClient()
        val mediaType: MediaType = "application/x-www-form-urlencoded".toMediaType()
        val sendBody = Converter().map(body).toRequestBody(mediaType)
        val request: Request = Request.Builder().url("${Url.get()}/user/update/profile/data").post(sendBody)
          .addHeader("X-Requested-With", "XMLHttpRequest").addHeader("Authorization", "Bearer $token").build()
        val response: Response = client.newCall(request).execute()
        val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
        val inputData: String = input.readLine()
        val convertJSON = JSONObject(inputData)
        input.close()
        return if (response.isSuccessful) {
          JSONObject().put("code", response.code).put("response", convertJSON["response"])
            .put("password", convertJSON["password"])
        } else {
          JSONObject().put("code", response.code).put(
            "response", convertJSON.getJSONObject("errors").getJSONArray(
              convertJSON.getJSONObject("errors").names()[0].toString()
            )[0]
          )
        }
      } catch (e: Exception) {
        e.printStackTrace()
        JSONObject().put("code", 500).put("response", "Koneksi tidak setabil/Response Tidak di temukan")
      }
    }
  }

  class Balance(private val token: String) : AsyncTask<Void, Void, JSONObject>() {
    override fun doInBackground(vararg params: Void?): JSONObject {
      try {
        val client = OkHttpClient()
        val request: Request =
          Request.Builder().url("${Url.get()}/user/balance").addHeader("X-Requested-With", "XMLHttpRequest")
            .addHeader("Authorization", "Bearer $token").build()
        val response: Response = client.newCall(request).execute()
        val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
        val inputData: String = input.readLine()
        val convertJSON = JSONObject(inputData)
        input.close()
        return if (response.isSuccessful) {
          JSONObject().put("code", response.code).put("balance", convertJSON["balance"])
            .put("harvest", convertJSON["harvest"]).put("down_line", convertJSON["down_line"])
            .put("admin", convertJSON["admin"]).put("data", convertJSON["data"]).put("nominal", convertJSON["nominal"])
            .put("package", convertJSON["package"]).put("codePin", convertJSON["code"])
            .put("bannerTitle", convertJSON["bannerTitle"]).put("bannerDescription", convertJSON["bannerDescription"])
        } else {
          JSONObject().put("code", response.code).put("response", R.string.code_425)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        return JSONObject().put("code", 500).put("response", "Koneksi tidak setabil/Response Tidak di temukan")
      }
    }
  }

  class Register(private val token: String, private val body: HashMap<String, String>) :
      AsyncTask<Void, Void, JSONObject>() {
    override fun doInBackground(vararg params: Void?): JSONObject {
      return try {
        val client = OkHttpClient()
        val mediaType: MediaType = "application/x-www-form-urlencoded".toMediaType()
        val sendBody = Converter().map(body).toRequestBody(mediaType)
        val request: Request =
          Request.Builder().url("${Url.get()}/register").post(sendBody).addHeader("X-Requested-With", "XMLHttpRequest")
            .addHeader("Authorization", "Bearer $token").build()
        val response: Response = client.newCall(request).execute()
        val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
        val inputData: String = input.readLine()
        val convertJSON = JSONObject(inputData)
        input.close()
        return if (response.isSuccessful) {
          JSONObject().put("code", response.code).put("response", convertJSON["response"])
        } else {
          JSONObject().put("code", response.code).put(
            "response", convertJSON.getJSONObject("errors").getJSONArray(
              convertJSON.getJSONObject("errors").names()[0].toString()
            )[0]
          )
        }
      } catch (e: Exception) {
        e.printStackTrace()
        JSONObject().put("code", 500).put("response", "Koneksi tidak setabil/Response Tidak di temukan")
      }
    }
  }

  class SendEmail(private val body: HashMap<String, String>) :
      AsyncTask<Void, Void, JSONObject>() {
    override fun doInBackground(vararg params: Void?): JSONObject {
      return try {
        val client = OkHttpClient()
        val mediaType: MediaType = "application/x-www-form-urlencoded".toMediaType()
        val sendBody = Converter().map(body).toRequestBody(mediaType)
        val request: Request = Request.Builder().url("${Url.get()}/request/password").post(sendBody)
          .addHeader("X-Requested-With", "XMLHttpRequest").build()
        val response: Response = client.newCall(request).execute()
        val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
        val inputData: String = input.readLine()
        val convertJSON = JSONObject(inputData)
        input.close()
        return if (response.isSuccessful) {
          JSONObject().put("code", response.code).put("response", convertJSON["response"])
        } else {
          JSONObject().put("code", response.code).put(
            "response", convertJSON.getJSONObject("errors").getJSONArray(
              convertJSON.getJSONObject("errors").names()[0].toString()
            )[0]
          )
        }
      } catch (e: Exception) {
        e.printStackTrace()
        JSONObject().put("code", 500).put("response", "Koneksi tidak setabil/Response Tidak di temukan")
      }
    }
  }
}