package com.habittracker.activity.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.habittracker.R
import com.habittracker.activity.addhabit.AddHabitActivity
import com.habittracker.activity.addpunishment.AddPunishmentActivity
import com.habittracker.activity.login.LoginActivity
import com.habittracker.activity.registerbio.RegisterBioActivity
import com.habittracker.fragment.entertainment.EntertainmentFragment
import com.habittracker.fragment.habit.HabitFragment
import com.habittracker.fragment.punishment.PunishmentFragment
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Child
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_redeem_reward.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast


class HomeActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val userId: String by lazy { PreferenceHelper(this).userId!! }
    private val userName: String by lazy { PreferenceHelper(this).userName!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setViewPager()

        databaseReference = FirebaseDatabase.getInstance().reference

        if (intent.getStringExtra("userid_data") != null) {
            initData(intent.getStringExtra("userid_data"))
        }else {
            initData(userId)
        }

        image_home_add.setOnClickListener {
            val listItems = arrayOf("Habit", "Entertainment", "Punishment", "Child")
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setItems(listItems) { dialogInterface, i ->
                val intent = Intent(this, AddHabitActivity::class.java)
                if (listItems[i] == "Habit"){
                    intent.putExtra("from", "insert")
                    intent.putExtra("type", "habit")
                    startActivity(intent)
                }else if (listItems[i] == "Entertainment") {
                    intent.putExtra("from", "insert")
                    intent.putExtra("type", "entertainment")
                    startActivity(intent)
                }else if (listItems[i] == "Punishment") {
                    val addPunishmentIntent = Intent(this, AddPunishmentActivity::class.java)
                    addPunishmentIntent.putExtra("from", "insert")
                    addPunishmentIntent.putExtra("type", "punishment")
                    startActivity(addPunishmentIntent)
                }else {
                    val registerBioIntent = Intent(this, RegisterBioActivity::class.java)
                    registerBioIntent.putExtra("addchild_status", "add")
                    startActivity(registerBioIntent)
                }
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
                        databaseReference.child(userName)
                            .child(userId)
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val child = dataSnapshot.getValue(Child::class.java)
                                    databaseReference.child(userName)
                                        .child(userId)
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
                    databaseReference.child(userName)
                        .child(userId)
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
                                            databaseReference.child(userName)
                                                .child(userId)
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
            val intent = Intent(this, RegisterBioActivity::class.java)
            intent.putExtra("addchild_status", "setting")
            startActivity(intent)
        }

        val context: Context = this

        image_home_switch.setOnClickListener {
            databaseReference.child(userName)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val listItems = ArrayList<String>()
                        val listId = ArrayList<String>()
                        for (noteDataSnapshot in dataSnapshot.children){
                            val child = noteDataSnapshot.getValue(Child::class.java)

                            child?.name?.let { it1 -> listItems.add(it1) }
                            child?.id?.let { it1 -> listId.add(it1) }
                        }
                        val list = listItems.toArray(arrayOfNulls<String>(listItems.size))
                        val dialogBuilder = AlertDialog.Builder(context)
                        dialogBuilder.setItems(list) { dialogInterface, i ->
                            initData(listId[i])
                            PreferenceHelper(context).userId = listId[i]
                            sendBroadcast(Intent("CHANGE"))
                            dialogInterface.dismiss()
                        }.show()
                    }
                })
        }

        image_home_logout.setOnClickListener {
            alert("Apakah anda yakin ingin melogout data anda dari device ini?", "Logout") {
                positiveButton("Ok"){
                    PreferenceHelper(this@HomeActivity).clearData()
                    FirebaseAuth.getInstance().signOut()

                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    mGoogleSignInClient = GoogleSignIn.getClient(this@HomeActivity,gso)
                    mGoogleSignInClient.signOut().addOnCompleteListener {
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                        finish()
                    }
                }
                negativeButton("Cancel"){}
            }.show()
        }
    }

    private fun initData(userId: String) {
        progress_home_loading.visibility = View.VISIBLE
        constraint_home_bio_inner.visibility = View.GONE
        databaseReference.child(userName).child(userId).addValueEventListener(object : ValueEventListener{
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
//                databaseReference.child(PreferenceHelper(context!!).userName)
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

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment = HabitFragment()
            when (position) {
                0 -> { fragment = HabitFragment() }
                1 -> { fragment = EntertainmentFragment() }
                2 -> { fragment = PunishmentFragment() }
            }
            return fragment
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
