package com.example.safehome.visitors.cab

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.databinding.FragmentAllowFrequentlyBinding
import com.example.safehome.databinding.FragmentCabAllowFrequentlyBinding
import com.example.safehome.databinding.FragmentCabAllowOnceBinding
import com.example.safehome.visitors.guest.GuestTypeAdapter
import com.example.safehome.visitors.guest.selectguest.SelectGuestActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CabAllowFrequentlyFragment : Fragment() {
    private lateinit var binding: FragmentCabAllowFrequentlyBinding
    private var categoryPopupWindow: PopupWindow? = null
    private var schedulePopupWindow: PopupWindow? = null
    private var guestTypeList: ArrayList<String> = ArrayList()
    private var scheduleList: ArrayList<String> = ArrayList()
    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCabAllowFrequentlyBinding.inflate(inflater, container, false)


        setGuestType()
        setSchedule()
        clickEvents()


        return binding.root
    }

    private fun setSchedule() {
        scheduleList.add("Weekly")
        scheduleList.add("Monthly")
        scheduleList.add("Custom")

    }

    private fun setGuestType() {

        guestTypeList.add("Ola")
        guestTypeList.add("Uber")
        guestTypeList.add("Quick Ride")
        guestTypeList.add("Meru")
        guestTypeList.add("Rapido")
        guestTypeList.add("Other")

    }

    private fun clickEvents() {

        binding.clSelectGuestType.setOnClickListener {


            if (schedulePopupWindow != null) {
                if (schedulePopupWindow!!.isShowing) {
                    schedulePopupWindow!!.dismiss()
                }
            }


            if (categoryPopupWindow != null) {
                if (categoryPopupWindow!!.isShowing) {
                    categoryPopupWindow!!.dismiss()
                } else {
                    categoryDropDown()
                }
            } else {
                categoryDropDown()
            }
        }
        binding.clSchedule.setOnClickListener {

            if (categoryPopupWindow != null) {
                if (categoryPopupWindow!!.isShowing) {
                    categoryPopupWindow!!.dismiss()
                }
            }
            if (schedulePopupWindow != null) {
                if (schedulePopupWindow!!.isShowing) {
                    schedulePopupWindow!!.dismiss()
                } else {
                    scheduleDropDown()
                }
            } else {
                scheduleDropDown()
            }
        }
        binding.startDateTxt?.setOnClickListener {
            val startDateDialog = DatePickerDialog(
                requireContext(),
                dateSetListener_book_now_start_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_startDate.get(Calendar.YEAR),
                cal_startDate.get(Calendar.MONTH),
                cal_startDate.get(Calendar.DAY_OF_MONTH)
            )

            startDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            startDateDialog.show()
        }

        binding.endDateTxt?.setOnClickListener {
            val endDateDialog = DatePickerDialog(
                requireContext(),
                dateSetListener__book_now_end_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_endDate.get(Calendar.YEAR),
                cal_endDate.get(Calendar.MONTH),
                cal_endDate.get(Calendar.DAY_OF_MONTH)
            )
            // Set a minimum date to disable previous dates
            try {
                val dateInMillis = Utils.convertStringToDateMillis(
                    binding.startDateTxt!!.text.toString(),
                    "dd/MM/yyyy"
                )
                endDateDialog.datePicker.minDate = dateInMillis
            } catch (e: Exception) {
                endDateDialog.datePicker.minDate =
                    System.currentTimeMillis() - 1000 // Subtract 1 second to prevent selecting the current day
            }
            // endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            endDateDialog.show()
        }





        binding.startTimeText?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, selectedHour, selectedMinute ->
                    //setTime("StartTime", selectedHour, selectedMinute)
                    binding.startTimeText!!.text = String.format(
                        "%02d:%02d %s", selectedHour, selectedMinute,
                        if (selectedHour < 12) "AM" else "PM"
                    )

                },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

    }

    private fun categoryDropDown() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)
        categoryPopupWindow = PopupWindow(
            view,
            binding.selectGuestTypeTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (guestTypeList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val guestTypeAdapter = GuestTypeAdapter(requireContext(), guestTypeList)

            dropDownRecyclerView.adapter = guestTypeAdapter
            guestTypeAdapter.setCallbackServiceProvider1(this@CabAllowFrequentlyFragment)
        }
        categoryPopupWindow!!.elevation = 10F
        categoryPopupWindow!!.showAsDropDown(binding.selectGuestTypeTxt, 0, 0, Gravity.CENTER)
    }

    private fun scheduleDropDown() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)
        schedulePopupWindow = PopupWindow(
            view,
            binding.scheduleTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (scheduleList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val guestTypeAdapter = GuestTypeAdapter(requireContext(), scheduleList)

            dropDownRecyclerView.adapter = guestTypeAdapter
            guestTypeAdapter.setCallbackDate(this@CabAllowFrequentlyFragment)
        }
        schedulePopupWindow!!.elevation = 10F
        schedulePopupWindow!!.showAsDropDown(binding.scheduleTxt, 0, 0, Gravity.CENTER)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun setGuestType(guestType: String) {
        if (categoryPopupWindow != null) {
            if (categoryPopupWindow!!.isShowing) {
                categoryPopupWindow!!.dismiss()
            }
        }
        if (guestType != null) {
            binding.selectGuestTypeTxt.text = guestType
//            selectedCategoryId = model.categoryId.toString()

            if (guestType.equals("Other")){
                binding.etServiceProvider.visibility = View.VISIBLE
                binding.tvServiceProvider.visibility = View.VISIBLE
            }else{
                binding.etServiceProvider.visibility = View.GONE
                binding.tvServiceProvider.visibility = View.GONE
            }

        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setSchedule(guestType: String) {
        if (schedulePopupWindow != null) {
            if (schedulePopupWindow!!.isShowing) {
                schedulePopupWindow!!.dismiss()
            }
        }
        if (guestType != null) {
            binding.scheduleTxt.text = guestType
//            selectedCategoryId = model.categoryId.toString()

            if (guestType.equals("Custom")) {

                binding.llDate.visibility = View.VISIBLE
            } else {
                binding.llDate.visibility = View.GONE
            }

        }


    }

    val dateSetListener__book_now_end_date = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            // disable dates before today
            val disablePastDates = Calendar.getInstance().timeInMillis - 1000
            view.setMinDate(disablePastDates)
            cal_endDate.set(Calendar.YEAR, year)
            cal_endDate.set(Calendar.MONTH, monthOfYear)
            cal_endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.endDateTxt!!.text = sdf.format(cal_endDate.getTime())
        }
    }

    // create an OnDateSetListener
    val dateSetListener_book_now_start_date = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            cal_startDate.set(Calendar.YEAR, year)
            cal_startDate.set(Calendar.MONTH, monthOfYear)
            cal_startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            // disable dates before today
            val disablePastDates = Calendar.getInstance().timeInMillis
            view.setMinDate(disablePastDates)

            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.startDateTxt!!.text = sdf.format(cal_startDate.getTime())
        }
    }

}