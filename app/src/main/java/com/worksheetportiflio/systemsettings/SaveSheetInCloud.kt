package com.worksheetportiflio.systemsettings

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.worksheetportiflio.repository.clouddatabase.model.WorkoutSheetModel
import com.worksheetportiflio.repository.localdatabase.dao.ExerciseDao
import com.worksheetportiflio.repository.localdatabase.dao.WorkoutSheetDao
import com.worksheetportiflio.repository.sharedpreferences.LocalUserData
import com.worksheetportiflio.systemsettings.Constants

suspend fun saveSheetInCloud(
    workoutSheetDao: WorkoutSheetDao,
    exerciseDao: ExerciseDao,
    localUserData: LocalUserData,
    cloudDB: FirebaseFirestore
) {
    val workoutSheet = workoutSheetDao.loadById(localUserData.get(Constants.Database.FKID_USER))
    val exerciseDao = exerciseDao.loadAllByFKId(localUserData.get(Constants.Database.FKID_USER))
    val mapValues =
        WorkoutSheetModel(
            nome = workoutSheet.studentName,
            objetivo = workoutSheet.objective,
            data = workoutSheet.startDate,
            exercises = exerciseDao,
            personal = workoutSheet.personal
        )

    cloudDB.collection(Constants.Database.WORKOUTSHEETCOLLECTION)
        .document(localUserData.get(Constants.Database.FKID_USER)).set(mapValues)
        .addOnSuccessListener {
            Log.i("informatio", "saveSheetInCloud: Ficha de treino salvo na nuvem corretamente")
        }
        .addOnFailureListener {
            Log.w("erro", "saveSheetInCloud: Ocorreu um erro ao tentar salvar a Ficha de treino")
        }
}