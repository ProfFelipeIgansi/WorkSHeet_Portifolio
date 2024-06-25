package com.worksheetportiflio.systemsettings

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun findWorkoutSheet(cloudDB: FirebaseFirestore, idUser: String) =
    cloudDB.collection(Constants.Database.WORKOUTSHEETCOLLECTION)
        .document(idUser)
        .get()
        .await()

