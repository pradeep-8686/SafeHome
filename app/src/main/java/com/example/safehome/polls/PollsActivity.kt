package com.example.safehome.polls

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityPollsBinding
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.model.GetAllPollDetailsModel
import com.example.safehome.model.GetPollResultModel
import com.example.safehome.model.YearModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PollsActivity : AppCompatActivity() {

    private lateinit var deletePollCall: Call<AddServiceBookingList>
    private lateinit var getPollResultCall: Call<GetPollResultModel>
    private var pollResultsList: ArrayList<GetPollResultModel.Data.Polldata> = ArrayList()
    private var totalPollsList: ArrayList<GetAllPollDetailsModel.Data.Poll> = ArrayList()
    private lateinit var getAllPollsDetailCall: Call<GetAllPollDetailsModel>
    private lateinit var binding: ActivityPollsBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""

    private var yearList: ArrayList<YearModel.Data> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private var selectedYear: String? = null
    private var pollResponseModelList: ArrayList<PollResponseModel> = ArrayList()
    private lateinit var pollsAdapter: PollsAdapter
    private var pollDeleteDialog: Dialog? = null
    private var pollViewResultDialog: Dialog? = null

    private lateinit var yearModel : Call<YearModel>

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPollsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ScreenFrom = intent.getStringExtra("ScreenFrom")

        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        addYearList()

        clickActions()
        addData()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        getAllPollsDetailsCall()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllPollsDetailsCall() {
        customProgressDialog.progressDialogShow(this@PollsActivity, this.getString(R.string.loading))
        getAllPollsDetailCall = apiInterface.getAllPollDetails("bearer "+Auth_Token,"", "",
        "", "1", "10")
        getAllPollsDetailCall.enqueue(object: Callback<GetAllPollDetailsModel> {
            override fun onResponse(
                call: Call<GetAllPollDetailsModel>,
                response: Response<GetAllPollDetailsModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (totalPollsList.isNotEmpty()) {
                                        totalPollsList.clear()
                                    }
                                    totalPollsList = response.body()!!.data.polls as ArrayList<GetAllPollDetailsModel.Data.Poll>
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())

                                }
                                populateData(totalPollsList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@PollsActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<GetAllPollDetailsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@PollsActivity, t.message.toString())
            }

        })
    }

    private fun clickActions() {

        binding.yearCl.setOnClickListener {

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

        binding.ivAddComplaint.setOnClickListener {
            val intent = Intent(this, RaisePollActivity::class.java)
            startActivity(intent)
        }

    }

    private fun addYearList() {
        yearModel = apiInterface.yearList(
            "Bearer " + Auth_Token
        )
        yearModel.enqueue(object : Callback<YearModel> {
            override fun onResponse(
                call: Call<YearModel>,
                response: Response<YearModel>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (yearList.isNotEmpty()) {
                            yearList.clear()
                        }
                        val facilitiesModel = response.body() as YearModel
                        yearList = facilitiesModel.data as ArrayList<YearModel.Data>
                        binding.yearTxt.text = yearList[0].year.toString()
                        selectedYear = yearList[0].year.toString()

                    } else {
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<YearModel>, t: Throwable) {
                Utils.showToast(this@PollsActivity, t.message.toString())
            }
        })
    }


    private fun yearDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        yearPopupWindow = PopupWindow(
            view,
            binding.yearTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (yearList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val yearsArray = yearList.map { it.year.toString() }
            val yearAdapter = YearAdapter(this, yearsArray)

            dropDownRecyclerView.adapter = yearAdapter
            yearAdapter.setPollsCallback(this@PollsActivity)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectPollsYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if (year != null) {
            binding.yearTxt.text = year
        }
        selectedYear = year

//            getPaidMaintenanaceDetailsByResident(selectedYear!!)

        Log.d(HistoryFragment.TAG, "$selectedYear!!")

    }

    private fun addData() {


        val o1 = PollResponseModel.Options("Yes", "75")
        val o2 = PollResponseModel.Options("No", "25")
        val optionList = ArrayList<PollResponseModel.Options>()
        optionList.add(o1)
        optionList.add(o2)

        val d1 = PollResponseModel(
            "Admin",
            "1 Day Ago",
            R.drawable.img_poll,
            "Should we sanitize community on a weekly basis",
            optionList,
            100,
            ""
        )
        pollResponseModelList.add(d1)

        val o11 = PollResponseModel.Options("Option 1", "60")
        val o12 = PollResponseModel.Options("Option 2", "20")
        val o13 = PollResponseModel.Options("Option 3", "10")
        val o14 = PollResponseModel.Options("Option 4", "10")
        val optionList1 = ArrayList<PollResponseModel.Options>()
        optionList1.add(o11)
        optionList1.add(o12)
        optionList1.add(o13)
        optionList1.add(o14)

        val d11 = PollResponseModel(
            "Admin",
            "2 Day Ago",
            null,
            "Should we sanitize community on a weekly basis",
            optionList1,
            0,
            ""
        )
        pollResponseModelList.add(d11)


        val optionList21 = ArrayList<PollResponseModel.Options>()

        val d21 = PollResponseModel(
            "Tejaswini",
            "2 Day Ago",
            null,
            "Should we sanitize community on a weekly basis",
            optionList21,
            20,
            "Block D - 101"
        )
        pollResponseModelList.add(d21)


    //    populateData(pollResponseModelList)

    }

    private fun populateData(pollResponseModelList: ArrayList<GetAllPollDetailsModel.Data.Poll>) {
        //    upcomingList.clear()
        if (pollResponseModelList.size == 0) {
            binding.emptyPollsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyPollsTxt.visibility = View.GONE
            binding.pollsRecyclerView.layoutManager = LinearLayoutManager(this)
            pollsAdapter =
                PollsAdapter(this, pollResponseModelList, User_Id!!)
            binding.pollsRecyclerView.adapter = pollsAdapter
            pollsAdapter.setCallback(this@PollsActivity)
            pollsAdapter.notifyDataSetChanged()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun viewPollResult(pollItem: Int) {
       viewPollResultCall(pollItem);
    }

    private fun viewPollResultDialog(
        pollResultsList: ArrayList<GetPollResultModel.Data.Polldata>
    ) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.poll_result_dialog_popup, null)
        pollViewResultDialog = Dialog(this, R.style.CustomAlertDialog)
        pollViewResultDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pollViewResultDialog!!.setContentView(view)
        pollViewResultDialog!!.setCanceledOnTouchOutside(true)
        pollViewResultDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(pollViewResultDialog!!.window!!.attributes)

        val close = view.findViewById<ImageView>(R.id.close)
        val optionRecyclerView = view.findViewById<RecyclerView>(R.id.optionRecyclerView)
        val tvNoOfVotes = view.findViewById<TextView>(R.id.tvNoOfVotes)

        var totalVoteCount = 0
        for (i in 0 until pollResultsList.size) {
            val voteCount = pollResultsList[i].voteCount
            totalVoteCount += voteCount
        }

        tvNoOfVotes.text = "Total Votes- ${totalVoteCount}"
        close.setOnClickListener {
            if (pollViewResultDialog!!.isShowing) {
                pollViewResultDialog!!.dismiss()
            }
        }
        optionRecyclerView.layoutManager = LinearLayoutManager(this)
        val pollsResultAdapter =
            PollsResultAdapter(this, pollResultsList)
        optionRecyclerView.adapter = pollsResultAdapter
        pollsResultAdapter.notifyDataSetChanged()

        pollViewResultDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun viewPollResultCall(pollItem: Int) {
        customProgressDialog.progressDialogShow(this@PollsActivity, this.getString(R.string.loading))
        getPollResultCall = apiInterface.getPollResults("bearer "+Auth_Token, pollItem)
        getPollResultCall.enqueue(object: Callback<GetPollResultModel> {
            override fun onResponse(
                call: Call<GetPollResultModel>,
                response: Response<GetPollResultModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (pollResultsList.isNotEmpty()) {
                                        pollResultsList.clear()
                                    }
                                    pollResultsList = response.body()!!.data.polldata as ArrayList<GetPollResultModel.Data.Polldata>
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())

                                }
                              //  populatePollsData(totalPollsList)
                                viewPollResultDialog(pollResultsList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@PollsActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<GetPollResultModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@PollsActivity, t.message.toString())
            }

        })
    }

    fun editPoll(pollItem: GetAllPollDetailsModel.Data.Poll) {
//        val intent = Intent(this, UpdatePollActivity::class.java)
//        startActivity(intent)

        val intent = Intent(this, RaisePollActivity::class.java)
        intent.putExtra("pollItem", pollItem)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deletePoll(pollItem: GetAllPollDetailsModel.Data.Poll) {

        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_tenant_dialog_popup, null)
        pollDeleteDialog = Dialog(this, R.style.CustomAlertDialog)
        pollDeleteDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pollDeleteDialog!!.setContentView(view)
        pollDeleteDialog!!.setCanceledOnTouchOutside(true)
        pollDeleteDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(pollDeleteDialog!!.window!!.attributes)


        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)
        val member_mob_number: TextView = view.findViewById(R.id.member_mob_number)
        member_mob_number.setText("Are you sure you want to delete this poll?")

        close.setOnClickListener {
            if (pollDeleteDialog!!.isShowing) {
                pollDeleteDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            deletePollServiceCall(pollItem)
            if (pollDeleteDialog!!.isShowing) {
                pollDeleteDialog!!.dismiss()
            }
        }

        no.setOnClickListener {
            if (pollDeleteDialog!!.isShowing) {
                pollDeleteDialog!!.dismiss()
            }
        }
        pollDeleteDialog!!.show()

    }

    fun clickAction(pollItem: GetAllPollDetailsModel.Data.Poll.PollOption) {

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deletePollServiceCall(pollItem: GetAllPollDetailsModel.Data.Poll) {
        customProgressDialog.progressDialogShow(this@PollsActivity, this.getString(R.string.loading))
        deletePollCall = apiInterface.deletePollDetails("bearer "+Auth_Token, pollItem.pollId)
        deletePollCall.enqueue(object: Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@PollsActivity, response.body()!!.message.toString())
                                    totalPollsList.remove(pollItem)
                                    pollsAdapter.notifyDataSetChanged()

                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@PollsActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@PollsActivity, t.message.toString())
            }

        })
    }
}