package com.example.safehome.services

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentServicesListBinding
import com.example.safehome.fragments.BaseFragment
import com.example.safehome.model.DailyHelpRoles
import com.example.safehome.model.ServiceDataList
import com.example.safehome.model.ServiceTypesList
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class ServicesListFragment : BaseFragment() {
    private lateinit var binding: FragmentServicesListBinding
    private var dailyHelpRolesList: ArrayList<DailyHelpRoles> = ArrayList()
    private lateinit var dailyHelpListAdapter: ServiceListAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var getServiceTypesListCall: Call<ServiceTypesList>
    private lateinit var apiInterface: APIInterface
    private var Auth_token: String?= null
    private var serviceTypesList: ArrayList<ServiceTypesList.ServicesListItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServicesListBinding.inflate(inflater, container, false)
        inIt()

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (serviceTypesList.isNotEmpty()){

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

        val servicesDataMemberList = ArrayList<ServiceTypesList.ServicesListItem>()
        val courseAry: ArrayList<ServiceTypesList.ServicesListItem> = serviceTypesList

        for (eachCourse in courseAry) {

            if (!eachCourse.serviceTypeName.isNullOrBlank() && eachCourse.serviceTypeName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.staffCount.toString().isNullOrBlank() && eachCourse.staffCount.toString().lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))

            ) {
                servicesDataMemberList.add(eachCourse)
            }
        }

        dailyHelpListAdapter.filterList(servicesDataMemberList);
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        Auth_token = Utils.getStringPref(requireContext(), "Token", "")!!

      //  addDailyHelpListData()
        //populateData(serviceTypesList)
        getServiceTypesListServiceCall()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getServiceTypesListServiceCall() {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        getServiceTypesListCall = apiInterface.GetServiceTypeswithServiceCount("bearer "+Auth_token)
        getServiceTypesListCall.enqueue(object: Callback<ServiceTypesList>{
            override fun onResponse(
                call: Call<ServiceTypesList>,
                response: Response<ServiceTypesList>
            ) {
               if (response.isSuccessful && response.body()!= null){
                   customProgressDialog.progressDialogDismiss()
                   if (response.body()!!.statusCode!= null){
                       when(response.body()!!.statusCode){
                           1 -> {
                               if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                   if (serviceTypesList.isNotEmpty()) {
                                       serviceTypesList.clear()
                                   }
                                   serviceTypesList = response.body()!!.data as ArrayList<ServiceTypesList.ServicesListItem>
                               //    Utils.showToast(requireContext(), response.body()!!.message.toString())

                               }
                               populateData(serviceTypesList)

                           }
                          /* 2 -> {
                               if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                   Utils.showToast(requireContext(), response.body()!!.message.toString())
                               }
                           }
                           3 -> {
                               if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                   Utils.showToast(requireContext(), response.body()!!.message.toString())
                               }
                           }*/
                           else -> {
                               if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                  Utils.showToast(requireContext(), response.body()!!.message.toString())
                               }
                           }
                       }
                   }
               }
            }

            override fun onFailure(call: Call<ServiceTypesList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })
    }

/*
    private fun addDailyHelpListData() {
        dailyHelpRolesList.add(
            DailyHelpRoles(
                "AC Service & Repair", "11", "1",
            )
        )
        dailyHelpRolesList.add(
            DailyHelpRoles(
                "Plumber", "3", "1",
            )
        )
        dailyHelpRolesList.add(
            DailyHelpRoles(
                "Electrician", "11", "1",
            )
        )
        dailyHelpRolesList.add(
            DailyHelpRoles(
                "Carpenter", "10", "1",
            )
        )
        dailyHelpRolesList.add(
            DailyHelpRoles(
                "Electricican", "15", "1",
            )
        )
        dailyHelpRolesList.add(
            DailyHelpRoles(
                "Appliance Repair", "14", "1",
            )
        )
        dailyHelpRolesList.add(
            DailyHelpRoles(
                "Pest Control", "16", "1",
            )
        )
        dailyHelpRolesList.add(
            DailyHelpRoles(
                "Home Painter", "21", "1",
            )
        )
        dailyHelpRolesList.add(
            DailyHelpRoles(
                "Full Home Cleaning", "20", "1",
            )
        )
    }
*/

    private fun populateData(serviceTypesList: ArrayList<ServiceTypesList.ServicesListItem>) {

        if (serviceTypesList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE

            binding.dailyHelpListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            dailyHelpListAdapter = ServiceListAdapter(requireContext(), serviceTypesList)
            binding.dailyHelpListRecyclerView.adapter = dailyHelpListAdapter
            dailyHelpListAdapter.setCallback(this@ServicesListFragment)
        }

    }

    fun selectedRole(serviceType: ServiceTypesList.ServicesListItem) {
//        val bundle = Bundle()
//        bundle.putString("MemberRole", dailyHelpMembers.memberDesignation)
//
//        val frag2 = DailyHelpMemberListFragment()
//        frag2.arguments = bundle
//
//        replaceFragment(frag2, R.id.daily_help_fragment_container)

        val intent = Intent(requireContext(), ServicesMemberListActivity::class.java)
        intent.putExtra("serviceTypeId", serviceType.serviceTypeId)
        intent.putExtra("serviceTypeName", serviceType.serviceTypeName)
        startActivity(intent)
        requireActivity().finish()
    }

}