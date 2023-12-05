package com.example.safehome.visitors

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentInviteVisitorsBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.visitors.cab.CabActivity
import com.example.safehome.visitors.delivery.DeliveryActivity
import com.example.safehome.visitors.guest.GuestActivity
import com.example.safehome.visitors.others.OthersActivity
import com.example.safehome.visitors.staff.StaffActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InviteVisitorsFragment(private val approvalStatus : ArrayList<ApprovalStatusModel.Data>?= null) : Fragment() {

    private lateinit var binding : FragmentInviteVisitorsBinding
    private lateinit var inviteVisitorsAdapter: InviteVisitorsAdapter
    private var visitorTypesList: ArrayList<VisitorsTypeDropdownModel.Data> = ArrayList()
    private lateinit var getAllVisitorTypesCall: Call<VisitorsTypeDropdownModel>
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

        binding = FragmentInviteVisitorsBinding.inflate(inflater, container, false)
        inIt()
        clickEvents()
        getVisitorsTypeServiceCall()
        return binding.root
    }
    

    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "User_Id", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getVisitorsTypeServiceCall() {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        getAllVisitorTypesCall = apiInterface.getAllVisitorTypeDropdown("bearer "+Auth_Token)
        getAllVisitorTypesCall.enqueue(object: Callback<VisitorsTypeDropdownModel> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(
                call: Call<VisitorsTypeDropdownModel>,
                response: Response<VisitorsTypeDropdownModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (visitorTypesList.isNotEmpty()) {
                                        visitorTypesList.clear()
                                    }
                                    visitorTypesList = response.body()!!.data as ArrayList<VisitorsTypeDropdownModel.Data>
                               //     Utils.showToast(requireContext(), response.body()!!.message.toString())

                                }
                                populateData(visitorTypesList)

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

            override fun onFailure(call: Call<VisitorsTypeDropdownModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })
    }

    private fun populateData(visitorTypesList: ArrayList<VisitorsTypeDropdownModel.Data>) {
        if (visitorTypesList.size == 0) {
            binding.emptyInviteVisitorsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyInviteVisitorsTxt.visibility = View.GONE
            binding.inviteVisitorsRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
            inviteVisitorsAdapter =
                InviteVisitorsAdapter(requireContext(), visitorTypesList)
            binding.inviteVisitorsRecyclerview.adapter = inviteVisitorsAdapter
            inviteVisitorsAdapter.setCallback(this@InviteVisitorsFragment)
            inviteVisitorsAdapter.notifyDataSetChanged()
        }
    }

    private fun clickEvents() {
      /*  binding.clGuest.setOnClickListener {

            val intent = Intent(requireContext(), GuestActivity::class.java)
            startActivity(intent)

        }
        binding.clCab.setOnClickListener {

            val intent = Intent(requireContext(), CabActivity::class.java)
            startActivity(intent)

        }
        binding.clDelivery.setOnClickListener {

            val intent = Intent(requireContext(), DeliveryActivity::class.java)
            startActivity(intent)

        }
        binding.clStaff.setOnClickListener {

            val intent = Intent(requireContext(), StaffActivity::class.java)
            startActivity(intent)

        }
        binding.clOthers.setOnClickListener {

            val intent = Intent(requireContext(), OthersActivity::class.java)
            startActivity(intent)

        }*/
    }

    fun itemCLick(inviteVisitorItem: VisitorsTypeDropdownModel.Data) {
        when (inviteVisitorItem.visitorTypeName) {
            "Guest" -> {
                val intent = Intent(requireContext(), GuestActivity::class.java)
                intent.putExtra("VisitorTypeId", inviteVisitorItem.visitorTypeId)
                intent.putExtra("approvalStatus", approvalStatus)
                startActivity(intent)
            }
            "Cab" -> {
                val intent = Intent(requireContext(), CabActivity::class.java)
                intent.putExtra("VisitorTypeId", inviteVisitorItem.visitorTypeId)
                intent.putExtra("approvalStatus", approvalStatus)
                startActivity(intent)
            }
            "Staff" -> {
                val intent = Intent(requireContext(), StaffActivity::class.java)
                intent.putExtra("VisitorTypeId", inviteVisitorItem.visitorTypeId)
                intent.putExtra("approvalStatus", approvalStatus)
                startActivity(intent)
            }
            "Delivery" -> {
                val intent = Intent(requireContext(), DeliveryActivity::class.java)
                intent.putExtra("approvalStatus", approvalStatus)
                intent.putExtra("VisitorTypeId", inviteVisitorItem.visitorTypeId)
                startActivity(intent)
            }
            "Other" -> {
                val intent = Intent(requireContext(), OthersActivity::class.java)
                intent.putExtra("VisitorTypeId", inviteVisitorItem.visitorTypeId)
                intent.putExtra("approvalStatus", approvalStatus)
                startActivity(intent)
            }
        }
    }

}