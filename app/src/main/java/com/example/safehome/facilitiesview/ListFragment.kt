package com.example.safehome.facilitiesview

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.FaciListAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentListBinding
import com.example.safehome.model.AllFacilitiesModel
import com.example.safehome.model.CategoryModel
import com.example.safehome.model.FacilitiesGalleyImagesModel
import com.example.safehome.model.FacilitiesList
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.viewpageradapter.FacilitiesGalleryViewPagerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.schedule


class ListFragment : Fragment() {
    private var galleryViewPager: ViewPager?= null
    private var galleryImageSliderDotsCount = 0
    private lateinit var galleyDotsImagesArray : Array<ImageView?>
    private lateinit var timer: Timer
    private lateinit var binding: FragmentListBinding
    private var fList: ArrayList<FacilitiesList> = ArrayList()
    private var galleryImagesList:ArrayList<FacilitiesGalleyImagesModel> = ArrayList()
    private lateinit var faciListAdapter: FaciListAdapter
    private var bookNowDialog: Dialog? = null
    private var confirmationDialog: Dialog? = null
    private var viewDetailsDialog: Dialog? = null
    private var showGalleryDetailsDialog: Dialog? = null
    private lateinit var number_of_days_et: TextView
    private lateinit var time_layout: LinearLayout
    private lateinit var number_of_hour_header: TextView
    private lateinit var number_of_hours_et: TextView
    private var start_time: TextView? = null
    private var end_time: TextView? = null
    private var end_date_txt: TextView? = null
    private var start_date_txt: TextView? = null
    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()

    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""

