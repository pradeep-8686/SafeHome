package com.example.safehome.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.complaints.ComplaintsActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.dailyhelp.DailyHelpActivity
import com.example.safehome.databinding.FragmentHomeScreenBinding
import com.example.safehome.eventsview.EventsActivity
import com.example.safehome.facilitiesview.FacilitiesActivity
import com.example.safehome.maintenance.MaintenanceActivity
import com.example.safehome.meetings.MeetingsActivity
import com.example.safehome.model.GetAllNoticeStatus
import com.example.safehome.model.ImageSource
import com.example.safehome.model.Notice
import com.example.safehome.notice.NoticeActivity
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.services.ServicesActivity
import com.example.safehome.viewpageradapter.HomeBottomViewPagerAdapter
import com.example.safehome.viewpageradapter.HomeTopViewPagerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import kotlin.concurrent.schedule

class HomeScreenFragment : Fragment() {

    private lateinit var binding: FragmentHomeScreenBinding
    private lateinit var essentialsDialog: Dialog
    private lateinit var commuteDialog: Dialog
    private lateinit var utilityDialog: Dialog
    private lateinit var couponsDialog: Dialog
    private var topImageSliderDotsCount = 0
    private var bottomImageSliderDotsCount = 0
    private lateinit var topDotsImageViewArray: Array<ImageView?>
    private lateinit var bottomDotsImageViewArray: Array<ImageView?>
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var imageSourceList: ArrayList<ImageSource>
    private lateinit var timer: Timer

    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var Auth_Token: String? = ""
    private var residentId: String?= null
    private lateinit var getAllNoticeCall: Call<GetAllNoticeStatus>
    private var getAllNoticeStatusList: ArrayList<Notice> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        residentId = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")


