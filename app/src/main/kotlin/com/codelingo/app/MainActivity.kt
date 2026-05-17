package com.codelingo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codelingo.app.navigation.Routes
import com.codelingo.app.ui.components.CodeLingoBottomBar
import com.codelingo.app.ui.components.ContentWidth
import com.codelingo.app.ui.components.shouldShowBottomBar
import com.codelingo.app.ui.screens.AuthScreen
import com.codelingo.app.ui.screens.AchievementsScreen
import com.codelingo.app.ui.screens.CoursePathScreen
import com.codelingo.app.ui.screens.CoursesScreen
import com.codelingo.app.ui.screens.HomeScreen
import com.codelingo.app.ui.screens.LessonScreen
import com.codelingo.app.ui.screens.NotFoundScreen
import com.codelingo.app.ui.screens.ProfileScreen
import com.codelingo.app.ui.screens.SettingsScreen
import com.codelingo.app.ui.theme.Background
import com.codelingo.app.ui.theme.CodeLingoTheme
import com.codelingo.app.viewmodel.AuthViewModel
import com.codelingo.app.viewmodel.GameViewModel
import com.codelingo.app.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as CodeLingoApp
        setContent {
            CodeLingoAppContent(app = app)
        }
    }
}

@Composable
fun CodeLingoAppContent(app: CodeLingoApp) {
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModel.Factory(app.gameRepository))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(app.settingsRepository))
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(
            app.authRepository,
            app.progressSyncRepository,
            app.courseRepository,
        ),
    )
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    var skipAuth by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentUser?.id) {
        if (currentUser != null) {
            app.progressSyncRepository.pullAndMerge()
            app.courseRepository.refreshFromRemote()
            app.progressSyncRepository.startRealtimeSync()
        }
    }

    CodeLingoTheme(darkTheme = isDarkTheme) {
        if (authViewModel.isConfigured && currentUser == null && !skipAuth) {
            AuthScreen(
                authViewModel = authViewModel,
                onAuthenticated = { skipAuth = false },
                onContinueOffline = { skipAuth = true },
            )
        } else {
            CodeLingoAppContentInner(
                gameViewModel = gameViewModel,
                settingsViewModel = settingsViewModel,
                courseRepository = app.courseRepository,
                authViewModel = authViewModel,
            )
        }
    }
}

@Composable
private fun CodeLingoAppContentInner(
    gameViewModel: GameViewModel,
    settingsViewModel: SettingsViewModel,
    courseRepository: com.codelingo.app.data.CourseRepository,
    authViewModel: AuthViewModel,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val gameState by gameViewModel.state.collectAsState()
    val showBottomBar = shouldShowBottomBar(currentRoute)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(
                    if (!showBottomBar) Modifier.navigationBarsPadding()
                    else Modifier,
                ),
            contentAlignment = Alignment.TopCenter,
        ) {
            ContentWidth {
                NavHost(
                    navController = navController,
                    startDestination = Routes.HOME,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    composable(Routes.HOME) {
                        HomeScreen(
                            state = gameState,
                            courseRepository = courseRepository,
                            onCourseClick = { navController.navigate(Routes.course(it)) },
                        )
                    }
                    composable(Routes.COURSES) {
                        CoursesScreen(
                            state = gameState,
                            courseRepository = courseRepository,
                            onCourseClick = { navController.navigate(Routes.course(it)) },
                        )
                    }
                    composable(
                        route = Routes.COURSE,
                        arguments = listOf(navArgument("courseId") { type = NavType.StringType }),
                    ) { entry ->
                        val courseId = entry.arguments?.getString("courseId") ?: return@composable
                        CoursePathScreen(
                            courseId = courseId,
                            state = gameState,
                            courseRepository = courseRepository,
                            onBack = { navController.popBackStack() },
                            onLessonClick = { cId, lId ->
                                navController.navigate(Routes.lesson(cId, lId))
                            },
                        )
                    }
                    composable(
                        route = Routes.LESSON,
                        arguments = listOf(
                            navArgument("courseId") { type = NavType.StringType },
                            navArgument("lessonId") { type = NavType.StringType },
                        ),
                    ) { entry ->
                        val courseId = entry.arguments?.getString("courseId") ?: return@composable
                        val lessonId = entry.arguments?.getString("lessonId") ?: return@composable
                        LessonScreen(
                            courseId = courseId,
                            lessonId = lessonId,
                            state = gameState,
                            courseRepository = courseRepository,
                            gameViewModel = gameViewModel,
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable(Routes.ACHIEVEMENTS) {
                        AchievementsScreen(state = gameState)
                    }
                    composable(Routes.PROFILE) {
                        ProfileScreen(
                            state = gameState,
                            courseRepository = courseRepository,
                            authViewModel = authViewModel,
                            onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        )
                    }
                    composable(Routes.SETTINGS) {
                        SettingsScreen(
                            settingsViewModel = settingsViewModel,
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable("not_found") {
                        NotFoundScreen(onGoHome = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        })
                    }
                }
            }
        }

        if (showBottomBar) {
            CodeLingoBottomBar(
                modifier = Modifier.navigationBarsPadding(),
                currentRoute = currentRoute?.substringBefore("/"),
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
