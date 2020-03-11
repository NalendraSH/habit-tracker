package com.habittracker.activity.registerlevel

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.habittracker.R
import com.habittracker.activity.home.HomeActivity
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Child
import kotlinx.android.synthetic.main.activity_register_level.*
import org.jetbrains.anko.longToast

class RegisterLevelActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private val userId: String by lazy { PreferenceHelper(this).userId!! }
    private val userName: String by lazy { PreferenceHelper(this).userName!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_level)

        databaseReference = FirebaseDatabase.getInstance().reference

        if (PreferenceHelper(this).userId != "" && intent.getStringExtra("addchild_status") == "setting"){
            databaseReference.child(userName)
                .child(userId)
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
            textview_register_level_title.text = "Settings"
            setBackNav()
        }else if (PreferenceHelper(this).userId != "" && intent.getStringExtra("addchild_status") == "add") {
            setBackNav()
        }

        button_register_simpan.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val id: String?
        if (PreferenceHelper(this).userId == "" || intent.getStringExtra("addchild_status") == "add"){
            id = databaseReference.child(userName).push().key
        }else {
            id = userId
        }
        progress_register_level.visibility = View.VISIBLE
        button_register_simpan.visibility = View.GONE
        if (intent.getStringExtra("addchild_status") == "add") {
            databaseReference.child(userName).child(id.toString()).setValue(
                Child(
                    id,
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
                )
            ).addOnSuccessListener {
                progress_register_level.visibility = View.GONE
                progress_register_level.visibility = View.VISIBLE
                PreferenceHelper(this).userId = id
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                finish()
                startActivity(intent)
            }.addOnFailureListener {
                progress_register_level.visibility = View.GONE
                progress_register_level.visibility = View.VISIBLE
                longToast("Register Failure. Please check your internet connection.")
            }
        }else {
            databaseReference.child(userName).child(id.toString()).child("name")
                .setValue(intent.getStringExtra("nama_lengkap"))
            databaseReference.child(userName).child(id.toString()).child("date_of_birth")
                .setValue(intent.getStringExtra("tgl_lahir"))
            databaseReference.child(userName).child(id.toString()).child("reward")
                .setValue(intent.getStringExtra("reward").toInt())
            databaseReference.child(userName).child(id.toString()).child("gender")
                .setValue(intent.getStringExtra("jenis_kelamin"))
            databaseReference.child(userName).child(id.toString()).child("level1")
                .setValue(edittext_register_level_1.text.toString().toInt())
            databaseReference.child(userName).child(id.toString()).child("level2")
                .setValue(edittext_register_level_2.text.toString().toInt())
            databaseReference.child(userName).child(id.toString()).child("level3")
                .setValue(edittext_register_level_3.text.toString().toInt())
            databaseReference.child(userName).child(id.toString()).child("level4")
                .setValue(edittext_register_level_4.text.toString().toInt())
            databaseReference.child(userName).child(id.toString()).child("level5")
                .setValue(edittext_register_level_5.text.toString().toInt())
                .addOnSuccessListener {
                    progress_register_level.visibility = View.GONE
                    progress_register_level.visibility = View.VISIBLE
                    PreferenceHelper(this).userId = id
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    finish()
                    startActivity(intent)
                }.addOnFailureListener {
                    progress_register_level.visibility = View.GONE
                    progress_register_level.visibility = View.VISIBLE
                    longToast("Update Failure. Please check your internet connection.")
                }
        }
    }

    private fun setBackNav() {
        toolbar_register_level.setNavigationIcon(R.drawable.ic_navigate_back)
        toolbar_register_level.setNavigationOnClickListener { finish() }
    }
}
