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
import com.worksheetportiflio.systemsettings.fetchWorkoutSheets
import com.worksheetportiflio.systemsettings.findEmailUser
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
    val name : StateFlow<String> = _name
    val objective = mutableStateOf("")
    private val _personal = MutableStateFlow("")
    val personal: StateFlow<String> = _personal
    val selectedDate = mutableStateOf("")
    val showDatePickerDialog = mutableStateOf(false)
    private var listWorkoutSheets = mutableStateOf<WorkoutSheetModel?>(null)

    private val sheetDao = localDB.workoutSheetDao()


    fun loadStudent() {
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

    fun haveSheetsSave() = listWorkoutSheets.value?.objetivo?.isNotEmpty()

    fun setObjective(value: String) {
        objective.value = value
    }

    fun setPersonal(value: String) {
        _personal.value = value
    }

    fun setSelectedDate(value: String) {
        selectedDate.value = value
    }


    fun setShowDatePickerDialog(value: Boolean) {
        showDatePickerDialog.value = value
    }

    fun initRegisterObjective() {
        viewModelScope.launch {
            listWorkoutSheets.value = fetchWorkoutSheets(cloudDB, localUserData)
            initiateAllVariables()
        }
    }

    private fun initiateAllVariables() {
        if (listWorkoutSheets.value!!.objetivo.isNotEmpty()) {
            setObjective(listWorkoutSheets.value!!.objetivo)
            setPersonal(listWorkoutSheets.value!!.personal)
            setSelectedDate(listWorkoutSheets.value!!.data)
        }
    }


    fun onDateSelected(millis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val formattedDate = calendar.timeInMillis.toBrazilianDateFormat()

        selectedDate.value = formattedDate
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
                    _name.value ?: "",
                    selectedDate.value,
                    objective.value,
                    personal.value
                )
                if (existingSheet != null) sheetDao.update(sheet)
                else sheetDao.insertAll(sheet)

            }
            navController.navigate(Routes.RegisterWorkoutSheetScreen.route)
        }
    }
}
