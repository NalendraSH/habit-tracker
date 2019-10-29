package com.habittracker.activity.addhabit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.habittracker.R
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Entertainment
import com.habittracker.model.Habit
import kotlinx.android.synthetic.main.activity_add_habit.*
import org.jetbrains.anko.longToast

class AddHabitActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var path: String
    private lateinit var value: Any

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        setBackNav()

        //set layout
        if (intent.getStringExtra("type") == "habit"){
            path = "habit"
            textinput_add_habit_poin_plus.hint = "Poin Plus"
            textinput_add_habit_poin_minus.hint = "Poin Minus"
            textview_add_habit_poin_1.text = "Poin"
            textview_add_habit_poin_2.text = "Poin"
        }else {
            path = "entertainment"
            textinput_add_habit_poin_plus.hint = "Coin Plus"
            textinput_add_habit_poin_minus.hint = "Coin Minus"
            textview_add_habit_poin_1.text = "Coin"
            textview_add_habit_poin_2.text = "Coin"
        }

        //fill edittext when from update
        if (intent.getStringExtra("from") == "update"){
            edittext_add_habit_nama_kegiatan.setText(intent.getStringExtra("nama_kegiatan"))
            edittext_add_habit_poin_plus.setText(intent.getStringExtra("poin_plus"))
            edittext_add_habit_poin_minus.setText(intent.getStringExtra("poin_minus"))
        }

        databaseReference = FirebaseDatabase.getInstance().reference
        button_add_habit_simpan.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val id: String?
        if (intent.getStringExtra("from") == "update"){
            id = intent.getStringExtra("id")
        }else {
            id = databaseReference.child(PreferenceHelper(this).userName).child(PreferenceHelper(this).userId).child(path).push().key
        }

        if (intent.getStringExtra("type") == "habit"){
            value = Habit(id,
                edittext_add_habit_nama_kegiatan.text.toString(),
                edittext_add_habit_poin_plus.text.toString().toInt(),
                edittext_add_habit_poin_minus.text.toString().toInt())
        }else {
            value = Entertainment(id,
                edittext_add_habit_nama_kegiatan.text.toString(),
                edittext_add_habit_poin_plus.text.toString().toInt(),
                edittext_add_habit_poin_minus.text.toString().toInt())
        }

        progress_add_habit.visibility = View.VISIBLE
        button_add_habit_simpan.visibility = View.GONE
        databaseReference.child(PreferenceHelper(this).userName)
            .child(PreferenceHelper(this).userId)
            .child(path)
            .child(id.toString())
            .setValue(value)
            .addOnSuccessListener {
                progress_add_habit.visibility = View.GONE
                button_add_habit_simpan.visibility = View.VISIBLE
                finish()
            }.addOnFailureListener {
                progress_add_habit.visibility = View.GONE
                button_add_habit_simpan.visibility = View.VISIBLE
                longToast("Add Habit Failure. Please check your internet connection.")
            }
    }

    private fun setBackNav() {
        toolbar_add_habit.setNavigationIcon(R.drawable.ic_navigate_back)
        toolbar_add_habit.setNavigationOnClickListener { finish() }
    }
}
