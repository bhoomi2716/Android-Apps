package com.example.prectice1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(var context: Context) : SQLiteOpenHelper(context, db_name,null, db_version)
{
    companion object
    {
        var db_name = "practice.dp"
        var table_name = "calculation"
        var db_version = 1
        var ID = "id"
        var column_calculation = "column_calculation"
    }

    override fun onCreate(db: SQLiteDatabase?)
    {
        var query = "CREATE TABLE " + table_name + "(" + ID + " INTEGER PRIMARY KEY," + column_calculation + "TEXT" + ")"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {
        var upquery = "DROP TABLE if exists" + table_name
        db!!.execSQL(upquery)
        onCreate(db)
    }

    fun insertColumn(formula : String) : Long
    {
        var insertDb = writableDatabase
        var insertValues = ContentValues()
        insertValues.put(column_calculation,formula)
        var insertQuery = insertDb.insert(table_name,null,insertValues)
        return insertQuery
    }

    fun viewColumn() : ArrayList<String>
    {
        val db = this.readableDatabase
        val cursor : Cursor = db.rawQuery("SELECT * FROM $table_name", null)
        val calculations = ArrayList<String>()

        if (cursor.moveToFirst())
        {
            do
            {
                val calculation = cursor.getString(1)
                calculations.add(calculation)
            }
            while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return calculations
    }
}