package com.example.roomdatabase

import android.app.Application

class EmployeeApp:Application() {

    val db by lazy {
        EmployeeDataBase.getInstant(this)
    }
}