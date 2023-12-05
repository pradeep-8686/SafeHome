package com.example.safehome.visitors

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.databinding.FragmentCurrentVisitorsLayoutBinding
import com.example.safehome.visitors.cab.CabActivity
import com.example.safehome.visitors.delivery.DeliveryActivity
import com.example.safehome.visitors.guest.GuestActivity
import com.example.safehome.visitors.others.OthersActivity
import com.example.safehome.visitors.staff.StaffActivity
import java.util.Locale


class CurrentVisitorsListFragment(private val approvalStatus : ArrayList<ApprovalStatusModel.Data>?= null): Fragment() {
    private lateinit var visitorsListAdapter: VisitorsListAdapter
    private lateinit var binding: FragmentCurrentVisitorsLayoutBinding
    private var currentVisitorsList:ArrayList<GetAllVisitorDetailsModel.Data.Event> = ArrayList()
    private var visitorDeleteDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentVisitorsLayoutBinding.inflate(inflater, container, false)
        inIt()
        clickEvents()
        return  binding.root
    }

    private fun clickEvents() {
        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (currentVisitorsList.isNotEmpty()){

                    filter(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun inIt() {
        binding.yearLayout.visibility = View.GONE
        binding.searchLayout.visibility = View.VISIBLE
        binding.visitorFiltersLayout.visibility = View.GONE
        val bundle = this.arguments
        if (bundle != null) {
          currentVisitorsList = bundle.getSerializable("visitorsList") as ArrayList<GetAllVisitorDetailsModel.Data.Event>
        }
        populateData(currentVisitorsList)
    }

    private fun populateData(currentVisitorsList: ArrayList<GetAllVisitorDetailsModel.Data.Event>) {


        Log.e("currentVisitorsList", ""+currentVisitorsList.size)
        if (currentVisitorsList.isEmpty()) {
            binding.emptyVisitorsTxt.visibility = View.VISIBLE
        } else {

            binding.emptyVisitorsTxt.visibility = View.GONE
            binding.visitorsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            visitorsListAdapter =
                VisitorsListAdapter(requireContext(), currentVisitorsList, "CurrentVisitors")
            binding.visitorsRecyclerview.adapter = visitorsListAdapter
            visitorsListAdapter.setCurrentVisitorsCallback(this@CurrentVisitorsListFragment)
            visitorsListAdapter.notifyDataSetChanged()
        }
    }
    private fun filter(text: String) {
        val filteredList = ArrayList<GetAllVisitorDetailsModel.Data.Event>()
        val visitorsList: ArrayList<GetAllVisitorDetailsModel.Data.Event> = currentVisitorsList

        for (eachCourse in visitorsList) {

            if (
                !eachCourse.visitorTypeName.isNullOrBlank() && eachCourse.visitorTypeName.lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.seviceProviderName.toString().isNullOrBlank() && eachCourse.seviceProviderName.toString().lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.name.toString().isNullOrBlank() && eachCourse.name.toString().lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.invitedBy.toString().isNullOrBlank() && eachCourse.invitedBy.toString().lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.allowFor.toString().isNullOrBlank() && eachCourse.allowFor.toString().lowercase(
                    Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))

            ) {
                filteredList.add(eachCourse)
            }
        }

        visitorsListAdapter.filteredList(filteredList)
    }

    fun deleteVisitorItem(visitorListItem: GetAllVisitorDetailsModel.Data.Event) {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_tenant_dialog_popup, null)
        visitorDeleteDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        visitorDeleteDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        visitorDeleteDialog!!.setContentView(view)
        visitorDeleteDialog!!.setCanceledOnTouchOutside(true)
        visitorDeleteDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(visitorDeleteDialog!!.window!!.attributes)


        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)
        val member_mob_number: TextView = view.findViewById(R.id.member_mob_number)
        member_mob_number.text = "Are you sure you want to delete this ${visitorListItem.visitorTypeName} Details?"

        close.setOnClickListener {
            if (visitorDeleteDialog!!.isShowing) {
                visitorDeleteDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            //    deletePollServiceCall(forumItem)
            if (visitorDeleteDialog!!.isShowing) {
                visitorDeleteDialog!!.dismiss()
            }
        }

        no.setOnClickListener {
            if (visitorDeleteDialog!!.isShowing) {
                visitorDeleteDialog!!.dismiss()
            }
        }
        visitorDeleteDialog!!.show()
    }

    fun editVisitorItem(visitorListItem: GetAllVisitorDetailsModel.Data.Event) {
        var intent: Intent? = null
        intent = when (visitorListItem.visitorTypeName) {
            "Cab" -> {
                Intent(requireContext(), CabActivity::class.java)
            }
            "Delivery" -> {
                Intent(requireContext(), DeliveryActivity::class.java)
            }
            "Guest" -> {
                Intent(requireContext(), GuestActivity::class.java)
            }
            "Staff" -> {
                Intent(requireContext(), StaffActivity::class.java)
            }
            else -> {
                Intent(requireContext(), OthersActivity::class.java)
            }
        }
        intent.putExtra("approvalStatus", approvalStatus)
        intent?.putExtra("visitorListItem", visitorListItem)
        intent?.putExtra("VisitorTypeId", visitorListItem.visitorTypeId)
        intent?.let { startActivity(it) }
    }

}