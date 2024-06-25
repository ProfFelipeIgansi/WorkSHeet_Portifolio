package com.worksheetportiflio.systemsettings

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.worksheetportiflio.repository.sharedpreferences.LocalUserData
import com.worksheetportiflio.systemsettings.Constants
import kotlinx.coroutines.tasks.await

suspend fun findWorkoutSheet(cloudDB: FirebaseFirestore, localUserData: LocalUserData): DocumentSnapshot =
    cloudDB.collection(Constants.Database.WORKOUTSHEETCOLLECTION)
        .document(localUserData.get(Constants.EMAIL))
        .get()
        .await()

