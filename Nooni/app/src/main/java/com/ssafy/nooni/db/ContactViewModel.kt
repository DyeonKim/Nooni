package com.ssafy.nooni.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ssafy.nooni.entity.Contact

class ContactViewModel(application: Application): AndroidViewModel(application) {
    private val repository = ContactsRespository(application)
    private val items = repository.getAll()

    fun insert(contact: Contact){
        repository.insert(contact)
    }

    fun delete(contact: Contact){
        repository.delete(contact)
    }

    fun getAll(): LiveData<List<Contact>> {
        return items
    }
}