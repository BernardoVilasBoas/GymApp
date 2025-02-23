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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton

import androidx.compose.material3.TextFieldDefaults

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ecra01(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val searchQuery = remember { mutableStateOf("") }
    val searchResults = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    val friendsList = remember { mutableStateOf<List<String>>(emptyList()) }
    val friendsNames = remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val friendsTrainings = remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }

    val showDialog = remember { mutableStateOf(false) }
    val selectedUser = remember { mutableStateOf<Pair<String, String>?>(null) }

    // Função para buscar usuários
    fun searchUsers(query: String) {
        if (query.isBlank()) {
            searchResults.value = emptyList()
            return
        }

        firestore.collection("users")
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: "Unknown"
                    id to name
                }.filter { it.first != currentUser?.uid }
                searchResults.value = users
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error searching users", e)
            }
    }

    // Função para carregar os nomes dos amigos
    fun loadFriendsNames(friendIds: List<String>) {
        val names = mutableMapOf<String, String>()
        friendIds.forEach { friendId ->
            firestore.collection("users").document(friendId).get()
                .addOnSuccessListener { doc ->
                    val name = doc.getString("name") ?: "Desconhecido"
                    names[friendId] = name
                    friendsNames.value = names
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Erro ao carregar nome do amigo", e)
                }
        }
    }

    // Função para carregar os treinos dos amigos
    fun loadFriendsTrainings(friendIds: List<String>) {
        val trainings = mutableMapOf<String, List<String>>()
        friendIds.forEach { friendId ->
            firestore.collection("trainings").whereEqualTo("userId", friendId).get()
                .addOnSuccessListener { docs ->
                    val trainingList = docs.mapNotNull { it.getString("trainingName") }
                    trainings[friendId] = trainingList
                    friendsTrainings.value = trainings
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Erro ao carregar treinos do amigo", e)
                }
        }
    }

    // Função para carregar amigos e seus dados
    fun loadFriends() {
        currentUser?.uid?.let { userId ->
            firestore.collection("friends").document(userId).get()
                .addOnSuccessListener { document ->
                    val friends = document.get("friendsList") as? List<String> ?: emptyList()
                    friendsList.value = friends
                    loadFriendsNames(friends)
                    loadFriendsTrainings(friends)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Erro ao carregar amigos", e)
                }
        }
    }

    // Função para adicionar amigo
    fun addFriend(friendId: String) {
        currentUser?.uid?.let { userId ->
            firestore.collection("friends").document(userId).update(
                "friendsList", FieldValue.arrayUnion(friendId)
            ).addOnSuccessListener {
                Log.d("Firestore", "Amigo adicionado com sucesso!")
                friendsList.value = friendsList.value + friendId
                loadFriends()
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao adicionar amigo", e)
            }
        }
    }

    // Função para remover amigo
    fun removeFriend(friendId: String) {
        currentUser?.uid?.let { userId ->
            firestore.collection("friends").document(userId).update(
                "friendsList", FieldValue.arrayRemove(friendId)
            ).addOnSuccessListener {
                Log.d("Firestore", "Amigo removido com sucesso!")
                friendsList.value = friendsList.value - friendId
                loadFriends() // Carregar novamente a lista de amigos
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao remover amigo", e)
            }
        }
    }

    // Chama a função para carregar amigos e seus dados assim que a tela for criada
    LaunchedEffect(Unit) {
        loadFriends()
    }

    // UI da tela
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagem de fundo
        Image(
            painter = painterResource(id = R.drawable.backgroundgymapp),
            contentDescription = "Imagem de fundo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Conteúdo da tela
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Logo no topo
            Image(
                painter = painterResource(id = R.drawable.logogymapp),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { query ->
                    searchQuery.value = query
                    searchUsers(query)
                },
                label = { Text("Pesquisar usuários...", color = Color.White) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        searchUsers(searchQuery.value)
                    }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                ),
                textStyle = TextStyle(color = Color.White) // Aqui você define a cor do texto
            )



            // Lista de amigos e seus treinos
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Meus Amigos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            LazyColumn {
                items(friendsList.value) { friendId ->
                    val friendName = friendsNames.value[friendId] ?: "Desconhecido"
                    val friendTrainings = friendsTrainings.value[friendId] ?: emptyList()

                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = friendName,
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            IconButton(onClick = {
                                removeFriend(friendId) // Remove amigo ao clicar
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_remove_circle_24),
                                    contentDescription = "Remover Amigo",
                                    tint = Color.White
                                )
                            }
                        }

                        // Lista de treinos do amigo
                        if (friendTrainings.isNotEmpty()) {
                            Text(
                                text = "Treinos:",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                            friendTrainings.forEach { training ->
                                Text(
                                    text = "- $training",
                                    fontSize = 14.sp,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(start = 32.dp, top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pop-up de confirmação de amizade
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Adicionar amigo?", color = Color.Black) },
                text = { Text("Você deseja adicionar ${selectedUser.value?.second} como amigo?", color = Color.Black) },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedUser.value?.first?.let { friendId ->
                                addFriend(friendId)
                            }
                            showDialog.value = false
                        }
                    ) {
                        Text("Aceitar")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
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
fun Ecra02(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val currentUserId = currentUser?.uid
    var workouts by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var showDialog by remember { mutableStateOf(false) }
    var editWorkout by remember { mutableStateOf<Map<String, Any>?>(null) }

    // Carregar treinos do Firestore
    LaunchedEffect(Unit) {
        if (currentUserId != null) {
            db.collection("userWorkouts")
                .document(currentUserId)
                .collection("workouts")
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("Firestore", "Erro ao buscar treinos", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        workouts = snapshot.documents.map { doc ->
                            doc.data?.plus("id" to doc.id) ?: emptyMap()
                        }
                    }
                }
        }
    }

    // Layout principal com a imagem de fundo
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Adiciona a imagem de fundo
        Image(
            painter = painterResource(id = R.drawable.backgroundgymapp),
            contentDescription = "Fundo do Gym App",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Conteúdo da tela
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (workouts.isEmpty()) {
                    items(1) {
                        Text(
                            text = "Nenhum treino disponível ainda.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(workouts) { workout ->
                        WorkoutItem(
                            nome = workout["nome"] as? String ?: "Sem Nome",
                            descricao = workout["descricao"] as? String ?: "Sem Descrição",
                            exercicios = workout["exercicios"] as? List<Map<String, Any>> ?: emptyList(),
                            data = workout["data"] as? Timestamp,
                            onEditClick = {
                                editWorkout = workout
                                showDialog = true
                            },
                            onDeleteClick = {
                                val workoutId = workout["id"] as? String
                                if (currentUserId != null && workoutId != null) {
                                    db.collection("userWorkouts")
                                        .document(currentUserId)
                                        .collection("workouts")
                                        .document(workoutId)
                                        .delete()
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "Treino removido com sucesso!")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Erro ao remover treino", e)
                                        }
                                }
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Adicionar Treino", color = Color.White)
            }
        }

        if (showDialog) {
            AddWorkoutDialog(
                initialWorkout = editWorkout,
                onDismiss = {
                    showDialog = false
                    editWorkout = null
                },
                onSubmit = { nome, descricao, exercicios ->
                    if (currentUserId != null) {
                        if (editWorkout != null) {
                            val workoutId = editWorkout!!["id"] as? String
                            if (workoutId != null) {
                                db.collection("userWorkouts")
                                    .document(currentUserId)
                                    .collection("workouts")
                                    .document(workoutId)
                                    .update(
                                        "nome", nome,
                                        "descricao", descricao,
                                        "exercicios", exercicios
                                    )
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Treino atualizado com sucesso!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Erro ao atualizar treino", e)
                                    }
                            }
                        } else {
                            val newWorkout = hashMapOf(
                                "nome" to nome,
                                "descricao" to descricao,
                                "data" to FieldValue.serverTimestamp(),
                                "exercicios" to exercicios
                            )
                            db.collection("userWorkouts")
                                .document(currentUserId)
                                .collection("workouts")
                                .add(newWorkout)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Treino adicionado com sucesso!")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Erro ao adicionar treino", e)
                                }
                        }
                    }
                    showDialog = false
                    editWorkout = null
                }
            )
        }
    }
}

@Composable
fun WorkoutItem(
    nome: String,
    descricao: String,
    exercicios: List<Map<String, Any>>,
    data: Timestamp?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val formattedDate = data?.toDate()?.toString() ?: "Sem Data"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = descricao, style = MaterialTheme.typography.bodyMedium)
        Text(text = "Data: $formattedDate", style = MaterialTheme.typography.bodySmall)
        Text(text = "Exercícios:")
        exercicios.forEach { exercicio ->
            Text("- ${exercicio["nome"]}: ${exercicio["series"]}x${exercicio["repeticoes"]}")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onEditClick() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Editar", color = Color.White)
            }

            Button(
                onClick = { onDeleteClick() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Remover", color = Color.White)
            }
        }
    }
}

