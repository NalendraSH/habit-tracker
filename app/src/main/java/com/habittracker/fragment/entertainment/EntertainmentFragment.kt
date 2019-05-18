package com.habittracker.fragment.entertainment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_entertainment, container, false)

        databaseReference = FirebaseDatabase.getInstance().reference
        initData()

        return v
    }

    private fun initData() {
        v.progress_entertainment.visibility = View.VISIBLE
        v.recycler_entertainment.visibility = View.GONE
        databaseReference.child("anak")
            .child(PreferenceHelper(context!!).userId)
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
                    v.recycler_entertainment.layoutManager = LinearLayoutManager(context)

                    v.progress_entertainment.visibility = View.GONE
                    v.recycler_entertainment.visibility = View.VISIBLE
                }

            })
    }
}
