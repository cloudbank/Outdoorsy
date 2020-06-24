package com.example.android.codelabs.paging.db

import androidx.room.TypeConverter
import com.droidteahouse.outdoorsy.vo.Included
import com.droidteahouse.outdoorsy.vo.Rental

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromRentalList(value: List<Rental>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Rental>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toRentalList(value: String): List<Rental> {
        val gson = Gson()
        val type = object : TypeToken<List<Rental>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromIncludedList(value: List<Included>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Included>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toIncludedList(value: String): List<Included> {
        val gson = Gson()
        val type = object : TypeToken<List<Included>>() {}.type
        return gson.fromJson(value, type)
    }


}

