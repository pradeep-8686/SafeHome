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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentCurrentVisitorsLayoutBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.visitors.cab.CabActivity
import com.example.safehome.visitors.delivery.DeliveryActivity
import com.example.safehome.visitors.guest.GuestActivity
import com.example.safehome.visitors.others.OthersActivity
import com.example.safehome.visitors.staff.DeleteVisitorDetailsModel
import com.example.safehome.visitors.staff.StaffActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ExpectedVisitorsListFragment(private val approvalStatus : ArrayList<ApprovalStatusModel.Data>?= null) : Fragment() {
    private lateinit var deleteVisitorDetailsCall: Call<DeleteVisitorDetailsModel>
    private lateinit var visitorsListAdapter: VisitorsListAdapter
    private lateinit var binding: FragmentCurrentVisitorsLayoutBinding
    private var currentVisitorsList:ArrayList<GetAllVisitorDetailsModel.Data.Event> = ArrayList()
    private var visitorDeleteDialog: Dialog? = null
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentVisitorsLayoutBinding.inflate(inflater, container, false)
        inIt()
        clickEvents()
        return  binding.root
    }

    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "User_Id", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

        binding.yearLayout.visibility = View.GONE
        binding.searchLayout.visibility = View.VISIBLE
        binding.visitorFiltersLayout.visibility = View.GONE
        val bundle = this.arguments
        if (bundle != null) {
            currentVisitorsList = bundle.getSerializable("visitorsList") as ArrayList<GetAllVisitorDetailsModel.Data.Event>
        }
        populateData(currentVisitorsList)
    }
    private fun clickEvents() {
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

    private fun populateData(currentVisitorsList: ArrayList<GetAllVisitorDetailsModel.Data.Event>) {
     /*   for (i in 0 until currentVisitorsList.size) {
            if (currentVisitorsList[i].visitorStatusId == 2){
                currentVisitorsList.add(currentVisitorsList[i])
            }
        }*/
        Log.e("currentVisitorsList", ""+currentVisitorsList.size)
        if (currentVisitorsList.size == 0) {
            binding.emptyVisitorsTxt.visibility = View.VISIBLE
        } else {

            binding.emptyVisitorsTxt.visibility = View.GONE
            binding.visitorsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            visitorsListAdapter =
                VisitorsListAdapter(requireContext(), currentVisitorsList, "ExpectedVisitors")
            binding.visitorsRecyclerview.adapter = visitorsListAdapter
            visitorsListAdapter.setCurrentVisitorsCallback(this@ExpectedVisitorsListFragment)
            visitorsListAdapter.notifyDataSetChanged()
        }
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


    @RequiresApi(Build.VERSION_CODES.Q)
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
                deleteVisitorDetailsServiceCall(visitorListItem)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteVisitorDetailsServiceCall(visitorListItem: GetAllVisitorDetailsModel.Data.Event) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        deleteVisitorDetailsCall = apiInterface.deleteVisitorDetails("bearer "+Auth_Token, visitorListItem.visitorId!!)
        deleteVisitorDetailsCall.enqueue(object: Callback<DeleteVisitorDetailsModel> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(
                call: Call<DeleteVisitorDetailsModel>,
                response: Response<DeleteVisitorDetailsModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                     currentVisitorsList.remove(visitorListItem)
                                     visitorsListAdapter.notifyDataSetChanged()
                                  //  totalVisitorDetailsList = response.body()!!.data.events as ArrayList<GetAllVisitorDetailsModel.Data.Event>
                                         Utils.showToast(requireContext(), response.body()!!.message.toString())

                                }
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

            override fun onFailure(call: Call<DeleteVisitorDetailsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })
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