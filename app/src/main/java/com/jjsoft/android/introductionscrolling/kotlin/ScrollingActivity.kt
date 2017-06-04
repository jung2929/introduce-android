package com.jjsoft.android.introductionscrolling.kotlin

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.jjsoft.android.introductionscrolling.R
import com.jjsoft.android.introductionscrolling.kotlin.data.*
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import kotlinx.android.synthetic.main.activity_scrolling.*
import java.io.ByteArrayOutputStream

class ScrollingActivity : AppCompatActivity() {
    val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(PREF_ID, Activity.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        // Cover 이미지뷰와 이미지를 겹치게두어 onClickListener 를 시스템 내부에서 인식자체를 못해서
        // Profile 이미지뷰의 onClickListener 를 따로 선언해주어 인식되도록 코딩하였습니다.
        image_view_profile_main.setOnClickListener {
            LovelyStandardDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setButtonsColorRes(R.color.colorPrimaryDark)
                    .setIcon(R.drawable.ic_person_white_36dp)
                    .setTitle(R.string.edit_profile_image)
                    .setMessage(R.string.confirm_to_change_image)
                    .setPositiveButton(R.string.camera, {
                        // 안드로이드 자체에서 Intent 로 제공하는 ACTION_IMAGE_CAPTURE 이기 때문에, 따로 권한요청을 할 필요가 없는걸 확인했습니다.
                        // 참고 - 박상권의 삽질블로그
                        // http://gun0912.tistory.com/55
                        // 참고 - Develop -> API Guides -> Common Intent (공통 인텐트)
                        // https://developer.android.com/guide/components/intents-common.html?hl=ko
                        val coverIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        coverIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
                        coverIntent.putExtra("return-data", true)
                        if (coverIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(coverIntent, PICK_FROM_PROFILE)
                        }
                    })
                    .setNegativeButton(R.string.gallery, {
                        // 안드로이드 자체에서 Intent 로 제공하는 ACTION_GET_CONTENT 이기 때문에, 따로 권한요청을 할 필요가 없는걸 확인했습니다.
                        // 참고 - 박상권의 삽질블로그
                        // http://gun0912.tistory.com/55
                        // 참고 - Develop -> API Guides -> Common Intent (공통 인텐트)
                        // https://developer.android.com/guide/components/intents-common.html?hl=ko
                        val coverIntent = Intent(Intent.ACTION_GET_CONTENT)
                        coverIntent.type = "image/*"
                        coverIntent.putExtra("crop", "true")
                        coverIntent.putExtra("return-data", true)
                        startActivityForResult(coverIntent, PICK_FROM_PROFILE)
                    })
                    .setNeutralButton(R.string.cancel, null)
                    .show()
        }

        setActivityData(PREF_INFORMATION)
        setActivityData(PREF_COVER)
        setActivityData(PREF_PROFILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) setActivityData(PREF_INFORMATION)
            PICK_FROM_COVER -> {
                val bundleExtra: Bundle? = data?.extras
                if (bundleExtra != null) {
                    saveBitmap(bundleExtra.getParcelable("data"), PREF_COVER)
                    image_view_cover_main.setImageBitmap(bundleExtra.getParcelable("data"))
                    text_view_cover_main.text = ""
                }
            }
            PICK_FROM_PROFILE -> {
                val bundleExtra: Bundle? = data?.extras
                if (bundleExtra != null) {
                    saveBitmap(bundleExtra.getParcelable("data"), PREF_PROFILE)
                    image_view_profile_main.setImageBitmap(bundleExtra.getParcelable("data"))
                    text_view_profile_main.text = ""
                }
            }
        }
    }

    fun saveBitmap(bitmap: Bitmap, preferenceCode: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val sharedPreferencesEditors = sharedPreferences.edit()
        sharedPreferencesEditors.putString(preferenceCode, encodedImage)
        sharedPreferencesEditors.apply()
    }

    fun controlButtonClick(v: View) {
        when (v.id) {
            R.id.image_view_cover_main -> {
                LovelyStandardDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.colorPrimaryDark)
                        .setIcon(R.drawable.ic_person_white_36dp)
                        .setTitle(R.string.edit_cover_image)
                        .setMessage(R.string.confirm_to_change_image)
                        .setPositiveButton(R.string.camera, {
                            val coverIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            coverIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
                            coverIntent.putExtra("return-data", true)
                            startActivityForResult(coverIntent, PICK_FROM_COVER)
                        })
                        .setNegativeButton(R.string.gallery, {
                            val coverIntent = Intent(Intent.ACTION_GET_CONTENT)
                            coverIntent.type = "image/*"
                            coverIntent.putExtra("crop", "true")
                            coverIntent.putExtra("return-data", true)
                            startActivityForResult(coverIntent, PICK_FROM_COVER)
                        })
                        .setNeutralButton(R.string.cancel, null)
                        .show()
            }
            R.id.image_button_write -> {
                val introductionInsert = Intent(this, IntroductionInsert::class.java)
                startActivityForResult(introductionInsert, REQUEST_CODE)
            }
            R.id.image_button_clear -> {
                LovelyStandardDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.colorPrimaryDark)
                        .setIcon(R.drawable.ic_clear_white_36dp)
                        .setTitle(R.string.Introduction_clear)
                        .setMessage(R.string.confirm_to_clear)
                        .setPositiveButton(R.string.okay, {
                            sharedPreferences.edit().clear().apply()
                            Toast.makeText(applicationContext, getString(R.string.completeDelete), Toast.LENGTH_SHORT).show()
                            setActivityData(PREF_INFORMATION)
                            setActivityData(PREF_COVER)
                            setActivityData(PREF_PROFILE)
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show()
            }
            R.id.image_button_about -> {
                val savedInformation: String? = sharedPreferences.getString("information", null)
                val convertedInformationData: Information? = if (savedInformation != null) Gson().fromJson(savedInformation, Information::class.java) else null
                val aboutDialogMessage: String by lazy {
                    if (convertedInformationData != null) {
                        "${getString(R.string.name)} : ${convertedInformationData.name ?: ""}" +
                                "\n${getString(R.string.sex)} : ${
                                when (convertedInformationData.sex) {
                                    true -> getString(R.string.gentleman)
                                    false -> getString(R.string.lady)
                                }
                                }" +
                                "\n${getString(R.string.age)} : ${convertedInformationData.age}" +
                                "\n${getString(R.string.student_id)} : ${convertedInformationData.studentId ?: ""}" +
                                "\n${getString(R.string.email)} : ${convertedInformationData.email ?: ""}" +
                                "\n${getString(R.string.tel)} : ${convertedInformationData.tel ?: ""}" +
                                "\n${getString(R.string.introduction)} : ${convertedInformationData.introduction ?: ""}"
                    } else getString(R.string.request_write)
                }

                LovelyStandardDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.colorPrimaryDark)
                        .setIcon(R.drawable.ic_about_white_36dp)
                        .setTitle(R.string.Introduction_about)
                        .setMessage(aboutDialogMessage)
                        .setNeutralButton(getString(R.string.okay), null)
                        .show()
            }
            R.id.text_view_email_main -> {
                val intentEmail = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${text_view_email_main.text}"))
                startActivity(intentEmail)
            }
            R.id.text_view_tel_main -> {
                LovelyStandardDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.colorPrimaryDark)
                        .setIcon(R.drawable.ic_phone_white_36dp)
                        .setTitle(R.string.tel)
                        .setMessage(R.string.confirm_to_action)
                        .setPositiveButton(R.string.sms, {
                            val intentSms = Intent(Intent.ACTION_SENDTO, Uri.parse("sms:${text_view_tel_main.text}"))
                            startActivity(intentSms)
                        })
                        .setNegativeButton(R.string.dial, {
                            val intentDial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${text_view_tel_main.text}"))
                            startActivity(intentDial)
                        })
                        .setNeutralButton(R.string.cancel, null)
                        .show()
            }
        }
    }

    fun setActivityData(preferenceString: String) {
        val savedData: String? = sharedPreferences.getString(preferenceString, null)
        when (preferenceString) {
            PREF_INFORMATION -> {
                // XML 에서 Enabled 설정이 제대로 먹히지 않아서 프로그래밍적으로 제어하였습니다.
                // seek_bar_age_main.isEnabled = false
                // disable 시켰을 때, seekBar 자체가 blur 되버려서 onTouchListener 에 터치시 아무런 동작을 안하도록 제어하였습니다.
                seek_bar_age_main.setOnTouchListener { _, _ -> true }

                val convertedInformationData: Information? = if (savedData != null) Gson().fromJson(savedData, Information::class.java) else null
                if (convertedInformationData != null) {
                    text_name.text = convertedInformationData.name ?: getString(R.string.name)
                    when (convertedInformationData.sex) {
                        true -> {
                            radio_button_gentleman_main.isEnabled = true
                            radio_button_gentleman_main.isChecked = true
                            radio_button_lady_main.isEnabled = false
                            radio_button_lady_main.isChecked = false
                        }
                        false -> {
                            radio_button_gentleman_main.isEnabled = false
                            radio_button_gentleman_main.isChecked = false
                            radio_button_lady_main.isEnabled = true
                            radio_button_lady_main.isChecked = true
                        }
                    }
                    text_view_age_main.text = convertedInformationData.age
                    seek_bar_age_main.progress = convertedInformationData.age.toInt()
                    text_student_id.text = convertedInformationData.studentId ?: getString(R.string.student_id)
                    text_view_email_main.text = convertedInformationData.email ?: getString(R.string.email)
                    text_view_tel_main.text = (convertedInformationData.tel) ?: getString(R.string.tel)
                    text_view_introduction_main.text = convertedInformationData.introduction ?: getString(R.string.introduction)
                } else {
                    text_name.text = getString(R.string.name)
                    text_student_id.text = getString(R.string.student_id)
                    text_view_email_main.text = null
                    text_view_tel_main.text = null
                    text_view_introduction_main.text = null
                    text_view_age_main.text = ""
                    seek_bar_age_main.progress = 0
                    radio_button_gentleman_main.isEnabled = false
                    radio_button_gentleman_main.isChecked = false
                    radio_button_lady_main.isEnabled = false
                    radio_button_lady_main.isChecked = false
                }
            }
            PREF_COVER -> {
                val decodedBitmapImageData: Bitmap? = if (savedData != null) {
                    val byteArray = Base64.decode(savedData, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                } else null

                if (decodedBitmapImageData != null) {
                    image_view_cover_main.setImageBitmap(decodedBitmapImageData)
                    text_view_cover_main.text = ""
                } else {
                    image_view_cover_main.setImageBitmap(null)
                    image_view_cover_main.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                    text_view_cover_main.text = getString(R.string.edit_cover_image)
                }
            }
            PREF_PROFILE -> {
                val decodedBitmapImageData: Bitmap? = if (savedData != null) {
                    val byteArray = Base64.decode(savedData, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                } else null

                if (decodedBitmapImageData != null) {
                    image_view_profile_main.setImageBitmap(decodedBitmapImageData)
                    text_view_profile_main.text = ""
                } else {
                    image_view_profile_main.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_person_white_128dp))
                    text_view_profile_main.text = getString(R.string.edit_profile_image)
                }
            }
        }
    }
}