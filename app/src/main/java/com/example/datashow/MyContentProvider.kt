package com.example.datashow

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.net.Uri

class MyContentProvider : ContentProvider() {
    private val staffDir = 0
    private val staffItem = 1
    private val authority = "com.example.datashow.provider"
    private var dbHelper: MyDatabaseHelper? = null
    private val STAFF_TABLE = "staff"
    private val uriMatcher by lazy {
        val matcher = UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(authority, "staff", staffDir)
        matcher.addURI(authority, "staff/#", staffItem)
        matcher
    }

    override fun onCreate() = context?.let {
        dbHelper = MyDatabaseHelper(it, STAFF_TABLE, 1)
        true
    } ?: false

    override fun insert(uri: Uri, values: ContentValues?) = dbHelper?.let {
        val db = it.writableDatabase
        val uriReturn = when (uriMatcher.match(uri)) {
            staffDir, staffItem -> {
                val newStaffId = db.insert(STAFF_TABLE, null, values)
                Uri.parse("content://$authority/book/$newStaffId")
            }
            else -> null
        }
        uriReturn

    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?)=dbHelper?.let{
        val db = it.writableDatabase
        val deleteRows = when (uriMatcher.match(uri)) {
            staffItem -> db.delete(STAFF_TABLE,selection,selectionArgs)
            staffDir-> {
                val staffId=uri.pathSegments[1]
                db.delete(STAFF_TABLE,"id = ?", arrayOf(staffId))
            }
            else->0
        }
        deleteRows
    }?:0




    override fun getType(uri: Uri): String? {
        when (uriMatcher.match(uri)) {
            staffDir -> {
                return "vnd.android.cursor.dir/vnd.com.example.datashow.provider.staff"
            }
            staffItem -> {
                return "vnd.android.cursor.item/vnd.com.example.datashow.provider.staff"
            }
            else -> {
                return null
            }
        }
    }


    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ) = dbHelper?.let {
        //query
        val db = it.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            staffDir -> db.query(
                STAFF_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
            staffItem -> {
                val staffId = uri.pathSegments[1]
                db.query(STAFF_TABLE, projection, "id = ?", arrayOf(staffId), null, null, sortOrder)
            }
            else -> null
        }
        cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ) = dbHelper?.let {

        val db = it.writableDatabase
        val updateRows = when (uriMatcher.match(uri)) {
            staffItem -> db.update(STAFF_TABLE,values,selection,selectionArgs)
            staffDir-> {
                val staffId=uri.pathSegments[1]
                db.update(STAFF_TABLE,values,"id = ?", arrayOf(staffId))
            }
            else->0
        }
        updateRows
    }?:0
}