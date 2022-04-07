package com.ssafy.nooni.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["person_id"], unique = true)])
data class Contact (
    var name: String,
    var phone: String,
    var photo_id: Long,
    var person_id: Long
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}