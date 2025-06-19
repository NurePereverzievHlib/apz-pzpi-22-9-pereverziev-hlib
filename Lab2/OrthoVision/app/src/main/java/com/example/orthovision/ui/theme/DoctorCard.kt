import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.orthovision.data.model.Doctor

@Composable
fun DoctorCard(doctor: Doctor, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(doctor.id) },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val avatarPainter = rememberAsyncImagePainter(
                model = doctor.avatarUrl ?: "https://via.placeholder.com/64"
            )

            Image(
                painter = avatarPainter,
                contentDescription = "Аватар лікаря",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = "ID: ${doctor.id}", style = MaterialTheme.typography.caption)
                Text(text = doctor.name ?: "Ім'я не вказано", style = MaterialTheme.typography.h6)
                Text(text = "Спеціальність: ${doctor.specialization ?: "невідома"}")
            }
        }
    }
}
