package top.abdl.space.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Filled.Home, Icons.Outlined.Home, "首页"),
    BottomNavItem(Screen.Diapers, Icons.Filled.Star, Icons.Outlined.StarOutline, "发现"),
    BottomNavItem(Screen.Notifications, Icons.Filled.Notifications, Icons.Outlined.Notifications, "通知"),
    BottomNavItem(Screen.Profile, Icons.Filled.Person, Icons.Outlined.Person, "我的")
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val authUiState by authViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Diapers.route,
        Screen.Notifications.route,
        Screen.Profile.route
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                // 悬浮药丸底栏
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = Color.Black.copy(alpha = 0.08f),
                                spotColor = Color.Black.copy(alpha = 0.12f)
                            ),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.screen.route
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 10.sp
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    if (item.screen == Screen.Profile) {
                                        val userId = authUiState.currentUser?.id
                                        if (userId != null) {
                                            navController.navigate(Screen.Profile.createRoute(userId)) {
                                                popUpTo(Screen.Home.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        } else {
                                            navController.navigate(Screen.Login.route)
                                        }
                                    } else {
                                        navController.navigate(item.screen.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
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
                fadeIn(animationSpec = tween(250)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(250)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(250)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(250)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(250)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(250)
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
                val forumViewModel: ForumViewModel = koinViewModel()
                HomeScreen(
                    forumViewModel = forumViewModel,
                    authViewModel = authViewModel,
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToCreatePost = { navController.navigate(Screen.CreatePost.route) },
                    onNavigateToPostDetail = { postId ->
                        navController.navigate(Screen.PostDetail.createRoute(postId))
                    },
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

            composable(Screen.Diapers.route) {
                val diaperViewModel: DiaperViewModel = koinViewModel()
                ForumScreen(
                    viewModel = diaperViewModel,
                    onNavigateToDiaperDetail = { id ->
                        navController.navigate(Screen.DiaperDetail.createRoute(id))
                    },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) }
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
