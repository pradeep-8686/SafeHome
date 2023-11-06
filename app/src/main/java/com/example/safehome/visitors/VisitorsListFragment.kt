package com.example.safehome.visitors

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.databinding.FragmentVisitorsListBinding
import com.example.safehome.maintenance.HistoryFragment


class VisitorsListFragment : Fragment() {

    private var yearPopupWindow: PopupWindow?= null
    private lateinit var binding : FragmentVisitorsListBinding
    private var visitorsList:ArrayList<VisitorsListModel> = ArrayList()
    private var expectedVisitorsList:ArrayList<VisitorsListModel> = ArrayList()
    private var historyVisitorsList:ArrayList<VisitorsListModel> = ArrayList()
    private var visitorDeleteDialog: Dialog? = null
    private var yearList: ArrayList<String> = ArrayList()
    private lateinit var visitorsListAdapter: VisitorsListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVisitorsListBinding.inflate(inflater, container, false)
        addVisitorsList()
        populateData()
        addYearList()
        inIt()
        return binding.root
    }

    private fun inIt() {
        binding.yearLayout.visibility = View.GONE

        binding.visitorsCurrentBtn.setOnClickListener {
            binding.visitorsCurrentBtn.background = requireContext().getDrawable(R.drawable.rectangler_vrify_bg)
            binding.visitorHistoryBtn.setBackgroundResource(0)
            binding.visitorExpectedBtn.setBackgroundResource(0)
            binding.yearLayout.visibility = View.GONE
            addVisitorsList()
            populateData()
        }
        binding.visitorExpectedBtn.setOnClickListener {
            binding.visitorExpectedBtn.background =
                requireContext().getDrawable(R.drawable.rectangler_vrify_bg)
            binding.visitorHistoryBtn.setBackgroundResource(0)
            binding.visitorsCurrentBtn.setBackgroundResource(0)
            binding.yearLayout.visibility = View.GONE

            addExpectedVisitorsList()
            populateExpectedVisitorsData()
        }

       binding.visitorHistoryBtn.setOnClickListener {
           binding.visitorHistoryBtn.background =
               requireContext().getDrawable(R.drawable.rectangler_vrify_bg)
           binding.visitorExpectedBtn.setBackgroundResource(0)
           binding.visitorsCurrentBtn.setBackgroundResource(0)
           binding.yearLayout.visibility = View.VISIBLE
           addHistoryVisitorsList()
           populateHistoryVisitorsData()
       }

        binding.yearCl.setOnClickListener {
            /*if (visitorsPopupWindow != null) {
                if (visitorsPopupWindow!!.isShowing) {
                    visitorsPopupWindow!!.dismiss()
                }
            }*/
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
    }
    private fun addYearList() {
        yearList.add("2023")
        yearList.add("2022")

    }

    private fun yearDropDown() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        yearPopupWindow = PopupWindow(
            view,
            binding.yearTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (yearList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val yearAdapter = YearAdapter(requireContext(), yearList)

            dropDownRecyclerView.adapter = yearAdapter
            yearAdapter.setVisitorsHistoryCallback(this@VisitorsListFragment)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)    }

    fun selectVisitorYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if (year != null) {
            binding.yearTxt.text = year
        }
  /*      selectedYear = year

//            getPaidMaintenanaceDetailsByResident(selectedYear!!)

        Log.d(HistoryFragment.TAG, "$selectedYear!!")*/

    }

    private fun populateHistoryVisitorsData() {
        if (historyVisitorsList.size == 0) {
            binding.emptyVisitorsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyVisitorsTxt.visibility = View.GONE
            binding.visitorsListRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            visitorsListAdapter =
                VisitorsListAdapter(requireContext(), historyVisitorsList , "HistoryVisitors")
            binding.visitorsListRecyclerview.adapter = visitorsListAdapter
            visitorsListAdapter.setCallback(this@VisitorsListFragment)
            visitorsListAdapter.notifyDataSetChanged()
        }
    }

    private fun addHistoryVisitorsList() {
        historyVisitorsList.clear()
        historyVisitorsList.add(
            VisitorsListModel("Cab", "Ola", "TS 13 Ab 0001", "10 July 2023",
                "Allowed By Teja", "3:00 PM", "Once", "", R.drawable.visitor_cab)
        )
    }

    private fun populateExpectedVisitorsData() {
       if (expectedVisitorsList.size == 0) {
            binding.emptyVisitorsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyVisitorsTxt.visibility = View.GONE
            binding.visitorsListRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            visitorsListAdapter =
                VisitorsListAdapter(requireContext(), expectedVisitorsList , "ExpectedVisitors")
            binding.visitorsListRecyclerview.adapter = visitorsListAdapter
            visitorsListAdapter.setCallback(this@VisitorsListFragment)
            visitorsListAdapter.notifyDataSetChanged()
        }
        }



    private fun addExpectedVisitorsList() {
        expectedVisitorsList.clear()
        expectedVisitorsList.add(
            VisitorsListModel("Cab", "Ola", "TS 13 Ab 0001", "10 July 2023",
                "Allowed By Teja", "3:00 PM", "Once", "", R.drawable.visitor_cab)
        )
        expectedVisitorsList.add(VisitorsListModel("Delivery", "Swiggy", "", "10 July 2023",
            "Allowed By Teja", "10:30 PM", "Once", "", R.drawable.visitor_delivery))
    }

    private fun populateData() {
        if (visitorsList.size == 0) {
            binding.emptyVisitorsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyVisitorsTxt.visibility = View.GONE
            binding.visitorsListRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            visitorsListAdapter =
                VisitorsListAdapter(requireContext(), visitorsList, "CurrentVisitors")
            binding.visitorsListRecyclerview.adapter = visitorsListAdapter
            visitorsListAdapter.setCallback(this@VisitorsListFragment)
            visitorsListAdapter.notifyDataSetChanged()
        }
    }

    private fun addVisitorsList() {
        visitorsList.clear()
        visitorsList.add(
            VisitorsListModel("Cab", "Ola", "TS 13 Ab 0001", "10 July 2023",
        "Allowed By Teja", "3:00 PM", "Once", "", R.drawable.visitor_cab)
        )
        visitorsList.add(VisitorsListModel("Delivery", "Swiggy", "", "10 July 2023",
            "Allowed By Teja", "10:30 PM", "Once", "", R.drawable.visitor_delivery))

        visitorsList.add(VisitorsListModel("Guest", "", "", "10 July 2023-10 August 2023",
            "Allowed By Teja", "10:30 PM", "Frequently(Monthly)", "Ramesh", R.drawable.visitor_guest))

        visitorsList.add(VisitorsListModel("Carpenter", "", "", "10 July 2023-18 July 2023",
            "Allowed By Teja", "10:30 PM", "Frequently(Weekly)", "Srinivas", R.drawable.visitor_staff))

    }

    fun deleteVisitorItem(visitorListItem: VisitorsListModel) {
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
        member_mob_number.text = "Are you sure you want to delete this ${visitorListItem.entryTitle} Details?"

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


}