package com.habittracker.fragment.punishment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.habittracker.R
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Punishment
import kotlinx.android.synthetic.main.fragment_punishment.*
import kotlinx.android.synthetic.main.fragment_punishment.view.*

/**
 * A simple [Fragment] subclass.
 */
class PunishmentFragment : Fragment() {

    companion object {
        private const val START_TIME_IN_MILLIS: Long = 600000
    }

    private var mCountDownTimer: CountDownTimer? = null

    private var mTimerRunning: Boolean = false

    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0

    private lateinit var v: View
    private lateinit var databaseReference: DatabaseReference
    private val listHabit: MutableList<Punishment> = mutableListOf()
    private val adapter = PunishmentAdapter(listHabit)
    private val userId: String by lazy { context?.let { PreferenceHelper(it).userId }!! }
    private val userName: String by lazy { context?.let { PreferenceHelper(it).userName }!! }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_punishment, container, false)

        databaseReference = FirebaseDatabase.getInstance().reference
        initData(userId)

        return v
    }

    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            initData(userId)
        }
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(mNotificationReceiver, IntentFilter("CHANGE"))
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(mNotificationReceiver)
    }

    private fun initData(userId: String) {
        v.progress_punishment.visibility = View.VISIBLE
        v.progress_punishment.visibility = View.GONE
        databaseReference.child(userName)
            .child(userId)
            .child("punishment")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    v.progress_punishment.visibility = View.GONE
                    v.progress_punishment.visibility = View.VISIBLE
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listHabit.clear()
                    for (noteDataSnapshot in dataSnapshot.children){
                        val habit = noteDataSnapshot.getValue(Punishment::class.java)

                        listHabit.add(habit!!)
                    }
                    v.recycler_punishment.adapter = adapter
                    v.recycler_punishment.layoutManager = LinearLayoutManager(context)

                    v.progress_punishment.visibility = View.GONE
                    v.recycler_punishment.visibility = View.VISIBLE
                }

            })
    }

    override fun onStop() {
        super.onStop()

        recycler_punishment.adapter = null
    }

}