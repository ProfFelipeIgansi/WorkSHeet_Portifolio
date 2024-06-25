package com.worksheetportiflio.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.worksheetportiflio.repository.clouddatabase.model.WorkoutSheetModel
import com.worksheetportiflio.repository.localdatabase.database.AppDatabase
import com.worksheetportiflio.repository.localdatabase.entity.Exercise
import com.worksheetportiflio.repository.localdatabase.entity.WorkoutSheet
import com.worksheetportiflio.repository.sharedpreferences.LocalUserData
import com.worksheetportiflio.systemsettings.Constants
import com.worksheetportiflio.systemsettings.fetchWorkoutSheets
import kotlinx.coroutines.launch

class ListWorkoutSheetsViewModel(
    private val cloudDB: FirebaseFirestore,
    private val localUserData: LocalUserData,
    localDB: AppDatabase
) : ViewModel() {
    private var listWorkoutSheets = mutableStateOf<WorkoutSheetModel?>(null)
    private var _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> get() = _exercises

    private var _workoutSheet = MutableLiveData<List<WorkoutSheet>>()
    val workoutSheet: LiveData<List<WorkoutSheet>> get() = _workoutSheet

    private val exerciseDao = localDB.exerciseDao()
    private val workoutSheetDao = localDB.workoutSheetDao()

    fun initDataFetching() {
        try {
            viewModelScope.launch {
                listWorkoutSheets.value = fetchWorkoutSheets(cloudDB, localUserData)
                setTableValues()
                _exercises.postValue(getExercisesFromLocalDB())
                _workoutSheet.postValue(getUserInfosFromLocalDB())
            }
        } catch (e: Exception) {
            Log.w("Erro", "initDataFetching: Ocorreu um erro ao tentar inicializar as variaveis")
        }
    }

    private suspend fun setTableValues(){
        setUserInfosInLocalDB(
            WorkoutSheet(
                studentName = listWorkoutSheets.value?.nome ?: "",
                startDate = listWorkoutSheets.value?.data ?: "",
                objective = listWorkoutSheets.value?.objetivo ?: "",
                personal = listWorkoutSheets.value?.personal ?: "",
                idSheet = if (listWorkoutSheets.value?.nome!!.isNotEmpty()) localUserData.get(
                    Constants.Database.FKID_USER
                ) else ""
            )
        )

        setExerciseInLocalDB(listWorkoutSheets.value?.exercises)
    }

    private suspend fun getUserInfosFromLocalDB() = workoutSheetDao.getAll()

    private suspend fun getExercisesFromLocalDB() = exerciseDao.getAll()


    private suspend fun setUserInfosInLocalDB(sheetInformations: WorkoutSheet?) {
        try {
            if (workoutSheetDao.getAll().isNotEmpty())workoutSheetDao.deleteAll()
            if (sheetInformations?.studentName!!.isNotEmpty()) {
                if (workoutSheetDao.getAll().isEmpty()) {
                    workoutSheetDao.insertAll(sheetInformations)
                } /*else workoutSheetDao.insertAllWithReplace(sheetInformations)*/
            }
        } catch (e: Exception) {
            Log.w(
                "Erro",
                "setUserInfosInLocalDB: Ocorreu um erro ao tentar inserir as WorkoutSheets"
            )
        }
    }

    private suspend fun setExerciseInLocalDB(exercises: List<Exercise>?) {
        try {
            if (exerciseDao.getAll().isNotEmpty()) exerciseDao.deleteAll()
            exercises?.forEach { exercise ->
                exerciseDao.insertAllWithReplace(exercise)
            }
        } catch (e: Exception) {
            Log.w("Erro", "setUserInfosInLocalDB: Ocorreu um erro ao tentar inserir os exercicios")
        }

    }
}
