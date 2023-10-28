package com.example.safehome.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.AvailabilityTime

class AssignToAdapter(private val context: Context, private val availabilityTimes: ArrayList<AvailabilityTime>,
                      private var sSelectedItems: String) :
    RecyclerView.Adapter<AssignToAdapter.ViewHolder>() {

    private val selectedItems = HashSet<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assign_to, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val availabilityTime = availabilityTimes[position]
        holder.textView.text = availabilityTime.Time


        if (sSelectedItems.isNotEmpty()){
            for (i in 0 until availabilityTimes.size) {
                val item: String = availabilityTimes[i].Time
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
        return availabilityTimes.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    // Function to get the selected items
    fun getSelectedItems(): List<String> {
        return selectedItems.map { availabilityTimes[it].Time }
    }
}