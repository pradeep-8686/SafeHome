package com.example.safehome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.activity.PersonalInfoActivity
import com.example.safehome.model.Block
import com.example.safehome.model.Flat
import com.example.safehome.model.Flats
import com.example.safehome.residentview.FlatActivity

class FlatAdapter(var context: Context, private var flatsList: ArrayList<Flat>) :
    RecyclerView.Adapter<FlatAdapter.MyViewHolder>() {

    private lateinit var flat: Flat
    private lateinit var personalInfoActivity: PersonalInfoActivity
    private lateinit var flatActivity: FlatActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.flats_item_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        flat = flatsList[position]
        holder.flatNameTv.tag = flat
        if (flat.flatNo.isNotEmpty()) {
            holder.flatNameTv.text = flat.flatNo
        }

        holder.flatNameTv.setOnClickListener {
            if (context is PersonalInfoActivity) {
                personalInfoActivity.selecttFlat(it.tag as Flat)
            } else {
                flatActivity.selectedFlat(it.tag as Flat)
            }
        }
    }

    override fun getItemCount(): Int {
        if (flatsList.isNotEmpty()) {
            return flatsList.size
        }
        return 0
    }

    fun setFlatCallback(personalInfoActivity: PersonalInfoActivity) {
        this.personalInfoActivity = personalInfoActivity
    }

    fun setCallbackFlat(flatActivity: FlatActivity) {
        this.flatActivity = flatActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flatNameTv: TextView = itemView.findViewById(R.id.flat_tv)
    }
}