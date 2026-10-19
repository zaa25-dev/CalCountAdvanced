package com.codepath.campgrounds

import android.app.Application

class CalorieApplication : Application() {
    val db by lazy { AppDatabase.getInstance(this) }
}