package com.example.finalfinalfinal

sealed class Destino(val route: String, val icon: Int, val title: String) {
    object Ecra01 : Destino(route = "ecra01", icon = R.drawable.baseline_home_24, title = "Menu")
    object Ecra02 : Destino(route = "ecra02", icon = R.drawable.baseline_fitness_center_24, title = "Treinos")
    object Ecra03 : Destino(route = "ecra03", icon = R.drawable.baseline_groups_24, title = "Fórum")
    object Ecra04 : Destino(route = "ecra04", icon = R.drawable.baseline_settings_24, title = "Perfil")
    companion object {
        val toList = listOf(Ecra01, Ecra02, Ecra03, Ecra04)
    }
}