    private lateinit var allFacilitiesModel : Call<AllFacilitiesModel>
    private var allFacilitiesModelList : ArrayList<AllFacilitiesModel.Data.Facility> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

//        addData()
        getAllFacilities()
        return binding.root
    }

    private fun addGalleyImagesData(imageList : List<AllFacilitiesModel.Data.Facility.FacilityImage>) {

        if (galleryImagesList.isNotEmpty()){
            galleryImagesList.clear()
        }
        for (model in imageList){
            galleryImagesList.add(FacilitiesGalleyImagesModel(model.imagePath))
        }

    }

    private fun addData() {
        fList.add(FacilitiesList("Club House", 2000, R.drawable.club_house))
        fList.add(FacilitiesList("Auditorium", 2000, R.drawable.banquet_hall))  //auditorium
        fList.add(FacilitiesList("Banquet Hall", 2000, R.drawable.club_house))
    }

    private fun populateData() {
        if (allFacilitiesModelList.size == 0){
            binding.emptyFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyFacilitiesTxt.visibility = View.GONE
            binding.fListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            faciListAdapter = FaciListAdapter(requireContext(), allFacilitiesModelList)
            binding.fListRecyclerView.adapter = faciListAdapter
            faciListAdapter.setCallback(this@ListFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllFacilities() {
        //progressbar
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        // here sign up service call
        allFacilitiesModel = apiInterface.getAllFacilities(
            "Bearer " + Auth_Token
        )
        allFacilitiesModel.enqueue(object : Callback<AllFacilitiesModel> {
            override fun onResponse(
                call: Call<AllFacilitiesModel>,
                response: Response<AllFacilitiesModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.facilitys.isNotEmpty()) {
                        if (allFacilitiesModelList.isNotEmpty()) {
                            allFacilitiesModelList.clear()
                        }
                        val allFacilitiesModelResponse = response.body() as AllFacilitiesModel
                        allFacilitiesModelList = allFacilitiesModelResponse.data.facilitys as ArrayList<AllFacilitiesModel.Data.Facility>

                    } else {
                        // vehilceModelDropDown()
                    }
                    populateData()

                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<AllFacilitiesModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectedBooknow(myDues: AllFacilitiesModel.Data.Facility) {
        // bookNowDialog(myDues)
        try {
            val fIntent = Intent(requireContext(), ListBookNowActivity::class.java)
            fIntent.putExtra("bookType", myDues.name)
            fIntent.putExtra("facilityId", myDues.facilityId)
            fIntent.putExtra("from", "List")
            fIntent.putExtra("bookByDay", myDues.residentsChargeByDay)
            fIntent.putExtra("bookByHour", myDues.residentsChargeByHour)
            fIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            requireContext().startActivity(fIntent)
        } catch (e: Exception) {
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun bookNowDialog(myDues: FacilitiesList) {
        /*       val layoutInflater: LayoutInflater =
                   requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
               val view = layoutInflater.inflate(R.layout.book_now_dialog, null)
               bookNowDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
               bookNowDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
               bookNowDialog!!.setContentView(view)
               bookNowDialog!!.setCanceledOnTouchOutside(true)
               bookNowDialog!!.setCancelable(true)

               val lp = WindowManager.LayoutParams()
               lp.copyFrom(bookNowDialog!!.window!!.attributes)

               lp.width = (Utils.screenWidth * 0.95).toInt()
               lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
               lp.gravity = Gravity.CENTER
               bookNowDialog!!.window!!.attributes = lp
               val close = view.findViewById<ImageView>(R.id.close_btn_click)
               val purpose_et = view.findViewById<EditText>(R.id.purpose_et)
               purpose_et.setBackgroundResource(android.R.color.transparent)

               number_of_hour_header = view.findViewById<TextView>(R.id.number_of_hour_header)
               number_of_hours_et = view.findViewById<EditText>(R.id.number_of_hours_et)
               number_of_days_et = view.findViewById(R.id.number_of_days_et)
               time_layout = view.findViewById(R.id.time_layout)
             //  number_of_days_et.addTextChangedListener(textWatcher);
               start_time = view.findViewById<TextView>(R.id.start_time_text)
               end_time = view.findViewById<TextView>(R.id.end_time)

               val comments_et = view.findViewById<EditText>(R.id.comments_et)
               comments_et.setBackgroundResource(android.R.color.transparent)

               val send_request_tv = view.findViewById<TextView>(R.id.send_request_tv)
               send_request_tv.setOnClickListener {
                  // confirmationPopup()
               }
               end_date_txt = view.findViewById<TextView>(R.id.end_date_txt)
               start_date_txt = view.findViewById<TextView>(R.id.start_date_txt)

               val title_tv = view.findViewById<TextView>(R.id.title_tv)
               title_tv.setText(myDues.fType)

               start_date_txt?.setOnClickListener {
                  val startDateDialog =  DatePickerDialog(requireContext(),
                       dateSetListener_book_now_start_date,
                       // set DatePickerDialog to point to today's date when it loads up
                       cal_startDate.get(Calendar.YEAR),
                       cal_startDate.get(Calendar.MONTH),
                       cal_startDate.get(Calendar.DAY_OF_MONTH))

                   startDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                   startDateDialog.show()
               }

               end_date_txt?.setOnClickListener {
                  val endDateDialog = DatePickerDialog(requireContext(),
                       dateSetListener__book_now_end_date,
                       // set DatePickerDialog to point to today's date when it loads up
                       cal_endDate.get(Calendar.YEAR),
                       cal_endDate.get(Calendar.MONTH),
                       cal_endDate.get(Calendar.DAY_OF_MONTH))
                   // Set a minimum date to disable previous dates
                   try {
                       val dateInMillis = Utils.convertStringToDateMillis(start_date_txt!!.text.toString(), "dd/MM/yyyy")
                       endDateDialog.datePicker.minDate = dateInMillis
                   }catch (e: Exception){
                       endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // Subtract 1 second to prevent selecting the current day
                   }
                  // endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                   endDateDialog.show()
               }

               start_time?.setOnClickListener {
                   val mcurrentTime = Calendar.getInstance()
                   val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
                   val minute = mcurrentTime[Calendar.MINUTE]
                   val mTimePicker: TimePickerDialog
                   mTimePicker = TimePickerDialog(
                       requireContext(),
                       { timePicker, selectedHour, selectedMinute ->
                           setTime("StartTime", selectedHour, selectedMinute)
                       },
                       hour,
                       minute,
                       true
                   ) //Yes 24 hour time

                   mTimePicker.setTitle("Select Time")
                   mTimePicker.show()
               }

               end_time?.setOnClickListener {
                   val mcurrentTime = Calendar.getInstance()
                   val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
                   val minute = mcurrentTime[Calendar.MINUTE]
                   val mTimePicker: TimePickerDialog
                   mTimePicker = TimePickerDialog(
                       requireContext(),
                       { timePicker, selectedHour, selectedMinute ->
                           setTime("EndTime", selectedHour, selectedMinute)
                       },
                       hour,
                       minute,
                       true
                   ) //Yes 24 hour time

                   mTimePicker.setTitle("Select Time")
                   mTimePicker.show()
               }

               close.setOnClickListener {
                   if (bookNowDialog!!.isShowing) {
                       bookNowDialog!!.dismiss()
                   }
               }
               bookNowDialog!!.show()*/
    }

    private fun setTime(Time: String, selectedHour: Int, selectedMinute: Int) {
        try {
            var hour_str: String? = null
            if (selectedHour < 10) {
                hour_str = "0" + selectedHour
            } else {
                hour_str = selectedHour.toString()
            }

            var minute_str: String? = null
            if (selectedMinute < 10) {
                minute_str = "0" + selectedMinute
            } else {
                minute_str = selectedMinute.toString()
            }
            if (Time.equals("StartTime")) {
                start_time?.setText("$hour_str:$minute_str")
            } else {
                end_time?.setText("$hour_str:$minute_str")
            }
        } catch (ex: Exception) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun confirmationPopup() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.confirmation_popup, null)
        confirmationDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        confirmationDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        confirmationDialog!!.setContentView(view)
        confirmationDialog!!.setCanceledOnTouchOutside(true)
        confirmationDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(confirmationDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        confirmationDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val text_msg: TextView = view.findViewById<TextView>(R.id.text_msg)
        val ok_btn: TextView = view.findViewById(R.id.ok_btn)

        text_msg.setText("Your Booking request has been sent successfully.")

        ok_btn.setOnClickListener {
            if (confirmationDialog!!.isShowing) {
                confirmationDialog!!.dismiss()
            }
            if (bookNowDialog != null) {
                if (bookNowDialog!!.isShowing) {
                    bookNowDialog!!.dismiss()
                }
            }
        }

        close.setOnClickListener {
            if (confirmationDialog!!.isShowing) {
                confirmationDialog!!.dismiss()
            }
        }

        confirmationDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectedViewDetails(facilitiesList: AllFacilitiesModel.Data.Facility) {
        viewDetailsPopup(facilitiesList)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun viewDetailsPopup(facilitiesList: AllFacilitiesModel.Data.Facility) {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_details_admin_popup, null)
        viewDetailsDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        viewDetailsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        viewDetailsDialog!!.setContentView(view)
        viewDetailsDialog!!.setCanceledOnTouchOutside(true)
        viewDetailsDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(viewDetailsDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        viewDetailsDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val per_hour_text: TextView = view.findViewById<TextView>(R.id.per_hour_text)
        val per_day_text: TextView = view.findViewById<TextView>(R.id.per_day_text)
        val ok_btn: TextView = view.findViewById(R.id.ok_btn)
        per_hour_text.setText(getString(R.string.Rs)+" "+facilitiesList.residentsChargeByHour +"/- Per Hour ")
        per_day_text.setText(getString(R.string.Rs)+ " "+ facilitiesList.residentsChargeByDay +"/- Per Day")

        close.setOnClickListener {
            if (viewDetailsDialog!!.isShowing) {
                viewDetailsDialog!!.dismiss()
            }
        }

        ok_btn.setOnClickListener {
            if (viewDetailsDialog!!.isShowing) {
                viewDetailsDialog!!.dismiss()
            }
        }

        viewDetailsDialog!!.show()
    }



    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            var text = number_of_days_et.text.toString()
            try {
                if (text.toInt() >= 2) {
                    time_layout.visibility = View.GONE
                    number_of_hour_header.visibility = View.GONE
                    number_of_hours_et.visibility = View.GONE
                } else {
                    time_layout.visibility = View.VISIBLE
                    number_of_hour_header.visibility = View.VISIBLE
                    number_of_hours_et.visibility = View.VISIBLE
                }
            } catch (ex: NumberFormatException) {
            }
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
            start_date_txt!!.text = sdf.format(cal_startDate.getTime())
        }
    }

    // create an OnDateSetListener
    val dateSetListener__book_now_end_date = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            // disable dates before today
            val disablePastDates = Calendar.getInstance().timeInMillis- 1000
            view.setMinDate(disablePastDates)
            cal_endDate.set(Calendar.YEAR, year)
            cal_endDate.set(Calendar.MONTH, monthOfYear)
            cal_endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            end_date_txt!!.text = sdf.format(cal_endDate.getTime())
        }
    }

    fun showGalleryImages(imageList : List<AllFacilitiesModel.Data.Facility.FacilityImage>) {
        addGalleyImagesData(imageList)
        showGalleryPopup()
    }

    @SuppressLint("MissingInflatedId")
    private fun showGalleryPopup() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.show_facilities_gallerylist_dialog, null)
        showGalleryDetailsDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        showGalleryDetailsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        showGalleryDetailsDialog!!.setContentView(view)
        showGalleryDetailsDialog!!.setCanceledOnTouchOutside(true)
        showGalleryDetailsDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(showGalleryDetailsDialog!!.window!!.attributes)

        showGalleryImagesViewPager(view)
        viewPagerAddChangeListener(view)
        val cancelImg = view.findViewById<ImageView>(R.id.gallery_dialog_closeImg)
        cancelImg.setOnClickListener{
            showGalleryDetailsDialog!!.dismiss()
            if (timer!= null){
                timer.cancel()
            }
        }

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        showGalleryDetailsDialog!!.window!!.attributes = lp
        showGalleryDetailsDialog!!.show()
    }

    private fun showGalleryImagesViewPager(view: View?) {
        galleryViewPager = view?.findViewById<ViewPager>(R.id.gallery_dialog_viewPager)
        val galleyViewPagerDotsLayout = view?.findViewById<LinearLayout>(R.id.gallery_view_pager_slider_dots)

        if(galleryImagesList.isNotEmpty()){
            // setup the gallery viewpager
            val facilitiesGalleryViewPagerAdapter = FacilitiesGalleryViewPagerAdapter(requireContext(), galleryImagesList)
            galleryViewPager!!.adapter = facilitiesGalleryViewPagerAdapter
            galleryImageSliderDotsCount = facilitiesGalleryViewPagerAdapter.count
            galleyDotsImagesArray= arrayOfNulls(galleryImageSliderDotsCount)
            for (i in 0 until galleryImageSliderDotsCount) {
                galleyDotsImagesArray[i] = ImageView(requireContext())
                galleyDotsImagesArray[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.non_active_dot
                    )
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                galleyViewPagerDotsLayout!!.addView(galleyDotsImagesArray[i], params)
            }

            galleyDotsImagesArray[0]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.active_dot
                )
            )

            timer = Timer()
            timer.schedule(5000, 5000) {
                runOnUiThread {
                    val currentItem = galleryViewPager!!.currentItem
                    if (currentItem <  galleyDotsImagesArray.size - 1) {
                        galleryViewPager!!.currentItem = currentItem + 1
                    } else {
                        galleryViewPager!!.currentItem = 0
                    }
                }
            }
        }

    }


    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }

    private fun viewPagerAddChangeListener(view: View) {
        //top slider
        galleryViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // nothing here
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until galleryImageSliderDotsCount) {
                    try {
                        galleyDotsImagesArray[i]?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.non_active_dot
                            )
                        )
                    } catch (ex: IllegalStateException) {
                    }
                }
                try {
                    galleyDotsImagesArray[position]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.active_dot
                        )
                    )
                } catch (ex: IllegalStateException) {
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // nothing here
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        /*  if (timer!= null){
              timer.cancel()
          }*/
    }
}