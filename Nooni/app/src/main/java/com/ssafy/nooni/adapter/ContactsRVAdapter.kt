package com.ssafy.nooni.adapter

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.nooni.entity.Contact
import de.hdodenhof.circleimageview.CircleImageView
import android.graphics.Bitmap

import android.graphics.BitmapFactory

import android.content.ContentUris

import android.content.ContentResolver
import android.util.Log
import java.lang.Exception
import android.os.Build

import android.graphics.drawable.shapes.OvalShape

import android.graphics.drawable.ShapeDrawable

import android.content.Context
import com.ssafy.nooni.R


class ContactsRVAdapter(context: Context) : RecyclerView.Adapter<ContactsRVAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener
    lateinit var itemLongClickListener: ItemLongClickListener

    private var list = ArrayList<Contact>()
    private val mContext: Context = context

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                if(this@ContactsRVAdapter::itemClickListener.isInitialized) {
                    itemClickListener.onClick(list[adapterPosition])
                }
            }

            itemView.setOnLongClickListener {
                if(this@ContactsRVAdapter::itemLongClickListener.isInitialized) {
                    itemLongClickListener.onClick(list[adapterPosition])
                }
                true
            }
        }


        private val text = itemView.findViewById<TextView>(R.id.tv_contactItem_name)
        private val picture = itemView.findViewById<CircleImageView>(R.id.imageView_contactItem_picture)
        fun bind(item: Contact){
            text.text = item.name

            picture.setImageDrawable(
                mContext.resources.getDrawable(R.drawable.ic_baseline_person_24)
            )
            val profile =
                loadContactPhoto(mContext.contentResolver, item.person_id, item.photo_id)
            if (profile != null) {
                if (Build.VERSION.SDK_INT >= 21) {
                    picture.background = ShapeDrawable(OvalShape())
                    picture.clipToOutline = true
                }
                picture.setImageBitmap(profile)
            } else {
                if (Build.VERSION.SDK_INT >= 21) {
                    picture.clipToOutline = false
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_contact_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(data: List<Contact>) {
        list = data as ArrayList<Contact>
        notifyDataSetChanged()
    }

    fun loadContactPhoto(cr: ContentResolver, id: Long, photo_id: Long): Bitmap? {
        var photoBytes: ByteArray? = null
        val photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id)
        val c = cr.query(
            photoUri, arrayOf(ContactsContract.CommonDataKinds.Photo.PHOTO),
            null, null, null
        )
        try {
            if (c!!.moveToFirst()) photoBytes = c.getBlob(0)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            c!!.close()
        }
        if (photoBytes != null) {
            return resizingBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size))
        } else Log.d("<<CONTACT_PHOTO>>", "second try also failed")
        return null
    }

    fun resizingBitmap(oBitmap: Bitmap?): Bitmap? {
        if (oBitmap == null) {
            return null
        }
        var width = oBitmap.width.toFloat()
        var height = oBitmap.height.toFloat()
        val resizing_size = 120f
        var rBitmap: Bitmap? = null
        if (width > resizing_size) {
            val fScale = (resizing_size / (width / 100))
            width *= fScale / 100
            height *= fScale / 100
        } else if (height > resizing_size) {
            val fScale = (resizing_size / (height / 100))
            width *= fScale / 100
            height *= fScale / 100
        }
        rBitmap = Bitmap.createScaledBitmap(oBitmap, width.toInt(), height.toInt(), true)
        return rBitmap
    }

    interface ItemClickListener {
        fun onClick(contact: Contact)
    }

    interface  ItemLongClickListener {
        fun onClick(contact: Contact)
    }


}
