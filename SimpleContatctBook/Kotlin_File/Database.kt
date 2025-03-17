package com.example.contacts

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database(var context: Context) : SQLiteOpenHelper(context, DB_NAME,null, DB_VERSION)
{
    companion object
    {
        var DB_NAME="tops.db"
        var TABLE_NAME="details"
        var ID="id"
        var NAME="name"
        var NUMBER="number"
        var DB_VERSION=1

    }

    override fun onCreate(p0: SQLiteDatabase?)
    {
        var query ="CREATE TABLE " + TABLE_NAME + "("+ ID + " INTEGER PRIMARY KEY," + NAME + " TEXT,"+ NUMBER + " TEXT" + ")"
        p0!!.execSQL(query)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int)
    {
        var upgradeQuery = "DROP TABLE if exists "+ TABLE_NAME
        p0!!.execSQL(upgradeQuery)
        onCreate(p0)
    }

    fun InsertContact(fields : TableFields) : Long
    {
        var insertDB = writableDatabase
        var insertValues = ContentValues()
        insertValues.put(NAME,fields.contact_name)
        insertValues.put(NUMBER,fields.contact_number)
        var insertQuery = insertDB.insert(TABLE_NAME, null ,insertValues)

        return insertQuery
    }

    fun ViewContacts() : ArrayList<TableFields>
    {
        var viewDB = readableDatabase
        var tableColoums = arrayOf(ID, NAME, NUMBER)
        var viewCursor = viewDB.query(TABLE_NAME,tableColoums,null,null,null,null,null,null)

        var contactArrayList = ArrayList<TableFields>()
        while (viewCursor.moveToNext())
        {
            var id = viewCursor.getInt(0)
            var name = viewCursor.getString(1)
            var number = viewCursor.getString(2)

            var fields = TableFields()
            fields.contact_id = id
            fields.contact_name = name
            fields.contact_number = number

            contactArrayList.add(fields)
        }
        viewCursor.close()
        return contactArrayList
    }

    fun UpdateContacts(fields: TableFields)
    {
        var updateDB = writableDatabase
        var updateValues = ContentValues()
        updateValues.put(ID,fields.contact_id)
        updateValues.put(NAME,fields.contact_name)
        updateValues.put(NUMBER,fields.contact_number)
        var updateQuery = ID + " = " + fields.contact_id
        updateDB.update(TABLE_NAME,updateValues,updateQuery,null)
    }



    fun DeleteDatabse(cid : Int)
    {
        var deleteDB = writableDatabase
        var deleteQuery = "$ID = ?"
        deleteDB.delete(TABLE_NAME, deleteQuery, arrayOf(cid.toString()))
    }

}