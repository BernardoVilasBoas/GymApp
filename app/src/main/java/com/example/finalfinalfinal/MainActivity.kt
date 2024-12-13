package com.example.finalfinalfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalfinalfinalTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
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
        composable("welcome") {
            WelcomeScreen(modifier = modifier)
        }
    }
}


@Composable
fun LoginScreen(navController: NavController, modifier: Modifier = Modifier) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
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
                text = "Bem-vindo!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            BasicTextField(
                value = username.value,
                onValueChange = { username.value = it },
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                Color.LightGray.copy(alpha = 0.3f),
                                MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (username.value.isEmpty()) {
                            Text("Email", color = Color.Gray)
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            BasicTextField(
                value = password.value,
                onValueChange = { password.value = it },
                visualTransformation = PasswordVisualTransformation(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                Color.LightGray.copy(alpha = 0.3f),
                                MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (password.value.isEmpty()) {
                            Text("Senha", color = Color.Gray)
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (username.value.isNotEmpty() && password.value.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(username.value, password.value)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Login bem-sucedido, redirecionar para a tela de boas-vindas
                                    navController.navigate("welcome")
                                } else {
                                    // Mostrar erro
                                    task.exception?.message?.let { error ->
                                        println("Erro: $error")
                                    }
                                }
                            }
                    } else {
                        println("Erro: Campos obrigatórios não preenchidos.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }


            TextButton(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Não tenho conta", color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
@Composable
fun SignUpScreen(navController: NavController, modifier: Modifier = Modifier) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
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
                color = MaterialTheme.colorScheme.primary
            )

            BasicTextField(
                value = email.value,
                onValueChange = { email.value = it },
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                Color.LightGray.copy(alpha = 0.3f),
                                MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (email.value.isEmpty()) {
                            Text("Email", color = Color.Gray)
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            BasicTextField(
                value = password.value,
                onValueChange = { password.value = it },
                visualTransformation = PasswordVisualTransformation(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                Color.LightGray.copy(alpha = 0.3f),
                                MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (password.value.isEmpty()) {
                            Text("Senha", color = Color.Gray)
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            BasicTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                visualTransformation = PasswordVisualTransformation(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                Color.LightGray.copy(alpha = 0.3f),
                                MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (confirmPassword.value.isEmpty()) {
                            Text("Confirmar Senha", color = Color.Gray)
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                        if (password.value == confirmPassword.value) {
                            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                                auth.createUserWithEmailAndPassword(email.value, password.value)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Cadastro bem-sucedido, pode navegar ou exibir mensagem
                                            navController.navigate("login")
                                        } else {
                                            // Mostrar erro
                                            task.exception?.message?.let { error ->
                                                println("Erro: $error")
                                            }
                                        }
                                    }
                            } else {
                                println("Erro: Formato de e-mail inválido.")
                            }
                        } else {
                            println("Erro: As senhas não coincidem.")
                        }
                    } else {
                        println("Erro: Todos os campos são obrigatórios.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cadastrar")
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


@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Bem-vindo!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
