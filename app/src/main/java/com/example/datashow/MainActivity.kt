package com.example.datashow

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.datashow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    public val STAFF_TABLE = "staff"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = MyDatabaseHelper(this, "test.db", 1)
        binding.btnCreateDb.setOnClickListener {
            dbHelper.writableDatabase
        }
        binding.addItem.setOnClickListener {
            val db = dbHelper.writableDatabase
            val value = ContentValues().apply {
                put("name", "zhangsan")
                put("gender", "male")
                put("department", "A")
                put("salary", 1000)
            }
            db.insert(STAFF_TABLE, null, value)
            Toast.makeText(this, "add success", 2).show()
        }

        binding.refresh.setOnClickListener {
            val readDb = dbHelper.readableDatabase
            val cursor = readDb.query(STAFF_TABLE, null, null, null, null, null, null, null)
            while (cursor.moveToNext()) {
                Log.d("MainActivity",cursor.getString(cursor.getColumnIndex("name")))
            }
            cursor.close()
        }

    }
}

class MyDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    val CREATE_STAFF = "create table staff ( " +
            "id integer primary key autoincrement," +
            "name text," +
            "gender text," +
            "department text," +
            "salary float)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_STAFF)
        Toast.makeText(context, "Create succeeded", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}