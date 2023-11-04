package com.example.safehome.forums

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityDetailForumItemBinding
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumsListDetailActivity : Activity() {
    private lateinit var addReplyCommentCall: Call<AddReplyCommentModel>
    private lateinit var deleteCommentCall: Call<AddServiceBookingList>
    private lateinit var binding: ActivityDetailForumItemBinding
    private lateinit var forumItem: GetAllForumDetailsModel.Data.Forum
    private lateinit var addCommentCall: Call<AddServiceBookingList>
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var getAllCommentsCall: Call<GetAllForumCommentsModel>
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var forumCommentsList: ArrayList<GetAllForumCommentsModel.Data> = ArrayList()
    private var totalForumsList: ArrayList<GetAllForumDetailsModel.Data.Forum> = ArrayList()
    private lateinit var getAllPollsDetailCall: Call<GetAllForumDetailsModel>
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""
    private var pollDeleteDialog: Dialog? = null
    private lateinit var forumAdapter: ForumAdapter
    private lateinit var forumCommentsAdapter: ForumCommentsAdapter
    private lateinit var deletePollCall: Call<AddUpdateForumResponse>

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailForumItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent!= null){
            forumItem = intent.getSerializableExtra("forumItem") as GetAllForumDetailsModel.Data.Forum
        }

        inIt()

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")
        ScreenFrom = intent.getStringExtra("ScreenFrom")

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, ForumsListActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }

        if (!forumItem.postedBy.isNullOrEmpty()) {
            if (forumItem.postedBy == "Resident"){
                binding.tvRaisedBy.text = "By : ${forumItem.postedBy} Block " + forumItem.block + "-" + forumItem.flatNo
            }else {
                binding.tvRaisedBy.text = "By : ${forumItem.postedBy}"
            }        }

        if (!forumItem.topic.isNullOrEmpty()) {
            binding.tvTopic.text = forumItem.topic
        }

        if (forumItem.attachment != null) {
            binding.ivForumImage.visibility = View.VISIBLE

            Glide.with(this@ForumsListDetailActivity)
                .load( forumItem.attachment)
                .fitCenter()
                .into(binding.ivForumImage)

        }else{
            binding.ivForumImage.visibility = View.GONE
        }

        if (!forumItem.description.isNullOrEmpty()) {
            binding.tvForumDescription.text = "${forumItem.description}"
        }

        if (!forumItem.viewCount.toString().isNullOrEmpty()) {
            binding.tvNoOfViews.text = "${forumItem.viewCount}"
        }

        if (!forumItem.commentCount.toString().isNullOrEmpty()) {
            binding.tvForumComment.text = "${forumItem.commentCount}"
        }

        binding.tvForumComment.setOnClickListener {
            viewForumCommentBtmSheet(it, forumItem)
        }

        if (forumItem.createdBy == User_Id!!.toInt()){
            binding.ivEdit.visibility = View.VISIBLE
            binding.ivDelete.visibility = View.VISIBLE

        }else{
            binding.ivEdit.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
        }

        binding.ivEdit.setOnClickListener {
            val intent = Intent(this, RaiseForumActivity::class.java)
            intent.putExtra("forumItem", forumItem)
            startActivity(intent)
            finish()
        }

        binding.ivDelete.setOnClickListener {
            deletePoll(forumItem)
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun viewForumCommentBtmSheet(it: View?, forumItem: GetAllForumDetailsModel.Data.Forum) {
        if (forumCommentsList.isNotEmpty()) {
            forumCommentsList.clear()
        }
        bottomSheetDialog = BottomSheetDialog(this@ForumsListDetailActivity, R.style.AppBottomSheetDialogTheme)
        val sheetView: View = layoutInflater.inflate(
            R.layout.forum_comments_btmsheet, it!!.findViewById<View>(R.id.main_ll) as? ViewGroup
        )
        val closeImg = sheetView.findViewById<ImageView>(R.id.close)
        commentsRecyclerView = sheetView.findViewById<RecyclerView>(R.id.comments_recyclerview)
        val etReply = sheetView.findViewById<EditText>(R.id.ed_comment)
        val sendImg = sheetView.findViewById<ImageView>(R.id.comment_send_img)
        sendImg.setOnClickListener {
            addForumCommentServiceCall(etReply.text.toString(), forumItem)
        }
        closeImg.setOnClickListener {
            if (bottomSheetDialog.isShowing){
                bottomSheetDialog.dismiss()
            }
        }
        getAllForumCommentsNetworkCall(forumItem)
        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllForumCommentsNetworkCall(forumItem1: GetAllForumDetailsModel.Data.Forum) {
        customProgressDialog.progressDialogShow(this@ForumsListDetailActivity, this.getString(R.string.loading))
        getAllCommentsCall = apiInterface.getAllCommentDetailsByForumId("bearer "+Auth_Token, forumItem1.forumID)
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
                                    Utils.showToast(this@ForumsListDetailActivity, response.body()!!.message.toString())
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
                Utils.showToast(this@ForumsListDetailActivity, t.message.toString())
            }

        })
    }

    private fun commentsPopulateData(forumCommentsList: ArrayList<GetAllForumCommentsModel.Data>) {
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        forumCommentsAdapter = ForumCommentsAdapter(this@ForumsListDetailActivity, forumCommentsList)
        commentsRecyclerView.adapter = forumCommentsAdapter
        forumCommentsAdapter.setCallback(this@ForumsListDetailActivity)
        forumCommentsAdapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addForumCommentServiceCall(
        comment: String,
        forumItem1: GetAllForumDetailsModel.Data.Forum
    ) {
        customProgressDialog.progressDialogShow(this@ForumsListDetailActivity, this.getString(R.string.loading))
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
                                    Utils.showToast(this@ForumsListDetailActivity, response.body()!!.message.toString())
                                    forumCommentsAdapter.notifyDataSetChanged()
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListDetailActivity, response.body()!!.message.toString())
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
                Utils.showToast(this@ForumsListDetailActivity, t.message.toString())
            }

        })
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deletePollServiceCall(forumItem: GetAllForumDetailsModel.Data.Forum) {
        customProgressDialog.progressDialogShow(this@ForumsListDetailActivity, this.getString(R.string.loading))
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
                                    Utils.showToast(this@ForumsListDetailActivity, response.body()!!.message.toString())
                                    moveToForumsListActivity()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListDetailActivity, response.body()!!.message.toString())
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
                Utils.showToast(this@ForumsListDetailActivity, t.message.toString())
            }

        })
    }

    private fun moveToForumsListActivity() {
        val intent = Intent(this, ForumsListActivity::class.java)
        startActivity(intent)
        finish()
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteForumComment(forumCommentItem: GetAllForumCommentsModel.Data) {
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
        member_mob_number.setText("Are you sure you want to delete this comment?")

        close.setOnClickListener {
            if (pollDeleteDialog!!.isShowing) {
                pollDeleteDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            deleteCommentServiceCall(forumCommentItem)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteCommentServiceCall(forumCommentItem: GetAllForumCommentsModel.Data) {
        customProgressDialog.progressDialogShow(this@ForumsListDetailActivity, this.getString(R.string.loading))
        deleteCommentCall = apiInterface.deleteForumCommentDetails("bearer "+Auth_Token, forumCommentItem.commentId.toString())
        deleteCommentCall.enqueue(object: Callback<AddServiceBookingList> {
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
                                    Utils.showToast(this@ForumsListDetailActivity, response.body()!!.message.toString())
                                 //   moveToForumsListActivity()
                                    forumCommentsList.remove(forumCommentItem)
                                    forumCommentsAdapter.notifyDataSetChanged()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListDetailActivity, response.body()!!.message.toString())
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
                Utils.showToast(this@ForumsListDetailActivity, t.message.toString())
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun addCommentReplyCall(forumCommentItem: GetAllForumCommentsModel.Data, message: String) {
        customProgressDialog.progressDialogShow(this@ForumsListDetailActivity, this.getString(R.string.loading))
        val jsonObject = JsonObject()
        jsonObject.addProperty("CommentId", forumCommentItem.commentId)
        jsonObject.addProperty("Message", message)
        addReplyCommentCall = apiInterface.addForumCommentReply("bearer "+Auth_Token, jsonObject)
        addReplyCommentCall.enqueue(object: Callback<AddReplyCommentModel> {
            override fun onResponse(
                call: Call<AddReplyCommentModel>,
                response: Response<AddReplyCommentModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListDetailActivity, response.body()!!.message.toString())
                                    //   moveToForumsListActivity()

                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ForumsListDetailActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<AddReplyCommentModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@ForumsListDetailActivity, t.message.toString())
            }

        })
    }


}