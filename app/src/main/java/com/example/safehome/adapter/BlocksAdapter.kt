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
import com.example.safehome.model.Block
import com.example.safehome.model.BlocksData
import com.example.safehome.residentview.FlatActivity

class BlocksAdapter(var context: Context, private var blockList: ArrayList<BlocksData>) :
    RecyclerView.Adapter<BlocksAdapter.MyViewHolder>() {

    private lateinit var block: BlocksData
    private lateinit var personalInfoActivity: PersonalInfoActivity
    private lateinit var flatActivity: FlatActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.blocks_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        block = blockList[position]
        holder.blockNameTv.tag = block
        if (block.block.isNotEmpty()) {
            holder.blockNameTv.text = block.block
        }

        holder.blockNameTv.setOnClickListener {
            if (context is PersonalInfoActivity) {
                personalInfoActivity.selectBlock(it.tag as BlocksData)
            } else {
                flatActivity.selectBlock(it.tag as BlocksData)
            }
        }
    }

    override fun getItemCount(): Int {
        if (blockList.isNotEmpty()) {
            return blockList.size
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
        val blockNameTv: TextView = itemView.findViewById(R.id.block_tv)
    }
}