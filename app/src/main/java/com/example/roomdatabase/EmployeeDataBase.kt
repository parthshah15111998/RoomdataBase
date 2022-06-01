package com.example.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase
import java.time.Instant

@Database(entities = [EmployeeEntity::class], version = 1)
abstract class EmployeeDataBase:RoomDatabase() {

    abstract fun employeeDao():EmployeeDao

    companion object{
        private var INSTANT:EmployeeDataBase ? = null

        fun getInstant(context: Context):EmployeeDataBase{
            synchronized(this){
                var instant= INSTANT

                if (instant == null){
                    instant = Room.databaseBuilder(
                        context.applicationContext,
                        EmployeeDataBase::class.java,
                    "employee_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANT=instant
                }
                return instant
            }
        }
    }
}