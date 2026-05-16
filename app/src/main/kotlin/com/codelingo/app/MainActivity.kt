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
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.codelingo.app.ui.screens.AchievementsScreen
import com.codelingo.app.ui.screens.CoursePathScreen
import com.codelingo.app.ui.screens.CoursesScreen
import com.codelingo.app.ui.screens.HomeScreen
import com.codelingo.app.ui.screens.LessonScreen
import com.codelingo.app.ui.screens.NotFoundScreen
import com.codelingo.app.ui.screens.ProfileScreen
import com.codelingo.app.ui.theme.Background
import com.codelingo.app.ui.theme.CodeLingoTheme
import com.codelingo.app.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as CodeLingoApp
        setContent {
            CodeLingoTheme {
                CodeLingoAppContent(
                    gameViewModel = viewModel(factory = GameViewModel.Factory(app.gameRepository)),
                    courseRepository = app.courseRepository,
                )
            }
        }
    }
}

@Composable
fun CodeLingoAppContent(
    gameViewModel: GameViewModel,
    courseRepository: com.codelingo.app.data.CourseRepository,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val gameState by gameViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
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
                        ProfileScreen(state = gameState, courseRepository = courseRepository)
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

        if (shouldShowBottomBar(currentRoute)) {
            CodeLingoBottomBar(
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
