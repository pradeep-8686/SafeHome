package com.example.safehome.eventsview

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.UpcomingAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentUpcomingBinding
import com.example.safehome.model.Events
import com.example.safehome.model.UpcomingEventsModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpcomingFragment : Fragment() {
    private lateinit var binding: FragmentUpcomingBinding
    private var upcomingList: ArrayList<Events> = ArrayList()
    private lateinit var upcomingAdapter: UpcomingAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var callGetAllUpComingEvents: Call<UpcomingEventsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpcomingBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

        getAllUpcomingEventsApiCall()
       // addData()
       // populateData()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllUpcomingEventsApiCall() {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        // here sign up service call
        callGetAllUpComingEvents = apiInterface.getAllUpcomingEvents("bearer " + Auth_Token, 10, 1, User_Id.toString())
        callGetAllUpComingEvents.enqueue(object : Callback<UpcomingEventsModel> {
            override fun onResponse(
                call: Call<UpcomingEventsModel>,
                response: Response<UpcomingEventsModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                Log.e("Response: ", response.body().toString())

                if (response.isSuccessful && response.body() != null) {
                        if (upcomingList.isNotEmpty()) {
                            upcomingList.clear()
                        }
                    if(response.body()!!.statusCode == 1) {
                        upcomingList = response.body()!!.data.events as ArrayList<Events>
                    }else{
                        Toast.makeText(
                            requireContext(),
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    populateData(upcomingList)

                }
            }

            override fun onFailure(call: Call<UpcomingEventsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

//    private fun addData() {
//        upcomingList.add(
//            Upcoming(
//                "Festival Celebrations", "Club House",
//                "10/04/2021", "10:00A.M", "10:00P.M", "Admin"
//            )
//        )
//
//        upcomingList.add(
//            Upcoming(
//                "Sports Event", "Cricket Ground Area",
//                "10/04/2021", "10:00A.M", "10:00P.M", "Admin"
//            )
//        )
//
//    }

    private fun populateData(upcomingList: ArrayList<Events>) {
    //    upcomingList.clear()

        if (upcomingList.size == 0){
            binding.emptyEventsTxt.visibility = View.VISIBLE
        }else {
            binding.emptyEventsTxt.visibility = View.GONE
            binding.upcomingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            upcomingAdapter = UpcomingAdapter(requireContext(), upcomingList)
            binding.upcomingRecyclerView.adapter = upcomingAdapter
            upcomingAdapter.notifyDataSetChanged()
        }
    }
}