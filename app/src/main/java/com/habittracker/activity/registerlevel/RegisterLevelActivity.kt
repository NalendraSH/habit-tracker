package com.habittracker.activity.registerlevel

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.habittracker.R
import com.habittracker.activity.home.HomeActivity
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Child
import kotlinx.android.synthetic.main.activity_register_level.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

class RegisterLevelActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_level)

        setBackNav()
        databaseReference = FirebaseDatabase.getInstance().reference

        if (PreferenceHelper(this).userId != ""){
            databaseReference.child("anak")
                .child(PreferenceHelper(this).userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val child = dataSnapshot.getValue(Child::class.java)
                        child?.level1?.let { edittext_register_level_1.setText(it.toString()) }
                        child?.level2?.let { edittext_register_level_2.setText(it.toString()) }
                        child?.level3?.let { edittext_register_level_3.setText(it.toString()) }
                        child?.level4?.let { edittext_register_level_4.setText(it.toString()) }
                        child?.level5?.let { edittext_register_level_5.setText(it.toString()) }
                    }
                })
            toolbar_register_level_title.text = "Setting"
        }

        button_register_simpan.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val id: String?
        if (PreferenceHelper(this).userId == ""){
            id = databaseReference.child("anak").push().key
        }else {
            id = PreferenceHelper(this).userId
        }
        progress_register_level.visibility = View.VISIBLE
        button_register_simpan.visibility = View.GONE
        databaseReference.child("anak").child(id.toString()).setValue(Child(
            intent.getStringExtra("nama_lengkap"),
            intent.getStringExtra("tgl_lahir"),
            intent.getStringExtra("reward").toInt(),
            0,
            intent.getStringExtra("jenis_kelamin"),
            0,
            0,
            edittext_register_level_1.text.toString().toInt(),
            edittext_register_level_2.text.toString().toInt(),
            edittext_register_level_3.text.toString().toInt(),
            edittext_register_level_4.text.toString().toInt(),
            edittext_register_level_5.text.toString().toInt(),
            null
        )).addOnSuccessListener {
            progress_register_level.visibility = View.GONE
            progress_register_level.visibility = View.VISIBLE
            PreferenceHelper(this).userId = id
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finish()
            startActivity(intent)
            toast("Register Success")
        }.addOnFailureListener {
            progress_register_level.visibility = View.GONE
            progress_register_level.visibility = View.VISIBLE
            longToast("Register Failure. Please check your internet connection.")
        }
    }

    private fun setBackNav() {
        toolbar_register_level.setNavigationIcon(R.drawable.ic_navigate_back)
        toolbar_register_level.setNavigationOnClickListener { finish() }
    }
}
