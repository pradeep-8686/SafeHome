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
import com.example.safehome.model.State
import com.example.safehome.model.StateName
import com.example.safehome.residentview.FlatActivity

class CommunityStatesAdapter(var context: Context, private var statesList: List<State>) :
    RecyclerView.Adapter<CommunityStatesAdapter.MyViewHolder>() {

    private lateinit var personalInfoActivity: PersonalInfoActivity
    private lateinit var flatActivity: FlatActivity
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.community_states_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       val stateName:State = statesList[position]
        holder.statesNameTv.tag = stateName
       // if (stateName.isNotEmpty()) {
            holder.statesNameTv.text = stateName.stateName
       // }

        holder.statesNameTv.setOnClickListener {
            if(context is PersonalInfoActivity){
                personalInfoActivity.selectState(it.tag as State)
            }else{
                flatActivity.selectState(it.tag as State)
            }
        }
    }

    override fun getItemCount(): Int {
        if (statesList.isNotEmpty()) {
           return statesList.size
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
        val statesNameTv: TextView = itemView.findViewById(R.id.state_tv)
    }

}