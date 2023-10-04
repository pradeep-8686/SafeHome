package com.example.safehome.complaints

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AvailabilityTimeAdapter
import com.example.safehome.adapter.ComplaintStatusAdapter
import com.example.safehome.adapter.HistoryAdapter
import com.example.safehome.adapter.UpcomingAdapter
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentPersonalBinding
import com.example.safehome.databinding.FragmentUpcomingBinding
import com.example.safehome.maintenance.CategoryAdapter
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.model.CategoryModel
import com.example.safehome.model.Events
import com.example.safehome.model.MaintenanceHistoryModel
import com.example.safehome.model.MyDues
import com.example.safehome.model.PersonalComplaintsModel
import com.example.safehome.model.UpcomingEventsModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class PersonalFragment : Fragment() {

    private lateinit var binding: FragmentPersonalBinding
    private var personalComplaintsModelList: ArrayList<PersonalComplaintsModel> = ArrayList()
    private lateinit var personalComplaintsAdapter: PersonalComplaintsAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var callPersonalComplaintsModel: Call<PersonalComplaintsModel>

    private var yearList: ArrayList<String> = ArrayList()
    private var complaintStatusList: ArrayList<String> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private var categoryPopupWindow: PopupWindow? = null
    private var viewComplaints: PopupWindow? = null
    private var viewComplaintsDialog: Dialog? = null

    private var selectedCategoryId: String? =  null
    private var selectedYear: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPersonalBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

//        getAllUpcomingEventsApiCall()
         addData()
         populateData(personalComplaintsModelList)
        addYearList()
        addCategoryList()

        binding.yearCl.setOnClickListener {
            if (categoryPopupWindow != null) {
                if (categoryPopupWindow!!.isShowing) {
                    categoryPopupWindow!!.dismiss()
                }
            }

            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                } else {
                    yearDropDown()
                }
            } else {
                yearDropDown()
            }
        }
        binding.clCategory.setOnClickListener {
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
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

        binding.sampleEditText.setOnTouchListener { v, event ->
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                }
            }
            if (categoryPopupWindow != null) {
                if (categoryPopupWindow!!.isShowing) {
                    categoryPopupWindow!!.dismiss()
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (personalComplaintsModelList.isNotEmpty()){

                    filter(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.ivAddComplaint.setOnClickListener {
            val intent = Intent(requireContext(), RaiseComplaintActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllUpcomingEventsApiCall(complaintStatus: String? = null, year: String? = null) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        // here sign up service call
        callPersonalComplaintsModel = apiInterface.getPersonalComplaints("bearer " + Auth_Token, complaintStatus, year)
        callPersonalComplaintsModel.enqueue(object : Callback<PersonalComplaintsModel> {
            override fun onResponse(
                call: Call<PersonalComplaintsModel>,
                response: Response<PersonalComplaintsModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                Log.e("Response: ", response.body().toString())

                if (response.isSuccessful && response.body() != null) {
                    if (personalComplaintsModelList.isNotEmpty()) {
                        personalComplaintsModelList.clear()
                    }
                }
            }

            override fun onFailure(call: Call<PersonalComplaintsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun addData() {
        val attachPhoto =  ArrayList<Int>()
        attachPhoto.add(R.drawable.com_img_1)
        attachPhoto.add(R.drawable.com_img_2)

        val c1 = PersonalComplaintsModel("Pending", "Resident Level", "Plumbing Issue", "High", "Admin", "Yes", "Water pipe is broken. Need immediate action","2 Days Ago",  attachPhoto)
        personalComplaintsModelList.add(c1)
        val attachPhoto2 =  ArrayList<Int>()
        attachPhoto2.add(R.drawable.com_img_3)
        attachPhoto2.add(R.drawable.com_img_4)
        attachPhoto2.add(R.drawable.com_img_5)
//In-Progress
        val c2 = PersonalComplaintsModel("Pending", "Resident Level", "Plumbing Issue", "Medium", "Admin", "Yes", "Water pipe is broken. Need immediate action","2 Days Ago", attachPhoto2)
        personalComplaintsModelList.add(c2)

        val attachPhoto3 =  ArrayList<Int>()
        attachPhoto3.add(R.drawable.com_img_6)
        attachPhoto3.add(R.drawable.com_img_7)

        val c3 = PersonalComplaintsModel("Resolved", "Resident Level", "Plumbing Issue", "Low", "Admin", "Yes", "Water pipe is broken. Need immediate action","3 Days Ago",attachPhoto3)
        personalComplaintsModelList.add(c3)

        val attachPhoto4 =  ArrayList<Int>()
        val c4 = PersonalComplaintsModel("Reinitiate", "Resident Level", "Plumbing Issue", "High", "Admin", "Yes", "Water pipe is broken. Need immediate action","4 Days Ago",attachPhoto4)
        personalComplaintsModelList.add(c4)
    }

    private fun populateData(personalComplaintsModelList: ArrayList<PersonalComplaintsModel>) {
        if (personalComplaintsModelList.size == 0){
            binding.emptyEventsTxt.visibility = View.VISIBLE
        }else {
            binding.emptyEventsTxt.visibility = View.GONE
            binding.personalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            personalComplaintsAdapter = PersonalComplaintsAdapter(requireContext(), personalComplaintsModelList)
            binding.personalRecyclerView.adapter = personalComplaintsAdapter
            personalComplaintsAdapter.setCallback(this@PersonalFragment)
            personalComplaintsAdapter.notifyDataSetChanged()
        }
    }

    private fun addYearList() {
        yearList.add("2023")
        yearList.add("2022")
    }

    private fun addCategoryList() {
        complaintStatusList.add("Pending")
        complaintStatusList.add("In Progress")
        complaintStatusList.add("Resolved")
        complaintStatusList.add("Reinitiate")
    }

    private fun yearDropDown() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        yearPopupWindow = PopupWindow(
            view,
            binding.yearTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (yearList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val yearAdapter = YearAdapter(requireContext(), yearList)

            dropDownRecyclerView.adapter = yearAdapter
            yearAdapter.setCallbackComplaintsYear(this@PersonalFragment)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectComplaintYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if (year != null) {
            binding.yearTxt.text = year
        }
        selectedYear = year

        Log.d(HistoryFragment.TAG, "$selectedCategoryId!! , $selectedYear!!")
    }

    private fun categoryDropDown() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        categoryPopupWindow = PopupWindow(
            view,
            binding.tvCategory.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (complaintStatusList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = ComplaintStatusAdapter(requireContext(), complaintStatusList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackComplaintStatus(this@PersonalFragment)
        }
        categoryPopupWindow!!.elevation = 10F
        categoryPopupWindow!!.showAsDropDown(binding.tvCategory, 0, 0, Gravity.CENTER)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if (categoryPopupWindow != null) {
            if (categoryPopupWindow!!.isShowing) {
                categoryPopupWindow!!.dismiss()
            }
        }
        if (viewComplaints != null) {
            if (viewComplaints!!.isShowing) {
                viewComplaints!!.dismiss()
            }
        }
    }
    fun filter(text: String) {
        val myDuesList = ArrayList<PersonalComplaintsModel>()
        val courseAry: ArrayList<PersonalComplaintsModel> = personalComplaintsModelList

        for (eachCourse in courseAry) {

            if (
                !eachCourse.complaintType.isNullOrBlank() && eachCourse.complaintType.lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))

            ) {
                myDuesList.add(eachCourse)
            }
        }

        personalComplaintsAdapter.filterList(myDuesList);
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun clickAction(personalComplaintsModel: PersonalComplaintsModel){


        val intent = Intent(requireContext(), ComplaintStatusActivity::class.java)
        intent.putExtra("status", personalComplaintsModel.status)
        startActivity(intent)
        /*if (viewComplaints != null) {
            if (viewComplaints!!.isShowing) {
                viewComplaints!!.dismiss()
            } else {
                availableTimePopup(personalComplaintsModel)
            }
        } else {
            availableTimePopup(personalComplaintsModel)
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun availableTimePopup(personalComplaintsModel: PersonalComplaintsModel) {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.popup_complaints_layout, null)
        viewComplaintsDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        viewComplaintsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        viewComplaintsDialog!!.setContentView(view)
        viewComplaintsDialog!!.setCanceledOnTouchOutside(true)
        viewComplaintsDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(viewComplaintsDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        viewComplaintsDialog!!.window!!.attributes = lp

        val resolved = "Is Your Complaint Resolved?"
        val  delayed= "Is the Action getting delayed?"

        val close = view.findViewById<ImageView>(R.id.close)
        val yes_btn: TextView = view.findViewById(R.id.yes_btn)
        val tv_delayed: TextView = view.findViewById(R.id.tv_delayed)

        val radio_group_re_initiate = view.findViewById<RadioGroup>(R.id.radio_group_re_initiate)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val radioButtonYes = view.findViewById<RadioButton>(R.id.radioButtonYes)
        val re_initiate_yes = view.findViewById<RadioButton>(R.id.re_initiate_yes)
        val re_initiate_no = view.findViewById<RadioButton>(R.id.re_initiate_no)
        val radioButtonNo = view.findViewById<RadioButton>(R.id.radioButtonNo)
        val tv_escalate_to = view.findViewById<TextView>(R.id.tv_escalate_to)
        val escalate_to_cl = view.findViewById<ConstraintLayout>(R.id.escalate_to_cl)
        val img_attach_photo = view.findViewById<ImageView>(R.id.img_attach_photo)
        val tv_attach_photo = view.findViewById<TextView>(R.id.tv_attach_photo)
        val tv_re_initiate = view.findViewById<TextView>(R.id.tv_re_initiate)

        tv_re_initiate.visibility = View.GONE
        radio_group_re_initiate.visibility = View.GONE
        tv_escalate_to.visibility = View.GONE
        escalate_to_cl.visibility = View.GONE
        if (personalComplaintsModel.status == "Pending"){
            tv_delayed.text = delayed

        }else if (personalComplaintsModel.status == "Resolved"){
            tv_delayed.text = resolved

        }else{
            tv_delayed.text = delayed

        }
        // Set up a listener for the RadioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            // Check which radio button is selected
            when (selectedRadioButton) {
                radioButtonYes -> {
                    if (tv_delayed.text == delayed){
                        tv_escalate_to.visibility = View.VISIBLE
                        escalate_to_cl.visibility = View.VISIBLE
                    }else{
                        img_attach_photo.visibility = View.VISIBLE
                        tv_attach_photo.visibility = View.VISIBLE
                        tv_re_initiate.visibility = View.GONE
                        radio_group_re_initiate.visibility = View.GONE
                    }


                }
                radioButtonNo -> {

                    if (tv_delayed.text == delayed){
                        tv_escalate_to.visibility = View.GONE
                        escalate_to_cl.visibility = View.GONE
                    }else{
                        img_attach_photo.visibility = View.GONE
                        tv_attach_photo.visibility = View.GONE

                        tv_re_initiate.visibility = View.VISIBLE
                        radio_group_re_initiate.visibility = View.VISIBLE
                    }
                }
            }
        }

        radio_group_re_initiate.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            // Check which radio button is selected
            when (selectedRadioButton) {
                re_initiate_yes -> {
                        tv_escalate_to.visibility = View.VISIBLE
                        escalate_to_cl.visibility = View.VISIBLE
                    tv_escalate_to.text = "Assigned To"

                }
                re_initiate_no -> {

                        tv_escalate_to.visibility = View.GONE
                        escalate_to_cl.visibility = View.GONE

                }
            }
        }

        yes_btn.setOnClickListener{
            if (viewComplaintsDialog!!.isShowing) {
                viewComplaintsDialog!!.dismiss()
            }
        }


        close.setOnClickListener {
            if (viewComplaintsDialog!!.isShowing) {
                viewComplaintsDialog!!.dismiss()
            }
        }

        viewComplaintsDialog!!.show()
    }

    fun setCallbackComplaintStatus(ComplaintStatus: String) {
        if (categoryPopupWindow != null) {
            if (categoryPopupWindow!!.isShowing) {
                categoryPopupWindow!!.dismiss()
            }
        }
        if (ComplaintStatus != null) {
            binding.tvCategory.text = ComplaintStatus
            selectedCategoryId = ComplaintStatus
        }
        Log.d(HistoryFragment.TAG, "$selectedCategoryId , $selectedYear")
    }


}