@Composable
fun AddWorkoutDialog(
    initialWorkout: Map<String, Any>?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, List<Map<String, Any>>) -> Unit
) {
    var nome by remember { mutableStateOf(initialWorkout?.get("nome") as? String ?: "") }
    var descricao by remember { mutableStateOf(initialWorkout?.get("descricao") as? String ?: "") }
    val exercicios = remember { mutableStateListOf<Map<String, Any>>() }

    initialWorkout?.get("exercicios")?.let {
        if (exercicios.isEmpty()) {
            exercicios.addAll(it as List<Map<String, Any>>)
        }
    }

    var showExerciseDialog by remember { mutableStateOf(false) }

    if (showExerciseDialog) {
        AddExerciseDialog(
            onDismiss = { showExerciseDialog = false },
            onSubmit = { nomeExercicio, series, repeticoes ->
                exercicios.add(
                    mapOf(
                        "nome" to nomeExercicio,
                        "series" to series,
                        "repeticoes" to repeticoes
                    )
                )
                showExerciseDialog = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (initialWorkout != null) "Editar Treino" else "Adicionar Treino") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Treino") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showExerciseDialog = true }) {
                    Text("Adicionar Exercício")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Exercícios adicionados:")
                exercicios.forEachIndexed { index, exercicio ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("- ${exercicio["nome"]}: ${exercicio["series"]}x${exercicio["repeticoes"]}", modifier = Modifier.weight(1f))
                        IconButton(onClick = { exercicios.removeAt(index) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remover Exercício"
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nome.isNotBlank() && descricao.isNotBlank()) {
                    onSubmit(nome, descricao, exercicios)
                }
            }) {
                Text("Salvar")
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
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int, Int) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var repeticoes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Adicionar Exercício") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Exercício") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = series,
                    onValueChange = { series = it },
                    label = { Text("Número de Séries") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = repeticoes,
                    onValueChange = { repeticoes = it },
                    label = { Text("Número de Repetições") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nome.isNotBlank() && series.isNotBlank() && repeticoes.isNotBlank()) {
                    onSubmit(nome, series.toInt(), repeticoes.toInt())
                }
            }) {
                Text("Adicionar")
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
fun Ecra03(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val currentUserId = currentUser?.uid
    var comments by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }
    var friends by remember { mutableStateOf(listOf<String>()) }
    var showDialog by remember { mutableStateOf(false) }
    var showFriendRequestDialog by remember { mutableStateOf(false) }
    var showIncomingRequestDialog by remember { mutableStateOf(false) }
    var friendRequests by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var selectedUserId by remember { mutableStateOf("") }
    var selectedRequest by remember { mutableStateOf<Pair<String, String>?>(null) }


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
    @Composable
    fun FriendsList(friends: List<String>) {
        // Exemplo simples de renderização de lista
        friends.forEach { friend ->
            Log.d("Friend", friend) // Aqui você pode renderizar um item da UI para cada amigo
        }
    }


    // Carregar lista de amigos
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            // Verificar se o documento de amigos do usuário já existe
            db.collection("friends")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Documento existe, obter a lista de amigos
                        val fetchedFriends = document.get("friendsList") as? List<*>
                        if (fetchedFriends != null) {
                            friends = fetchedFriends.mapNotNull { it as? String }
                        } else {
                            Log.e(
                                "Firestore",
                                "Campo friendsList não encontrado no documento existente."
                            )
                        }
                    } else {
                        // Documento não existe, criar o documento com uma lista vazia de amigos
                        Log.i(
                            "Firestore",
                            "Documento de amigos não existe, criando documento com lista vazia."
                        )
                        val newFriendsMap = mapOf("friendsList" to emptyList<String>())
                        db.collection("friends")
                            .document(currentUserId)
                            .set(newFriendsMap) // Criar documento com lista vazia de amigos
                            .addOnSuccessListener {
                                Log.i("Firestore", "Documento criado com sucesso com lista vazia!")
                                friends = emptyList() // Inicializar amigos com lista vazia
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    "Firestore",
                                    "Erro ao criar documento de amigos: ${e.localizedMessage}",
                                    e
                                )
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Erro ao buscar amigos: ${e.localizedMessage}", e)
                }
        } else {
            Log.e("Auth", "Usuário não autenticado ou ID do usuário é nulo.")
        }
    }


    // Verificar pedidos de amizade recebidos
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            db.collection("friendRequests")
                .whereEqualTo("receiverId", currentUserId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("Firestore", "Erro ao buscar pedidos de amizade", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val requests = snapshot.documents.map { doc ->
                            val senderId = doc.getString("senderId") ?: ""
                            val requestId = doc.id
                            senderId to requestId
                        }
                        friendRequests = requests
                        if (requests.isNotEmpty()) {
                            val firstRequest = requests.first()
                            db.collection("users").document(firstRequest.first)
                                .get()
                                .addOnSuccessListener { userDoc ->
                                    val senderName = userDoc.getString("name") ?: firstRequest.first
                                    selectedRequest = senderName to firstRequest.second
                                    showIncomingRequestDialog = true
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Erro ao buscar nome do remetente", e)
                                }
                        }
                    }
                }
        }
    }

    @Composable
    fun IncomingRequestDialog(
        senderName: String,
        requestId: String,
        onDismiss: () -> Unit,
        onAccept: () -> Unit,
        onDecline: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Pedido de Amizade") },
            text = { Text(text = "Você recebeu um pedido de amizade de $senderName. Deseja aceitar?") },
            confirmButton = {
                Button(onClick = onAccept) {
                    Text("Aceitar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDecline) {
                    Text("Recusar")
                }
            }
        )
    }

    if (showIncomingRequestDialog && selectedRequest != null) {
        val (senderName, requestId) = selectedRequest!!
        IncomingRequestDialog(
            senderName = senderName,
            requestId = requestId,
            onDismiss = { showIncomingRequestDialog = false },
            onAccept = {
                if (currentUserId != null && selectedRequest != null) {
                    val senderId = selectedRequest!!.first // Usar o senderId
                    val friendUpdate = db.collection("friends").document(currentUserId)
                    val senderFriendUpdate = db.collection("friends").document(senderId)

                    db.runTransaction { transaction ->
                        // 1. Verifique se o documento de amigos de ambos existe
                        val currentUserDoc = transaction.get(friendUpdate)
                        val senderUserDoc = transaction.get(senderFriendUpdate)

                        // 2. Se o documento de amigos de algum usuário não existir, criá-lo
                        if (!currentUserDoc.exists()) {
                            transaction.set(
                                friendUpdate,
                                mapOf("friendsList" to mutableListOf(senderId))
                            ) // Criar o documento com a lista de amigos
                        }
                        if (!senderUserDoc.exists()) {
                            transaction.set(
                                senderFriendUpdate,
                                mapOf("friendsList" to mutableListOf(currentUserId))
                            ) // Criar o documento com a lista de amigos
                        }

                        // 3. Obter as listas de amigos
                        val currentFriends =
                            currentUserDoc.get("friendsList") as? MutableList<String>
                                ?: mutableListOf()
                        val senderFriends = senderUserDoc.get("friendsList") as? MutableList<String>
                            ?: mutableListOf()

                        // 4. Adicione o novo amigo às listas de ambos, se ainda não estiverem na lista
                        if (!currentFriends.contains(senderId)) currentFriends.add(senderId)
                        if (!senderFriends.contains(currentUserId)) senderFriends.add(currentUserId)

                        // 5. Atualize as listas de amigos em ambos os documentos
                        transaction.update(friendUpdate, "friendsList", currentFriends)
                        transaction.update(senderFriendUpdate, "friendsList", senderFriends)

                        // 6. Criar a amizade na coleção de amizades (bilateral)
                        val friendshipDoc = db.collection("friendships").document()
                        val friendshipData = mapOf(
                            "user1Id" to currentUserId,
                            "user2Id" to senderId
                        )

                        // Criar a amizade no Firestore
                        transaction.set(friendshipDoc, friendshipData)

                    }.addOnSuccessListener {
                        // 7. Após a transação, remover o pedido de amizade
                        db.collection("friendRequests").document(requestId).delete()
                        Log.d("Firestore", "Amizade aceita com sucesso!")
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "Erro ao aceitar amizade", e)
                    }
                }

                showIncomingRequestDialog = false
            }
            ,



                    onDecline = {
                db.collection("friendRequests").document(requestId).delete()
                    .addOnSuccessListener {
                        Log.d("Firestore", "Pedido de amizade recusado com sucesso!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erro ao recusar pedido de amizade", e)
                    }
                showIncomingRequestDialog = false
            }
        )
    }

    @Composable
    fun CommentItem(
        userName: String,
        comment: String,
        isCurrentUser: Boolean,
        isFriend: Boolean,
        userId: String,
        onUserIconClick: () -> Unit
    ) {
        val maxWidthDp = (0.8f * LocalConfiguration.current.screenWidthDp).dp
        val backgroundColor = if (isCurrentUser) Color(0xFF4CAF50) else Color(0xFF81C784)
        val textColor =
            if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isCurrentUser && !isFriend) {
                IconButton(onClick = onUserIconClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = "Adicionar $userName",
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

// Imagem de fundo
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.backgroundgymapp),
            contentDescription = "Fundo do Gym App",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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

                        // Só calcular isFriend quando amigos já estiverem carregados
                        val isFriend = friends.contains(userId) && userId != currentUserId
                        CommentItem(
                            userName = displayName,
                            comment = comment,
                            isCurrentUser = isCurrentUser,
                            isFriend = isFriend, // Passa a variável isFriend para a função CommentItem
                            userId = userId,
                            onUserIconClick = {
                                selectedUserId = userId
                                showFriendRequestDialog = true
                            }
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
    }



    @Composable
    fun CommentDialog(
        onDismiss: () -> Unit,
        onSubmit: (String, String) -> Unit
    ) {
        var topic by remember { mutableStateOf("") }
        var comment by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Adicionar Comentário") },
            text = {
                Column {
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Tópico") },
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
                    if (topic.isNotBlank() && comment.isNotBlank()) {
                        onSubmit(topic, comment)
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

    if (showDialog) {
        CommentDialog(
            onDismiss = { showDialog = false },
            onSubmit = { topic, comment ->
                if (currentUserId != null) {
                    val newComment = hashMapOf(
                        "name" to (currentUser.displayName ?: "Anônimo"),
                        "userId" to currentUserId,
                        "comment" to "Tópico: $topic\n$comment",
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



    if (showFriendRequestDialog) {
        AlertDialog(
            onDismissRequest = { showFriendRequestDialog = false },
            title = { Text(text = "Enviar Pedido de Amizade") },
            text = {
                Text(text = "Deseja enviar um pedido de amizade?")
            },
            confirmButton = {
                Button(onClick = {
                    if (currentUserId != null && selectedUserId.isNotEmpty()) {
                        // Verificar se já existe um pedido de amizade pendente
                        db.collection("friendRequests")
                            .whereEqualTo("senderId", currentUserId)
                            .whereEqualTo("receiverId", selectedUserId)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (querySnapshot.isEmpty) {
                                    // Se não houver pedido pendente, enviar um novo pedido
                                    val friendRequest = hashMapOf(
                                        "senderId" to currentUserId,
                                        "receiverId" to selectedUserId,
                                        "timestamp" to FieldValue.serverTimestamp()
                                    )
                                    db.collection("friendRequests").add(friendRequest)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "Firestore",
                                                "Pedido de amizade enviado com sucesso!"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(
                                                "Firestore",
                                                "Erro ao enviar pedido de amizade",
                                                e
                                            )
                                        }
                                } else {
                                    // Se já existe um pedido pendente, mostrar uma mensagem ou não fazer nada
                                    Log.d("Firestore", "Já existe um pedido de amizade pendente.")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Erro ao verificar pedidos de amizade", e)
                            }

                        showFriendRequestDialog = false
                    }
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFriendRequestDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}



@Composable
fun Ecra04(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userName = remember { mutableStateOf("Usuário") } // Estado para armazenar o nome do usuário
    val currentUser = auth.currentUser

    val showDeleteDialog = remember { mutableStateOf(false) } // Controle do diálogo para confirmar exclusão de conta
    val showPasswordDialog = remember { mutableStateOf(false) }
    val showEmailDialog = remember { mutableStateOf(false) }
    val oldPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val newEmail = remember { mutableStateOf("") }

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
            text = "Olá, ${userName.value}!",
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

        Divider()

        // Botão para apagar a conta
        Button(
            onClick = { showDeleteDialog.value = true },
            colors = ButtonDefaults.buttonColors(Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apagar Conta", color = Color.White)
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

    // Diálogo de confirmação para apagar a conta
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(text = "Apagar Conta")
            },
            text = {
                Text(
                    text = "Tem certeza de que deseja apagar sua conta? Essa ação não pode ser desfeita.",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        deleteAccount(auth, firestore, navController)
                        showDeleteDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text("Confirmar", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Função para apagar a conta do usuário
fun deleteAccount(auth: FirebaseAuth, firestore: FirebaseFirestore, navController: NavController) {
    val user = auth.currentUser ?: return
    val userId = user.uid

    // Remove os dados do Firestore
    firestore.collection("users").document(userId).delete()
        .addOnSuccessListener {
            println("Dados do usuário excluídos do Firestore.")
        }
        .addOnFailureListener {
            println("Erro ao excluir dados do usuário: ${it.message}")
        }

    // Remove o usuário do Firebase Authentication
    user.delete()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Conta do usuário excluída com sucesso.")
                navController.navigate("login") { // Redireciona para a tela de login
                    popUpTo(0) // Limpa o histórico de navegação
                }
            } else {
                println("Erro ao excluir conta do usuário: ${task.exception?.message}")
            }
        }
}
