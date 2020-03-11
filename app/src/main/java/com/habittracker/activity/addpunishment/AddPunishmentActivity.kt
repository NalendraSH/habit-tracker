package com.habittracker.activity.addpunishment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.habittracker.R
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Punishment
import kotlinx.android.synthetic.main.activity_add_punishment.*
import org.jetbrains.anko.longToast
import java.util.concurrent.TimeUnit

class AddPunishmentActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var path: String
    private lateinit var value: Any
    private val userId: String by lazy { PreferenceHelper(this).userId!! }
    private val userName: String by lazy { PreferenceHelper(this).userName!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_punishment)

        setBackNav()

        path = "punishment"

        //fill edittext when from update
        if (intent.getStringExtra("from") == "update"){
            edittext_add_punishment_name.setText(intent.getStringExtra("punishment_name"))
            val timeMillis = intent.getLongExtra("time_in_millis", 0)
            val hours = TimeUnit.MILLISECONDS.toHours(timeMillis).toInt()
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeMillis))
            spinner_add_punishment_hours.setSelection(hours)
            spinner_add_punishment_minutes.setSelection(minutes.toInt())
        }

        databaseReference = FirebaseDatabase.getInstance().reference
        button_add_punishment_save.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val id: String?
        if (intent.getStringExtra("from") == "update"){
            id = intent.getStringExtra("id")
        }else {
            id = databaseReference.child(userName).child(userId).child(path).push().key
        }

        val durationInMillis = spinner_add_punishment_minutes.selectedItem.toString().toLong() * 60000 +
                spinner_add_punishment_hours.selectedItem.toString().toLong() * 3600000
        value = Punishment(id,
            edittext_add_punishment_name.text.toString(),
            durationInMillis)

        progress_add_punishment.visibility = View.VISIBLE
        button_add_punishment_save.visibility = View.GONE
        databaseReference.child(userName)
            .child(userId)
            .child(path)
            .child(id.toString())
            .setValue(value)
            .addOnSuccessListener {
                progress_add_punishment.visibility = View.GONE
                button_add_punishment_save.visibility = View.VISIBLE
                finish()
            }.addOnFailureListener {
                progress_add_punishment.visibility = View.GONE
                button_add_punishment_save.visibility = View.VISIBLE
                longToast("Add Habit Failure. Please check your internet connection.")
            }
    }

    private fun setBackNav() {
        toolbar_add_punishment.setNavigationIcon(R.drawable.ic_navigate_back)
        toolbar_add_punishment.setNavigationOnClickListener { finish() }
    }
}
