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
import com.example.safehome.visitors.ScheduleModel
import com.example.safehome.visitors.ServiceProviderModel
import com.example.safehome.visitors.cab.CabAllowFrequentlyFragment
import com.example.safehome.visitors.cab.CabAllowOnceFragment
import com.example.safehome.visitors.delivery.DeliveryAllowFrequentlyFragment
import com.example.safehome.visitors.delivery.DeliveryAllowOnceFragment
import com.example.safehome.visitors.others.OthersAllowFrequentlyFragment
import com.example.safehome.visitors.staff.StaffAllowFrequentlyFragment
import com.example.safehome.visitors.staff.StaffAllowOnceFragment

class GuestScheduleAdapter(var context: Context, private var guestTypeList: List<ScheduleModel.Data>) :
    RecyclerView.Adapter<GuestScheduleAdapter.MyViewHolder>() {
    private var allowFrequentlyFragment1: AllowFrequentlyFragment? = null
    private var cabAllowFrequentlyFragment1 : CabAllowFrequentlyFragment? = null
    private var deliveryAllowFrequentlyFragment1 : DeliveryAllowFrequentlyFragment? = null
    private var staffAllowFrequentlyFragment1 : StaffAllowFrequentlyFragment? = null
    private var othersAllowFrequentlyFragment : OthersAllowFrequentlyFragment? = null

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
        val guestType = guestTypeList[position]
        holder.ageGroupTv.tag = guestType.name
        if (guestType.name.isNotEmpty()) {
            holder.ageGroupTv.text = guestType.name
        }

        holder.ageGroupTv.setOnClickListener {


                if (allowFrequentlyFragment1 != null) {

                    allowFrequentlyFragment1?.setSchedule(guestType)
                }


                if (cabAllowFrequentlyFragment1 != null) {

                    cabAllowFrequentlyFragment1?.setSchedule(guestType)
                }

                if (deliveryAllowFrequentlyFragment1 != null) {

                    deliveryAllowFrequentlyFragment1?.setSchedule(guestType)
                }

                if (staffAllowFrequentlyFragment1 != null) {

                    staffAllowFrequentlyFragment1?.setSchedule(guestType)
                }
                if (othersAllowFrequentlyFragment != null) {

                    othersAllowFrequentlyFragment?.setSchedule(guestType)
                }


        }
    }

    override fun getItemCount(): Int {
        if (guestTypeList.isNotEmpty()) {
            return guestTypeList.size
        }
        return 0
    }

    fun setCallbackSchedule(allowFrequentlyFragment1: AllowFrequentlyFragment) {
        this.allowFrequentlyFragment1 = allowFrequentlyFragment1
    }

    fun setCallbackDate(cabAllowFrequentlyFragment: CabAllowFrequentlyFragment) {
        this.cabAllowFrequentlyFragment1 = cabAllowFrequentlyFragment
    }


    fun setCallbackDeliveryDate(deliveryAllowFrequentlyFragment1: DeliveryAllowFrequentlyFragment) {
        this.deliveryAllowFrequentlyFragment1 = deliveryAllowFrequentlyFragment1
    }


    fun setCallbackStaffDate(staffAllowFrequentlyFragment1: StaffAllowFrequentlyFragment) {
        this.staffAllowFrequentlyFragment1 = staffAllowFrequentlyFragment1
    }

    fun setCallbackOthersDate(othersAllowFrequentlyFragment: OthersAllowFrequentlyFragment) {
        this.othersAllowFrequentlyFragment = othersAllowFrequentlyFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}