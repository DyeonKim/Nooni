package com.ssafy.nooni.db

import androidx.room.*
import com.ssafy.nooni.entity.Contact

@Dao
interface ContactDao {
    @Insert
    fun insert(contact: Contact)

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @Query("SELECT * FROM Contact")
    fun getAll(): List<Contact>

    @Query("DELETE FROM Contact WHERE id = :id")
    fun deleteContactById(id: Int)
}