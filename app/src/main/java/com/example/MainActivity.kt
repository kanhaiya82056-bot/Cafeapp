package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.data.local.ServiceEntity
import com.example.data.model.UserRole
import com.example.ui.components.TopRoleBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ServiceCatalogScreen
import com.example.ui.screens.ServiceDetailScreen
import com.example.ui.screens.StaffScreen
import com.example.ui.screens.TrackerScreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryOrange
import com.example.ui.theme.VishwakarmaTheme
import com.example.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VishwakarmaTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

enum class ScreenRoute(val route: String, val label: String) {
    HOME("home", "Home"),
    CATALOG("catalog", "Services"),
    TRACKER("tracker", "Track"),
    AI("ai", "Ask AI"),
    STAFF("staff", "Queue"),
    ADMIN("admin", "Admin"),
    PROFILE("profile", "Profile"),
    SERVICE_DETAIL("service_detail", "Detail")
}

@Composable
fun MainAppScreen(viewModel: AppViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    var currentRoute by remember { mutableStateOf(ScreenRoute.HOME) }
    val selectedServiceForApply by viewModel.selectedServiceForApply.collectAsState()

    Scaffold(
        topBar = {
            TopRoleBar(
                currentRole = currentRole,
                onRoleSelect = { role ->
                    viewModel.setRole(role)
                    when (role) {
                        UserRole.CUSTOMER -> currentRoute = ScreenRoute.HOME
                        UserRole.STAFF -> currentRoute = ScreenRoute.STAFF
                        UserRole.ADMIN -> currentRoute = ScreenRoute.ADMIN
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                when (currentRole) {
                    UserRole.CUSTOMER -> {
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.HOME,
                            onClick = { currentRoute = ScreenRoute.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.CATALOG,
                            onClick = { currentRoute = ScreenRoute.CATALOG },
                            icon = { Icon(Icons.Default.GridView, contentDescription = "Services") },
                            label = { Text("Services", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.TRACKER,
                            onClick = { currentRoute = ScreenRoute.TRACKER },
                            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Track") },
                            label = { Text("Track", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.AI,
                            onClick = { currentRoute = ScreenRoute.AI },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Ask AI") },
                            label = { Text("Ask AI", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.PROFILE,
                            onClick = { currentRoute = ScreenRoute.PROFILE },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                    }

                    UserRole.STAFF -> {
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.STAFF,
                            onClick = { currentRoute = ScreenRoute.STAFF },
                            icon = { Icon(Icons.Default.Badge, contentDescription = "Queue") },
                            label = { Text("Queue", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.CATALOG,
                            onClick = { currentRoute = ScreenRoute.CATALOG },
                            icon = { Icon(Icons.Default.GridView, contentDescription = "Services") },
                            label = { Text("Catalog", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.PROFILE,
                            onClick = { currentRoute = ScreenRoute.PROFILE },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                    }

                    UserRole.ADMIN -> {
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.ADMIN,
                            onClick = { currentRoute = ScreenRoute.ADMIN },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                            label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.CATALOG,
                            onClick = { currentRoute = ScreenRoute.CATALOG },
                            icon = { Icon(Icons.Default.GridView, contentDescription = "Services") },
                            label = { Text("Catalog", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.STAFF,
                            onClick = { currentRoute = ScreenRoute.STAFF },
                            icon = { Icon(Icons.Default.Badge, contentDescription = "Queue") },
                            label = { Text("Queue", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                        NavigationBarItem(
                            selected = currentRoute == ScreenRoute.PROFILE,
                            onClick = { currentRoute = ScreenRoute.PROFILE },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navBarColors()
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentRoute,
            modifier = Modifier.padding(paddingValues),
            label = "screen_transition"
        ) { route ->
            when (route) {
                ScreenRoute.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCatalog = { currentRoute = ScreenRoute.CATALOG },
                    onNavigateToServiceDetail = { service ->
                        viewModel.selectedServiceForApply.value = service
                        currentRoute = ScreenRoute.SERVICE_DETAIL
                    },
                    onNavigateToTracker = { currentRoute = ScreenRoute.TRACKER },
                    onNavigateToAi = { currentRoute = ScreenRoute.AI }
                )

                ScreenRoute.CATALOG -> ServiceCatalogScreen(
                    viewModel = viewModel,
                    onApplyClick = { service ->
                        viewModel.selectedServiceForApply.value = service
                        currentRoute = ScreenRoute.SERVICE_DETAIL
                    }
                )

                ScreenRoute.SERVICE_DETAIL -> {
                    selectedServiceForApply?.let { service ->
                        ServiceDetailScreen(
                            service = service,
                            viewModel = viewModel,
                            onBackClick = { currentRoute = ScreenRoute.CATALOG },
                            onApplicationSubmitted = { appId ->
                                currentRoute = ScreenRoute.TRACKER
                            }
                        )
                    } ?: run {
                        currentRoute = ScreenRoute.CATALOG
                    }
                }

                ScreenRoute.TRACKER -> TrackerScreen(viewModel = viewModel)

                ScreenRoute.AI -> AiAssistantScreen(viewModel = viewModel)

                ScreenRoute.STAFF -> StaffScreen(viewModel = viewModel)

                ScreenRoute.ADMIN -> AdminScreen(viewModel = viewModel)

                ScreenRoute.PROFILE -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun navBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = SecondaryOrange,
    selectedTextColor = SecondaryOrange,
    unselectedIconColor = Color.White.copy(alpha = 0.7f),
    unselectedTextColor = Color.White.copy(alpha = 0.7f),
    indicatorColor = Color.White.copy(alpha = 0.15f)
)
