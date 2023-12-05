package com.example.safehome.visitors.staff

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.visitors.guest.selectguest.ContactsFragment

class StaffServiceBookedAdapter(
    private val context: Context,
    private val staffServiceBookedDropdownList: ArrayList<StaffServiceBookedDropDownModel.Data>,
    private val fragmentType: String
): RecyclerView.Adapter<StaffServiceBookedAdapter.MyViewHolder>() {
     private lateinit var contactsFragment: ContactsFragment
     private lateinit var staffAllowOnceFragment: StaffAllowOnceFragment
     private lateinit var staffAllowFrequentlyFragment: StaffAllowFrequentlyFragment
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.contact_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = staffServiceBookedDropdownList[position]

        if (contact.staffName.isNotEmpty()) {
            holder.contactName.text = contact.staffName
        }

        if (contact.mobileNo.isNotEmpty()) {
            holder.contactNumber.text = contact.mobileNo
        }

        if (contact.isSelected) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.selected_color))
            holder.tickImageView.visibility = View.VISIBLE

        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.contact_bg))
            holder.tickImageView.visibility = View.INVISIBLE
        }

        /*        holder.itemView.setOnClickListener {
        //            contactsFragment.setContact(contact)

                    val position = getAdapterPos
                    val item = contactsList[position]
                    item.isSelected = !item.isSelected
                    notifyItemChanged(position)

                }*/
    }

    override fun getItemCount(): Int {
        if (staffServiceBookedDropdownList.isNotEmpty()) {
            return staffServiceBookedDropdownList.size
        }
        return 0
    }

    fun setCallbackServiceType(contactsFragment: ContactsFragment) {
        this.contactsFragment = contactsFragment
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.tv_contact_name)
        val contactNumber: TextView = itemView.findViewById(R.id.tv_contact_number)
        val tickImageView: ImageView = itemView.findViewById(R.id.tickImageView)

        init {
            itemView.setOnClickListener {

                val position = adapterPosition // Get the position of the clicked item
                val item = staffServiceBookedDropdownList[position] // Access the data item at this position

                // Toggle the selection state of the item
                item.isSelected = !item.isSelected
                notifyItemChanged(position) // Notify the adapter of the change
                if (fragmentType == "staffFrequentlyFragment"){
                    staffAllowFrequentlyFragment.setContact(item)
                }else {
                    staffAllowOnceFragment.setContact(item)
                }
            }
        }

    }


    fun setStaffAllowOnceCallBAck(staffAllowOnceFragment: StaffAllowOnceFragment) {
     this.staffAllowOnceFragment = staffAllowOnceFragment
    }

    fun setStaffAllowFrequentlyCallBAck(staffAllowFrequentlyFragment: StaffAllowFrequentlyFragment) {
     this.staffAllowFrequentlyFragment = staffAllowFrequentlyFragment
    }


}