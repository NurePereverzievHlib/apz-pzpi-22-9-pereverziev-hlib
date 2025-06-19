import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orthovision.data.model.Doctor
import com.example.orthovision.data.remote.api.DoctorApi
import com.example.orthovision.data.remote.repository.DoctorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorsViewModel(
    private val repository: DoctorsRepository
) : ViewModel() {

    val doctors = mutableStateOf<List<Doctor>>(emptyList())

    fun loadDoctorsByClinic(clinicId: String) {
        viewModelScope.launch {
            val fetchedDoctors = repository.getDoctorsByClinic(clinicId)
            doctors.value = fetchedDoctors
        }
    }
}

