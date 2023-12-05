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
import com.example.safehome.visitors.ServiceProviderModel
import com.example.safehome.visitors.cab.CabAllowFrequentlyFragment
import com.example.safehome.visitors.cab.CabAllowOnceFragment
import com.example.safehome.visitors.delivery.DeliveryAllowFrequentlyFragment
import com.example.safehome.visitors.delivery.DeliveryAllowOnceFragment
import com.example.safehome.visitors.others.OthersAllowFrequentlyFragment
import com.example.safehome.visitors.staff.StaffAllowFrequentlyFragment
import com.example.safehome.visitors.staff.StaffAllowOnceFragment

class GuestTypeAdapter(var context: Context, private var guestTypeList: List<ServiceProviderModel.Data>) :
    RecyclerView.Adapter<GuestTypeAdapter.MyViewHolder>() {
    private var allowOnceFragment: AllowOnceFragment? = null
    private var allowFrequentlyFragment: AllowFrequentlyFragment? = null
    private var cabAllowOnceFragment : CabAllowOnceFragment? = null
    private var cabAllowFrequentlyFragment : CabAllowFrequentlyFragment? = null
    private var deliveryAllowOnceFragment : DeliveryAllowOnceFragment? = null
    private var deliveryAllowFrequentlyFragment : DeliveryAllowFrequentlyFragment? = null
    private var staffAllowOnceFragment : StaffAllowOnceFragment? = null
    private var staffAllowFrequentlyFragment : StaffAllowFrequentlyFragment? = null

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

                if (allowOnceFragment != null) {

                    allowOnceFragment?.setGuestType(guestType)
                }
                if (allowFrequentlyFragment != null) {

                    allowFrequentlyFragment?.setGuestType(guestTypeList[position])
                }

                if (cabAllowOnceFragment != null) {

                    cabAllowOnceFragment?.setServiceProvider(guestTypeList[position])
                }
                if (cabAllowFrequentlyFragment != null) {

                    cabAllowFrequentlyFragment?.setGuestType(guestTypeList[position])
                }


                if (deliveryAllowOnceFragment != null) {

                    deliveryAllowOnceFragment?.setServiceProvider(guestTypeList[position])
                }
                if (deliveryAllowFrequentlyFragment != null) {

                    deliveryAllowFrequentlyFragment?.setServiceProvider(guestTypeList[position])
                }

                if (staffAllowOnceFragment != null) {

                    staffAllowOnceFragment?.setServiceProvider(guestTypeList[position])
                }

                if (staffAllowFrequentlyFragment != null) {

                    staffAllowFrequentlyFragment?.setServiceProvider(guestTypeList[position])
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


    fun setCallbackServiceProvider(cabAllowOnceFragment: CabAllowOnceFragment) {
        this.cabAllowOnceFragment = cabAllowOnceFragment
    }


    fun setCallbackServiceProvider1(cabAllowFrequentlyFragment: CabAllowFrequentlyFragment) {
        this.cabAllowFrequentlyFragment = cabAllowFrequentlyFragment
    }
    fun setCallbackDeliveryOnce(deliveryAllowOnceFragment: DeliveryAllowOnceFragment) {
        this.deliveryAllowOnceFragment = deliveryAllowOnceFragment
    }

    fun setCallbackDeliveryFrq(deliveryAllowFrequentlyFragment: DeliveryAllowFrequentlyFragment) {
        this.deliveryAllowFrequentlyFragment = deliveryAllowFrequentlyFragment
    }

    fun setCallbackStaffOnce(staffAllowOnceFragment: StaffAllowOnceFragment) {
        this.staffAllowOnceFragment = staffAllowOnceFragment
    }


    fun setCallbackStaffFrq(staffAllowFrequentlyFragment: StaffAllowFrequentlyFragment) {
        this.staffAllowFrequentlyFragment = staffAllowFrequentlyFragment
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}