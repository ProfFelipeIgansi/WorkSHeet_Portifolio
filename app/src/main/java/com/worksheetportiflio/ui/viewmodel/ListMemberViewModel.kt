package com.worksheetportiflio.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.worksheetportiflio.repository.clouddatabase.model.UserModel
import com.worksheetportiflio.repository.sharedpreferences.LocalUserData
import com.worksheetportiflio.systemsettings.Constants
import com.worksheetportiflio.systemsettings.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ListMemberViewModel(
    private val cloudDB: FirebaseFirestore,
    private val navController: NavController,
    private val localUserData: LocalUserData
) : ViewModel() {

    private val _listUsers = MutableStateFlow<List<UserModel>>(emptyList())
    val listUsers: StateFlow<List<UserModel>> = _listUsers

    fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val alunos = cloudDB.collection(Constants.USERTABLENAME)
                    .whereEqualTo(Constants.ROLE, Constants.STUDENT)
                    .get()
                    .await()
                val users = alunos.map { it.toObject(UserModel::class.java) }
                _listUsers.value = users
            } catch (e: Exception) {
                Log.e("ListMemberViewModel", "Error loading users", e)
            }
        }
    }

    fun onUserSelected(user: UserModel) {
        localUserData.save(Constants.SELECTED_NAME_STUDENT, user.nome)
        localUserData.save(Constants.SELECTED_EMAIL_STUDENT, user.email)
        navController.navigate(Routes.ObjectiveRegisterScreen.route)
    }
}