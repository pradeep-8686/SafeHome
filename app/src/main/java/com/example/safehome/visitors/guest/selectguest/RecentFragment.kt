package com.example.safehome.visitors.guest.selectguest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.safehome.R
import com.example.safehome.visitors.ApprovalStatusModel


class RecentFragment(private val approvalStatus : ArrayList<ApprovalStatusModel.Data>?= null) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recent, container, false)
    }

}