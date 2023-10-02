package com.example.safehome.viewpageradapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.model.FacilitiesGalleyImagesModel
import com.example.safehome.model.ImageSource
import com.work.done.utils.setResizableText

class FacilitiesGalleryViewPagerAdapter (private val context: Context, private var galleyList: List<FacilitiesGalleyImagesModel>) :
    PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var facilitiesGalleyImagesModel: FacilitiesGalleyImagesModel

    override fun getCount(): Int {
        return galleyList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater!!.inflate(R.layout.gallery_list_view_pager_item, null)

        val imageView = view.findViewById<View>(R.id.galley_view_pager_img) as ImageView
        facilitiesGalleyImagesModel = galleyList[position]

        Glide.with(context)
            .load("https://qa.msafehome.com/${facilitiesGalleyImagesModel.Img}")
            .fitCenter()
            .into(imageView)

    //    imageView.setImageResource(galleyList[position])
//        imageView.setImageResource(facilitiesGalleyImagesModel.Img)
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



