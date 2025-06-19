package com.example.orthovision

import DoctorDetailScreen
import DoctorsViewModel
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.orthovision.data.TokenManager
import com.example.orthovision.data.api.RetrofitInstance
import com.example.orthovision.data.model.AppointmentTime
import com.example.orthovision.data.model.Doctor
import com.example.orthovision.data.remote.repository.*
import com.example.orthovision.ui.screens.doctors.DoctorsScreen
import com.example.orthovision.ui.screens.doctors.DoctorsViewModelFactory
import com.example.orthovision.ui.screens.home.HomeViewModel
import com.example.orthovision.ui.screens.home.HomeViewModelFactory
import com.example.orthovision.ui.screens.login.LoginScreen
import com.example.orthovision.ui.screens.login.LoginViewModel
import com.example.orthovision.ui.screens.login.LoginViewModelFactory
import com.example.orthovision.ui.screens.home.MainScreen
import com.example.orthovision.ui.screens.medicalRecord.MedicalRecordViewModel
import com.example.orthovision.ui.screens.medicalRecord.MedicalRecordViewModelFactory
import com.example.orthovision.ui.screens.register.RegisterScreen
import com.example.orthovision.ui.screens.register.RegisterViewModel
import com.example.orthovision.ui.screens.register.RegisterViewModelFactory
import com.example.orthovision.ui.screens.appointments.AppointmentsViewModel
import com.example.orthovision.ui.screens.appointments.AppointmentsViewModelFactory
import com.example.orthovision.ui.screens.appointments.PendingAppointmentsScreen
import com.example.orthovision.ui.theme.OrthoVisionTheme
import com.example.orthovision.ui.screens.smartGlasses.SmartGlassesStatisticsScreenFactory
import com.example.orthovision.ui.screens.smartGlasses.SmartGlassesStatisticsViewModel
import com.example.orthovision.ui.statistics.SmartGlassesStatisticsScreen
import ui.screens.MedicalRecordsScreen

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(applicationContext)

        setContent {
            OrthoVisionTheme {
                val navController = rememberNavController()

                // Ініціалізація всіх репозиторіїв і фабрик
                val loginFactory = LoginViewModelFactory(AuthRepository(RetrofitInstance.authApi))
                val registerFactory = RegisterViewModelFactory(AuthRepository(RetrofitInstance.authApi))

                val clinicRepository = ClinicRepository(RetrofitInstance.clinicApi)
                val homeFactory = HomeViewModelFactory(clinicRepository)

                val doctorApi = RetrofitInstance.doctorApi
                val doctorsRepository = DoctorsRepository(doctorApi)
                val doctorsFactory = DoctorsViewModelFactory(doctorsRepository)

                val medicalRecordApi = RetrofitInstance.medicalRecordApi
                val medicalRecordRepository = MedicalRecordRepository(medicalRecordApi)
                val medicalRecordViewModelFactory = MedicalRecordViewModelFactory(medicalRecordRepository)

                val appointmentsRepository = AppointmentsRepository(RetrofitInstance.appointmentApi)
                val appointmentsFactory = AppointmentsViewModelFactory(appointmentsRepository)

                val glassesRepository = SmartGlassesStatisticsRepository(RetrofitInstance.smartGlassesApi)
                val glassesFactory = SmartGlassesStatisticsScreenFactory(glassesRepository)

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        val loginViewModel: LoginViewModel = viewModel(factory = loginFactory)
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onRegisterClick = {
                                navController.navigate("register")
                            }
                        )
                    }

                    composable("register") {
                        val registerViewModel: RegisterViewModel = viewModel(factory = registerFactory)
                        RegisterScreen(
                            viewModel = registerViewModel,
                            onRegisterSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("main") {
                        val homeViewModel: HomeViewModel = viewModel(factory = homeFactory)
                        MainScreen(
                            viewModel = homeViewModel,
                            onClinicClick = { clinicId ->
                                navController.navigate("doctors/$clinicId")
                            },
                            onNavigateToCard = {
                                navController.navigate("card")
                            },
                            onNavigateToRecords = {
                                navController.navigate("records")
                            },
                            onNavigateToGlasses = {
                                navController.navigate("glasses")
                            }
                        )
                    }

                    composable("doctors/{clinicId}") { backStackEntry ->
                        val clinicId = backStackEntry.arguments?.getString("clinicId") ?: ""
                        val doctorsViewModel: DoctorsViewModel = viewModel(factory = doctorsFactory)
                        DoctorsScreen(
                            clinicId = clinicId,
                            viewModel = doctorsViewModel,
                            onDoctorClick = { doctorId ->
                                val patientId = TokenManager.getUserId()
                                navController.navigate("doctor_detail/$doctorId/$clinicId/$patientId")
                            }
                        )
                    }

                    composable(
                        route = "doctor_detail/{doctorId}/{clinicId}/{patientId}",
                        arguments = listOf(
                            navArgument("doctorId") { type = NavType.IntType },
                            navArgument("clinicId") { type = NavType.IntType },
                            navArgument("patientId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val context = LocalContext.current

                        val doctorId = backStackEntry.arguments?.getInt("doctorId") ?: 0
                        val clinicId = backStackEntry.arguments?.getInt("clinicId") ?: 0
                        val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1

                        val doctor by produceState<Doctor?>(initialValue = null) {
                            value = doctorsRepository.getDoctorById(doctorId.toString())
                        }

                        val appointments by produceState<List<AppointmentTime>>(initialValue = emptyList()) {
                            value = appointmentsRepository.getAppointmentsByDoctor(doctorId, clinicId) ?: emptyList()
                        }

                        doctor?.let {
                            DoctorDetailScreen(
                                doctor = it,
                                clinicId = clinicId,
                                patientId = patientId,
                                onAppointmentCreated = {
                                    Toast.makeText(context, "Вітаю, ви записались на прийом!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    composable("card") {
                        val medicalRecordViewModel: MedicalRecordViewModel = viewModel(factory = medicalRecordViewModelFactory)
                        val userId = TokenManager.getUserId()
                        MedicalRecordsScreen(
                            viewModel = medicalRecordViewModel,
                            userId = userId
                        )
                    }

                    composable("records") {
                        val patientId = TokenManager.getUserId()
                        val appointmentsViewModel: AppointmentsViewModel = viewModel(factory = appointmentsFactory)
                        PendingAppointmentsScreen(
                            viewModel = appointmentsViewModel,
                            patientId = patientId,
                            onAppointmentCancelled = {
                                Toast.makeText(this@MainActivity, "Запис скасовано", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    composable("glasses") {
                        val glassesViewModel: SmartGlassesStatisticsViewModel = viewModel(factory = glassesFactory)
                        SmartGlassesStatisticsScreen(viewModel = glassesViewModel)
                    }
                }
            }
        }
    }
}
