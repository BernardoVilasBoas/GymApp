package com.example.finalfinalfinal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Configurações",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Divider()

        // Opção para alterar o e-mail
        Button(
            onClick = {
                val user = auth.currentUser
                user?.let {
                    val newEmail = "novoeemail@exemplo.com" // Substitua por um valor real
                    user.updateEmail(newEmail)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                println("E-mail alterado com sucesso!")
                            } else {
                                println("Erro ao alterar e-mail: ${task.exception?.message}")
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alterar E-mail")
        }

        // Opção para alterar a senha
        Button(
            onClick = {
                val user = auth.currentUser
                user?.let {
                    val newPassword = "NovaSenha123" // Substitua por um valor real
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                println("Senha alterada com sucesso!")
                            } else {
                                println("Erro ao alterar senha: ${task.exception?.message}")
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alterar Senha")
        }

        // Fazer logout
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("login") { // Redireciona para a tela de login
                    popUpTo(0) // Limpa o histórico de navegação
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sair")
        }
    }
}
