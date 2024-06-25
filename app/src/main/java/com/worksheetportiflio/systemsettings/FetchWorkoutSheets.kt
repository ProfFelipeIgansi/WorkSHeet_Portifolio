package com.worksheetportiflio.systemsettings

import com.google.firebase.firestore.FirebaseFirestore
import com.worksheetportiflio.repository.clouddatabase.model.WorkoutSheetModel
import com.worksheetportiflio.repository.sharedpreferences.LocalUserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun fetchWorkoutSheets(
    cloudDB: FirebaseFirestore,
    localUserData: LocalUserData
): WorkoutSheetModel {
    val querySnapshot = withContext(Dispatchers.IO) {
        findWorkoutSheet(cloudDB, localUserData)
    }
    return querySnapshot.toObject(WorkoutSheetModel::class.java) ?: WorkoutSheetModel()
}