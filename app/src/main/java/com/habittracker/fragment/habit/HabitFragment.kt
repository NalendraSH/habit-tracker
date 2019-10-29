package com.habittracker.fragment.habit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.habittracker.R
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Habit
import kotlinx.android.synthetic.main.fragment_habit.view.*


class HabitFragment: Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private val listHabit: MutableList<Habit> = mutableListOf()
    private val adapter = HabitAdapter(listHabit)
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_habit, container, false)

        databaseReference = FirebaseDatabase.getInstance().reference
        initData(PreferenceHelper(context!!).userId)

        return v
    }

    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            context.let { PreferenceHelper(it).userId }?.let { initData(it) }
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
        v.progress_habit.visibility = View.VISIBLE
        v.recycler_habit.visibility = View.GONE
        databaseReference.child(PreferenceHelper(context!!).userName)
            .child(userId)
            .child("habit")
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    v.progress_habit.visibility = View.GONE
                    v.recycler_habit.visibility = View.VISIBLE
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listHabit.clear()
                    for (noteDataSnapshot in dataSnapshot.children){
                        val habit = noteDataSnapshot.getValue(Habit::class.java)

                        listHabit.add(habit!!)
                    }
                    v.recycler_habit.adapter = adapter
                    v.recycler_habit.layoutManager =
                        androidx.recyclerview.widget.LinearLayoutManager(context)

                    v.progress_habit.visibility = View.GONE
                    v.recycler_habit.visibility = View.VISIBLE
                }

            })
    }
}
