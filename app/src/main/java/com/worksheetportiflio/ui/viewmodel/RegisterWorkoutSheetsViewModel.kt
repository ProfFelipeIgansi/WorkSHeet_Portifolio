package com.worksheetportiflio.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.worksheetportiflio.repository.clouddatabase.model.WorkoutSheetModel
import com.worksheetportiflio.repository.localdatabase.database.AppDatabase
import com.worksheetportiflio.repository.localdatabase.entity.Exercise
import com.worksheetportiflio.repository.sharedpreferences.LocalUserData
import com.worksheetportiflio.repository.sharedpreferences.SessionManager
import com.worksheetportiflio.systemsettings.Constants
import com.worksheetportiflio.systemsettings.Routes
import com.worksheetportiflio.systemsettings.fetchWorkoutSheets
import com.worksheetportiflio.systemsettings.saveSheetInCloud
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterWorkoutSheetsViewModel(
    val localDB: AppDatabase,
    val navController: NavController,
    val sessionManager: SessionManager,
    val cloudDB: FirebaseFirestore,
    val localUserData: LocalUserData
) : ViewModel() {

    var isDialogOpen = mutableStateOf(false)
    var selectedExerciseType = mutableStateOf("")
    var exerciseName = mutableStateOf("")
    var exerciseRepetitions = mutableStateOf("")
    var exerciseWeight = mutableStateOf("")

    private var listWorkoutSheets = mutableStateOf<WorkoutSheetModel?>(null)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val exerciseDao = localDB.exerciseDao()
    private val workoutSheetDao = localDB.workoutSheetDao()


    fun setName(value: String) {
        exerciseName.value = value
    }

    fun setReps(value: String) {
        exerciseRepetitions.value = value
    }

    fun setWeight(value: String) {
        exerciseWeight.value = value
    }

    private fun openDialog() {
        isDialogOpen.value = true
    }

    fun closeDialog() {
        isDialogOpen.value = false
    }

    fun confirmAction() {
        exerciseName.value = ""
        exerciseRepetitions.value = ""
        exerciseWeight.value = ""
        closeDialog()
    }
    private suspend fun haveExercisesInCloud(){
        listWorkoutSheets.value = fetchWorkoutSheets(cloudDB, localUserData)
        if (listWorkoutSheets.value!!.exercises.isEmpty()){
            saveSheetInCloud(workoutSheetDao, exerciseDao, localUserData, cloudDB)
        }
    }
    private  suspend fun ifHaveConflictSaveFromLocalDatabaseToCloud(){
        listWorkoutSheets.value = fetchWorkoutSheets(cloudDB, localUserData)
        if (listWorkoutSheets.value!!.exercises != exerciseDao.getAll()){
            saveSheetInCloud(workoutSheetDao, exerciseDao, localUserData, cloudDB)
        }
    }


    private fun verifyIfHaveExercisesSavedInCloud() {
        scope.launch {
            haveExercisesInCloud()
        }
    }

    private fun verifyIfHaveConflict() {
        scope.launch {
            ifHaveConflictSaveFromLocalDatabaseToCloud()
        }
    }

    fun navigateToListExercises() {
        verifyIfHaveExercisesSavedInCloud()
        verifyIfHaveConflict()
        navController.navigate(Routes.ListWorkoutSheet.route)
    }

    fun dismissAction(destination: String) {
        closeDialog()
        verifyIfHaveExercisesSavedInCloud()
        verifyIfHaveConflict()
        sessionManager.saveAuthenticationStage(destination)
        navController.navigate(destination)
    }

    @Suppress("SENSELESS_COMPARISON")
    fun cadastrarExercicio() {
        val fkUser = localUserData.get(Constants.Database.FKID_USER)
        val exercise = Exercise(
            idExercise = 0,
            type = selectedExerciseType.value.lowercase(),
            name = exerciseName.value.lowercase(),
            weight = exerciseWeight.value,
            repetitions = exerciseRepetitions.value.lowercase(),
            fkIdSheet = fkUser
        )
        if (localUserData.get(Constants.SELECTED_EMAIL_STUDENT) != "") {
            if (selectedExerciseType.value.isNotEmpty() && exerciseName.value.isNotEmpty() && exerciseRepetitions.value.isNotEmpty()) {
                viewModelScope.launch {
                    val existingExercise = exerciseDao.loadExerciseByName(exerciseName.value.lowercase())
                    if (existingExercise == null) exerciseDao.insertAll(exercise)
                    else exerciseDao.update(exercise)
                    Log.i("information", "cadastrarExercicio: ${exerciseDao.loadAll()}")
                }
                openDialog()
            }
        }
    }
}