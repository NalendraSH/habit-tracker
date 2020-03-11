package com.habittracker.fragment.habit

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.habittracker.R
import com.habittracker.activity.addhabit.AddHabitActivity
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Child
import com.habittracker.model.Habit
import kotlinx.android.synthetic.main.item_habit.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor

class HabitAdapter(private val habit: MutableList<Habit> = mutableListOf()):
        RecyclerView.Adapter<HabitAdapter.ViewHolder>(){

    private lateinit var context: Context
    private val userId: String by lazy { PreferenceHelper(context).userId!! }
    private val userName: String by lazy { PreferenceHelper(context).userName!! }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false))
    }

    override fun getItemCount(): Int = habit.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(habit[position], context)
        holder.itemView.setOnLongClickListener {
            val listItems = arrayOf("Update", "Delete")
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setItems(listItems) { dialogInterface, i ->
                val databaseReference = FirebaseDatabase.getInstance().reference
                if (listItems[i] == "Update"){
                    context.startActivity(context.intentFor<AddHabitActivity>(
                        "from" to "update",
                        "type" to "habit",
                        "id" to habit[position].id,
                        "nama_kegiatan" to habit[position].name,
                        "poin_plus" to habit[position].point_plus.toString(),
                        "poin_minus" to habit[position].point_minus.toString()
                    ))
                }else {
                    context.alert(R.string.habit_delete_alert_content, R.string.habit_delete_alert_title){
                        positiveButton("Ok"){
                            //delete data
                            databaseReference.child(userName)
                                .child(userId)
                                .child("habit")
                                .child(habit[position].id!!)
                                .removeValue()
                        }
                        negativeButton("Cancel"){ it.dismiss() }
                    }.show()
                }
                dialogInterface.dismiss()
            }.show()
            true
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val databaseReference = FirebaseDatabase.getInstance().reference
        private lateinit var context: Context
        private val userId: String by lazy { PreferenceHelper(context).userId!! }
        private val userName: String by lazy { PreferenceHelper(context).userName!! }

        fun bindView(items: Habit, context: Context){
            this.context = context
            itemView.textview_habit_name.text = items.name

            itemView.button_habit_substract.background = context.resources.getDrawable(R.drawable.shape_left_corner_filled)
            itemView.button_habit_add.background = context.resources.getDrawable(R.drawable.shape_right_corner_filled)

            databaseReference.child(userName)
                .child(userId)
                .addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val child = dataSnapshot.getValue(Child::class.java)

                        itemView.button_habit_add.setOnClickListener {
                            databaseReference.child(userName)
                                .child(userId)
                                .child("points")
                                .setValue(child?.points?.plus(items.point_plus!!))

                            databaseReference.child(userName)
                                .child(userId)
                                .child("totalrewards")
                                .setValue(child?.totalrewards?.plus(items.point_plus?.times(child.reward!!)!!))

                            MediaPlayer.create(context, R.raw.yeey).start()
                        }

                        itemView.button_habit_substract.setOnClickListener {
                            databaseReference.child(userName)
                                .child(userId)
                                .child("points")
                                .setValue(child?.points?.minus(items.point_plus!!))

                            databaseReference.child(userName)
                                .child(userId)
                                .child("totalrewards")
                                .setValue(child?.totalrewards?.minus(items.point_minus?.times(child.reward!!)!!))

                            MediaPlayer.create(context, R.raw.huu).start()
                        }
                    }

                })

        }
    }

}