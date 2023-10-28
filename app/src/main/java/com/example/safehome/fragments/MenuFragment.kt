package com.example.safehome.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.complaints.ComplaintsActivity
import com.example.safehome.dailyhelp.DailyHelpActivity
import com.example.safehome.databinding.FragmentMenuBinding
import com.example.safehome.eventsview.EventsActivity
import com.example.safehome.facilitiesview.FacilitiesActivity
import com.example.safehome.forums.ForumsListActivity
import com.example.safehome.maintenance.MaintenanceActivity
import com.example.safehome.meetings.MeetingsActivity
import com.example.safehome.notice.NoticeActivity
import com.example.safehome.polls.PollsActivity
import com.example.safehome.services.ServicesActivity


class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var essentialsDialog: Dialog
    private lateinit var commuteDialog: Dialog
    private lateinit var utilityDialog: Dialog
    private lateinit var couponsDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.essentialMenuLayout.setOnClickListener{
            essentialsPopup()
        }
        binding.utilityMenuLayout.setOnClickListener {
            utilityPopup()
        }
        binding.couponsMenuLayout.setOnClickListener {

        }
        binding.commuteMenuLayout.setOnClickListener {
            commutePopup()
        }
        binding.maintenanceMenuLayout.setOnClickListener {
            try {
                val maintenanceIntent = Intent(requireContext(), MaintenanceActivity::class.java)
                maintenanceIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                maintenanceIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                requireContext().startActivity(maintenanceIntent)
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.facilitiesLayout.setOnClickListener {
            try {
                val fIntent = Intent(requireContext(), FacilitiesActivity::class.java)
                fIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                fIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(fIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.eventsMenuLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), EventsActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.dailyHelpLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), DailyHelpActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.apply {
            try {
                noticeLayout.setOnClickListener{
                    val eIntent = Intent(requireContext(), NoticeActivity::class.java)
                    eIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    eIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                    getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    requireContext().startActivity(eIntent)
                }
            }catch (e: Exception){

            }
        }
        binding.servicesLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), ServicesActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.meetingMenuLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), MeetingsActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                getActivity()?.overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                );
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.complaintsMenuLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), ComplaintsActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                getActivity()?.overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                );
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.pollsMenuLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), PollsActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                getActivity()?.overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                );
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.forumMenuLayout.setOnClickListener {
            try {
                val eIntent = Intent(requireContext(), ForumsListActivity::class.java)
                eIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                eIntent.putExtra("ScreenFrom", "MenuScreenFrag")
                getActivity()?.overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                );
                requireContext().startActivity(eIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    private fun essentialsPopup() {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.essentials_dialog, null)
        essentialsDialog = Dialog(requireActivity(), R.style.CustomAlertDialog)
        essentialsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        essentialsDialog.setContentView(view)
        essentialsDialog.setCanceledOnTouchOutside(true)
        essentialsDialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(essentialsDialog.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.8).toInt()
        //  lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        essentialsDialog.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        val cancel_img: ImageView = view.findViewById(R.id.cancel_img)

        cancel_img.setOnClickListener {

            if (essentialsDialog.isShowing) {
                essentialsDialog.dismiss()
            }
        }

        essentialsDialog.show()
    }

    private fun commutePopup() {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.commute_pop_up_dialog, null)
        commuteDialog = Dialog(requireActivity(), R.style.CustomAlertDialog)
        commuteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        commuteDialog.setContentView(view)
        commuteDialog.setCanceledOnTouchOutside(true)
        commuteDialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(commuteDialog.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT

        // lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        commuteDialog.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        val cancel_img: ImageView = view.findViewById(R.id.cancel_img)

        cancel_img.setOnClickListener {

            if (commuteDialog.isShowing) {
                commuteDialog.dismiss()
            }
        }
        commuteDialog.show()
    }

    private fun utilityPopup() {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.utility_pop_up_dialog, null)
        utilityDialog = Dialog(requireActivity(), R.style.CustomAlertDialog)
        utilityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        utilityDialog.setContentView(view)
        utilityDialog.setCanceledOnTouchOutside(true)
        utilityDialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(utilityDialog.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        // lp.height = (Utils.screenHeight * 0.5).toInt()
        // lp.height =LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        utilityDialog.window!!.attributes = lp
        // batchInformationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        val cancel_img: ImageView = view.findViewById(R.id.cancel_img)

        cancel_img.setOnClickListener {

            if (utilityDialog.isShowing) {
                utilityDialog.dismiss()
            }
        }
        utilityDialog.show()
    }


}