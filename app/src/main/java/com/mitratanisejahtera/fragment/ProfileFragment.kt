package com.mitratanisejahtera.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mitratanisejahtera.MainActivity
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.FragmentLoading
import com.mitratanisejahtera.controller.ImageGeneratorController
import com.mitratanisejahtera.controller.LogoutController
import com.mitratanisejahtera.controller.UserController
import com.mitratanisejahtera.model.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.concurrent.schedule

class ProfileFragment : Fragment() {
  private lateinit var user: User
  private lateinit var loadingFragment: FragmentLoading
  private lateinit var imageGeneratorController: ImageGeneratorController
  private lateinit var profileImage: CircleImageView
  private lateinit var username: TextView
  private lateinit var ktp: TextView
  private lateinit var name: TextView
  private lateinit var email: TextView
  private lateinit var address: TextView
  private lateinit var phone: TextView
  private lateinit var bank: TextView
  private lateinit var pinBank: TextView

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_profile, container, false)
    user = User(context)
    loadingFragment = FragmentLoading(this.requireActivity())
    loadingFragment.openDialog()
    username = view.findViewById(R.id.usernameTextView)
    profileImage = view.findViewById(R.id.profileImageView)
    ktp = view.findViewById(R.id.ktpTextView)
    name = view.findViewById(R.id.nameTextView)
    email = view.findViewById(R.id.emailTextView)
    address = view.findViewById(R.id.addressTextView)
    phone = view.findViewById(R.id.phoneTextView)
    bank = view.findViewById(R.id.bankTextView)
    pinBank = view.findViewById(R.id.pinBankTextView)
    getUser(user.token)
    return view
  }

  private fun getUser(token: String) {
    Timer().schedule(100) {
      val response = UserController.Get(token).execute().get()
      if (response["code"] == 200) {
        if (response.getJSONObject("response")["status"] == 0) {
          LogoutController(token).execute().get()
          activity?.runOnUiThread {
            val goTo = Intent(context, MainActivity::class.java)
            User(context).clear()
            loadingFragment.closeDialog()
            startActivity(goTo)
          }
        }
        activity?.runOnUiThread {
          changeProfileImage(response.getJSONObject("response")["image"].toString())
          username.text = response.getJSONObject("response")["username"].toString()
          if (response.getJSONObject("response")["status"] == 1) {
            val ktpIdWithTextMassage =
              response.getJSONObject("response")["id_identity_card"].toString() + "-Anda belum upload KTP"
            ktp.text = ktpIdWithTextMassage
          } else {
            ktp.text = response.getJSONObject("response")["id_identity_card"].toString()
          }
          name.text = response.getJSONObject("response")["name"].toString()
          email.text = response.getJSONObject("response")["email"].toString()
          address.text = response.getJSONObject("response")["address"].toString()
          phone.text = response.getJSONObject("response")["phone"].toString()
          bank.text = response.getJSONObject("response")["bank"].toString()
          pinBank.text = response.getJSONObject("response")["pin_bank"].toString()
          loadingFragment.closeDialog()
        }
      } else {
        activity?.runOnUiThread {
          val goTo = Intent(activity?.applicationContext, MainActivity::class.java)
          user.clear()
          activity?.finish()
          loadingFragment.closeDialog()
          startActivity(goTo)
        }
      }
    }
  }

  private fun changeProfileImage(urlImage: String) {
    if (urlImage.isNotEmpty()) {
      imageGeneratorController = ImageGeneratorController(urlImage)
      val gitBitmap = imageGeneratorController.execute().get()
      profileImage.setImageBitmap(gitBitmap)
    }
  }
}
