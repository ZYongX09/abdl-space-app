package top.abdl.space.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import top.abdl.space.ui.auth.AuthViewModel
import top.abdl.space.ui.auth.ForgotPasswordScreen
import top.abdl.space.ui.auth.LoginScreen
import top.abdl.space.ui.auth.RegisterScreen
import top.abdl.space.ui.diapers.DiaperDetailScreen
import top.abdl.space.ui.diapers.DiaperListScreen
import top.abdl.space.ui.diapers.DiaperViewModel
import top.abdl.space.ui.diapers.SubmitRatingScreen
import top.abdl.space.ui.forum.CreatePostScreen
import top.abdl.space.ui.forum.ForumScreen
import top.abdl.space.ui.forum.ForumViewModel
import top.abdl.space.ui.forum.PostDetailScreen
import top.abdl.space.ui.home.HomeScreen
import top.abdl.space.ui.notifications.NotificationScreen
import top.abdl.space.ui.splash.SplashScreen
import top.abdl.space.ui.notifications.NotificationViewModel
import top.abdl.space.ui.profile.EditProfileScreen
import top.abdl.space.ui.profile.ProfileScreen
import top.abdl.space.ui.profile.ProfileViewModel
import top.abdl.space.ui.search.SearchScreen
import top.abdl.space.ui.search.SearchViewModel
import top.abdl.space.ui.settings.AboutScreen
import top.abdl.space.ui.settings.SettingsScreen
import top.abdl.space.ui.settings.SettingsViewModel

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object Forum : Screen("forum")
    data object PostDetail : Screen("post/{postId}") {
        fun createRoute(postId: Int) = "post/$postId"
    }
    data object CreatePost : Screen("create_post")
    data object Diapers : Screen("diapers")
    data object DiaperDetail : Screen("diaper/{diaperId}") {
        fun createRoute(diaperId: Int) = "diaper/$diaperId"
    }
    data object SubmitRating : Screen("submit_rating/{diaperId}") {
        fun createRoute(diaperId: Int) = "submit_rating/$diaperId"
    }
    data object Search : Screen("search")
    data object Notifications : Screen("notifications")
    data object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: Int) = "profile/$userId"
    }
    data object EditProfile : Screen("edit_profile")
    data object Settings : Screen("settings")
    data object About : Screen("about")
}

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Default.Home, "首页"),
    BottomNavItem(Screen.Forum, Icons.Default.Search, "动态"),
    BottomNavItem(Screen.Diapers, Icons.Default.Search, "纸尿裤"),
    BottomNavItem(Screen.Notifications, Icons.Default.Notifications, "通知"),
    BottomNavItem(Screen.Settings, Icons.Default.Settings, "设置")
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val authUiState by authViewModel.uiState.collectAsState()

    val startDestination = if (authUiState.isLoggedIn) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Forum.route,
        Screen.Diapers.route,
        Screen.Notifications.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        val destination = if (authUiState.isLoggedIn) {
                            Screen.Home.route
                        } else {
                            Screen.Login.route
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onResetSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Forum.route) {
                val forumViewModel: ForumViewModel = koinViewModel()
                ForumScreen(
                    viewModel = forumViewModel,
                    onNavigateToPostDetail = { postId ->
                        navController.navigate(Screen.PostDetail.createRoute(postId))
                    },
                    onNavigateToCreatePost = {
                        navController.navigate(Screen.CreatePost.route)
                    },
                    onNavigateToProfile = { userId ->
                        navController.navigate(Screen.Profile.createRoute(userId))
                    }
                )
            }

            composable(
                route = Screen.PostDetail.route,
                arguments = listOf(navArgument("postId") { type = NavType.IntType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
                val forumViewModel: ForumViewModel = koinViewModel()
                PostDetailScreen(
                    viewModel = forumViewModel,
                    postId = postId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProfile = { userId ->
                        navController.navigate(Screen.Profile.createRoute(userId))
                    }
                )
            }

            composable(Screen.CreatePost.route) {
                val forumViewModel: ForumViewModel = koinViewModel()
                CreatePostScreen(
                    viewModel = forumViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPostCreated = { navController.popBackStack() }
                )
            }

            composable(Screen.Diapers.route) {
                val diaperViewModel: DiaperViewModel = koinViewModel()
                DiaperListScreen(
                    viewModel = diaperViewModel,
                    onNavigateToDiaperDetail = { diaperId ->
                        navController.navigate(Screen.DiaperDetail.createRoute(diaperId))
                    }
                )
            }

            composable(
                route = Screen.DiaperDetail.route,
                arguments = listOf(navArgument("diaperId") { type = NavType.IntType })
            ) { backStackEntry ->
                val diaperId = backStackEntry.arguments?.getInt("diaperId") ?: return@composable
                val diaperViewModel: DiaperViewModel = koinViewModel()
                DiaperDetailScreen(
                    viewModel = diaperViewModel,
                    diaperId = diaperId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSubmitRating = { id ->
                        navController.navigate(Screen.SubmitRating.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.SubmitRating.route,
                arguments = listOf(navArgument("diaperId") { type = NavType.IntType })
            ) { backStackEntry ->
                val diaperId = backStackEntry.arguments?.getInt("diaperId") ?: return@composable
                val diaperViewModel: DiaperViewModel = koinViewModel()
                SubmitRatingScreen(
                    viewModel = diaperViewModel,
                    diaperId = diaperId,
                    onNavigateBack = { navController.popBackStack() },
                    onRatingSubmitted = { navController.popBackStack() }
                )
            }

            composable(Screen.Search.route) {
                val searchViewModel: SearchViewModel = koinViewModel()
                SearchScreen(
                    viewModel = searchViewModel,
                    onNavigateToPostDetail = { postId ->
                        navController.navigate(Screen.PostDetail.createRoute(postId))
                    },
                    onNavigateToDiaperDetail = { diaperId ->
                        navController.navigate(Screen.DiaperDetail.createRoute(diaperId))
                    },
                    onNavigateToProfile = { userId ->
                        navController.navigate(Screen.Profile.createRoute(userId))
                    }
                )
            }

            composable(Screen.Notifications.route) {
                val notificationViewModel: NotificationViewModel = koinViewModel()
                NotificationScreen(
                    viewModel = notificationViewModel,
                    onNavigateToPost = { postId ->
                        navController.navigate(Screen.PostDetail.createRoute(postId))
                    }
                )
            }

            composable(
                route = Screen.Profile.route,
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
                val profileViewModel: ProfileViewModel = koinViewModel()
                ProfileScreen(
                    viewModel = profileViewModel,
                    userId = userId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEditProfile = {
                        navController.navigate(Screen.EditProfile.route)
                    },
                    onNavigateToProfile = { id ->
                        navController.navigate(Screen.Profile.createRoute(id))
                    }
                )
            }

            composable(Screen.EditProfile.route) {
                val profileViewModel: ProfileViewModel = koinViewModel()
                EditProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onProfileUpdated = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = koinViewModel()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAbout = {
                        navController.navigate(Screen.About.route)
                    }
                )
            }

            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
