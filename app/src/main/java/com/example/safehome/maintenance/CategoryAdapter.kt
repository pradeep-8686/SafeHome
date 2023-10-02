package com.example.safehome.maintenance

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.CategoryModel

class CategoryAdapter(var context: Context, private var categoryList: List<CategoryModel.Data>) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
    private lateinit var historyFragment: HistoryFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.age_group_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stateName: String = categoryList[position].categoryName
        holder.ageGroupTv.tag = stateName
        if (stateName.isNotEmpty()) {
            holder.ageGroupTv.text = stateName
        }

        holder.ageGroupTv.setOnClickListener {
            historyFragment.setCategory(categoryList[position])

        }
    }

    override fun getItemCount(): Int {
        if (categoryList.isNotEmpty()) {
            return categoryList.size
        }
        return 0
    }


    fun setCallbackServiceType(historyFragment : HistoryFragment) {
        this.historyFragment = historyFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}