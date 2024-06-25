package com.worksheetportiflio.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.worksheetportiflio.repository.clouddatabase.model.UserModel
import com.worksheetportiflio.repository.clouddatabase.model.WorkoutSheetModel
import com.worksheetportiflio.repository.localdatabase.database.AppDatabase
import com.worksheetportiflio.repository.localdatabase.entity.WorkoutSheet
import com.worksheetportiflio.repository.sharedpreferences.LocalUserData
import com.worksheetportiflio.systemsettings.Constants
import com.worksheetportiflio.systemsettings.Routes
import com.worksheetportiflio.systemsettings.findEmailUser
import com.worksheetportiflio.systemsettings.findUserIDFromFirestore
import com.worksheetportiflio.systemsettings.findWorkoutSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class RegisterObjectiveViewModel(
    private val navController: NavController,
    val localUserData: LocalUserData,
    localDB: AppDatabase,
    val cloudDB: FirebaseFirestore
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _objective = MutableStateFlow("")
    val objective: StateFlow<String> = _objective

    private val _personal = MutableStateFlow("")
    val personal: StateFlow<String> = _personal

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate

    val showDatePickerDialog = mutableStateOf(false)

    private val sheetDao = localDB.workoutSheetDao()


    fun loadName() {
        viewModelScope.launch {
            val querySnapshot = withContext(Dispatchers.IO) {
                findEmailUser(cloudDB, localUserData.get(Constants.SELECTED_EMAIL_STUDENT))
            }
            val user =
                if (!querySnapshot.isEmpty) querySnapshot.documents[0].toObject(UserModel::class.java)
                else UserModel()
            _name.value = user?.nome ?: ""
        }
    }

    fun loadPersonal() {
        viewModelScope.launch {
            findUserIDFromFirestore(
                cloudDB,
                localUserData.get(Constants.SELECTED_EMAIL_STUDENT),
                localUserData
            )
            val querySnapshot = withContext(Dispatchers.IO) {
                findWorkoutSheet(
                    cloudDB = cloudDB,
                    idUser = localUserData.get(Constants.Database.FKID_USER)
                )
            }
            val workoutSheetModel = querySnapshot.toObject(WorkoutSheetModel::class.java)
            _personal.value = workoutSheetModel?.personal ?: ""
        }
    }

    fun loadDate() {
        viewModelScope.launch {
            findUserIDFromFirestore(
                cloudDB,
                localUserData.get(Constants.SELECTED_EMAIL_STUDENT),
                localUserData
            )
            val querySnapshot = withContext(Dispatchers.IO) {
                findWorkoutSheet(
                    cloudDB = cloudDB,
                    idUser = localUserData.get(Constants.Database.FKID_USER)
                )
            }
            val workoutSheetModel = querySnapshot.toObject(WorkoutSheetModel::class.java)
            _selectedDate.value = workoutSheetModel?.data ?: ""
        }
    }

    fun loadObjective() {
        viewModelScope.launch {
            findUserIDFromFirestore(
                cloudDB,
                localUserData.get(Constants.SELECTED_EMAIL_STUDENT),
                localUserData
            )
            val querySnapshot = withContext(Dispatchers.IO) {
                findWorkoutSheet(
                    cloudDB = cloudDB,
                    idUser = localUserData.get(Constants.Database.FKID_USER)
                )
            }
            val workoutSheetModel = querySnapshot.toObject(WorkoutSheetModel::class.java)
            _objective.value = workoutSheetModel?.objetivo ?: ""
        }
    }

    fun setObjective(value: String) {
        _objective.value = value
    }

    fun setPersonal(value: String) {
        _personal.value = value
    }

    fun setSelectedDate(value: String) {
        _selectedDate.value = value
    }


    fun setShowDatePickerDialog(value: Boolean) {
        showDatePickerDialog.value = value
    }


    fun onDateSelected(millis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val formattedDate = calendar.timeInMillis.toBrazilianDateFormat()

        _selectedDate.value = formattedDate
        showDatePickerDialog.value = false
    }

    private fun Long.toBrazilianDateFormat(): String {
        val date = Date(this)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        return format.format(date)
    }

    @Suppress("SENSELESS_COMPARISON")
    fun onNextButtonClick() {
        if (objective.value.isNotBlank() && selectedDate.value.isNotBlank()) {
            viewModelScope.launch {
                val fkID = localUserData.get(Constants.Database.FKID_USER)
                val existingSheet = sheetDao.loadByName(_name.value)
                val sheet = WorkoutSheet(
                    fkID,
                    _name.value,
                    _selectedDate.value,
                    _objective.value,
                    _personal.value
                )
                if (existingSheet != null) sheetDao.update(sheet)
                else sheetDao.insertAll(sheet)

            }
            navController.navigate(Routes.RegisterWorkoutSheetScreen.route)
        }
    }
}
