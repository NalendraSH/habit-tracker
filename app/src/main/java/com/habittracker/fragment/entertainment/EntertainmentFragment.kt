package com.habittracker.fragment.entertainment


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
import com.habittracker.model.Entertainment
import kotlinx.android.synthetic.main.fragment_entertainment.view.*

class EntertainmentFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private val listEntertainment: MutableList<Entertainment> = mutableListOf()
    private val adapter = EntertainmentAdapter(listEntertainment)
    private lateinit var v: View
    private val userId: String by lazy { context?.let { PreferenceHelper(it).userId }!! }
    private val userName: String by lazy { context?.let { PreferenceHelper(it).userName }!! }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_entertainment, container, false)

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
        v.progress_entertainment.visibility = View.VISIBLE
        v.recycler_entertainment.visibility = View.GONE
        databaseReference.child(userName)
            .child(userId)
            .child("entertainment")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    v.progress_entertainment.visibility = View.GONE
                    v.recycler_entertainment.visibility = View.VISIBLE
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listEntertainment.clear()
                    for (noteDataSnapshot in dataSnapshot.children){
                        val habit = noteDataSnapshot.getValue(Entertainment::class.java)

                        listEntertainment.add(habit!!)
                    }
                    v.recycler_entertainment.adapter = adapter
                    v.recycler_entertainment.layoutManager =
                        androidx.recyclerview.widget.LinearLayoutManager(context)

                    v.progress_entertainment.visibility = View.GONE
                    v.recycler_entertainment.visibility = View.VISIBLE
                }

            })
    }
}
