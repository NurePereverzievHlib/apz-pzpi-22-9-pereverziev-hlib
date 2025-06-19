import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.orthovision.data.remote.repository.AuthRepository
import com.example.orthovision.data.api.RetrofitInstance
import com.example.orthovision.ui.screens.register.RegisterScreen
import com.example.orthovision.ui.screens.register.RegisterViewModel
import com.example.orthovision.ui.screens.login.LoginScreen
import com.example.orthovision.ui.screens.login.LoginViewModel
import com.example.orthovision.ui.screens.login.LoginViewModelFactory
import com.example.orthovision.ui.screens.register.RegisterViewModelFactory

@Composable
fun OrthoVisionApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val repository = AuthRepository(RetrofitInstance.authApi)
            val factory = LoginViewModelFactory(repository)
            val viewModel: LoginViewModel = viewModel(factory = factory)

            LoginScreen(viewModel = viewModel, {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }) {
                navController.navigate("register")
            }
        }

        composable("register") {
            val repository = AuthRepository(RetrofitInstance.authApi)
            val factory = RegisterViewModelFactory(repository)
            val viewModel: RegisterViewModel = viewModel(factory = factory)

            RegisterScreen(viewModel = viewModel) {
                navController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }

    }


}
