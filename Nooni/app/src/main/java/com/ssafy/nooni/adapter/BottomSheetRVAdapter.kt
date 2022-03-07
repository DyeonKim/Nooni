package com.ssafy.nooni.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.nooni.R

class BottomSheetRVAdapter: RecyclerView.Adapter<BottomSheetRVAdapter.ViewHolder>() {
    private var data = listOf<String>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val text = itemView.findViewById<TextView>(R.id.tv_allergy_item)
        fun bind(item: String){
            text.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetRVAdapter.ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_allergy_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    fun setData(list: List<String>) {
        data = list
        notifyDataSetChanged()
    }

}