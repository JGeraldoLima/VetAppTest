package com.example.petmedtracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.petmedtracker.presentation.addmedication.AddMedicationScreen
import com.example.petmedtracker.presentation.addpet.AddPetScreen
import com.example.petmedtracker.presentation.petdetail.PetDetailScreen
import com.example.petmedtracker.presentation.petlist.PetListScreen

const val ROUTE_PET_LIST = "pet_list"
const val ROUTE_PET_DETAIL = "pet_detail/{petId}"
const val ROUTE_ADD_MEDICATION = "add_medication/{petId}"
const val ROUTE_ADD_PET = "add_pet"

fun petDetailRoute(petId: String) = "pet_detail/$petId"
fun addMedicationRoute(petId: String) = "add_medication/$petId"

@Composable
fun PetMedTrackerNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_PET_LIST
    ) {
        composable(ROUTE_PET_LIST) {
            PetListScreen(
                onPetClick = { petId -> navController.navigate(petDetailRoute(petId)) },
                onAddPet = { navController.navigate(ROUTE_ADD_PET) }
            )
        }
        composable(ROUTE_ADD_PET) {
            AddPetScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = ROUTE_PET_DETAIL,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            PetDetailScreen(
                navBackStackEntry = backStackEntry,
                onAddMedication = { petId -> navController.navigate(addMedicationRoute(petId)) },
                onSharePdf = { },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = ROUTE_ADD_MEDICATION,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddMedicationScreen(
                navBackStackEntry = backStackEntry,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
