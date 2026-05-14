package com.example.channapatnatoys.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.channapatnatoys.ui.components.ChannapatnaTopBar
import com.example.channapatnatoys.ui.screens.catalog.CatalogScreen
import com.example.channapatnatoys.ui.screens.home.HomeScreen
import com.example.channapatnatoys.ui.screens.howmade.HowMadeScreen
import com.example.channapatnatoys.ui.screens.meetmaker.MeetMakerScreen
import com.example.channapatnatoys.ui.screens.verify.VerifyScreen
import com.example.channapatnatoys.ui.theme.CreamWhite
import com.example.channapatnatoys.ui.theme.DarkBg
import com.example.channapatnatoys.ui.theme.TurmericYellow

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Verify : Screen("verify?toyId={toyId}", "Verify", Icons.Filled.Search) {
        fun createRoute(toyId: String) = "verify?toyId=$toyId"
    }
    object HowMade : Screen("how_made", "How It's Made", Icons.Filled.Info)
    object MeetMaker : Screen("meet_maker", "Meet Maker", Icons.Filled.LocationOn)
    object Catalog : Screen("catalog", "Catalog", Icons.Filled.List)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Verify,
    Screen.HowMade,
    Screen.MeetMaker,
    Screen.Catalog
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            ChannapatnaTopBar()
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkBg,
                contentColor = TurmericYellow
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    // Strip arguments from route for comparison
                    val baseRoute = screen.route.substringBefore("?")
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = null,
                        selected = currentDestination?.hierarchy?.any { it.route?.startsWith(baseRoute) == true } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TurmericYellow,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = CreamWhite
                        ),
                        onClick = {
                            if (screen == Screen.Home) {
                                // For Home: always pop everything back to home
                                navController.popBackStack(
                                    route = "home",
                                    inclusive = false
                                )
                            } else {
                                navController.navigate(baseRoute) {
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() + slideInVertically { it / 2 } }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToVerify = {
                        navController.navigate("verify") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = Screen.Verify.route,
                arguments = listOf(navArgument("toyId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val toyId = backStackEntry.arguments?.getString("toyId")
                VerifyScreen(toyIdArg = toyId)
            }
            composable(Screen.HowMade.route) {
                HowMadeScreen()
            }
            composable(Screen.MeetMaker.route) {
                MeetMakerScreen()
            }
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onNavigateToVerify = { toyId ->
                        navController.navigate(Screen.Verify.createRoute(toyId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
