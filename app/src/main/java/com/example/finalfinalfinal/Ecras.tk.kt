package com.example.finalfinalfinal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ecra01(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val userName = remember { mutableStateOf("Usuário") }

    // Buscar o nome do usuário no Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        if (!name.isNullOrEmpty()) {
                            userName.value = name
                        } else {
                            println("Campo 'name' ausente ou vazio no documento do usuário $userId")
                        }
                    } else {
                        println("Documento do usuário $userId não encontrado.")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Erro ao buscar nome do usuário: ${exception.message}")
                }
        } else {
            println("Usuário não autenticado.")
        }
    }

    // UI da tela inicial
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Bem-vindo, ${userName.value}!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}




@Composable
fun Ecra02(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(text = stringResource(id = R.string.ecra02),
            fontWeight = FontWeight.Bold, color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center, fontSize = 18.sp
        )
    }
}






@Composable
fun Ecra03(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(text = stringResource(id = R.string.ecra03),
            fontWeight = FontWeight.Bold, color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center, fontSize = 18.sp
        )
    }
}

@Composable
fun Ecra04(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userName = remember { mutableStateOf("Usuário") } // Estado para armazenar o nome do usuário
    val currentUser = auth.currentUser

    // Busca o nome do usuário no Firestore sempre que o ID do usuário mudar ou o currentUser for alterado
    LaunchedEffect(auth.currentUser?.uid) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName.value = document.getString("name") ?: "Usuário"
                    }
                }
                .addOnFailureListener {
                    println("Erro ao buscar nome do usuário: ${it.message}")
                }
        }
    }


    // Estados para controlar os diálogos e os valores dos campos
    val showPasswordDialog = remember { mutableStateOf(false) }
    val showEmailDialog = remember { mutableStateOf(false) }
    val oldPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val newEmail = remember { mutableStateOf("") }

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

        // Saudação personalizada com o nome do usuário
        Text(
            text = "Olá, ${userName.value}!", // O nome será atualizado aqui
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Divider()

        // Botão para abrir o diálogo de alteração de e-mail
        Button(
            onClick = { showEmailDialog.value = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alterar E-mail")
        }

        Divider()

        // Botão para abrir o diálogo de alteração de senha
        Button(
            onClick = { showPasswordDialog.value = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alterar Senha")
        }

        Divider()

        // Opção para fazer logout
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

    // Diálogo de alteração de senha
    if (showPasswordDialog.value) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog.value = false },
            title = {
                Text(text = "Alterar Senha")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    OutlinedTextField(
                        value = oldPassword.value,
                        onValueChange = { oldPassword.value = it },
                        label = { Text("Senha Antiga") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword.value,
                        onValueChange = { newPassword.value = it },
                        label = { Text("Nova Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPassword.value,
                        onValueChange = { confirmPassword.value = it },
                        label = { Text("Confirmar Nova Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword.value == confirmPassword.value) {
                            val user = auth.currentUser
                            user?.let {
                                // Aqui você pode adicionar lógica para verificar a senha antiga antes de alterar
                                user.updatePassword(newPassword.value)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            println("Senha alterada com sucesso!")
                                        } else {
                                            println("Erro ao alterar senha: ${task.exception?.message}")
                                        }
                                    }
                            }
                        } else {
                            println("Erro: As senhas não coincidem.")
                        }
                        showPasswordDialog.value = false // Fecha o diálogo
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showPasswordDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de alteração de e-mail
    if (showEmailDialog.value) {
        AlertDialog(
            onDismissRequest = { showEmailDialog.value = false },
            title = {
                Text(text = "Alterar E-mail")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    OutlinedTextField(
                        value = newEmail.value,
                        onValueChange = { newEmail.value = it },
                        label = { Text("Novo E-mail") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val user = auth.currentUser
                        user?.let {
                            user.updateEmail(newEmail.value)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        println("E-mail alterado com sucesso!")
                                    } else {
                                        println("Erro ao alterar e-mail: ${task.exception?.message}")
                                    }
                                }
                        }
                        showEmailDialog.value = false // Fecha o diálogo
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showEmailDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
