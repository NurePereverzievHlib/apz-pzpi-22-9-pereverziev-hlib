package controllers

import (
	"log"
	"time"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"

	"ortho_vision_api/models"
)

// CreateAppointment - створення запису на прийом
func CreateAppointment(c *fiber.Ctx) error {
	var appointment models.Appointment

	// Отримуємо дані з тіла запиту
	if err := c.BodyParser(&appointment); err != nil {
		return c.Status(fiber.StatusBadRequest).SendString("Invalid request")
	}

	// Перевіряємо, чи є доступний час
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	// Перевіряємо доступний час
	var availableTime models.AppointmentTimes
	err := db.Where("id = ? AND is_booked = ?", appointment.AppointmentTimeID, false).First(&availableTime).Error
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
				"message": "The selected time is not available",
			})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error checking available time",
		})
	}

	// Якщо час доступний, змінюємо його статус на "booked"
	availableTime.IsBooked = true
	if err := db.Save(&availableTime).Error; err != nil {
		log.Println("Error updating appointment time status:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error updating appointment time status",
		})
	}

	// Встановлюємо статус прийому на "pending" і додаємо новий запис
	appointment.Status = "pending"
	if err := db.Create(&appointment).Error; err != nil {
		log.Println("Error saving appointment:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error saving appointment",
		})
	}

	// Відповідь про успішний запис
	return c.Status(fiber.StatusCreated).JSON(fiber.Map{
		"message": "Appointment created successfully",
		"data":    appointment,
	})
}

// DeleteAppointment - видалення запису на прийом
func DeleteAppointment(c *fiber.Ctx) error {
	// Отримуємо ID запису на прийом з параметрів запиту
	appointmentID := c.Params("id")

	// Перевіряємо з'єднання з базою даних
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	// Знаходимо запис на прийом
	var appointment models.Appointment
	err := db.First(&appointment, "id = ?", appointmentID).Error
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
				"message": "Appointment not found",
			})
		}
		log.Println("Error finding appointment:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error finding appointment",
		})
	}

	// Оновлюємо статус часу на "available" (is_booked = false)
	var availableTime models.AppointmentTimes
	err = db.First(&availableTime, "id = ?", appointment.AppointmentTimeID).Error
	if err != nil {
		log.Println("Error finding appointment time:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error finding appointment time",
		})
	}

	availableTime.IsBooked = false
	if err := db.Save(&availableTime).Error; err != nil {
		log.Println("Error updating appointment time status:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error updating appointment time status",
		})
	}

	// Видаляємо запис на прийом
	if err := db.Delete(&appointment).Error; err != nil {
		log.Println("Error deleting appointment:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error deleting appointment",
		})
	}

	// Відповідь про успішне видалення
	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "Appointment deleted successfully",
	})
}

// GetAppointmentsByPatientID - отримання всіх записів на прийом для конкретного пацієнта
func GetAppointmentsByPatientID(c *fiber.Ctx) error {
	// Отримуємо ID пацієнта з параметрів запиту
	patientID := c.Params("patientID")

	// Перевіряємо з'єднання з базою даних
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	// Знаходимо всі записи для пацієнта
	var appointments []models.Appointment
	err := db.Where("patient_id = ?", patientID).Find(&appointments).Error
	if err != nil {
		log.Println("Error finding appointments:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error finding appointments",
		})
	}

	// Якщо записів немає, повертаємо відповідь про їх відсутність
	if len(appointments) == 0 {
		return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
			"message": "No appointments found for the specified patient",
		})
	}

	// Повертаємо знайдені записи
	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "Appointments retrieved successfully",
		"data":    appointments,
	})
}

func GetAppointmentsByDoctor(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	doctorID := c.Params("id")

	// Спрощена структура для відповіді
	type AppointmentResponse struct {
		ID            uint      `json:"id"`
		Status        string    `json:"status"`
		Reason        string    `json:"reason"`
		CreatedAt     time.Time `json:"created_at"`
		PatientID     uint      `json:"patient_id"`
		PatientName   string    `json:"patient_name"`
		AvailableTime time.Time `json:"available_time"`
	}

	var results []AppointmentResponse

	// Джойнимо appointment_times та users
	err := db.Table("appointments as a").
		Select(`
			a.id, a.status, a.reason, a.created_at,
			a.patient_id, u.name as patient_name,
			at.available_time
		`).
		Joins("JOIN appointment_times at ON a.appointment_time_id = at.id").
		Joins("JOIN users u ON a.patient_id = u.id").
		Where("at.doctor_id = ?", doctorID).
		Order("at.available_time ASC").
		Scan(&results).Error

	if err != nil {
		log.Println("Error fetching appointments:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error fetching appointments",
		})
	}

	return c.JSON(fiber.Map{
		"message":      "Appointments retrieved successfully",
		"appointments": results,
	})
}

func UpdateAppointment(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	appointmentID := c.Params("appointment_id")
	if appointmentID == "" {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "Appointment ID is required",
		})
	}

	var appointment models.Appointment
	if err := db.First(&appointment, "id = ?", appointmentID).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
				"message": "Appointment not found",
			})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error fetching appointment",
		})
	}

	type UpdateAppointmentInput struct {
		Status            string `json:"status"`
		Reason            string `json:"reason"`
		AppointmentTimeID *uint  `json:"appointment_time_id"`
	}
	var input UpdateAppointmentInput
	if err := c.BodyParser(&input); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "Invalid request data",
		})
	}

	validStatuses := map[string]bool{
		"pending":   true,
		"confirmed": true,
		"cancelled": true,
		"completed": true,
	}
	if input.Status != "" && !validStatuses[input.Status] {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "Invalid status value",
		})
	}

	if input.Status != "" {
		appointment.Status = input.Status
	}
	if input.Reason != "" {
		appointment.Reason = input.Reason
	}
	if input.AppointmentTimeID != nil {
		var newTime models.AppointmentTimes
		if err := db.First(&newTime, "id = ?", *input.AppointmentTimeID).Error; err != nil {
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
				"message": "Invalid appointment_time_id",
			})
		}
		appointment.AppointmentTimeID = *input.AppointmentTimeID
	}

	if err := db.Save(&appointment).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error updating appointment",
		})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "Appointment updated successfully",
		"data":    appointment,
	})
}