        dialogPopupEvents()
        viewPagerAddChangeListener()
       //  getAlbumsData()
        addImageDataSource()
        loadTopImageView()
        clickEvents()
     //   getAllnoticeApiCall("","2023")

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllnoticeApiCall(noticeView: String?, year: String) {
        Log.e("Token", Auth_Token.toString())
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        getAllNoticeCall = apiInterface.getallNoticesStatus("bearer " + Auth_Token,10,1,noticeView,year)
        getAllNoticeCall.enqueue(object : Callback<GetAllNoticeStatus> {
            override fun onResponse(
                call: Call<GetAllNoticeStatus>,
                response: Response<GetAllNoticeStatus>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    //  Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    if (getAllNoticeStatusList.isNotEmpty()) {
                                        getAllNoticeStatusList.clear()
                                    }
                                    getAllNoticeStatusList =
                                        response.body()!!.data.noticedata as ArrayList<Notice>

                                 //   populateTopImageSliderData(getAllNoticeStatusList)

                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<GetAllNoticeStatus>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }


    private fun clickEvents() {

        binding.maintenanceLayout.setOnClickListener {
            try {
                val maintenanceIntent = Intent(requireContext(), MaintenanceActivity::class.java)
                maintenanceIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                maintenanceIntent.putExtra("ScreenFrom", "HomeScreenFrag")
                requireContext().startActivity(maintenanceIntent)
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.facilitiesLayout.setOnClickListener {
            try {
                val fIntent = Intent(requireContext(), FacilitiesActivity::class.java)
                fIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                fIntent.putExtra("ScreenFrom", "HomeScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(fIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.eventsLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), EventsActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "HomeScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.dailyHelpLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), DailyHelpActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "HomeScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.apply {
            try {
                noticesLayout.setOnClickListener{
                    val eIntent = Intent(requireContext(), NoticeActivity::class.java)
                    eIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    eIntent.putExtra("ScreenFrom", "HomeScreenFrag")
                    getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    requireContext().startActivity(eIntent)
                }
            }catch (e: Exception){

            }
        }
        binding.servicesLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), ServicesActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "HomeScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.llComplaints.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), ComplaintsActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "HomeScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.llMeeting.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), MeetingsActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "HomeScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun loadTopImageView() {
        // here loading image from pixel
//        Glide.with(requireActivity())
//            .load("https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?auto=compress&cs=tinysrgb&w=600")
//            .fitCenter()
//            .into(binding.topImageView)

        Glide.with(requireActivity())
            .load(R.drawable.special_offer)
            .fitCenter()
            .into(binding.topImageView)
    }

    private fun addImageDataSource() {
        // here add image data source

        imageSourceList = ArrayList()
        imageSourceList.add(
            ImageSource(
                "1",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard. Lorem Ipsum has been the industry's standard...",
                "food related"
            )
        )
        imageSourceList.add(
            ImageSource(
                "2",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard Lorem Ipsum has been the industry's standard...",
                "grocery related"
            )
        )
        imageSourceList.add(
            ImageSource(
                "3",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard Lorem Ipsum has been the industry's standard...",
                "e-commerce related"
            )
        )

        populateTopImageSliderData(imageSourceList)
        populateBottomImageSliderData(imageSourceList)
    }

    private fun viewPagerAddChangeListener() {
        //top slider
        binding.topViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // nothing here
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until topImageSliderDotsCount) {
                    try {
                        topDotsImageViewArray[i]?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.non_active_dot
                            )
                        )
                    } catch (ex: IllegalStateException) {
                    }
                }
                try {
                    topDotsImageViewArray[position]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.active_dot
                        )
                    )
                } catch (ex: IllegalStateException) {
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // nothing here
            }
        })

        // bottom slider
        binding.bottomViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // nothing here
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until bottomImageSliderDotsCount) {
                    try {
                        bottomDotsImageViewArray[i]?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.non_active_dot
                            )
                        )
                    } catch (ex: IllegalStateException) {
                    }
                }
                try {
                    bottomDotsImageViewArray[position]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.active_dot
                        )
                    )
                } catch (ex: IllegalStateException) {
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // nothing here
            }
        })
    }

    private fun dialogPopupEvents() {
        // here dialog pop up events logic
        binding.essentialLayout.setOnClickListener {
            essentialsPopup()
        }
        binding.commuteLayout.setOnClickListener {
            commutePopup()
        }
        binding.utilityImageView.setOnClickListener {
            utilityPopup()
        }
        binding.couponsImageView.setOnClickListener {
            //couponsPopup()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun essentialsPopup() {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.essentials_dialog, null)
        essentialsDialog = Dialog(requireActivity(), R.style.CustomAlertDialog)
        essentialsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        essentialsDialog.setContentView(view)
        essentialsDialog.setCanceledOnTouchOutside(true)
        essentialsDialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(essentialsDialog.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.8).toInt()
        //  lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        essentialsDialog.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        val cancel_img:ImageView = view.findViewById(R.id.cancel_img)

        cancel_img.setOnClickListener {

            if (essentialsDialog.isShowing) {
                essentialsDialog.dismiss()
            }
        }

        essentialsDialog.show()
    }

    private fun commutePopup() {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.commute_pop_up_dialog, null)
        commuteDialog = Dialog(requireActivity(), R.style.CustomAlertDialog)
        commuteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        commuteDialog.setContentView(view)
        commuteDialog.setCanceledOnTouchOutside(true)
        commuteDialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(commuteDialog.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height =LinearLayout.LayoutParams.WRAP_CONTENT

        // lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        commuteDialog.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        val cancel_img:ImageView = view.findViewById(R.id.cancel_img)

        cancel_img.setOnClickListener {

            if (commuteDialog.isShowing) {
                commuteDialog.dismiss()
            }
        }
        commuteDialog.show()
    }

    private fun utilityPopup() {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.utility_pop_up_dialog, null)
        utilityDialog = Dialog(requireActivity(), R.style.CustomAlertDialog)
        utilityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        utilityDialog.setContentView(view)
        utilityDialog.setCanceledOnTouchOutside(true)
        utilityDialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(utilityDialog.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        // lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height =LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        utilityDialog.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        val cancel_img:ImageView = view.findViewById(R.id.cancel_img)

        cancel_img.setOnClickListener {

            if (utilityDialog.isShowing) {
                utilityDialog.dismiss()
            }
        }
        utilityDialog.show()
    }

    private fun couponsPopup() {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.essentials_dialog, null)
        couponsDialog = Dialog(requireActivity())
        couponsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        couponsDialog.setContentView(view)
        couponsDialog.setCanceledOnTouchOutside(true)
        couponsDialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(couponsDialog.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        couponsDialog.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        couponsDialog.show()
    }


    private fun populateTopImageSliderData(body: ArrayList<ImageSource>) {

        if (body.isNotEmpty()) {
            val homeTopViewPagerAdapter = HomeTopViewPagerAdapter(requireContext(), body)
            binding.topViewPager.adapter = homeTopViewPagerAdapter
            topImageSliderDotsCount = homeTopViewPagerAdapter.count
            topDotsImageViewArray = arrayOfNulls(topImageSliderDotsCount)

            for (i in 0 until topImageSliderDotsCount) {
                topDotsImageViewArray[i] = ImageView(requireContext())
                topDotsImageViewArray[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.non_active_dot
                    )
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                binding.topViewPagerSliderDots.addView(topDotsImageViewArray[i], params)
            }

            topDotsImageViewArray[0]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.active_dot
                )
            )
            topImageSlideAutomatically(3000)
        }
    }

    private fun populateBottomImageSliderData(body: List<ImageSource>) {
        if (body.isNotEmpty()) {
            val homeBottomViewPagerAdapter = HomeBottomViewPagerAdapter(requireContext(), body)
            binding.bottomViewPager.adapter = homeBottomViewPagerAdapter
            bottomImageSliderDotsCount = homeBottomViewPagerAdapter.count
            bottomDotsImageViewArray = arrayOfNulls(bottomImageSliderDotsCount)

            for (i in 0 until bottomImageSliderDotsCount) {
                bottomDotsImageViewArray[i] = ImageView(requireContext())
                bottomDotsImageViewArray[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.non_active_dot
                    )
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                binding.bottomViewPagerSliderDots.addView(bottomDotsImageViewArray[i], params)
            }

            bottomDotsImageViewArray[0]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.active_dot
                )
            )
            bottomImageSlideAutomatically(3000)
        }
    }

    private fun topImageSlideAutomatically(milliSeconds: Long) {
        /*   Thread {

               // top image slider loop
               for (i in 0..topImageSliderDotsCount) {
                   //sleep
                   try {
                       Thread.sleep(milliSeconds)
                   } catch (e: InterruptedException) {
                       e.stackTrace
                   }
                   //change position
                   // runOnUiThread { binding.topViewPager.currentItem = i }

                   // alternative code
                   handler.post {
                       // Your code to run on the UI thread
                       // For example:
                       binding.topViewPager.currentItem = i
                   }
               }

           }.start()*/

        timer = Timer()
        timer.schedule(5000, 5000) {
            runOnUiThread {
                val currentItem = binding.topViewPager.currentItem
                if (currentItem < topDotsImageViewArray.size - 1) {
                    binding.topViewPager.currentItem = currentItem + 1
                } else {
                    binding.topViewPager.currentItem = 0
                }
            }
        }
    }

    private fun bottomImageSlideAutomatically(milliSeconds: Long) {
        /*Thread {

            // top image slider loop
            for (i in 0..bottomImageSliderDotsCount) {
                //sleep
                try {
                    Thread.sleep(milliSeconds)
                } catch (e: InterruptedException) {
                    e.stackTrace
                }
                //change position
                // runOnUiThread { binding.topViewPager.currentItem = i }

                // alternative code
                handler.post {
                    // Your code to run on the UI thread
                    // For example:
                    binding.bottomViewPager.currentItem = i
                }
            }

        }.start()*/

        timer = Timer()
        timer.schedule(5000, 5000) {
            runOnUiThread {
                val currentItem = binding.bottomViewPager.currentItem
                if (currentItem < bottomDotsImageViewArray.size - 1) {
                    binding.bottomViewPager.currentItem = currentItem + 1
                } else {
                    binding.bottomViewPager.currentItem = 0
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (timer!= null){
            timer.cancel()
        }
    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }
}