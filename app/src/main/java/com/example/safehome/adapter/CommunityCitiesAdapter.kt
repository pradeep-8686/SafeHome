package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.activity.PersonalInfoActivity
import com.example.safehome.model.CitiesData
import com.example.safehome.model.State
import com.example.safehome.residentview.FlatActivity

class CommunityCitiesAdapter(var context: Context, var citiesList: List<CitiesData>) :
    RecyclerView.Adapter<CommunityCitiesAdapter.MyViewHolder>() {

    private lateinit var personalInfoActivity: PersonalInfoActivity
    private lateinit var flatActivity: FlatActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.community_cities_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name: CitiesData = citiesList[position]
        holder.cityNameTv.tag = name

//        if (name.isNotEmpty()) {
            holder.cityNameTv.text = name.cityName
//        }

        holder.cityNameTv.setOnClickListener {
            if (context is PersonalInfoActivity) {
                personalInfoActivity.selectCity(it.tag as CitiesData)
            } else {
                flatActivity.selectCity(it.tag as CitiesData)
            }
        }
    }

    override fun getItemCount(): Int {
        if (citiesList.isNotEmpty()) {
            return citiesList.size
        }
        return 0
    }

    fun setCallback(personalInfoActivity: PersonalInfoActivity) {
        this.personalInfoActivity = personalInfoActivity
    }

    fun setCallbackFlat(flatActivity: FlatActivity) {
        this.flatActivity = flatActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityNameTv: TextView = itemView.findViewById(R.id.city_tv)
    }
}