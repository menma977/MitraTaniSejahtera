package com.mitratanisejahtera.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mitratanisejahtera.MainActivity
import com.mitratanisejahtera.R
import com.mitratanisejahtera.config.FragmentLoading
import com.mitratanisejahtera.controller.ImageGeneratorController
import com.mitratanisejahtera.controller.LogoutController
import com.mitratanisejahtera.controller.UserController
import com.mitratanisejahtera.model.User
import com.mitratanisejahtera.view.*
import com.mitratanisejahtera.view.barcode.QRActivity
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class HomeFragment : Fragment() {
  private lateinit var user: User
  private lateinit var loadingFragment: FragmentLoading
  private lateinit var imageGeneratorController: ImageGeneratorController
  private lateinit var profileImage: CircleImageView
  private lateinit var name: TextView
  private lateinit var typeUser: TextView
  private lateinit var email: TextView
  private lateinit var balanceHarvest: TextView
  private lateinit var balance: TextView
  private lateinit var downLine: TextView
  private lateinit var totalPackage: TextView
  private lateinit var textPin: TextView
  private lateinit var ledger: ImageButton
  private lateinit var ledgerHarvest: ImageButton
  private lateinit var binary: ImageButton
  private lateinit var qr: ImageButton
  private lateinit var order: ImageButton
  private lateinit var addUser: ImageButton
  private lateinit var withdraw: ImageButton
  private lateinit var uploadKTP: ImageButton
  private lateinit var editProfile: ImageButton
  private lateinit var detailGallery: ImageButton
  private lateinit var addUserLinear: LinearLayout
  private lateinit var pinLinear: LinearLayout
  private lateinit var balanceLinear: LinearLayout
  private lateinit var groupLinear: LinearLayout
  private lateinit var pin: ImageButton
  private lateinit var goTo: Intent

  private lateinit var banner: LinearLayout
  private lateinit var title: TextView
  private lateinit var description: TextView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_home, container, false)

    user = User(context)
    loadingFragment = FragmentLoading(this.requireActivity())
    loadingFragment.openDialog()

    profileImage = view.findViewById(R.id.profileImage)
    name = view.findViewById(R.id.name)
    typeUser = view.findViewById(R.id.typeUser)
    email = view.findViewById(R.id.email)
    balance = view.findViewById(R.id.balanceTextView)
    balanceHarvest = view.findViewById(R.id.balanceHarvestTextView)
    downLine = view.findViewById(R.id.downLine)
    totalPackage = view.findViewById(R.id.totalPackageTextView)
    textPin = view.findViewById(R.id.pinTextView)

    ledger = view.findViewById(R.id.buttonLedger)
    ledgerHarvest = view.findViewById(R.id.buttonLedgerHarvest)
    binary = view.findViewById(R.id.buttonShowDownLine)

    qr = view.findViewById(R.id.buttonQR)
    order = view.findViewById(R.id.buttonOrder)
    addUser = view.findViewById(R.id.buttonAddUser)
    withdraw = view.findViewById(R.id.buttonWithdraw)
    uploadKTP = view.findViewById(R.id.buttonUploadKTP)
    editProfile = view.findViewById(R.id.buttonEditProfile)
    detailGallery = view.findViewById(R.id.buttonTotalPackage)
    pin = view.findViewById(R.id.buttonPin)

    pinLinear = view.findViewById(R.id.pinLinearLayout)
    balanceLinear = view.findViewById(R.id.balanceLinearLayout)
    groupLinear = view.findViewById(R.id.groupLinearLayout)
    addUserLinear = view.findViewById(R.id.addUserLinearLayout)

    banner = view.findViewById(R.id.banner)
    title = view.findViewById(R.id.titleBanner)
    description = view.findViewById(R.id.descriptionBanner)

    getUser(user.token)

    ledger.setOnClickListener {
      goTo = Intent(view.context, LedgerWebViewActivity::class.java).putExtra("type", 1)
      startActivity(goTo)
    }

    ledgerHarvest.setOnClickListener {
      goTo = Intent(view.context, LedgerWebViewActivity::class.java).putExtra("type", 2)
      startActivity(goTo)
    }

    binary.setOnClickListener {
      goTo = Intent(view.context, BinaryWebViewActivity::class.java)
      startActivity(goTo)
    }

    qr.setOnClickListener {
      goTo = Intent(view.context, QRActivity::class.java)
      startActivity(goTo)
    }

    order.setOnClickListener {
      goTo = Intent(view.context, OrderActivity::class.java)
      startActivity(goTo)
    }

    addUser.setOnClickListener {
      goTo = Intent(view.context, RegisterActivity::class.java)
      startActivity(goTo)
    }

    withdraw.setOnClickListener {
      goTo = Intent(view.context, WithdrawActivity::class.java)
      startActivity(goTo)
    }

    uploadKTP.setOnClickListener {
      if (user.status != 2) {
        goTo = Intent(view.context, ImageActivity::class.java)
        startActivity(goTo)
      } else {
        Toast.makeText(view.context, "Anda Sudah di validati oleh Admin", Toast.LENGTH_SHORT).show()
      }
    }

    editProfile.setOnClickListener {
      goTo = Intent(view.context, EditProfileActivity::class.java)
      startActivity(goTo)
    }

    detailGallery.setOnClickListener {
      goTo = Intent(view.context, GalleryActivity::class.java)
      startActivity(goTo)
    }

    pin.setOnClickListener {
      goTo = Intent(view.context, PinWebViewActivity::class.java)
      startActivity(goTo)
    }

    return view
  }

  private fun getUser(token: String) {
    Timer().schedule(100) {
      val response = UserController.Get(token).execute().get()
      val responseBalance = UserController.Balance(token).execute().get()
      if (response["code"] == 200 && responseBalance["code"] == 200) {
        if (response.getJSONObject("response")["status"] == 0) {
          LogoutController(token).execute().get()
          activity?.runOnUiThread {
            val goTo = Intent(context, MainActivity::class.java)
            User(context).clear()
            loadingFragment.closeDialog()
            startActivity(goTo)
          }
        } else {
          val json = JSONObject()
          json.put("token", token)
          json.put("username", response.getJSONObject("response")["username"].toString())
          json.put("image", response.getJSONObject("response")["image"].toString())
          json.put("status", response.getJSONObject("response")["status"].toString().toInt())
          json.put("type", response.getJSONObject("response")["role"].toString())
          user.set(json.toString())
          activity?.runOnUiThread {
            pinLinear.visibility = LinearLayout.GONE
            if (response.getJSONObject("response")["role"] == 2 || response.getJSONObject("response")["role"] == 3) {
              balanceLinear.visibility = LinearLayout.GONE
              groupLinear.visibility = LinearLayout.GONE
              addUserLinear.visibility = LinearLayout.GONE
            }
            changeProfileImage(response.getJSONObject("response")["image"].toString())
            name.text = response.getJSONObject("response")["name"].toString()
            typeUser.text = response.getJSONObject("response")["type"].toString()
            email.text = response.getJSONObject("response")["email"].toString()
            balance.text = responseBalance["balance"].toString()
            balanceHarvest.text = responseBalance["harvest"].toString()
            downLine.text = responseBalance["down_line"].toString()
            totalPackage.text = responseBalance["package"].toString()
            textPin.text = responseBalance["codePin"].toString()
            if ((responseBalance["bannerTitle"].toString()
                .isEmpty() || responseBalance["bannerTitle"] == "null") && (responseBalance["bannerDescription"].toString()
                .isEmpty() || responseBalance["bannerDescription"] == "null")
            ) {
              banner.visibility = LinearLayout.GONE
            } else {
              title.text = responseBalance["bannerTitle"].toString()
              description.text = responseBalance["bannerDescription"].toString()
            }
            loadingFragment.closeDialog()
          }
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
