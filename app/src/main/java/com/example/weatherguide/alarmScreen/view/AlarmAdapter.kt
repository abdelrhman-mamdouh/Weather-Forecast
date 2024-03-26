package com.example.weatherguide.alarmScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.R
import com.example.weatherguide.databinding.AlarmItemBinding
import com.example.weatherguide.favoriteScreen.OnClickListener
import com.example.weatherguide.model.AlarmDate
import com.google.android.material.snackbar.Snackbar

class AlarmAdapter (
    private var alarm: List<AlarmDate>,
    private val listener: OnClickListener<AlarmDate>,
    private val context: Context
) : RecyclerView.Adapter<AlarmAdapter.ViewHolderAlarms>() {

    private lateinit var binding: AlarmItemBinding

    data class ViewHolderAlarms(val binding: AlarmItemBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAlarms {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.alarm_item, parent, false)
        return ViewHolderAlarms(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderAlarms, position: Int) {

        val currentAlarm = alarm[position]
        holder.binding.alarm = currentAlarm
        holder.binding.removeButton.setOnClickListener {
            listener.onClickRemove(currentAlarm)
            Snackbar.make(holder.itemView,"Alarms removed successfully",Snackbar.LENGTH_SHORT).show()
        }
    }
    override fun getItemCount(): Int {
        return alarm.size
    }

    fun setList(updatedLocations: List<AlarmDate>) {
        alarm = updatedLocations
        notifyDataSetChanged()
    }
}