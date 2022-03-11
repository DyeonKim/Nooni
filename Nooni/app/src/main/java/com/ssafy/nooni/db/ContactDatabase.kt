package com.ssafy.nooni.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ssafy.nooni.entity.Contact

@Database(entities = [Contact::class], version = 1)
abstract class ContactDatabase: RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        private var instance: ContactDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ContactDatabase? {
            if(instance == null) {
                synchronized(ContactDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ContactDatabase::class.java,
                        "contact-database"
                    ).build()
                }
            }
            return instance
        }
    }
}