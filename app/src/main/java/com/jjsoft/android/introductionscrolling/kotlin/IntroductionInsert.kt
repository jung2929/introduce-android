package com.jjsoft.android.introductionscrolling.kotlin

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.jjsoft.android.introductionscrolling.R
import com.jjsoft.android.introductionscrolling.kotlin.data.Information
import com.jjsoft.android.introductionscrolling.kotlin.data.PREF_ID
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import kotlinx.android.synthetic.main.activity_introduction_insert.*

class IntroductionInsert : AppCompatActivity() {
    val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(PREF_ID, Activity.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction_insert)

        edit_text_name.setOnEditorActionListener(onEditorActionListener)
        edit_text_student_id.setOnEditorActionListener(onEditorActionListener)
        edit_text_email.setOnEditorActionListener(onEditorActionListener)
        edit_text_tel.setOnEditorActionListener(onEditorActionListener)

        seek_bar_age.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                text_view_age.text = progress.toString()
            }
        })

        val savedInformation: String? = sharedPreferences.getString("information", null)
        val convertedInformationData: Information? = if (savedInformation != null) Gson().fromJson(savedInformation, Information::class.java) else null
        if (convertedInformationData != null) {
            setActivityData(convertedInformationData)
        } else {
            material_text_name.setHasFocus(true)
            text_view_age.text = seek_bar_age.progress.toString()
        }
    }

    fun controlButtonClick(v: View) {
        when (v.id) {
            R.id.button_save -> {
                button_save.text = getString(R.string.okay)
                button_save.background = ContextCompat.getDrawable(applicationContext, R.drawable.btn_default_attr)

                val aboutDialogMessage =
                        "${getString(R.string.name)} : ${edit_text_name.text ?: ""}" +
                                "\n${getString(R.string.sex)} : ${
                                when (radio_group_sex.checkedRadioButtonId) {
                                    R.id.radio_button_gentleman -> getString(R.string.gentleman)
                                    R.id.radio_button_lady -> getString(R.string.lady)
                                    else -> getString(R.string.gentleman)
                                }
                                }" +
                                "\n${getString(R.string.age)} : ${text_view_age.text}" +
                                "\n${getString(R.string.student_id)} : ${edit_text_student_id.text ?: ""}" +
                                "\n${getString(R.string.email)} : ${edit_text_email.text ?: ""}" +
                                "\n${getString(R.string.tel)} : ${edit_text_tel.text ?: ""}" +
                                "\n${getString(R.string.introduction)} : ${edit_text_introduction.text ?: ""}"


                LovelyStandardDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.colorPrimaryDark)
                        .setIcon(R.drawable.ic_write_white_36dp)
                        .setTitle(R.string.confirm_to_insert)
                        .setMessage(aboutDialogMessage)
                        .setPositiveButton(R.string.okay, {
                            when (save()) {
                                true -> {
                                    button_save.text = getString(R.string.confirm)
                                    button_save.background = ContextCompat.getDrawable(applicationContext, R.drawable.btn_confirm_attr)
                                    setResult(Activity.RESULT_OK)
                                }
                                false -> {
                                    button_save.text = getString(R.string.error)
                                    button_save.background = ContextCompat.getDrawable(applicationContext, R.drawable.btn_cancel_attr)
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, { setResult(Activity.RESULT_CANCELED) })
                        .show()
            }
            R.id.button_exit -> {
                finish()
            }
        }
    }

    fun save(): Boolean {
        val information = Information()
        information.name = if (edit_text_name.text.toString() != "") edit_text_name.text.toString() else null
        information.sex = when (radio_group_sex.checkedRadioButtonId) {
            R.id.radio_button_gentleman -> true
            R.id.radio_button_lady -> false
            else -> true
        }
        information.age = seek_bar_age.progress.toString()
        information.studentId = if (edit_text_student_id.text.toString() != "") edit_text_student_id.text.toString() else null
        information.email = if (edit_text_email.text.toString() != "") edit_text_email.text.toString() else null
        information.tel = if (edit_text_tel.text.toString() != "") edit_text_tel.text.toString() else null
        information.introduction = if (edit_text_introduction.text.toString() != "") edit_text_introduction.text.toString() else null

        val sharedPreferencesEditors = getSharedPreferences(PREF_ID, Activity.MODE_PRIVATE).edit()

        if (information.name.isNullOrBlank() && information.studentId.isNullOrBlank() &&
                information.email.isNullOrBlank() && information.tel.isNullOrBlank() && information.introduction.isNullOrBlank()) {
            /*sharedPreferencesEditors.putString("information", null)
            sharedPreferencesEditors.apply()*/
            Toast.makeText(applicationContext, "입력한 값이 없습니다.", Toast.LENGTH_SHORT).show()
            return false
        } else {
            sharedPreferencesEditors.putString("information", Gson().toJson(information))
            sharedPreferencesEditors.apply()
            return true
        }
    }

    val onEditorActionListener = TextView.OnEditorActionListener { v, _, _ ->
        when (v.id) {
            R.id.edit_text_name -> {
                material_text_student_id.setHasFocus(true)
            }
            R.id.edit_text_student_id -> {
                material_text_email.setHasFocus(true)
            }
            R.id.edit_text_email -> {
                material_text_tel.setHasFocus(true)
            }
            R.id.edit_text_tel -> {
                edit_text_introduction.requestFocus()
            }
        }
        true
    }

    fun setActivityData(information: Information) {
        if (!information.name.isNullOrEmpty()) {
            edit_text_name.setText(information.name.toString())
            material_text_name.setHasFocus(true)
        }
        if (information.sex) radio_button_gentleman.isChecked = true else radio_button_lady.isChecked = true
        text_view_age.text = information.age
        seek_bar_age.progress = information.age.toInt()
        if (!information.studentId.isNullOrEmpty()) {
            edit_text_student_id.setText(information.studentId.toString())
            material_text_student_id.setHasFocus(true)
        }
        if (!information.email.isNullOrEmpty()) {
            edit_text_email.setText(information.email)
            material_text_email.setHasFocus(true)
        }
        if (!information.tel.isNullOrEmpty()) {
            edit_text_tel.setText(information.tel.toString())
            material_text_tel.setHasFocus(true)
        }
        if (!information.introduction.isNullOrEmpty()) {
            edit_text_introduction.setText(information.introduction)
        }
    }
}
