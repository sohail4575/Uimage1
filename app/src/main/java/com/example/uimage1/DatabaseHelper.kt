package com.example.uimage1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(private val context:Context):SQLiteOpenHelper(context,"School",null,1) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val query = "CREATE TABLE Student (Id INTEGER PRIMARY KEY AUTOINCREMENT,Title TEXT,Description TEXT,Image BLOB)"
        p0?.execSQL(query)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
    fun insert(title:String?,description:String?,image:ByteArray){
        val db = this.writableDatabase
        val value = ContentValues()
        value.put("Title",title)
        value.put("Description",description)
        value.put("Image",image)
        db.insert("Student",null,value)
        db.close()

    }
}