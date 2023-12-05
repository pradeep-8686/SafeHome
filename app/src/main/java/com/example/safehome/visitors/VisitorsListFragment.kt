package com.example.safehome.visitors

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
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentVisitorsListBinding
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


class VisitorsListFragment(private val approvalStatus : ArrayList<ApprovalStatusModel.Data>?= null)  : Fragment() {

    private var totalVisitorDetailsList: ArrayList<GetAllVisitorDetailsModel.Data.Event> = ArrayList()
    private lateinit var getAllVisitorDetailsCall: Call<GetAllVisitorDetailsModel>
    private lateinit var binding : FragmentVisitorsListBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var yearPopupWindow: PopupWindow?= null
    private var visitorDeleteDialog: Dialog? = null
    private var yearList: ArrayList<String> = ArrayList()
    private lateinit var visitorsListAdapter: VisitorsListAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVisitorsListBinding.inflate(inflater, container, false)
     //   addVisitorsList()
        inIt()

        getAllVisitorsDetailsServiceCall()
        //populateData(totalVisitorDetailsList)
        addYearList()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllVisitorsDetailsServiceCall() {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        getAllVisitorDetailsCall = apiInterface.getAllVisitorDetails("bearer "+Auth_Token, "", "",
        "", "","","", "","", "", "", "1", "10", "")
        getAllVisitorDetailsCall.enqueue(object: Callback<GetAllVisitorDetailsModel> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(
                call: Call<GetAllVisitorDetailsModel>,
                response: Response<GetAllVisitorDetailsModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (totalVisitorDetailsList.isNotEmpty()) {
                                        totalVisitorDetailsList.clear()
                                    }
                                    totalVisitorDetailsList = response.body()!!.data.events as ArrayList<GetAllVisitorDetailsModel.Data.Event>
                                    //     Utils.showToast(requireContext(), response.body()!!.message.toString())

                                }
                            //    populateData(totalVisitorDetailsList)
                                replaceCurrentVisitorsFragment()
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<GetAllVisitorDetailsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })
    }

    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "User_Id", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

        binding.yearLayout.visibility = View.GONE
        binding.searchLayout.visibility = View.GONE
        binding.visitorFiltersLayout.visibility = View.GONE

        binding.visitorsCurrentBtn.setOnClickListener {
            replaceCurrentVisitorsFragment()
        }
        binding.visitorExpectedBtn.setOnClickListener {
            replaceExpectedVisitorsFragment()
        }
        binding.visitorHistoryBtn.setOnClickListener {
            replaceHistoryVisitorsFragment()
        }

        binding.visitorFiltersLayout.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
            val sheetView: View = layoutInflater.inflate(
                R.layout.visitors_history_btmsheet, it!!.findViewById<View>(R.id.main_ll) as? ViewGroup
            )
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
                if (totalVisitorDetailsList.isNotEmpty()){

                    filter(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun replaceCurrentVisitorsFragment() {
        binding.visitorsCurrentBtn.background = requireContext().getDrawable(R.drawable.rectangler_vrify_bg)
        binding.visitorHistoryBtn.setBackgroundResource(0)
        binding.visitorExpectedBtn.setBackgroundResource(0)

        val visitorsListFragment = CurrentVisitorsListFragment(approvalStatus)
        val finalList : ArrayList<GetAllVisitorDetailsModel.Data.Event> = totalVisitorDetailsList.filter { it.visitorStatusId == 1 } as ArrayList<GetAllVisitorDetailsModel.Data.Event>
        val bundle = Bundle()
        bundle.putSerializable("visitorsList", finalList)
        visitorsListFragment.arguments = bundle
        val fragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.visitor_fragment_container,
            visitorsListFragment
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun replaceExpectedVisitorsFragment() {

        binding.visitorExpectedBtn.background = requireContext().getDrawable(R.drawable.rectangler_vrify_bg)
        binding.visitorsCurrentBtn.setBackgroundResource(0)
        binding.visitorHistoryBtn.setBackgroundResource(0)

        val visitorsListFragment = ExpectedVisitorsListFragment(approvalStatus)
        val finalList : ArrayList<GetAllVisitorDetailsModel.Data.Event> = totalVisitorDetailsList.filter { it.visitorStatusId == 2 } as ArrayList<GetAllVisitorDetailsModel.Data.Event>

        val bundle = Bundle()
        bundle.putSerializable("visitorsList", finalList)
        visitorsListFragment.arguments = bundle
        val fragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.visitor_fragment_container,
            visitorsListFragment
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun replaceHistoryVisitorsFragment() {
        binding.visitorHistoryBtn.background = requireContext().getDrawable(R.drawable.rectangler_vrify_bg)
        binding.visitorsCurrentBtn.setBackgroundResource(0)
        binding.visitorExpectedBtn.setBackgroundResource(0)

        val visitorsListFragment = HistoryVisitorsListFragment(approvalStatus)
        val finalList : ArrayList<GetAllVisitorDetailsModel.Data.Event> = totalVisitorDetailsList.filter { it.visitorStatusId == 3 } as ArrayList<GetAllVisitorDetailsModel.Data.Event>

        val bundle = Bundle()
        bundle.putSerializable("visitorsList", finalList)
        visitorsListFragment.arguments = bundle
        val fragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.visitor_fragment_container,
            visitorsListFragment
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }





    private fun filter(text: String) {
        val filteredList = ArrayList<GetAllVisitorDetailsModel.Data.Event>()
        val visitorsList: ArrayList<GetAllVisitorDetailsModel.Data.Event> = totalVisitorDetailsList

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
        yearList.add("2023")
        yearList.add("2022")
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
            yearAdapter.setVisitorsHistoryCallback(this@VisitorsListFragment)
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
        intent?.putExtra("visitorListItem", visitorListItem)
        intent?.putExtra("VisitorTypeId", visitorListItem.visitorTypeId)
        intent?.let { startActivity(it) }
    }

}