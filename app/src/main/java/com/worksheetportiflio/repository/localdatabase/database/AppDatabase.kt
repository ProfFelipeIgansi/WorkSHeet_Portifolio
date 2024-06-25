package com.worksheetportiflio.repository.localdatabase.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.worksheetportiflio.repository.localdatabase.dao.ExerciseDao
import com.worksheetportiflio.repository.localdatabase.dao.WorkoutSheetDao
import com.worksheetportiflio.repository.localdatabase.entity.Exercise
import com.worksheetportiflio.repository.localdatabase.entity.WorkoutSheet

@Database(
    entities = [Exercise::class, WorkoutSheet::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutSheetDao(): WorkoutSheetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }


        fun dropDatabase(context: Context){
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }
        }
    }

}
