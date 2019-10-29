package com.habittracker.fragment.punishment

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.habittracker.R
import com.habittracker.activity.addpunishment.AddPunishmentActivity
import com.habittracker.library.PreferenceHelper
import com.habittracker.model.Punishment
import kotlinx.android.synthetic.main.item_punishment.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor
import java.util.concurrent.TimeUnit

class PunishmentAdapter(private val punishment: MutableList<Punishment> = mutableListOf()):
        RecyclerView.Adapter<PunishmentAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_punishment, parent, false))
    }

    override fun getItemCount(): Int = punishment.size

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        val prefs = context.getSharedPreferences("source.prefs.user.timer."+holder.index, MODE_PRIVATE)
        val editor = prefs!!.edit()

        editor.putLong("millis_left", holder.getTimeLeftInMillis())
        editor.putBoolean("timer_running", holder.getTimerRunning())
        editor.putLong("end_time", holder.getEndTime())

        editor.apply()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.context = context
        holder.index = position
        holder.bindView(punishment[position])

        holder.itemView.setOnLongClickListener {
            val listItems = arrayOf("Update", "Delete")
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setItems(listItems) { dialogInterface, i ->
                val databaseReference = FirebaseDatabase.getInstance().reference
                if (listItems[i] == "Update"){
                    context.startActivity(context.intentFor<AddPunishmentActivity>(
                        "from" to "update",
                        "type" to "habit",
                        "id" to punishment[position].id,
                        "punishment_name" to punishment[position].name,
                        "time_in_millis" to punishment[position].timeInMillis
                    ))
                }else {
                    context.alert(R.string.habit_delete_alert_content, R.string.habit_delete_alert_title){
                        positiveButton("Ok"){
                            //delete data
                            databaseReference.child(PreferenceHelper(context).userName)
                                .child(PreferenceHelper(context).userId)
                                .child("punishment")
                                .child(punishment[position].id!!)
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
        var context: Context? = null
        var index: Int? = 0
        private var mCountDownTimer: CountDownTimer? = null

        private var mTimerRunning: Boolean = false
        private var mTimeLeftInMillis: Long = 0
        private var mEndTime: Long = 0

        private var timeMillis: Long = 0

        fun getTimerRunning():Boolean{
            return mTimerRunning
        }
        fun getTimeLeftInMillis():Long{
            return mTimeLeftInMillis
        }
        fun getEndTime():Long{
            return mEndTime
        }

        fun bindView(item: Punishment){
            itemView.textview_item_punishment_title.text = item.name

            item.timeInMillis?.let { timeMillis = it }
//            mTimeLeftInMillis = timeMillis

            initDataFromPref()
            updateCountDownText()

            itemView.imageview_item_punishment_play.setOnClickListener {
                if (mTimerRunning) {
                    pauseTimer()
                } else {
                    startTimer()
                }
            }
            itemView.imageview_item_punishment_pause.setOnClickListener {
                if (mTimerRunning) {
                    pauseTimer()
                } else {
                    startTimer()
                }
            }
            itemView.imageview_item_punishment_stop.setOnClickListener {
                resetTimer()
            }
        }

        private fun initDataFromPref() {
            val prefs = context?.getSharedPreferences("source.prefs.user.timer."+index, MODE_PRIVATE)

            mTimeLeftInMillis = prefs!!.getLong("millis_left", timeMillis)
            mTimerRunning = prefs.getBoolean("timer_running", false)

            updateCountDownText()
            updateButtons()

            if (mTimerRunning) {
                mEndTime = prefs.getLong("end_time", 0)
                mTimeLeftInMillis = mEndTime - System.currentTimeMillis()

                if (mTimeLeftInMillis < 0) {
                    mTimeLeftInMillis = 0
                    mTimerRunning = false
                    updateCountDownText()
                    updateButtons()
                } else {
                    startTimer()
                }
            }
        }

        private fun startTimer() {
            mEndTime = System.currentTimeMillis() + mTimeLeftInMillis

            mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    mTimeLeftInMillis = millisUntilFinished
                    updateCountDownText()
                }

                override fun onFinish() {
                    mTimerRunning = false
                    updateButtons()
                }
            }.start()

            mTimerRunning = true
            updateButtons()
        }

        private fun pauseTimer() {
            mCountDownTimer?.cancel()
            mTimerRunning = false
            updateButtons()
        }

        private fun resetTimer() {
            mTimeLeftInMillis = timeMillis
            pauseTimer()
            updateCountDownText()
            updateButtons()
        }

        private fun updateCountDownText() {
            val timeFormatted = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis),
                TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis)),
                TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis)))
            itemView.textview_item_punishment_time.text = timeFormatted
        }

        private fun updateButtons() {
            if (mTimerRunning) {
                itemView.imageview_item_punishment_play.visibility = View.GONE
                itemView.imageview_item_punishment_pause.visibility = View.VISIBLE
            } else {
                itemView.imageview_item_punishment_play.visibility = View.VISIBLE
                itemView.imageview_item_punishment_pause.visibility = View.GONE
            }
        }
    }
}