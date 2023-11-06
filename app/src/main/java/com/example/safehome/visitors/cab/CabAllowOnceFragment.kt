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
import com.example.safehome.databinding.FragmentAllowOnceBinding
import com.example.safehome.databinding.FragmentCabAllowOnceBinding
import com.example.safehome.visitors.guest.GuestTypeAdapter
import com.example.safehome.visitors.guest.selectguest.SelectGuestActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CabAllowOnceFragment : Fragment() {

    private lateinit var binding : FragmentCabAllowOnceBinding
    private var categoryPopupWindow: PopupWindow? = null
    private var guestTypeList: ArrayList<String> = ArrayList()
    var cal_startDate = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCabAllowOnceBinding.inflate(inflater, container, false)


        setGuestType()
        clickEvents()

        return binding.root
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

        binding.startTimeText?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, selectedHour, selectedMinute ->
                    //setTime("StartTime", selectedHour, selectedMinute)
                    binding.startTimeText!!.text = String.format("%02d:%02d %s", selectedHour, selectedMinute,
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
            guestTypeAdapter.setCallbackServiceProvider(this@CabAllowOnceFragment)
        }
        categoryPopupWindow!!.elevation = 10F
        categoryPopupWindow!!.showAsDropDown(binding.selectGuestTypeTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setServiceProvider(guestType: String) {
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

    private val dateSetListener_book_now_start_date = object : DatePickerDialog.OnDateSetListener {
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