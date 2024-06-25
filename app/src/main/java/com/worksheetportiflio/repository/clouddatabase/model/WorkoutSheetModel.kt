package com.worksheetportiflio.repository.clouddatabase.model

import com.worksheetportiflio.repository.localdatabase.entity.Exercise

data class WorkoutSheetModel (
    val nome: String = "",
    val objetivo: String = "",
    val data: String = "",
    val personal: String = "",
    val exercises: List<Exercise> = listOf()
)