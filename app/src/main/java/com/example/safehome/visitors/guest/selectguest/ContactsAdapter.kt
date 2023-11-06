package com.example.safehome.visitors.guest.selectguest

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

class ContactsAdapter(var context: Context, private var contactsList: List<ContactsModel>) :
    RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {
    private lateinit var contactsFragment: ContactsFragment

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
        val contact = contactsList[position]

        if (contact.name.isNotEmpty()) {
            holder.contactName.text = contact.name
        }

        if (contact.number.isNotEmpty()) {
            holder.contactNumber.text = contact.number
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
        if (contactsList.isNotEmpty()) {
            return contactsList.size
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
                val item = contactsList[position] // Access the data item at this position

                // Toggle the selection state of the item
                item.isSelected = !item.isSelected
                notifyItemChanged(position) // Notify the adapter of the change
            }
        }

    }

    fun filterList(contactsList: List<ContactsModel>) {
        this.contactsList = contactsList;
        notifyDataSetChanged();
    }


}