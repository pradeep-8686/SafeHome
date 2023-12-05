package com.example.safehome.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AllCommunitiesAdapter
import com.example.safehome.adapter.BlocksAdapter
import com.example.safehome.adapter.CommunityCitiesAdapter
import com.example.safehome.adapter.CommunityStatesAdapter
import com.example.safehome.adapter.FlatAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityPersonalInfoBinding
import com.example.safehome.model.AllCommunities
import com.example.safehome.model.Block
import com.example.safehome.model.Blocks
import com.example.safehome.model.BlocksData
import com.example.safehome.model.CitiesData
import com.example.safehome.model.CommunityCities
import com.example.safehome.model.CommunityDetails
import com.example.safehome.model.CommunityStates
import com.example.safehome.model.Flat
import com.example.safehome.model.Flats
import com.example.safehome.model.MobileSignUp
import com.example.safehome.model.State
import com.example.safehome.model.UserDetail
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalInfoActivity : AppCompatActivity() {

    private var mobileNumber: String? = null
    private var statesPopupWindow: PopupWindow? = null
    private var citiesPopupWindow: PopupWindow? = null
    private var communitiesPopupWindow: PopupWindow? = null
    private var blocksPopupWindow: PopupWindow? = null
    private lateinit var binding: ActivityPersonalInfoBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var communityStatesCall: Call<CommunityStates>
    private lateinit var communityCitiesCall: Call<CommunityCities>
    private lateinit var allCommunitiesCall: Call<AllCommunities>
    private lateinit var blocksCall: Call<Blocks>
    private var statesList: ArrayList<State> = ArrayList()
    private var citiesList: ArrayList<CitiesData> = ArrayList()
    private var communitiesList: ArrayList<CommunityDetails> = ArrayList()
    private var blocksList: ArrayList<BlocksData> = ArrayList()
    private var selectedState: String = ""
    private var selectedCity: String = ""
    private var selectedCommunity: String = "0"
    private var selectedBlockid: String = ""
    private var selectedBlock: String = ""
    private var selectedFlot: String = ""
    private lateinit var customProgressDialog: CustomProgressDialog
    private var isAgree = false
    private lateinit var updateUserCall: Call<UserDetail>
    private var CommunityId = 0
    private var flatsList: ArrayList<Flat> = ArrayList()
    private lateinit var flatCall: Call<Flats>
    private var flatPopupWindow: PopupWindow? = null
    private lateinit var  User_Id: String

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // progressbar
        customProgressDialog = CustomProgressDialog()

        var name:String = Utils.getStringPref(this,"First_Name","") +" "+
                Utils.getStringPref(this,"Last_Name","")
         User_Id = Utils.getStringPref(this,"User_Id","").toString()
        binding.userNameText.text = "Welcome "+ name+ "!"

        binding.selectStateHeader.text =
            Html.fromHtml(getString(R.string.state) + "" + "<font color='white'>*</font>")
        binding.selectCityHeader.text =
            Html.fromHtml(getString(R.string.city) + "" + "<font color='white'>*</font>")
        binding.selectCommunityHeader.text =
            Html.fromHtml(getString(R.string.community) + "" + "<font color='white'>*</font>")
        binding.selectBlockHeader.text =
            Html.fromHtml(getString(R.string.block) + "" + "<font color='white'>*</font>")
        binding.selectFlatHeader.text =
            Html.fromHtml(getString(R.string.flat) + "" + "<font color='white'>*</font>")

        getUserData()
        addCheckBoxChangeListener()
        setupApiService()
        getDataFromServer()
        buttonClickEvents()
    }

    private fun getUserData() {
        // here ave user data
        mobileNumber = Utils.getStringPref(this, "Mobile_Number", "")
    }

    private fun addCheckBoxChangeListener() {
        binding.agreeCb.setOnCheckedChangeListener { _, isChecked ->
            isAgree = isChecked
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buttonClickEvents() {
        binding.selectStateCl.setOnClickListener {

            if (statesPopupWindow != null) {
                if (statesPopupWindow!!.isShowing) {
                    statesPopupWindow!!.dismiss()
                } else {
                    showStatesDropDown()
                }
            } else {
                showStatesDropDown()
            }
            removeOtherthanState()
        }

        binding.selectCityCl.setOnClickListener {
            if (selectedState.isNotEmpty()) {
                if (citiesPopupWindow != null) {

                    if (citiesPopupWindow!!.isShowing) {
                        citiesPopupWindow!!.dismiss()
                    } else {
                        showCitiesDropDown()
                    }
                } else {
                    showCitiesDropDown()
                }

            } else {
                Utils.showToast(this, this.getString(R.string.select_state))
            }
            removeOtherthanCity()
        }

        binding.selectCommunityCl.setOnClickListener {
            if (selectedState.isNotEmpty()) {
                if (selectedCity.isNotEmpty()) {
                    if (communitiesPopupWindow != null) {

                        if (communitiesPopupWindow!!.isShowing) {
                            communitiesPopupWindow!!.dismiss()
                        } else {
                            showCommunitiesDropDown()
                        }
                    } else {
                        showCommunitiesDropDown()
                    }
                } else {
                    Utils.showToast(this, this.getString(R.string.select_city))
                }
            } else {
                Utils.showToast(this, this.getString(R.string.select_state))
            }
            removeOtherthanCommunity()
        }

        binding.selectBlockCl.setOnClickListener {
            if (selectedState.isNotEmpty()) {
                if (selectedCity.isNotEmpty()) {
                    if (selectedCommunity.isNotEmpty()) {
                        if (blocksPopupWindow != null) {

                            if (blocksPopupWindow!!.isShowing) {
                                blocksPopupWindow!!.dismiss()
                            } else {
                                showBlocksDropDown()
                            }
                        } else {
                            showBlocksDropDown()
                        }

                    } else {
                        Utils.showToast(this, this.getString(R.string.select_community))
                    }

                } else {
                    Utils.showToast(this, this.getString(R.string.select_city))
                }
            } else {
                Utils.showToast(this, this.getString(R.string.select_state))
            }
            removeOtherthanBlock()
        }

        binding.selectFlatCl.setOnClickListener{
            flatClicking()
        }

        binding.submitBtn.setOnClickListener {

            if (doSubmitValidation()) {
                updateUserDetailsServiceCall()
            }
        }
    }

    private fun removeOtherthanBlock() {
        if(statesPopupWindow != null) {
            if (statesPopupWindow!!.isShowing) {
                statesPopupWindow!!.dismiss()
            }
        }
        if(citiesPopupWindow != null) {
            if (citiesPopupWindow!!.isShowing) {
                citiesPopupWindow!!.dismiss()
            }
        }
        if(communitiesPopupWindow != null) {
            if (communitiesPopupWindow!!.isShowing) {
                communitiesPopupWindow!!.dismiss()
            }
        }
        if (flatPopupWindow != null) {
            if (flatPopupWindow!!.isShowing) {
                flatPopupWindow!!.dismiss()
            }
        }
    }

    private fun removeOtherthanCommunity() {
        if(statesPopupWindow != null) {
            if (statesPopupWindow!!.isShowing) {
                statesPopupWindow!!.dismiss()
            }
        }
        if(citiesPopupWindow != null) {
            if (citiesPopupWindow!!.isShowing) {
                citiesPopupWindow!!.dismiss()
            }
        }
        if(blocksPopupWindow != null) {
            if (blocksPopupWindow!!.isShowing) {
                blocksPopupWindow!!.dismiss()
            }
        }
        if (flatPopupWindow != null) {
            if (flatPopupWindow!!.isShowing) {
                flatPopupWindow!!.dismiss()
            }
        }
    }

    private fun removeOtherthanCity() {
        if (statesPopupWindow != null) {
            if (statesPopupWindow!!.isShowing) {
                statesPopupWindow!!.dismiss()
            }
        }
        if(communitiesPopupWindow != null) {
            if (communitiesPopupWindow!!.isShowing) {
                communitiesPopupWindow!!.dismiss()
            }
        }
        if(blocksPopupWindow != null) {
            if (blocksPopupWindow!!.isShowing) {
                blocksPopupWindow!!.dismiss()
            }
        }
        if (flatPopupWindow != null) {
            if (flatPopupWindow!!.isShowing) {
                flatPopupWindow!!.dismiss()
            }
        }
    }

    private fun removeOtherthanState() {
        if(citiesPopupWindow!= null) {
            if (citiesPopupWindow!!.isShowing) {
                citiesPopupWindow!!.dismiss()
            }
        }
        if(communitiesPopupWindow != null) {
            if (communitiesPopupWindow!!.isShowing) {
                communitiesPopupWindow!!.dismiss()
            }
        }
        if(blocksPopupWindow != null) {
            if (blocksPopupWindow!!.isShowing) {
                blocksPopupWindow!!.dismiss()
            }
        }
        if (flatPopupWindow != null) {
            if (flatPopupWindow!!.isShowing) {
                flatPopupWindow!!.dismiss()
            }
        }
    }

    private fun navigationToSignInScreen() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateUserDetailsServiceCall() {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))
        // here create signup input object
        val jsonObject = JsonObject()
        with(jsonObject) {
            addProperty("CommunityId", CommunityId.toInt())
            addProperty("blockId", selectedBlockid.toInt())
            addProperty("flatId", selectedFlot.toInt())
            addProperty("userId", User_Id.toInt())
        }

        updateUserCall = apiInterface.updateUserDetails(jsonObject)
        updateUserCall.enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                customProgressDialog.progressDialogDismiss()

                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data

                    if (response.body()!!.statusCode != null) {

                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@PersonalInfoActivity,
                                        response.body()!!.message
                                    )
                                    navigationToSignInScreen()
                                }
                            }

                            2 -> {
                                // invalid mobile number
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@PersonalInfoActivity,
                                        response.body()!!.message
                                    )
                                  //  navigationToSignInScreen()
                                }
                            }

                            3 -> {
                                //something went wrong
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@PersonalInfoActivity,
                                        response.body()!!.message
                                    )
                                //    navigationToSignInScreen()
                                }
                            }

                            else -> {
                                // here nothing
                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@PersonalInfoActivity, t.message.toString())
            }
        })
    }

    private fun doSubmitValidation(): Boolean {
        if (selectedState.isEmpty()) {
            Utils.showToast(this, this.getString(R.string.select_state))
            return false
        }
        if (selectedCity.isEmpty()) {
            Utils.showToast(this, this.getString(R.string.select_city))
            return false
        }
        if (selectedCommunity.isEmpty()) {
            Utils.showToast(this, this.getString(R.string.select_community))
            return false
        }
        if (selectedBlockid.isEmpty()) {
            Utils.showToast(this, this.getString(R.string.select_block))
            return false
        }
        if (selectedFlot.isEmpty()) {
            Utils.showToast(this, this.getString(R.string.select_flot))
            return false
        }
        if (!isAgree) {
            Utils.showToast(this, this.getString(R.string.agree_terms_and_conditions))
            return false
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getDataFromServer() {
        getCommunityStatesServiceCall()
    }

    private fun showStatesDropDown() {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        statesPopupWindow = PopupWindow(
            view,
            binding.selectStateTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (statesList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val communityStatesAdapter = CommunityStatesAdapter(this, statesList)

            dropDownRecyclerView.adapter = communityStatesAdapter
            communityStatesAdapter.setCallback(this@PersonalInfoActivity)
        }
        statesPopupWindow!!.elevation = 10F
        statesPopupWindow!!.showAsDropDown(binding.selectStateTxt, 0, 0, Gravity.CENTER)
    }

    private fun showCitiesDropDown() {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        citiesPopupWindow = PopupWindow(
            view,
            binding.selectCityTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (citiesList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val communityCitiesAdapter = CommunityCitiesAdapter(this, citiesList)

            dropDownRecyclerView.adapter = communityCitiesAdapter
            communityCitiesAdapter.setCallback(this@PersonalInfoActivity)
        }
        citiesPopupWindow!!.elevation = 10F
        citiesPopupWindow!!.showAsDropDown(binding.selectCityTxt, 0, 0, Gravity.CENTER)

    }

    private fun showCommunitiesDropDown() {

        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        communitiesPopupWindow = PopupWindow(
            view,
            binding.selectCommunityTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (communitiesList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val allCommunitiesAdapter = AllCommunitiesAdapter(this, communitiesList)

            dropDownRecyclerView.adapter = allCommunitiesAdapter
            allCommunitiesAdapter.setCallback(this@PersonalInfoActivity)
        }
        communitiesPopupWindow!!.elevation = 10F
        communitiesPopupWindow!!.showAsDropDown(binding.selectCommunityTxt, 0, 0, Gravity.CENTER)
    }

    private fun showBlocksDropDown() {

        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        blocksPopupWindow = PopupWindow(
            view,
            binding.selectBlockTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (blocksList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val blocksAdapter = BlocksAdapter(this, blocksList)

            dropDownRecyclerView.adapter = blocksAdapter
            blocksAdapter.setCallback(this@PersonalInfoActivity)
        }
        blocksPopupWindow!!.elevation = 10F
        blocksPopupWindow!!.showAsDropDown(binding.selectBlockTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getBlocksServiceCall(clientId: Int) {
        //progressbar
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        blocksCall = apiInterface.getBlocks(clientId)
        blocksCall.enqueue(object : Callback<Blocks> {
            override fun onResponse(
                call: Call<Blocks>,
                response: Response<Blocks>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.data != null &&
                        response.body()!!.message != null && response.body()!!.message.isNotEmpty()
                    ) {
                        // here save states list
                        if (blocksList.isNotEmpty()) {
                            blocksList.clear()
                        }
                        blocksList = response.body()!!.data as ArrayList<BlocksData>
                        Log.e("BlocksData", ""+blocksList);
                    }
                }
            }

            override fun onFailure(call: Call<Blocks>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@PersonalInfoActivity, t.message.toString())
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllCommunitiesServiceCall(s: Int) {
        //progressbar
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        // here sign up service call
        allCommunitiesCall = apiInterface.getAllCommunities(s)
        allCommunitiesCall.enqueue(object : Callback<AllCommunities> {
            override fun onResponse(
                call: Call<AllCommunities>,
                response: Response<AllCommunities>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.data != null &&
                        response.body()!!.data != null && response.body()!!.data.isNotEmpty()
                    ) {
                        // here save states list
                        if (communitiesList.isNotEmpty()) {
                            communitiesList.clear()
                        }
                        communitiesList = response.body()!!.data as ArrayList<CommunityDetails>
                    }
                }
            }

            override fun onFailure(call: Call<AllCommunities>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@PersonalInfoActivity, t.message.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getCommunityCitiesServiceCall(s: Int) {
        //progressbar
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        // here sign up service call
        communityCitiesCall = apiInterface.getCommunityCities(s)
        communityCitiesCall.enqueue(object : Callback<CommunityCities> {
            override fun onResponse(
                call: Call<CommunityCities>,
                response: Response<CommunityCities>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.data != null &&
                        response.body()!!.message != null && response.body()!!.message.isNotEmpty()
                    ) {
                        // here save states list
                        if (citiesList.isNotEmpty()) {
                            citiesList.clear()
                        }
                        citiesList = response.body()!!.data as ArrayList<CitiesData>
                    }
                }
            }

            override fun onFailure(call: Call<CommunityCities>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@PersonalInfoActivity, t.message.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getCommunityStatesServiceCall() {
        //progressbar
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        // here sign up service call
        communityStatesCall = apiInterface.getCommunityStates()
        communityStatesCall.enqueue(object : Callback<CommunityStates> {
            override fun onResponse(
                call: Call<CommunityStates>,
                response: Response<CommunityStates>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.data != null &&
                        response.body()!!.message != null && response.body()!!.message.isNotEmpty()
                    ) {
                        // here save states list
                        if (statesList.isNotEmpty()) {
                            statesList.clear()
                        }
                        statesList = response.body()!!.data as ArrayList<State>
                    }
                }
            }

            override fun onFailure(call: Call<CommunityStates>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@PersonalInfoActivity, t.message.toString())
            }
        })
    }

    private fun setupApiService() {
        // here setup retrofit service
        apiInterface = APIClient.getClient(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectState(s: State) {
        if (statesPopupWindow != null) {
            if (statesPopupWindow!!.isShowing) {
                statesPopupWindow!!.dismiss()
            }
        }
        if (s != null) {
            selectedState = s.stateName
            binding.selectStateTxt.text = s.stateName
            // here city api call
            getCommunityCitiesServiceCall(s.stateId)
        }

        clearBelowStateSelectedDetails()
    }

    private fun clearBelowStateSelectedDetails() {
        if (citiesPopupWindow != null) {
            if (citiesPopupWindow!!.isShowing) {
                citiesPopupWindow!!.dismiss()
            }
        }
        if (communitiesPopupWindow != null) {
            if (communitiesPopupWindow!!.isShowing) {
                communitiesPopupWindow!!.dismiss()
            }
        }
        if (blocksPopupWindow != null) {
            if (blocksPopupWindow!!.isShowing) {
                blocksPopupWindow!!.dismiss()
            }
        }
        binding.selectCityTxt.text = ""
        binding.selectCommunityTxt.text = ""
        binding.selectBlockTxt.text = ""

        binding.selectCityTxt.text = getString(R.string.select_city)
        binding.selectCommunityTxt.text = getString(R.string.select_community)
        binding.selectBlockTxt.text = getString(R.string.select_block)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectCity(s: CitiesData) {
        if (citiesPopupWindow != null) {
            if (citiesPopupWindow!!.isShowing) {
                citiesPopupWindow!!.dismiss()
            }
        }
        if (s != null) {
            selectedCity = s.cityName
            binding.selectCityTxt.text = s.cityName
            // here communities api call
            getAllCommunitiesServiceCall(s.cityId)
        }
        clearBelowCitySelectedData()
    }

    private fun clearBelowCitySelectedData() {
        if (communitiesPopupWindow != null) {
            if (communitiesPopupWindow!!.isShowing) {
                communitiesPopupWindow!!.dismiss()
            }
        }
        if (blocksPopupWindow != null) {
            if (blocksPopupWindow!!.isShowing) {
                blocksPopupWindow!!.dismiss()
            }
        }

        binding.selectCommunityTxt.text = ""
        binding.selectBlockTxt.text = ""
        binding.selectCommunityTxt.text = getString(R.string.select_community)
        binding.selectBlockTxt.text = getString(R.string.select_block)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectCommunity(communityDetails: CommunityDetails) {
        if (communitiesPopupWindow != null) {
            if (communitiesPopupWindow!!.isShowing) {
                communitiesPopupWindow!!.dismiss()
            }
        }
        if (communityDetails.communityName.isNotEmpty()) {
            selectedCommunity = communityDetails.communityId.toString()
            binding.selectCommunityTxt.text = communityDetails.communityName

            // her block id saving
            if (communityDetails.communityId != null) {
                CommunityId = communityDetails.communityId
                // here blocks api call
                getBlocksServiceCall(CommunityId)
            }
        }

        clearCommunityBelowSelectedData()
    }

    private fun clearCommunityBelowSelectedData() {
        if (blocksPopupWindow != null) {
            if (blocksPopupWindow!!.isShowing) {
                blocksPopupWindow!!.dismiss()
            }
        }

        binding.selectBlockTxt.text = ""
        binding.selectBlockTxt.text = getString(R.string.select_block)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectBlock(block: BlocksData) {
        if (blocksPopupWindow != null) {
            if (blocksPopupWindow!!.isShowing) {
                blocksPopupWindow!!.dismiss()
            }
        }
        if (block.block.isNotEmpty()) {
            selectedBlockid = block.blockId.toString()
            selectedBlock = block.block

            binding.selectBlockTxt.text = block.block

            if(selectedBlockid.isNotEmpty()){
                getFlatServiceCall(selectedBlockid.toInt())
            }
        }
        clearSelectedBelowBlock()
    }

    private fun clearSelectedBelowBlock() {
        binding.selectFlatTxt.text = ""
        binding.selectFlatTxt.text = getString(R.string.select_flot)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getFlatServiceCall(BlockId: Int) {
        //progressbar
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        flatCall = apiInterface.getFlats(selectedCommunity.toInt(), BlockId)
        flatCall.enqueue(object : Callback<Flats> {
            override fun onResponse(
                call: Call<Flats>,
                response: Response<Flats>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.data != null &&
                        response.body()!!.message != null && response.body()!!.message.isNotEmpty()
                    ) {
                        // here save states list
                        if (flatsList.isNotEmpty()) {
                            flatsList.clear()
                        }
                        flatsList = response.body()!!.data as ArrayList<Flat>
                    }
                }
            }

            override fun onFailure(call: Call<Flats>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@PersonalInfoActivity, t.message.toString())
            }
        })

    }

    private fun flatClicking() {
        if (selectedState.isNotEmpty()) {
            if (selectedCity.isNotEmpty()) {
                if (selectedCommunity.isNotEmpty()) {
                    if(selectedBlock.isNotEmpty()) {
                        if (flatPopupWindow != null) {
                            if (flatPopupWindow!!.isShowing) {
                                flatPopupWindow!!.dismiss()
                            } else {
                                showFlatDropDown()
                            }
                        } else {
                            showFlatDropDown()
                        }
                    }else{
                        Utils.showToast(this, this.getString(R.string.select_block))
                    }
                } else {
                    Utils.showToast(this, this.getString(R.string.select_community))
                }

            } else {
                Utils.showToast(this, this.getString(R.string.select_city))
            }
        } else {
            Utils.showToast(this, this.getString(R.string.select_state))
        }
    }

    private fun showFlatDropDown() {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        flatPopupWindow = PopupWindow(
            view,
            binding.selectFlatTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (flatsList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val flatAdapter = FlatAdapter(this, flatsList)

            dropDownRecyclerView.adapter = flatAdapter
            flatAdapter.setFlatCallback(this@PersonalInfoActivity)
        }
        flatPopupWindow!!.elevation = 10F
        flatPopupWindow!!.showAsDropDown(binding.selectFlatTxt, 0, 0, Gravity.CENTER)
    }

    fun selecttFlat(flat: Flat) {
        if (flatPopupWindow != null) {
            if (flatPopupWindow!!.isShowing) {
                flatPopupWindow!!.dismiss()
            }
        }

        if (flat.flatId>0) {
            selectedFlot = flat.flatId.toString()
            binding.selectFlatTxt.text = flat.flatNo.toString()
        }
    }
}