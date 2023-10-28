package com.example.safehome

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.safehome.activity.BaseActivity
import com.example.safehome.activity.SignInActivity
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityHomeBinding
import com.example.safehome.fragments.AroundMeFragment
import com.example.safehome.fragments.CommunityFragment
import com.example.safehome.fragments.HomeScreenFragment
import com.example.safehome.fragments.MarketPlaceFragment
import com.example.safehome.fragments.MenuFragment
import com.example.safehome.fragments.SafeHomeUserDropDownFragment
import com.example.safehome.model.GetAllNoticeStatus
import com.example.safehome.model.Notice
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.residentview.AddFlatActivity
import com.example.safehome.residentview.AddMemeberActivity
import com.example.safehome.residentview.AddTenantActivity
import com.example.safehome.residentview.AddVehicleActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var bottomNavigationViewView: BottomNavigationView
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var logoutConfirmationDialog: Dialog? = null
    var Auth_Token: String? = ""
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var getAllNoticeCall: Call<GetAllNoticeStatus>
    private var getAllNoticeStatusList: ArrayList<Notice> = ArrayList()
    private var noticeViewList: ArrayList<String> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Auth_Token = Utils.getStringPref(this@HomeActivity, "Token", "")
       // customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@HomeActivity)

        // Find reference to bottom navigation view
        bottomNavigationViewView = binding.appBarLayout.bottomNavigationView

        // here handling Defult homescreen Fragment and if user went to main screen and come back to HomeActivity based on Screen navigate the frags
        onBackFromMainScreen(intent.getStringExtra("ScreenFrom"))

      //  getAllnoticeApiCall(false,"2023")

        // Find reference to side navigation view
        navigationView = binding.navView
        navigationView.setItemIconTintList(null);
        var userName: String? = Utils.getStringPref(this,"userName","")
        var communityName: String? = Utils.getStringPref(this,"communityName","")
        var block: String? = Utils.getStringPref(this,"block","")
        var flatNo: String? = Utils.getStringPref(this,"flatNo","")
        var block_flat = block + "-" + flatNo

        if (userName!!.isEmpty()) {
            navigationView?.getHeaderView(0)?.findViewById<TextView>(R.id.name_txt)?.text = "Tejaswini Pasham"
        }else{
            navigationView?.getHeaderView(0)?.findViewById<TextView>(R.id.name_txt)?.text = userName
        }
        if(communityName!!.isNotEmpty()){
            navigationView?.getHeaderView(0)?.findViewById<TextView>(R.id.community_name_txt)?.text = communityName
        }
        if(block_flat!!.isNotEmpty()){
            navigationView?.getHeaderView(0)?.findViewById<TextView>(R.id.block_num_txt)?.text = block_flat
        }

        bottomNavigationViewView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(R.id.fragment_container, HomeScreenFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.community -> {
                    replaceFragment(R.id.fragment_container, CommunityFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.aroundMe -> {
                    replaceFragment(R.id.fragment_container, AroundMeFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.marketplace -> {
                    replaceFragment(R.id.fragment_container, MarketPlaceFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.menu -> {
                    replaceFragment(R.id.fragment_container, MenuFragment())
                    return@setOnItemSelectedListener true
                }
            }
            true
        }


        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        val inflator = LayoutInflater.from(this)
        val v = inflator.inflate(R.layout.toolbar_layout, null)
        v.layoutParams = ViewGroup.MarginLayoutParams(
            Toolbar.LayoutParams.MATCH_PARENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        val safeUserLayout = v.findViewById<LinearLayout>(R.id.main_user_layout)
        safeUserLayout.setOnClickListener {
            replaceFragment(R.id.fragment_container, SafeHomeUserDropDownFragment())
        }
        toolbar.addView(v)

        drawer = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.syncState()
        drawer.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false
        toggle.toolbarNavigationClickListener = View.OnClickListener {
            println("toggle.toolbarNavigationClickListener")
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(Gravity.LEFT) //CLOSE Nav Drawer!
            } else {
                drawer.openDrawer(Gravity.LEFT) //OPEN Nav Drawer!
            }
        }

        navigationView.setNavigationItemSelectedListener(this)

        onBackPressedDispatcher.addCallback(callback)
    }

    private fun onBackFromMainScreen(ScreenFrom: String?) {
        when(ScreenFrom){
            "HomeScreenFrag" ->{
                replaceFragment(R.id.fragment_container, HomeScreenFragment())
                bottomNavigationViewView.setSelectedItemId(R.id.home);
            }
            "MenuScreenFrag" ->{
                replaceFragment(R.id.fragment_container, MenuFragment())
                bottomNavigationViewView.setSelectedItemId(R.id.menu);
            }
            else ->{
                replaceFragment(R.id.fragment_container, HomeScreenFragment())
                bottomNavigationViewView.setSelectedItemId(R.id.home);
            }
        }
    }

    // for going to back
    val callback = onBackPressedDispatcher.addCallback(this) {
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_family -> {
                val intent = Intent(this, AddMemeberActivity::class.java)
                startActivity(intent)
                drawer.closeDrawer(GravityCompat.START)
            }

            R.id.my_vehicles -> {
                val intent = Intent(this, AddVehicleActivity::class.java)
                startActivity(intent)
                drawer.closeDrawer(GravityCompat.START)
            }

            R.id.tenant -> {
                val intent = Intent(this, AddTenantActivity::class.java)
                startActivity(intent)
                drawer.closeDrawer(GravityCompat.START)
            }

            R.id.add_flat -> {
                val intent = Intent(this, AddFlatActivity::class.java)
                startActivity(intent)
                drawer.closeDrawer(GravityCompat.START)
            }

            R.id.signout -> {
                confirmationLogoutPopup()
            }
        }
        return true
    }

    private fun clearData() {
        Utils.saveStringPref(
            this@HomeActivity,
            "userName",
            ""
        )
        Utils.saveStringPref(
            this@HomeActivity,
            "userEmail",
            ""
        )
        Utils.saveStringPref(
            this@HomeActivity,
            "mobileNo",
            ""
        )
        Utils.saveStringPref(
            this@HomeActivity,
            "communityName",
            ""
        )
        Utils.saveStringPref(
            this@HomeActivity,
            "block",
            ""
        )
        Utils.saveStringPref(
            this@HomeActivity,
            "flatNo",
            ""
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun confirmationLogoutPopup() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.confirmation_logout_popup, null)
        logoutConfirmationDialog = Dialog(this@HomeActivity, R.style.CustomAlertDialog)
        logoutConfirmationDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        logoutConfirmationDialog!!.setContentView(view)
        logoutConfirmationDialog!!.setCanceledOnTouchOutside(true)
        logoutConfirmationDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(logoutConfirmationDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        logoutConfirmationDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val no_btn: TextView = view.findViewById(R.id.no_btn)
        val yes_btn: TextView = view.findViewById(R.id.yes_btn)

        no_btn.setOnClickListener {
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }
        }
        yes_btn.setOnClickListener{
            Utils.savebooleanPref(this, "IsLoggedIn", false)
            clearData()
            val loginIntent = Intent(this, SignInActivity::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(loginIntent)
            finish()
            overridePendingTransition(R.anim.enter_from_left_frag, R.anim.exit_to_right_frag)
            logoutConfirmationDialog!!.dismiss()
        }


        close.setOnClickListener {
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }
        }

        logoutConfirmationDialog!!.show()
    }

    override fun onResume() {
        super.onResume()
        // here handling Defult homescreen Fragment and if user went to main screen and come back to HomeActivity based on Screen navigate the frags
        onBackFromMainScreen(intent.getStringExtra("ScreenFrom"))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllnoticeApiCall(noticeView: String, year: String) {
        Log.e("Token", Auth_Token.toString())
       // customProgressDialog.progressDialogShow(this@NoticeActivity, this.getString(R.string.loading))
        getAllNoticeCall = apiInterface.getallNoticesStatus("bearer " + Auth_Token,10,1, noticeView,year)
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
                                        response.body()!!.data as ArrayList<Notice>
                                   // latestNoticeData()
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@HomeActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                   // customProgressDialog.progressDialogDismiss()
                    Utils.showToast(this@HomeActivity, response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<GetAllNoticeStatus>, t: Throwable) {
               // customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@HomeActivity, t.message.toString())
            }
        })
    }

//    private fun latestNoticeData() {
//        val bundle = Bundle()
//        bundle.putString("edttext", "From Activity")
//        replaceFragment()
//    }


}