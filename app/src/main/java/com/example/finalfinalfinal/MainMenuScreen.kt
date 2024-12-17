package com.example.finalfinalfinal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ginásio e Treinos") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Box(
            modifier = modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagem ou logo do ginásio
                Image(
                    painter = painterResource(id = R.drawable.baseline_sports_gymnastics_24), // Adicione um recurso de imagem no projeto
                    contentDescription = "Logo do Ginásio",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Text(
                    text = "Bem-vindo ao App do Ginásio!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Botões para acessar diferentes funcionalidades
                Button(
                    onClick = { /* Navegar para a tela de treinos */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Meus Treinos")
                }

                Button(
                    onClick = { /* Navegar para a tela de progressos */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Progresso")
                }

                Button(
                    onClick = { navController.navigate("Configs") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Configurações")
                }


            }
        }
    }

}

