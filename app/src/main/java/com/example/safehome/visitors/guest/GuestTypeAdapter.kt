package com.example.safehome.visitors.guest

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.visitors.cab.CabAllowFrequentlyFragment
import com.example.safehome.visitors.cab.CabAllowOnceFragment

class GuestTypeAdapter(var context: Context, private var guestTypeList: List<String>) :
    RecyclerView.Adapter<GuestTypeAdapter.MyViewHolder>() {
    private var allowOnceFragment: AllowOnceFragment? = null
    private var allowFrequentlyFragment: AllowFrequentlyFragment? = null
    private var allowFrequentlyFragment1: AllowFrequentlyFragment? = null
    private var cabAllowOnceFragment : CabAllowOnceFragment? = null
    private var cabAllowFrequentlyFragment : CabAllowFrequentlyFragment? = null
    private var cabAllowFrequentlyFragment1 : CabAllowFrequentlyFragment? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.guest_type_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val guestType: String = guestTypeList[position]
        holder.ageGroupTv.tag = guestType
        if (guestType.isNotEmpty()) {
            holder.ageGroupTv.text = guestType
        }

        holder.ageGroupTv.setOnClickListener {

                if (allowOnceFragment != null) {

                    allowOnceFragment?.setGuestType(guestTypeList[position])
                }
                if (allowFrequentlyFragment != null) {

                    allowFrequentlyFragment?.setGuestType(guestTypeList[position])
                }
                if (allowFrequentlyFragment1 != null) {

                    allowFrequentlyFragment1?.setSchedule(guestTypeList[position])
                }
                if (cabAllowOnceFragment != null) {

                    cabAllowOnceFragment?.setServiceProvider(guestTypeList[position])
                }
                if (cabAllowFrequentlyFragment != null) {

                    cabAllowFrequentlyFragment?.setGuestType(guestTypeList[position])
                }
                if (cabAllowFrequentlyFragment1 != null) {

                    cabAllowFrequentlyFragment1?.setSchedule(guestTypeList[position])
                }


        }
    }

    override fun getItemCount(): Int {
        if (guestTypeList.isNotEmpty()) {
            return guestTypeList.size
        }
        return 0
    }


    fun setCallbackServiceType(allowOnceFragment: AllowOnceFragment) {
        this.allowOnceFragment = allowOnceFragment
    }

    fun setCallbackServiceType(allowFrequentlyFragment: AllowFrequentlyFragment) {
        this.allowFrequentlyFragment = allowFrequentlyFragment
    }

    fun setCallbackSchedule(allowFrequentlyFragment1: AllowFrequentlyFragment) {
        this.allowFrequentlyFragment1 = allowFrequentlyFragment1
    }
    fun setCallbackServiceProvider(cabAllowOnceFragment: CabAllowOnceFragment) {
        this.cabAllowOnceFragment = cabAllowOnceFragment
    }
    fun setCallbackDate(cabAllowFrequentlyFragment: CabAllowFrequentlyFragment) {
        this.cabAllowFrequentlyFragment1 = cabAllowFrequentlyFragment
    }

    fun setCallbackServiceProvider1(cabAllowFrequentlyFragment: CabAllowFrequentlyFragment) {
        this.cabAllowFrequentlyFragment = cabAllowFrequentlyFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}