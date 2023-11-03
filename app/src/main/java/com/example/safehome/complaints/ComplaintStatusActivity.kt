package com.example.safehome.complaints

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
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
import android.widget.RadioButton
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.CategoryRaiseComplaintAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityComplaintStatusBinding
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog

class ComplaintStatusActivity : AppCompatActivity() {

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var binding: ActivityComplaintStatusBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var status: String? = ""
    private var showGalleryDetailsDialog: Dialog? = null

    private var escalateToPopUpWindow: PopupWindow? = null
    private var escalateToList: ArrayList<String> = ArrayList()


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        status = intent.getStringExtra("status")

        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")


        binding.tittleTxt.text = "Plumbing Issue"
        binding.tvCategory.text = status

        binding.backBtnClick.setOnClickListener {
            onBackPressed()
        }

        addEscalateToList()
        loadRadioGroup()
        clickActions()

        /*   if (status == "pending"){
               binding.tvDetailsDisclosedTitle.visibility = View.GONE
               binding.tvDetailsDisclosed.visibility = View.GONE
           }*/
    }

    private fun clickActions() {

        binding.imageView3.setOnClickListener {
            showGalleryPopup(R.drawable.dummy_img_1)
        }
        binding.imageView2.setOnClickListener {
            showGalleryPopup(R.drawable.dummy_img_1)
        }

        binding.escalateToCl.setOnClickListener{
            escalateToPopUp()
        }

        binding.yesBtn.setOnClickListener {
            showBootomSheetDialog(it)
        }

        /*  binding.mainLayout.setOnClickListener {

              if (attachPhotoWindow?.isShowing == true) {
                  attachPhotoWindow?.dismiss()
              }

              if (keepPollPopupWindow?.isShowing == true) {
                  keepPollPopupWindow?.dismiss()
              }

          }*/

    }

    @SuppressLint("MissingInflatedId")
    private fun showBootomSheetDialog(view: View) {
        bottomSheetDialog = BottomSheetDialog(this@ComplaintStatusActivity, R.style.AppBottomSheetDialogTheme)
        val sheetView: View = layoutInflater.inflate(
            R.layout.forum_comments_btmsheet, view!!.findViewById<View>(R.id.main_ll) as? ViewGroup
        )
        val closeImg = sheetView.findViewById<ImageView>(R.id.close)
        //  commentsRecyclerView = sheetView.findViewById<RecyclerView>(R.id.comments_recyclerview)
        val commentsLayout = sheetView.findViewById<RelativeLayout>(R.id.comments_rl)
        commentsLayout.visibility = View.VISIBLE
        val replyLayout = sheetView.findViewById<RelativeLayout>(R.id.forum_reply_layout)
        replyLayout.visibility = View.VISIBLE
        val etReply = sheetView.findViewById<EditText>(R.id.ed_comment)
        val sendImg = sheetView.findViewById<ImageView>(R.id.comment_send_img)
        sendImg.setOnClickListener {
            //  addForumCommentServiceCall(etReply.text.toString(), forumItem)
        }
        closeImg.setOnClickListener {
            if (bottomSheetDialog.isShowing){
                bottomSheetDialog.dismiss()
            }
        }
        //  getAllForumCommentsNetworkCall(forumItem)
        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
    }

    private fun addEscalateToList() {
        escalateToList.add("Admin")
        escalateToList.add("President")
        escalateToList.add("Treasurer")
    }

    private fun escalateToPopUp() {
        if (escalateToPopUpWindow != null) {
            if (escalateToPopUpWindow!!.isShowing) {
                escalateToPopUpWindow!!.dismiss()
            } else {
                escalateToPopUpWindow()
            }
        } else {
            escalateToPopUpWindow()
        }

    }

    private fun escalateToPopUpWindow() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        escalateToPopUpWindow = PopupWindow(
            view,
            binding.escalateToTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (escalateToList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@ComplaintStatusActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = CategoryRaiseComplaintAdapter(this@ComplaintStatusActivity, escalateToList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackEscalateTo(this@ComplaintStatusActivity)
        }
        escalateToPopUpWindow!!.elevation = 10F
        escalateToPopUpWindow!!.showAsDropDown(binding.escalateToCl, 0, 0, Gravity.CENTER)
    }

    fun setCallbackEscalateTo(category: String) {
        if (escalateToPopUpWindow != null) {
            if (escalateToPopUpWindow!!.isShowing) {
                escalateToPopUpWindow!!.dismiss()
            }
        }
        if (category != null) {
            binding.escalateToTxt.text = category
            // selectedCategoryId = ComplaintStatus
        }
        Log.d(HistoryFragment.TAG, "$category")
    }

    private fun loadRadioGroup() {

        val resolved = "Is Your Complaint Resolved?"
        val  delayed= "Is the Action getting delayed?"

        binding.tvReInitiate.visibility = View.GONE
        binding.radioGroupReInitiate.visibility = View.GONE
        binding.tvEscalateTo.visibility = View.GONE
        binding.escalateToCl.visibility = View.GONE


        if (status == "Pending"){
            binding.tvDelayed.text = delayed

        }else if (status == "Resolved"){
            binding.tvDelayed.text = resolved

        }else{
            binding.tvDelayed.text = delayed

        }

        // Set up a listener for the RadioGroup
        binding.radioGroup.setOnCheckedChangeListener { view, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            // Check which radio button is selected
            when (selectedRadioButton) {
                binding.radioButtonYes -> {
                    if ( binding.tvDelayed.text == delayed){
                        binding.tvEscalateTo.visibility = View.VISIBLE
                        binding.escalateToCl.visibility = View.VISIBLE
                    }else{
                        binding.imgAttachPhoto.visibility = View.VISIBLE
                        binding.tvAttachPhoto.visibility = View.VISIBLE
                        binding.tvReInitiate.visibility = View.GONE
                        binding.radioGroupReInitiate.visibility = View.GONE
                    }
                }
                binding.radioButtonNo -> {
                    if ( binding.tvDelayed.text == delayed){
                        binding.tvEscalateTo.visibility = View.GONE
                        binding.escalateToCl.visibility = View.GONE
                    }else{
                        binding.imgAttachPhoto.visibility = View.GONE
                        binding.tvAttachPhoto.visibility = View.GONE

                        binding.tvReInitiate.visibility = View.VISIBLE
                        binding.radioGroupReInitiate.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.radioGroupReInitiate.setOnCheckedChangeListener { view, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            // Check which radio button is selected
            when (selectedRadioButton) {
                binding.reInitiateYes -> {
                    binding.tvEscalateTo.visibility = View.VISIBLE
                    binding.escalateToCl.visibility = View.VISIBLE
                    binding.tvEscalateTo.text = "Assigned To"

                }
                binding.reInitiateNo -> {

                    binding.tvEscalateTo.visibility = View.GONE
                    binding.escalateToCl.visibility = View.GONE

                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showGalleryPopup(fileName : Int) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.show_image_dialog, null)
        showGalleryDetailsDialog = Dialog(this, R.style.CustomAlertDialog)
        showGalleryDetailsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        showGalleryDetailsDialog!!.setContentView(view)
        showGalleryDetailsDialog!!.setCanceledOnTouchOutside(true)
        showGalleryDetailsDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(showGalleryDetailsDialog!!.window!!.attributes)

        val viewImage = view.findViewById<PhotoView>(R.id.viewImage)

        Glide.with(this)
            .load(fileName)
            .fitCenter()
            .into(viewImage)

        viewImage.isZoomable = true
        viewImage.maximumScale = 5.0f
        viewImage.mediumScale = 2.5f

        viewImage.setOnClickListener {

        }
        val cancelImg = view.findViewById<ImageView>(R.id.gallery_dialog_closeImg)
        cancelImg.setOnClickListener{
            showGalleryDetailsDialog!!.dismiss()
        }

//        lp.width = (Utils.screenWidth * 0.9).toInt()
//        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
//        lp.gravity = Gravity.CENTER
//        showGalleryDetailsDialog!!.window!!.attributes = lp
        showGalleryDetailsDialog!!.show()
    }
}