package com.habittracker.fragment.entertainment

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.habittracker.R
import com.habittracker.activity.addhabit.AddHabitActivity
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Child
import com.habittracker.model.Entertainment
import kotlinx.android.synthetic.main.item_habit.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor

class EntertainmentAdapter(private val entertainment: MutableList<Entertainment> = mutableListOf()):
        androidx.recyclerview.widget.RecyclerView.Adapter<EntertainmentAdapter.ViewHolder>(){

    private lateinit var context: Context
    private val userId: String by lazy { PreferenceHelper(context).userId!! }
    private val userName: String by lazy { PreferenceHelper(context).userName!! }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false))
    }

    override fun getItemCount(): Int = entertainment.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(entertainment[position], context)
        holder.itemView.setOnLongClickListener {
            val listItems = arrayOf("Update", "Delete")
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setItems(listItems) { dialogInterface, i ->
                val databaseReference = FirebaseDatabase.getInstance().reference
                if (listItems[i] == "Update"){
                    context.startActivity(context.intentFor<AddHabitActivity>(
                        "from" to "update",
                        "type" to "entertainment",
                        "id" to entertainment[position].id,
                        "nama_kegiatan" to entertainment[position].name,
                        "poin_plus" to entertainment[position].coin_plus.toString(),
                        "poin_minus" to entertainment[position].coin_minus.toString()
                    ))
                }else {
                    context.alert(R.string.habit_delete_alert_content, R.string.habit_delete_alert_title){
                        positiveButton("Ok"){
                            databaseReference.child(userName)
                                .child(userId)
                                .child("entertainment")
                                .child(entertainment[position].id!!)
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

    class ViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        private val databaseReference = FirebaseDatabase.getInstance().reference
        private lateinit var context: Context
        private val userId: String by lazy { PreferenceHelper(context).userId!! }
        private val userName: String by lazy { PreferenceHelper(context).userName!! }

        fun bindView(items: Entertainment, context: Context){
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
                                .child("coins")
                                .setValue(child?.coins?.plus(items.coin_plus!!))

                            MediaPlayer.create(context, R.raw.yeey).start()
                        }

                        itemView.button_habit_substract.setOnClickListener {
                            databaseReference.child(userName)
                                .child(userId)
                                .child("coins")
                                .setValue(child?.coins?.minus(items.coin_plus!!))

                            MediaPlayer.create(context, R.raw.huu).start()
                        }
                    }

                })

        }
    }

}