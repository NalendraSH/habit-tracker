package com.habittracker.activity.home

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.google.firebase.database.*
import com.habittracker.R
import com.habittracker.activity.addhabit.AddHabitActivity
import com.habittracker.activity.registerbio.RegisterBioActivity
import com.habittracker.fragment.entertainment.EntertainmentFragment
import com.habittracker.fragment.habit.HabitFragment
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Child
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_redeem_reward.*
import kotlinx.android.synthetic.main.dialog_redeem_reward.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast


class HomeActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setViewPager()

        databaseReference = FirebaseDatabase.getInstance().reference
        initData()

        image_home_add.setOnClickListener {
            val listItems = arrayOf("Habit", "Entertainment")
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setItems(listItems) { dialogInterface, i ->
                val intent = Intent(this, AddHabitActivity::class.java)
                if (listItems[i] == "Habit"){
                    intent.putExtra("from", "insert")
                    intent.putExtra("type", "habit")
                }else {
                    intent.putExtra("from", "insert")
                    intent.putExtra("type", "entertainment")
                }
                startActivity(intent)
                dialogInterface.dismiss()
            }.show()
        }

        image_home_redeem.setOnClickListener {
            val listItems = arrayOf("Tukarkan dengan uang", "Tukarkan dengan mainan")
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setItems(listItems) { dialogInterface, i ->
                if (listItems[i] == "Tukarkan dengan uang"){
                    val builder = AlertDialog.Builder(this)
                    val dialogView = layoutInflater.inflate(R.layout.dialog_redeem_reward, null)
                    builder.setView(dialogView)
                    builder.setPositiveButton("Ok") { _, _ ->
                        databaseReference.child("anak")
                            .child(PreferenceHelper(this).userId)
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val child = dataSnapshot.getValue(Child::class.java)
                                    databaseReference.child("anak")
                                        .child(PreferenceHelper(this@HomeActivity).userId)
                                        .child("totalrewards")
                                        .setValue(child?.totalrewards?.minus(dialogView.edittext_dialog_redeem.text.toString().toInt()))
                                }
                            })
                    }
                    builder.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alertDialog = builder.create()
                    alertDialog.setTitle("Masukkan nominal")
                    alertDialog.show()
                }else {
                    databaseReference.child("anak")
                        .child(PreferenceHelper(this).userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                            }
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val child = dataSnapshot.getValue(Child::class.java)
                                if (child?.coins!! < 15){
                                    alert("Mohon maaf, coin belum mencukupi untuk ditukar") {
                                        positiveButton("Ok"){ it.dismiss() }
                                    }.show()
                                }else {
                                    alert("Apakah anda yakin ingin menukar coin dengan mainan?"){
                                        positiveButton("Ya"){
                                            databaseReference.child("anak")
                                                .child(PreferenceHelper(this@HomeActivity).userId)
                                                .child("coins")
                                                .setValue(child.coins.minus(15))
                                            longToast("Berhasil menukarkan dengan hadiah")
                                        }
                                        negativeButton("Tidak"){
                                            it.dismiss()
                                        }
                                    }.show()
                                }
                            }
                        })
                }
                dialogInterface.dismiss()
            }.show()
        }

        image_home_settings.setOnClickListener {
            startActivity(Intent(this, RegisterBioActivity::class.java))
        }
    }

    private fun initData() {
        progress_home_loading.visibility = View.VISIBLE
        constraint_home_bio_inner.visibility = View.GONE
        databaseReference.child("anak").child(PreferenceHelper(this).userId).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                progress_home_loading.visibility = View.GONE
                constraint_home_bio_inner.visibility = View.VISIBLE
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val child = dataSnapshot.getValue(Child::class.java)
                textview_home_name.text = child?.name

                textview_home_reward.text = child?.totalrewards.toString()
//                val totalReward = child?.reward?.times(child.points!!)
//                textview_home_reward.text = totalReward.toString()
//                databaseReference.child("anak")
//                    .child(PreferenceHelper(this@HomeActivity).userId)
//                    .child("totalrewards")
//                    .setValue(totalReward)

                textview_home_coin.text = child?.coins.toString()

                if (child?.points!! < child.level2!!){
                    textview_home_level.text = "Level 1"
                    image_home_avatar.setImageResource(R.drawable.img_avatar_level_1)
                    progress_home_star.max = child.level2
                    progress_home_star.progress = child.points
                    textview_home_points_indicator.text = child.points.toString() + "/" + child.level2
                }else if (child.points >= child.level2 && child.points < child.level3!!){
                    textview_home_level.text = "Level 2"
                    image_home_avatar.setImageResource(R.drawable.img_avatar_level_2)
                    progress_home_star.max = child.level3 - child.level2
                    progress_home_star.progress = child.points - child.level2
                    textview_home_points_indicator.text = child.points.toString() + "/" + child.level3
                }else if (child.points >= child.level3!! && child.points < child.level4!!){
                    textview_home_level.text = "Level 3"
                    image_home_avatar.setImageResource(R.drawable.img_avatar_level_3)
                    progress_home_star.max = child.level4 - child.level3
                    progress_home_star.progress = child.points - child.level3
                    textview_home_points_indicator.text = child.points.toString() + "/" + child.level4
                }else if (child.points >= child.level4!! && child.points < child.level5!!){
                    textview_home_level.text = "Level 4"
                    image_home_avatar.setImageResource(R.drawable.img_avatar_level_4)
                    progress_home_star.max = child.level5 - child.level4
                    progress_home_star.progress = child.points - child.level4
                    textview_home_points_indicator.text = child.points.toString() + "/" + child.level5
                }else if (child.points >= child.level5!!){
                    textview_home_level.text = "Level 5"
                    image_home_avatar.setImageResource(R.drawable.img_avatar_level_5)
                    progress_home_star.max = 30
                    progress_home_star.progress = 30
                    textview_home_points_indicator.text = "Max"
                }
//                progress_home_star.max = 30
//                child?.points?.let { progress_home_star.progress = it }
//                textview_home_points_indicator.text = child?.points.toString() + "/30"

                progress_home_loading.visibility = View.GONE
                constraint_home_bio_inner.visibility = View.VISIBLE
            }

        })
    }

    private fun setViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        viewpager_container_home.adapter = sectionsPagerAdapter

        viewpager_container_home.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs_home))
        tabs_home.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewpager_container_home))
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> { HabitFragment() }
                1 -> { EntertainmentFragment() }
                else -> null
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }
}
