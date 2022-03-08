package com.ssafy.nooni.db

import android.app.Application
import androidx.lifecycle.LiveData
import com.ssafy.nooni.entity.Contact

class ContactsRespository(application: Application) {
    private val contactDao: ContactDao
    private val contactList: LiveData<List<Contact>>

    init {
        var db = ContactDatabase.getInstance(application)
        contactDao = db!!.contactDao()
        contactList = db.contactDao().getAll()
    }

    fun insert(contact: Contact) {
        contactDao.insert(contact)
    }

    fun delete(contact: Contact) {
        contactDao.delete(contact)
    }

    fun getAll(): LiveData<List<Contact>> {
        return contactDao.getAll()
    }
}