package com.example.weatherguide.alarmScreen.view

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.alarmScreen.broadcastReceiver.AlarmReceiver
import com.example.weatherguide.alarmScreen.viewModel.AlarmViewModel
import com.example.weatherguide.alarmScreen.viewModel.AlarmViewModelFactory
import com.example.weatherguide.databinding.FragmentAlertsBinding
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.favoriteScreen.OnClickListener
import com.example.weatherguide.network.ApiState
import com.example.weatherguide.model.AlarmDate
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class AlertsFragment : Fragment(), OnClickListener<AlarmDate> {
    private lateinit var binding: FragmentAlertsBinding

    private val selectedDate = Calendar.getInstance()
    private val selectedTime = Calendar.getInstance()

    private lateinit var adapter: AlarmAdapter
    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var alarmViewModelFactory: AlarmViewModelFactory
    lateinit var alarms: List<AlarmDate>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AlarmAdapter(
            emptyList(), this, requireContext()
        )
        binding.alarmRecyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.alarmRecyclerView.layoutManager = linearLayoutManager
        alarmViewModelFactory = AlarmViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteSourceDataImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())
            )
        )
        alarmViewModel = ViewModelProvider(
            requireActivity(), alarmViewModelFactory
        )[AlarmViewModel::class.java]
        binding.alarmRecyclerView.adapter = adapter
        lifecycleScope.launch {
            alarmViewModel.alarm.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        binding.alarmRecyclerView.visibility = View.GONE
                        showLoading(true)
                    }

                    is ApiState.Success -> {
                        showLoading(false)
                        binding.alarmRecyclerView.visibility = View.VISIBLE
                        alarms = state.data
                        showData(state.data)
                    }

                    else -> {
                        showLoading(false)
                    }
                }
            }
        }
        binding.fabAddAlarm.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notificationManager =
                        requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (notificationManager.areNotificationsEnabled()) {
                        showDatePickerDialog()
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            123
                        )
                    }
                } else {
                    showDatePickerDialog()
                }
            } else {
                showDatePickerDialog()
            }
        }
    }

    private fun showTimePickerDialog() {

        val currentTime = Calendar.getInstance()

        val timePickerDialog = TimePickerDialog(
            requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }
                if (selectedTime.timeInMillis >= currentTime.timeInMillis) {
                    scheduleAlarm(selectedDate, selectedTime)
                } else {
                    val rootView = requireActivity().findViewById<View>(android.R.id.content)
                    Snackbar.make(rootView, "Please select a future time.", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false
        )

        timePickerDialog.show()
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                showTimePickerDialog()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }

    private fun scheduleAlarm(date: Calendar, time: Calendar) {
        val alarmDate = AlarmDate(
            id = System.currentTimeMillis(),
            dateTime = formatDateTime(date, time)
        )
        alarmViewModel.addAlarm(alarmDate)

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("alarm_id", alarmDate.id)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarmDate.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmTime = Calendar.getInstance().apply {
            set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                0
            )
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AlarmManager.RTC_WAKEUP
        } else {
            AlarmManager.RTC
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                alarmType, alarmTime.timeInMillis, pendingIntent
            )
        } else {
            alarmManager.setExact(alarmType, alarmTime.timeInMillis, pendingIntent)
        }

    }

    private fun formatDateTime(date: Calendar, time: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("hh:mm a")
        val formattedDate = dateFormat.format(date.time)
        val formattedTime = timeFormat.format(time.time)
        return "$formattedTime\n$formattedDate"
    }

    private fun showData(alarms: List<AlarmDate>) {
        adapter.apply {
            val sortedList = alarms.sortedBy { alarmDate ->
                SimpleDateFormat("hh:mm a\nyyyy-MM-dd").parse(alarmDate.dateTime)
            }
            setList(sortedList)
            notifyDataSetChanged()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onClickRemove(item: AlarmDate) {
        alarmViewModel.removeAlarm(item)
        cancelAlarm(item)
    }

    private fun cancelAlarm(alarmDate: AlarmDate) {

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarmDate.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    override fun onClickLocationFavorite(item: AlarmDate) {

    }

}
