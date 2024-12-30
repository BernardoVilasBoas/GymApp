package com.example.finalfinalfinal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    // Variável para armazenar o nome do usuário
    var userName: String by mutableStateOf("Utilizador")
        private set

    // Variável para armazenar o email do usuário
    var userEmail: String by mutableStateOf("")
        private set

    // Método para atualizar o nome do usuário
    fun updateUserName(name: String) {
        userName = name
    }

    // Método para atualizar o email do usuário
    fun updateUserEmail(email: String) {
        userEmail = email
    }
}