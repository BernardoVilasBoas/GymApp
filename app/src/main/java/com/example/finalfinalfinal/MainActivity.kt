package com.example.finalfinalfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalfinalfinal.ui.theme.FinalfinalfinalTheme
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState

import androidx.compose.runtime.getValue

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalfinalfinalTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProgramaPrincipal()
                }
            }
        }
    }
}

@Composable
fun ProgramaPrincipal() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            // Exibe a barra de navegação somente se não estiver na tela de login ou signup
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != "login" && currentRoute != "signup") {
                BottomNavigationBar(navController = navController, appItems = Destino.toList)
            }
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                AppNavigation(navController = navController)
            }
        }
    )
}


@Composable
fun BottomNavigationBar(navController: NavController, appItems: List<Destino>) {
    BottomNavigation(backgroundColor = colorResource(id = R.color.purple_700),contentColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        appItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title, tint=if(currentRoute == item.route) Color.White else Color.White.copy(.4F)) },
                label = { Text(text = item.title, color = if(currentRoute == item.route) Color.White else Color.White.copy(.4F)) },
                selectedContentColor = Color.White, // esta instrução devia funcionar para o efeito (animação), para o ícone e para a cor do texto, mas só funciona para o efeito
                unselectedContentColor = Color.White.copy(0.4f), // esta instrução não funciona, por isso resolve-se acima no 'tint' do icon e na 'color' da label
                alwaysShowLabel = true, // colocar 'false' significa que o texto só aparece debaixo do ícone selecionado (em vez de debaixo de todos)
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route -> popUpTo(route) { saveState = true } }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, modifier = modifier)
        }
        composable("signup") {
            SignUpScreen(navController = navController, modifier = modifier)
        }
        composable(Destino.Ecra01.route) {
            Ecra01(navController = navController) // Passando o navController corretamente
        }
        composable(Destino.Ecra02.route) {
            Ecra02(navController = navController) // Passando o navController corretamente
        }
        composable(Destino.Ecra03.route) {
            Ecra03(navController = navController) // Passando o navController corretamente
        }
        composable(Destino.Ecra04.route) {
            Ecra04(navController = navController) // Passando o navController corretamente
        }
    }
}


@Composable
fun LoginScreen(navController: NavController, modifier: Modifier = Modifier) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título da tela
            Text(
                text = "Bem-vindo!",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            // Campo de email com ícone
            OutlinedTextField(
                value = username.value,
                onValueChange = {
                    username.value = it
                    errorMessage.value = "" // Limpa a mensagem de erro ao digitar
                },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Ícone de email"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de senha com ícone e botão para alternar visibilidade
            OutlinedTextField(
                value = password.value,
                onValueChange = {
                    password.value = it
                    errorMessage.value = "" // Limpa a mensagem de erro ao digitar
                },
                label = { Text("Senha") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Ícone de senha"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        val iconId = if (isPasswordVisible.value) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24
                        Image(
                            painter = painterResource(id = iconId),
                            contentDescription = if (isPasswordVisible.value) "Ocultar senha" else "Mostrar senha"
                        )
                    }


                },
                visualTransformation = if (isPasswordVisible.value) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Mensagem de erro
            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Botão de login
            Button(
                onClick = {
                    if (username.value.isNotEmpty() && password.value.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(username.value, password.value)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Login bem-sucedido, redirecionar para a tela de boas-vindas
                                    navController.navigate("Ecra01") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    // Mostrar erro específico
                                    errorMessage.value = when (val exception = task.exception) {
                                        is FirebaseAuthInvalidCredentialsException -> "Senha incorreta. Tente novamente."
                                        is FirebaseAuthInvalidUserException -> {
                                            if (exception.errorCode == "ERROR_USER_NOT_FOUND") {
                                                "Utilizador não encontrado. Verifique o email."
                                            } else {
                                                "Erro ao fazer login. Tente novamente."
                                            }
                                        }
                                        else -> "Erro ao fazer login. Tente novamente."
                                    }
                                }
                            }
                    } else {
                        errorMessage.value = "Preencha todos os campos."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Entrar", fontWeight = FontWeight.Bold)
            }

            // Botão de cadastro
            TextButton(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Não tenho conta",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun SignUpScreen(navController: NavController, modifier: Modifier = Modifier) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val isConfirmPasswordVisible = remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Crie sua conta",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Campo para o nome do usuário
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Nome") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Nome")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo para o e-mail
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            // Campo para a senha
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Senha") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Senha")
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        val iconId = if (isPasswordVisible.value) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24
                        Image(
                            painter = painterResource(id = iconId),
                            contentDescription = if (isPasswordVisible.value) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            // Campo para confirmar a senha
            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text("Confirmar Senha") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Confirmar Senha")
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        val iconId = if (isPasswordVisible.value) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24
                        Image(
                            painter = painterResource(id = iconId),
                            contentDescription = if (isPasswordVisible.value) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                },
                visualTransformation = if (isConfirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            // Botão de cadastro
            Button(
                onClick = {
                    if (name.value.isEmpty() || email.value.isEmpty() || password.value.isEmpty()) {
                        println("Erro: Todos os campos são obrigatórios.")
                        return@Button
                    }

                    if (password.value != confirmPassword.value) {
                        println("Erro: As senhas não coincidem.")
                        return@Button
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                        println("Erro: Formato de e-mail inválido.")
                        return@Button
                    }

                    auth.createUserWithEmailAndPassword(email.value, password.value)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val userId = user?.uid

                                // Atualizar o perfil no Firebase Authentication
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.value)
                                    .build()

                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            // Salvar o nome no Firestore
                                            val db = FirebaseFirestore.getInstance()
                                            val userData = mapOf(
                                                "name" to name.value,
                                                "email" to email.value
                                            )
                                            if (userId != null) {
                                                db.collection("users").document(userId).set(userData)
                                                    .addOnSuccessListener {
                                                        println("Usuário salvo com sucesso no Firestore.")
                                                        navController.navigate("login")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        println("Erro ao salvar usuário no Firestore: ${e.message}")
                                                    }
                                            }
                                        } else {
                                            println("Erro ao atualizar o perfil: ${updateTask.exception?.message}")
                                        }
                                    }
                            } else {
                                println("Erro ao criar a conta: ${task.exception?.message}")
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cadastrar", fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Já tenho conta", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    FinalfinalfinalTheme {
        SignUpScreen(navController = rememberNavController())
    }
}
