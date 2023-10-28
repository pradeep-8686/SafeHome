package com.example.safehome.polls

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.PollsPostedDropDownModel

class PollsPostedToAdapter(private val context: Context, private val PollsAssignedToList: ArrayList<PollsPostedDropDownModel.Data>,
                           private var sSelectedItems: String) :
    RecyclerView.Adapter<PollsPostedToAdapter.ViewHolder>() {
    private val selectedItems = HashSet<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollsPostedToAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assign_to, parent, false)
        return PollsPostedToAdapter.ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: PollsPostedToAdapter.ViewHolder, position: Int) {
        val availabilityTime = PollsAssignedToList[position]
        holder.textView.text = availabilityTime.name


        if (sSelectedItems.isNotEmpty()){
            for (i in 0 until PollsAssignedToList.size) {
                val item: String = PollsAssignedToList[i].name
                if (sSelectedItems.contains(item)) {
                    selectedItems.add(i)
                }
            }
        }
        val selectedDrawable = ContextCompat.getDrawable(context, R.drawable.black_border_bg_8dp)
        val deSelectedDrawable = ContextCompat.getDrawable(context, R.drawable.deselect_border_bg_8dp)


        if (selectedItems.contains(position)) {
            holder.textView.background = deSelectedDrawable

            holder.textView.setTextColor(Color.WHITE)
        } else {
            holder.textView.background = selectedDrawable

            holder.textView.setTextColor(Color.BLACK)

        }

        holder.itemView.setOnClickListener {
            sSelectedItems = ""
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)
        }
    }
    override fun getItemCount(): Int {
        return PollsAssignedToList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)

    }
    fun getSelectedItems(): List<String> {
        return selectedItems.map { PollsAssignedToList[it].name }
    }
    fun getSelectedItemsId(): List<String> {
        return selectedItems.map { PollsAssignedToList[it].postedToId.toString() }
    }

}