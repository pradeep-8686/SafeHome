package com.example.safehome.visitors

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.databinding.FragmentCurrentVisitorsLayoutBinding
import com.example.safehome.model.YearModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.visitors.cab.CabActivity
import com.example.safehome.visitors.delivery.DeliveryActivity
import com.example.safehome.visitors.guest.GuestActivity
import com.example.safehome.visitors.others.OthersActivity
import com.example.safehome.visitors.staff.StaffActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class HistoryVisitorsListFragment(private val approvalStatus : ArrayList<ApprovalStatusModel.Data>?= null): Fragment() {
    private lateinit var visitorsListAdapter: VisitorsListAdapter
    private lateinit var binding: FragmentCurrentVisitorsLayoutBinding
    private var currentVisitorsList:ArrayList<GetAllVisitorDetailsModel.Data.Event> = ArrayList()

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var yearPopupWindow: PopupWindow?= null
    private var visitorDeleteDialog: Dialog? = null
    private var yearList: ArrayList<YearModel.Data> = ArrayList()
    private lateinit var yearModel : Call<YearModel>
    private lateinit var apiInterface: APIInterface
    var Auth_Token: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentVisitorsLayoutBinding.inflate(inflater, container, false)

        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")
        apiInterface = APIClient.getClient(requireContext())


        addYearList()
        inIt()
        clickEvents()

        return  binding.root
    }

    private fun inIt() {
        binding.yearLayout.visibility = View.VISIBLE
        binding.searchLayout.visibility = View.VISIBLE
        binding.visitorFiltersLayout.visibility = View.VISIBLE

        val bundle = this.arguments
        if (bundle != null) {
          currentVisitorsList = bundle.getSerializable("visitorsList") as ArrayList<GetAllVisitorDetailsModel.Data.Event>
        }
        populateData(currentVisitorsList)
    }

    private fun populateData(currentVisitorsList: ArrayList<GetAllVisitorDetailsModel.Data.Event>) {
        /*for (i in 0 until currentVisitorsList.size) {
            if (currentVisitorsList[i].visitorStatusId == 1){
                currentVisitorsList.add(currentVisitorsList[i])
            }
        }*/

        Log.e("currentVisitorsList", ""+currentVisitorsList.size)
        if (currentVisitorsList.isEmpty()) {
            binding.emptyVisitorsTxt.visibility = View.VISIBLE
        } else {

            binding.emptyVisitorsTxt.visibility = View.GONE
            binding.visitorsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            visitorsListAdapter =
                VisitorsListAdapter(requireContext(), currentVisitorsList, "HistoryVisitors")
            binding.visitorsRecyclerview.adapter = visitorsListAdapter
            visitorsListAdapter.setCurrentVisitorsCallback(this@HistoryVisitorsListFragment)
            visitorsListAdapter.notifyDataSetChanged()
        }
    }
    private fun clickEvents() {
        binding.visitorFiltersLayout.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
            val sheetView: View = layoutInflater.inflate(
                R.layout.visitors_history_btmsheet, it!!.findViewById<View>(R.id.main_ll) as? ViewGroup
            )
            val tvAll = sheetView.findViewById<TextView>(R.id.tvAll)
            val tvCab = sheetView.findViewById<TextView>(R.id.tvCab)
            val tvDelivery = sheetView.findViewById<TextView>(R.id.tvDelivery)
            val tvGuest = sheetView.findViewById<TextView>(R.id.tvGuest)
            val tvStaff = sheetView.findViewById<TextView>(R.id.tvStaff)

            tvAll.setOnClickListener {
                if (bottomSheetDialog.isShowing){
                    bottomSheetDialog.dismiss()
                }

                populateData(currentVisitorsList)
            }
            tvGuest.setOnClickListener {
                if (bottomSheetDialog.isShowing) {
                    bottomSheetDialog.dismiss()
                }

                val allList = currentVisitorsList.filter { it.visitorTypeName == "Guest" }
                populateData(allList as ArrayList<GetAllVisitorDetailsModel.Data.Event>)
            }

            tvCab.setOnClickListener {
                if (bottomSheetDialog.isShowing) {
                    bottomSheetDialog.dismiss()
                }
                val allList = currentVisitorsList.filter { it.visitorTypeName == "Cab" }
                populateData(allList as ArrayList<GetAllVisitorDetailsModel.Data.Event>)
            }

            tvDelivery.setOnClickListener {
                if (bottomSheetDialog.isShowing) {
                    bottomSheetDialog.dismiss()
                }
                val allList = currentVisitorsList.filter { it.visitorTypeName == "Delivery" }
                populateData(allList as ArrayList<GetAllVisitorDetailsModel.Data.Event>)
            }

            tvStaff.setOnClickListener {
                if (bottomSheetDialog.isShowing) {
                    bottomSheetDialog.dismiss()
                }
                val allList = currentVisitorsList.filter { it.visitorTypeName == "Staff" }
                populateData(allList as ArrayList<GetAllVisitorDetailsModel.Data.Event>)
            }

            val closeImg = sheetView.findViewById<ImageView>(R.id.ic_close)
            closeImg.setOnClickListener {
                if (bottomSheetDialog.isShowing){
                    bottomSheetDialog.dismiss()
                }
            }
            //    getAllForumCommentsNetworkCall(forumItem)
            bottomSheetDialog.setContentView(sheetView)
            bottomSheetDialog.show()

        }
        binding.yearCl.setOnClickListener {
            /*if (visitorsPopupWindow != null) {
                if (visitorsPopupWindow!!.isShowing) {
                    visitorsPopupWindow!!.dismiss()
                }
            }*/
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

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (currentVisitorsList.isNotEmpty()){

                    filter(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun filter(text: String) {
        val filteredList = ArrayList<GetAllVisitorDetailsModel.Data.Event>()
        val visitorsList: ArrayList<GetAllVisitorDetailsModel.Data.Event> = currentVisitorsList

        for (eachCourse in visitorsList) {

            if (
                !eachCourse.visitorTypeName.isNullOrBlank() && eachCourse.visitorTypeName.lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.seviceProviderName.toString().isNullOrBlank() && eachCourse.seviceProviderName.toString().lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.name.toString().isNullOrBlank() && eachCourse.name.toString().lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.invitedBy.toString().isNullOrBlank() && eachCourse.invitedBy.toString().lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.allowFor.toString().isNullOrBlank() && eachCourse.allowFor.toString().lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))

            ) {
                filteredList.add(eachCourse)
            }
        }

        visitorsListAdapter.filteredList(filteredList)
    }

    private fun addYearList() {
//        yearList.add("2023")
//        yearList.add("2022")

        yearModel = apiInterface.yearList(
            "Bearer " + Auth_Token
        )
        yearModel.enqueue(object : Callback<YearModel> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onResponse(
                call: Call<YearModel>,
                response: Response<YearModel>
            ) {
//                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (yearList.isNotEmpty()) {
                            yearList.clear()
                        }
                        val facilitiesModel = response.body() as YearModel
                        yearList = facilitiesModel.data as ArrayList<YearModel.Data>

                        binding.yearTxt.text = yearList[0].year.toString()
//                        selectedYear = yearList[0].year.toString()

                    } else {
                        // vehilceModelDropDown()
                    }
                } else {
                    //  vehilceModelDropDown()
                }
            }
            override fun onFailure(call: Call<YearModel>, t: Throwable) {
//                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })

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
            val yearsArray = yearList.map { it.year.toString() }
            val yearAdapter = YearAdapter(requireContext(), yearsArray)
            dropDownRecyclerView.adapter = yearAdapter
            yearAdapter.setVisitorsHistoryCallback(this@HistoryVisitorsListFragment)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)    }

    fun selectVisitorYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if (year != null) {
            binding.yearTxt.text = year
        }
        /*      selectedYear = year

      //            getPaidMaintenanaceDetailsByResident(selectedYear!!)

              Log.d(HistoryFragment.TAG, "$selectedYear!!")*/

    }


    fun deleteVisitorItem(visitorListItem: GetAllVisitorDetailsModel.Data.Event) {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_tenant_dialog_popup, null)
        visitorDeleteDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        visitorDeleteDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        visitorDeleteDialog!!.setContentView(view)
        visitorDeleteDialog!!.setCanceledOnTouchOutside(true)
        visitorDeleteDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(visitorDeleteDialog!!.window!!.attributes)


        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)
        val member_mob_number: TextView = view.findViewById(R.id.member_mob_number)
        member_mob_number.text = "Are you sure you want to delete this ${visitorListItem.visitorTypeName} Details?"

        close.setOnClickListener {
            if (visitorDeleteDialog!!.isShowing) {
                visitorDeleteDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            //    deletePollServiceCall(forumItem)
            if (visitorDeleteDialog!!.isShowing) {
                visitorDeleteDialog!!.dismiss()
            }
        }

        no.setOnClickListener {
            if (visitorDeleteDialog!!.isShowing) {
                visitorDeleteDialog!!.dismiss()
            }
        }
        visitorDeleteDialog!!.show()
    }

    fun editVisitorItem(visitorListItem: GetAllVisitorDetailsModel.Data.Event) {
        var intent: Intent? = null
        intent = when (visitorListItem.visitorTypeName) {
            "Cab" -> {
                Intent(requireContext(), CabActivity::class.java)
            }
            "Delivery" -> {
                Intent(requireContext(), DeliveryActivity::class.java)
            }
            "Guest" -> {
                Intent(requireContext(), GuestActivity::class.java)
            }
            "Staff" -> {
                Intent(requireContext(), StaffActivity::class.java)
            }
            else -> {
                Intent(requireContext(), OthersActivity::class.java)
            }
        }
        intent.putExtra("approvalStatus", approvalStatus)
        intent?.putExtra("visitorListItem", visitorListItem)
        intent?.putExtra("VisitorTypeId", visitorListItem.visitorTypeId)
        intent?.let { startActivity(it) }
    }
}