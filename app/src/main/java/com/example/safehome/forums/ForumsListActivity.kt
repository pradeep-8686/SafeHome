package com.example.safehome.forums

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityForumsListBinding
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.model.GetPollResultModel
import com.example.safehome.model.YearModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumsListActivity : AppCompatActivity() {

    private lateinit var addCommentCall: Call<AddServiceBookingList>
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var getAllCommentsCall: Call<GetAllForumCommentsModel>
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var deletePollCall: Call<AddUpdateForumResponse>
    private lateinit var getPollResultCall: Call<GetPollResultModel>
    private var forumCommentsList: ArrayList<GetAllForumCommentsModel.Data> = ArrayList()
    private var totalForumsList: ArrayList<GetAllForumDetailsModel.Data.Forum> = ArrayList()
    private lateinit var getAllPollsDetailCall: Call<GetAllForumDetailsModel>
    private lateinit var binding: ActivityForumsListBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""

    private var yearList: ArrayList<YearModel.Data> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private var selectedYear: String? = null
    private var pollDeleteDialog: Dialog? = null
    private var pollViewResultDialog: Dialog? = null
    private lateinit var yearModel : Call<YearModel>
    private lateinit var forumAdapter: ForumAdapter
    private lateinit var forumCommentsAdapter: ForumCommentsAdapter


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumsListBinding.inflate(layoutInflater)
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
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        getAllPollsDetailsCall()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllPollsDetailsCall() {
        customProgressDialog.progressDialogShow(this@ForumsListActivity, this.getString(R.string.loading))
        getAllPollsDetailCall = apiInterface.getAllForumDetails("bearer "+Auth_Token,"", "",
        "", "1", "10")
        getAllPollsDetailCall.enqueue(object: Callback<GetAllForumDetailsModel> {
            override fun onResponse(
                call: Call<GetAllForumDetailsModel>,
                response: Response<GetAllForumDetailsModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (totalForumsList.isNotEmpty()) {
                                        totalForumsList.clear()
                                    }
                                    totalForumsList = response.body()!!.data.forum
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())

                                }
                                populateData(totalForumsList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<GetAllForumDetailsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@ForumsListActivity, t.message.toString())
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

        binding.ivAddForum.setOnClickListener {
            val intent = Intent(this, RaiseForumActivity::class.java)
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
                Utils.showToast(this@ForumsListActivity, t.message.toString())
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
            yearAdapter.setForumsCallback(this@ForumsListActivity)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectForumsYear(year: String) {
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


    private fun populateData(forumResponseModelList: ArrayList<GetAllForumDetailsModel.Data.Forum>) {
        //    upcomingList.clear()
        if (forumResponseModelList.size == 0) {
            binding.emptyPollsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyPollsTxt.visibility = View.GONE
            binding.forumsRecyclerView.layoutManager = LinearLayoutManager(this)
            forumAdapter =
                ForumAdapter(this, forumResponseModelList, User_Id!!)
            binding.forumsRecyclerView.adapter = forumAdapter
            forumAdapter.setCallback(this@ForumsListActivity)
            forumAdapter.notifyDataSetChanged()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun viewComment(forumItem: View, forumItem1: GetAllForumDetailsModel.Data.Forum) {
//       viewPollResultCall(forumItem);
        viewPollResultDialog(forumItem, forumItem1)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun viewPollResultDialog(
        forumItem: View,
        forumItem1: GetAllForumDetailsModel.Data.Forum
    ) {
        /*val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.forum_view_comment_dialog_popup, null)
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
       *//* val pollsResultAdapter =
            PollsResultAdapter(this, pollResultsList)
        optionRecyclerView.adapter = pollsResultAdapter
        pollsResultAdapter.notifyDataSetChanged()*//*

        pollViewResultDialog!!.show()*/
        if (forumCommentsList.isNotEmpty()) {
            forumCommentsList.clear()
        }
        bottomSheetDialog = BottomSheetDialog(this@ForumsListActivity, R.style.AppBottomSheetDialogTheme)
        val sheetView: View = layoutInflater.inflate(
            R.layout.forum_comments_btmsheet, forumItem.findViewById<View>(R.id.main_ll) as? ViewGroup
        )
        val closeImg = sheetView.findViewById<ImageView>(R.id.close)
        commentsRecyclerView = sheetView.findViewById<RecyclerView>(R.id.comments_recyclerview)
        val etReply = sheetView.findViewById<EditText>(R.id.ed_comment)
        val sendImg = sheetView.findViewById<ImageView>(R.id.comment_send_img)
        sendImg.setOnClickListener {
            addForumCommentServiceCall(etReply.text.toString(), forumItem1)
        }
        closeImg.setOnClickListener {
            if (bottomSheetDialog.isShowing){
                bottomSheetDialog.dismiss()
            }
        }
        getAllForumCommentsNetworkCall(forumItem1)
        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addForumCommentServiceCall(
        comment: String,
        forumItem1: GetAllForumDetailsModel.Data.Forum
    ) {
        customProgressDialog.progressDialogShow(this@ForumsListActivity, this.getString(R.string.loading))
        var jsonObject = JsonObject()
        jsonObject.addProperty("ForumId", forumItem1.forumID)
        jsonObject.addProperty("Comment", comment)
        addCommentCall = apiInterface.addForumCommentDetails("bearer "+Auth_Token, jsonObject)
        addCommentCall.enqueue(object: Callback<AddServiceBookingList> {
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
                                    Utils.showToast(this@ForumsListActivity, response.body()!!.message.toString())
                                    forumAdapter.notifyDataSetChanged()
                                }

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListActivity, response.body()!!.message.toString())
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
                Utils.showToast(this@ForumsListActivity, t.message.toString())
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllForumCommentsNetworkCall(forumItem1: GetAllForumDetailsModel.Data.Forum) {
        customProgressDialog.progressDialogShow(this@ForumsListActivity, this.getString(R.string.loading))
        getAllCommentsCall = apiInterface.getAllCommentDetailsByForumId("bearer "+Auth_Token, 27)
        getAllCommentsCall.enqueue(object: Callback<GetAllForumCommentsModel> {
            override fun onResponse(
                call: Call<GetAllForumCommentsModel>,
                response: Response<GetAllForumCommentsModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (forumCommentsList.isNotEmpty()) {
                                        forumCommentsList.clear()
                                    }
                                    forumCommentsList = response.body()!!.data as ArrayList<GetAllForumCommentsModel.Data>
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    commentsPopulateData(forumCommentsList)
                                }
                                //  populatePollsData(totalPollsList)
//                                viewPollResultDialog(pollResultsList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<GetAllForumCommentsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@ForumsListActivity, t.message.toString())
            }

        })
    }

    private fun commentsPopulateData(forumCommentsList: ArrayList<GetAllForumCommentsModel.Data>) {
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        forumCommentsAdapter = ForumCommentsAdapter(this@ForumsListActivity, forumCommentsList)
        commentsRecyclerView.adapter = forumCommentsAdapter
      //  forumCommentsAdapter.setCallback(this@ForumsListActivity)
        forumCommentsAdapter.notifyDataSetChanged()
    }


    fun editPoll(forumItem: GetAllForumDetailsModel.Data.Forum) {
//        val intent = Intent(this, UpdatePollActivity::class.java)
//        startActivity(intent)

        val intent = Intent(this, RaiseForumActivity::class.java)
        intent.putExtra("forumItem", forumItem)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deletePoll(forumItem: GetAllForumDetailsModel.Data.Forum) {

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
        member_mob_number.setText("Are you sure you want to delete this Forum?")

        close.setOnClickListener {
            if (pollDeleteDialog!!.isShowing) {
                pollDeleteDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            deletePollServiceCall(forumItem)
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

    fun clickAction(forumItem: GetAllForumDetailsModel.Data.Forum) {

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deletePollServiceCall(forumItem: GetAllForumDetailsModel.Data.Forum) {
        customProgressDialog.progressDialogShow(this@ForumsListActivity, this.getString(R.string.loading))
        deletePollCall = apiInterface.deleteForumDetails("bearer "+Auth_Token, forumItem.forumID)
        deletePollCall.enqueue(object: Callback<AddUpdateForumResponse> {
            override fun onResponse(
                call: Call<AddUpdateForumResponse>,
                response: Response<AddUpdateForumResponse>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListActivity, response.body()!!.message.toString())
                                    totalForumsList.remove(forumItem)
                                    forumAdapter.notifyDataSetChanged()

                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<AddUpdateForumResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@ForumsListActivity, t.message.toString())
            }

        })
    }


}