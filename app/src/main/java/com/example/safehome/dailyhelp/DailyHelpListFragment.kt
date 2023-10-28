package com.example.safehome.dailyhelp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentDailyHelpListBinding
import com.example.safehome.fragments.BaseFragment
import com.example.safehome.model.AllFacilitiesModel
import com.example.safehome.model.DailyHelpRoles
import com.example.safehome.model.FaciBookings
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class DailyHelpListFragment : BaseFragment() {
    private lateinit var binding: FragmentDailyHelpListBinding
    private var dailyHelpRolesList: ArrayList<DailyHelpRoles.Data> = ArrayList()
    private lateinit var dailyHelpListAdapter: DailyHelpListAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var allDailyHelpModel : Call<DailyHelpRoles>

    var User_Id: String? = ""
    var Auth_Token: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDailyHelpListBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

        getDailyHelpList()
        populateData()

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (dailyHelpRolesList.isNotEmpty()) {
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

        val myDuesList = ArrayList<DailyHelpRoles.Data>()

        val courseAry: ArrayList<DailyHelpRoles.Data> = dailyHelpRolesList

        for (eachCourse in courseAry) {
            if (!eachCourse.staffTypeName.isNullOrBlank() && eachCourse.staffTypeName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))||
                !eachCourse.staffCount.toString().isNullOrBlank() && eachCourse.staffCount.toString().lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))
            ) {
                myDuesList.add(eachCourse)
            }
        }

        dailyHelpListAdapter.filterList(myDuesList);
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getDailyHelpList() {
        //progressbar
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        // here sign up service call
        allDailyHelpModel = apiInterface.getDailyHelpList(
            "Bearer " + Auth_Token
        )
        allDailyHelpModel.enqueue(object : Callback<DailyHelpRoles> {
            override fun onResponse(
                call: Call<DailyHelpRoles>,
                response: Response<DailyHelpRoles>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (dailyHelpRolesList.isNotEmpty()) {
                            dailyHelpRolesList.clear()
                        }
                        val  dailyHelpRoles = response.body() as DailyHelpRoles
                        val dailyHelpRolesListTotal = dailyHelpRoles.data as ArrayList<DailyHelpRoles.Data>
                        for(i in dailyHelpRolesListTotal){
                            if(i.serviceTo.equals("Individual Resident")){
                                dailyHelpRolesList.add(i)
                            }
                        }


                    } else {
                        // vehilceModelDropDown()
                    }
                    populateData()
                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<DailyHelpRoles>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }


    private fun populateData() {
        if (dailyHelpRolesList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.dailyHelpListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            dailyHelpListAdapter = DailyHelpListAdapter(requireContext(), dailyHelpRolesList)
            binding.dailyHelpListRecyclerView.adapter = dailyHelpListAdapter
            dailyHelpListAdapter.setCallback(this@DailyHelpListFragment)
        }

    }

    fun selectedRole(dailyHelpMembers: DailyHelpRoles.Data) {
//        val bundle = Bundle()
//        bundle.putString("MemberRole", dailyHelpMembers.memberDesignation)
//
//        val frag2 = DailyHelpMemberListFragment()
//        frag2.arguments = bundle
//
//        replaceFragment(frag2, R.id.daily_help_fragment_container)

        val intent = Intent(requireContext(), DailyHelpMemberListActivity::class.java)
        intent.putExtra("MemberRole", dailyHelpMembers.staffTypeId.toString())
        intent.putExtra("MemberRoleName", dailyHelpMembers.staffTypeName.toString())
        startActivity(intent)
        requireActivity().finish()
    }

}