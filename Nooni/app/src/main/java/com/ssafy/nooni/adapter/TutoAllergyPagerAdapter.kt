package com.ssafy.nooni.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.ssafy.nooni.R

class TutoAllergyPagerAdapter(private val files: ArrayList<Pair<String, String>>)
    : RecyclerView.Adapter<TutoAllergyPagerAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(
        parent.context).inflate(R.layout.list_tutorial_allergy, parent, false)) {

        val lottie = itemView.findViewById<LottieAnimationView>(R.id.lottie_view)
        val textView = itemView.findViewById<TextView>(R.id.tv_allergy_tutorial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ) = PagerViewHolder((parent))

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.lottie.apply {
            setAnimation(files[position].first)
            repeatCount = LottieDrawable.INFINITE
            playAnimation()
        }
        holder.textView.text = files[position].second
    }

    override fun getItemCount() = files.size

}