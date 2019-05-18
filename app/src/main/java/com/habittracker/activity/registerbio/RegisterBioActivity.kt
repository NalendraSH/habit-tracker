package com.habittracker.activity.registerbio

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import com.google.firebase.database.*
import com.habittracker.R
import com.habittracker.activity.registerlevel.RegisterLevelActivity
import com.habittracker.formatDate
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Child
import kotlinx.android.synthetic.main.activity_register_bio.*
import org.jetbrains.anko.intentFor
import java.util.*

class RegisterBioActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private var tglLahir = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_bio)
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)

        databaseReference = FirebaseDatabase.getInstance().reference

        if (PreferenceHelper(this).userId != ""){
            databaseReference.child("anak")
                .child(PreferenceHelper(this).userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val child = dataSnapshot.getValue(Child::class.java)
                        edittext_register_nama_lengkap.setText(child?.name)
                        edittext_register_tanggal_lahir.setText(child?.date_of_birth)
                        child?.reward?.let { edittext_register_reward.setText(it.toString()) }
                        if (child?.gender == "Laki-laki"){
                            radiogroup_register_jenis_kelamin.check(R.id.radiobutton_register_laki)
                        }else {
                            radiogroup_register_jenis_kelamin.check(R.id.radiobutton_register_perempuan)
                        }
                        imageview_register_logo.visibility = View.GONE
                        toolbar_register_bio.visibility = View.VISIBLE
                        toolbar_register_bio.setNavigationIcon(R.drawable.ic_navigate_back)
                        toolbar_register_bio.setNavigationOnClickListener { finish() }
                    }
                })
        }

        edittext_register_tanggal_lahir.setOnClickListener {
            showDatePicker()
        }
        edittext_register_tanggal_lahir.setOnTouchListener { _, _ ->
            val inputMethodManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(edittext_register_tanggal_lahir.windowToken, 0)
            false
        }

        val jenisKelamin = findViewById<RadioButton>(radiogroup_register_jenis_kelamin.checkedRadioButtonId)
        button_register_lanjutkan.setOnClickListener {
            startActivity(intentFor<RegisterLevelActivity>(
                "nama_lengkap" to edittext_register_nama_lengkap.text.toString(),
                "tgl_lahir" to edittext_register_tanggal_lahir.text.toString(),
                "reward" to edittext_register_reward.text.toString(),
                "jenis_kelamin" to jenisKelamin.text.toString()
            ))
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val date = Calendar.getInstance()
                date.set(year, monthOfYear, dayOfMonth)
                edittext_register_tanggal_lahir.setText(date.time.toString().formatDate("EEE MMM dd HH:mm:ss zzz yyyy"))
                tglLahir = date.time.toString().formatDate("EEE MMM dd HH:mm:ss zzz yyyy")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }
}
