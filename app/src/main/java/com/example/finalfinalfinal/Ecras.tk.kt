package com.example.finalfinalfinal

import android.util.Log
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    // UI da tela inicial com design aprimorado
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fundo claro
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mensagem de boas-vindas
        Text(
            text = "Bem-vindo, ${userName.value}!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Banner ou imagem principal
        Image(
            painter = painterResource(id = R.drawable.logogymapp), // Adicione uma imagem no drawable
            contentDescription = "Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botões de ação ou atalhos para funcionalidades
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton("Treinos", R.drawable.baseline_groups_24) {
                navController.navigate("treinos")
            }
            ActionButton("Nutrição", R.drawable.baseline_home_24) {
                navController.navigate("nutricao")
            }
            ActionButton("Progresso", R.drawable.baseline_settings_24) {
                navController.navigate("progresso")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Texto motivacional
        Text(
            text = "Continue alcançando seus objetivos!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ActionButton(label: String, iconId: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = label,
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
                .padding(12.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}





@Composable
fun Ecra02(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val userWorkouts = remember { mutableStateListOf<Map<String, String>>() } // Lista de treinos do usuário
    val showDialog = remember { mutableStateOf(false) }

    // Buscar treinos do usuário no Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("workouts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val workouts = querySnapshot.documents.map { document ->
                        document.data as Map<String, String> // Map com os dados do treino
                    }
                    userWorkouts.clear()
                    userWorkouts.addAll(workouts)
                }
                .addOnFailureListener { exception ->
                    println("Erro ao buscar treinos do usuário: ${exception.message}")
                }
        } else {
            println("Usuário não autenticado.")
        }
    }

    // Tela de treinos
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fundo claro
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Meus Treinos",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Lista de treinos
        if (userWorkouts.isEmpty()) {
            Text(
                text = "Nenhum treino encontrado. Adicione seu primeiro treino!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userWorkouts) { workout ->
                    WorkoutCard(workout)
                }
            }
        }

        // Botão para adicionar treino
        FloatingActionButton(
            onClick = { showDialog.value = true },
            backgroundColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_24), // Adicione o ícone de adicionar no drawable
                contentDescription = "Adicionar treino",
                tint = Color.White
            )
        }

        // Diálogo para adicionar treino
        if (showDialog.value) {
            AddWorkoutDialog(
                onDismiss = { showDialog.value = false },
                onAddWorkout = { workoutName, workoutDescription ->
                    // Salvar treino no Firestore
                    if (userId != null) {
                        val newWorkout = hashMapOf(
                            "userId" to userId,
                            "name" to workoutName,
                            "description" to workoutDescription
                        )
                        firestore.collection("workouts")
                            .add(newWorkout)
                            .addOnSuccessListener {
                                userWorkouts.add(newWorkout)
                                showDialog.value = false
                            }
                            .addOnFailureListener { exception ->
                                println("Erro ao adicionar treino: ${exception.message}")
                            }
                    }
                }
            )
        }
    }
}

@Composable
fun WorkoutCard(workout: Map<String, String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = workout["name"] ?: "Treino sem nome",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = workout["description"] ?: "Sem descrição",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun AddWorkoutDialog(
    onDismiss: () -> Unit,
    onAddWorkout: (String, String) -> Unit
) {
    var workoutName by remember { mutableStateOf("") }
    var workoutDescription by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Adicionar Treino", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                TextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    label = { Text("Nome do Treino") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = workoutDescription,
                    onValueChange = { workoutDescription = it },
                    label = { Text("Descrição do Treino") },
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddWorkout(workoutName, workoutDescription)
                },
                enabled = workoutName.isNotBlank() && workoutDescription.isNotBlank()
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}







@Composable
fun Ecra03(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val currentUserId = currentUser?.uid
    var comments by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }
    var showDialog by remember { mutableStateOf(false) }

    // Carregar comentários do Firestore
    LaunchedEffect(Unit) {
        db.collection("exerciseComments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Erro ao buscar comentários", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val fetchedComments = snapshot.documents.map { doc ->
                        val userId = doc.getString("userId") ?: ""
                        val name = doc.getString("name") ?: "Anônimo"
                        val comment = doc.getString("comment") ?: ""
                        Triple(userId, name, comment)
                    }
                    comments = fetchedComments
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Lista de comentários
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (comments.isEmpty()) {
                items(1) {
                    Text(
                        text = "Nenhum comentário disponível ainda.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(comments) { (userId, userName, comment) ->
                    val isCurrentUser = userId == currentUserId
                    val displayName = if (isCurrentUser) "Você" else userName
                    CommentItem(
                        userName = displayName,
                        comment = comment,
                        isCurrentUser = isCurrentUser,
                        userId = userId,
                        navController = navController
                    )
                }
            }
        }

        // Botão para abrir o diálogo
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Adicionar Comentário")
        }
    }

    // Exibir o diálogo de comentários
    if (showDialog) {
        CommentDialog(
            onDismiss = { showDialog = false },
            onSubmit = { exercise, rating, comment ->
                if (currentUserId != null) {
                    val newComment = hashMapOf(
                        "name" to (currentUser.displayName ?: "Anônimo"),
                        "userId" to currentUserId,
                        "comment" to "Exercício: $exercise\nAvaliação: ${"⭐".repeat(rating)}\n$comment",
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    db.collection("exerciseComments").add(newComment)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Comentário adicionado com sucesso!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Erro ao adicionar comentário", e)
                        }
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun CommentDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int, String) -> Unit
) {
    var exercise by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(3) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Adicionar Comentário") },
        text = {
            Column {
                OutlinedTextField(
                    value = exercise,
                    onValueChange = { exercise = it },
                    label = { Text("Exercício") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Avaliação: ${"⭐".repeat(rating)}")
                Slider(
                    value = rating.toFloat(),
                    onValueChange = { rating = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentário") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (exercise.isNotBlank() && comment.isNotBlank()) {
                    onSubmit(exercise, rating, comment)
                }
            }) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CommentItem(userName: String, comment: String, isCurrentUser: Boolean, userId: String, navController: NavController) {
    val maxWidthDp = (0.8f * LocalConfiguration.current.screenWidthDp).dp
    val backgroundColor = if (isCurrentUser) Color(0xFF4CAF50) else Color(0xFF81C784)
    val textColor = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isCurrentUser) {
            IconButton(
                onClick = { navController.navigate("${Destino.Ecra04}/$userId") }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_fitness_center_24),
                    contentDescription = "Perfil de $userName",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(
            modifier = Modifier
                .widthIn(max = maxWidthDp)
                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(text = userName, fontWeight = FontWeight.Bold, color = textColor)
            Text(text = comment, color = textColor)
        }
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
