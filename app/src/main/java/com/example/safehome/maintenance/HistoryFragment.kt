package com.example.safehome.maintenance

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.HistoryAdapter
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentHistoryBinding
import com.example.safehome.model.CategoryModel
import com.example.safehome.model.MaintenanceHistoryModel
import com.example.safehome.model.MyDues
import com.example.safehome.model.VehicleModel
import com.example.safehome.model.VehicleModelResp
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.services.ServicesTypeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private var myDyesList: ArrayList<MyDues> = ArrayList()
    private lateinit var historyAdapter: HistoryAdapter
    private var yearList: ArrayList<String> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private var categoryPopupWindow: PopupWindow? = null

    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var maintenanceHistoryModel: Call<MaintenanceHistoryModel>
    private var maintenanceHistoryList: ArrayList<MaintenanceHistoryModel.Data> = ArrayList()
    private lateinit var categoryMode: Call<CategoryModel>
    private var categoryModelList: ArrayList<CategoryModel.Data> = ArrayList()
    private var selectedCategoryId: String? =  null
    private var selectedYear: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

//        addMyDuesData()
        populateData()
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
                if (maintenanceHistoryList.isNotEmpty()){

                    filter(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


        return binding.root
    }

    fun filter(text: String) {

        val myDuesList = ArrayList<MaintenanceHistoryModel.Data>()
        val courseAry: ArrayList<MaintenanceHistoryModel.Data> = maintenanceHistoryList

        for (eachCourse in courseAry) {
            var invoiceDate: String?= null
            if (eachCourse.invoiceDate.isNotEmpty()){
                val invoiceDates = eachCourse.invoiceDate.split("T")
                invoiceDate = Utils.changeDateFormat(invoiceDates[0])
            }

            if (
                !eachCourse.catageroyName.isNullOrBlank() && eachCourse.catageroyName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.invoiceAmount.toString().isNullOrBlank() && eachCourse.invoiceAmount.toString().lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.paidAmount.toString().isNullOrBlank() && eachCourse.paidAmount.toString().lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.invoiceDate.isNullOrBlank() && eachCourse.invoiceDate.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.paymentStatus.isNullOrBlank() && eachCourse.paymentStatus.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))
            ) {
                myDuesList.add(eachCourse)
            }
        }

        historyAdapter.filterList(myDuesList);
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadDefaultData(categoryModelList : ArrayList<CategoryModel.Data>) {

        binding.yearTxt.text = "2023"
        selectedYear = yearList[0]

        if (categoryModelList.isNotEmpty()) {
            binding.tvCategory.text = categoryModelList[0].categoryName
            selectedCategoryId = categoryModelList[0].categoryId.toString()
        }
        getPaidMaintenanaceDetailsByResident( selectedCategoryId, selectedYear)
        Log.d(TAG, "$selectedCategoryId , $selectedYear")


    }

    private fun addYearList() {
        yearList.add("2023")
        yearList.add("2022")
        yearList.add("2021")
        yearList.add("2020")
    }


    private fun populateData() {

        if (maintenanceHistoryList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            historyAdapter = HistoryAdapter(requireContext(), maintenanceHistoryList)
            binding.historyRecyclerView.adapter = historyAdapter
        }
    }

    private fun addMyDuesData() {
        myDyesList.add(
            MyDues(
                "Common Area Maintainance", "", "Dec 2023",
                "27/07/2023", 500, R.drawable.common_area_maintainance_icon, "", "Online"
            )
        )
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
            yearAdapter.setCallback(this@HistoryFragment)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if (year != null) {
            binding.yearTxt.text = year
        }
        selectedYear = year
        if (selectedCategoryId != null){

            getPaidMaintenanaceDetailsByResident(selectedCategoryId!!, selectedYear!!)
        }else{
            getPaidMaintenanaceDetailsByResident(selectedYear!!)

        }

        Log.d(TAG, "$selectedCategoryId!! , $selectedYear!!")

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

        if (categoryModelList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = CategoryAdapter(requireContext(), categoryModelList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackServiceType(this@HistoryFragment)
        }
        categoryPopupWindow!!.elevation = 10F
        categoryPopupWindow!!.showAsDropDown(binding.tvCategory, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setCategory(model: CategoryModel.Data) {
        if (categoryPopupWindow != null) {
            if (categoryPopupWindow!!.isShowing) {
                categoryPopupWindow!!.dismiss()
            }
        }
        if (model != null) {
            binding.tvCategory.text = model.categoryName
            selectedCategoryId = model.categoryId.toString()
        }
        if (selectedYear != null){

            getPaidMaintenanaceDetailsByResident(selectedCategoryId!!, selectedYear!!)
        }else{
            getPaidMaintenanaceDetailsByResident(selectedCategoryId!!)

        }
        Log.d(TAG, "$selectedCategoryId , $selectedYear")

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
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getPaidMaintenanaceDetailsByResident(categoryId: String? = null, year: String? = null) {
        //progressbar
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        // here sign up service call
        maintenanceHistoryModel = apiInterface.getPaidMaintenanaceDetailsByResident(
            "Bearer " + Auth_Token, User_Id!!.toInt(), year, categoryId
        )
        maintenanceHistoryModel.enqueue(object : Callback<MaintenanceHistoryModel> {
            override fun onResponse(
                call: Call<MaintenanceHistoryModel>,
                response: Response<MaintenanceHistoryModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (maintenanceHistoryList.isNotEmpty()) {
                            maintenanceHistoryList.clear()
                        }
                        maintenanceHistoryList =
                            response.body()!!.data as ArrayList<MaintenanceHistoryModel.Data>


                    } else {
                        // vehilceModelDropDown()
                    }
                    populateData()
//                    historyAdapter.notifyDataSetChanged()
                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<MaintenanceHistoryModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addCategoryList() {
        //progressbar
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        // here sign up service call
        categoryMode = apiInterface.getAllMaintenanceCategorys(
            "Bearer " + Auth_Token
        )
        categoryMode.enqueue(object : Callback<CategoryModel> {
            override fun onResponse(
                call: Call<CategoryModel>,
                response: Response<CategoryModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (categoryModelList.isNotEmpty()) {
                            categoryModelList.clear()
                        }
                        val categoryResponse = response.body() as CategoryModel
                        categoryModelList = categoryResponse.data as ArrayList<CategoryModel.Data>

                        loadDefaultData(categoryModelList)


                    } else {
                        // vehilceModelDropDown()
                    }
                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    companion object {
        const val TAG = "HistoryFragment"
    }

}