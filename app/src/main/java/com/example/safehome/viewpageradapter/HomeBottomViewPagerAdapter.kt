package com.example.safehome.viewpageradapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.safehome.R
import com.example.safehome.model.ImageSource
import com.work.done.utils.setResizableText

class HomeBottomViewPagerAdapter (private val context: Context, private var albumList: List<ImageSource>) :
    PagerAdapter(){
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageSource: ImageSource

    override fun getCount(): Int {
        return albumList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater!!.inflate(R.layout.home_bottom_view_pager_list, null)

        val imageView = view.findViewById<View>(R.id.imageView) as ImageView
        val noticeSubject = view.findViewById<View>(R.id.notice_subject) as TextView
        val alertNumber = view.findViewById<View>(R.id.alert_number) as TextView



        imageSource = albumList[position]

        noticeSubject.text = imageSource.title
        val fullText = imageSource.title
        noticeSubject.setResizableText(fullText.toString(),2,true)
        alertNumber.text=imageSource.id


        // imageView.setImageResource(images[position])
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}