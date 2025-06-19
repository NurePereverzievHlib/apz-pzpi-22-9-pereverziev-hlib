package controllers

import (
	"log"
	"ortho_vision_api/models"

	"strconv"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

// CreateDisease - створення нового запису про хворобу
func CreateDisease(c *fiber.Ctx) error {
	var disease models.Disease

	// Отримуємо дані з тіла запиту
	if err := c.BodyParser(&disease); err != nil {
		return c.Status(fiber.StatusBadRequest).SendString("Invalid request")
	}

	// Перевіряємо з'єднання з базою даних
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	// Створюємо запис
	if err := db.Create(&disease).Error; err != nil {
		log.Println("Error saving disease:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error saving disease",
		})
	}

	return c.Status(fiber.StatusCreated).JSON(fiber.Map{
		"message": "Disease created successfully",
		"data":    disease,
	})
}

// DeleteDisease - видалення запису про хворобу
func DeleteDisease(c *fiber.Ctx) error {
	diseaseID := c.Params("id")

	// Перевіряємо з'єднання з базою даних
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	// Видаляємо запис
	if err := db.Delete(&models.Disease{}, diseaseID).Error; err != nil {
		log.Println("Error deleting disease:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error deleting disease",
		})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "Disease deleted successfully",
	})
}

// UpdateDisease - оновлення запису про хворобу
func UpdateDisease(c *fiber.Ctx) error {
	diseaseID := c.Params("id")
	var updatedData models.Disease

	// Отримуємо дані з тіла запиту
	if err := c.BodyParser(&updatedData); err != nil {
		return c.Status(fiber.StatusBadRequest).SendString("Invalid request")
	}

	// Перевіряємо з'єднання з базою даних
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	// Оновлюємо запис
	if err := db.Model(&models.Disease{}).Where("id = ?", diseaseID).Updates(updatedData).Error; err != nil {
		log.Println("Error updating disease:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error updating disease",
		})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "Disease updated successfully",
	})
}

// GetMedicalRecord - отримання всіх хвороб для призначення (appointment) за його ID
func GetUserMedicalRecord(c *fiber.Ctx) error {
	// Отримуємо userID з path параметрів
	userIDStr := c.Params("userID")
	if userIDStr == "" {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "userID parameter is required",
		})
	}

	// Конвертація userID в int
	userID, err := strconv.Atoi(userIDStr)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "userID must be a valid integer",
		})
	}

	// Отримуємо DB з контексту Fiber
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	// Шукаємо всі призначення (appointments) для цього користувача
	var appointments []models.Appointment
	if err := db.Where("patient_id = ?", userID).Find(&appointments).Error; err != nil {
		log.Println("Error fetching appointments:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error fetching appointments",
		})
	}

	if len(appointments) == 0 {
		return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
			"message": "No appointments found for the user",
		})
	}

	// Збираємо ID всіх appointments
	appointmentIDs := make([]uint, len(appointments))
	for i, appt := range appointments {
		appointmentIDs[i] = appt.ID
	}

	// Знаходимо всі хвороби, які прив’язані до цих призначень
	var diseases []models.Disease
	if err := db.Where("appointment_id IN ?", appointmentIDs).Find(&diseases).Error; err != nil {
		log.Println("Error fetching diseases:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error fetching diseases",
		})
	}

	// Групуємо хвороби за appointmentID
	diseasesByAppointment := make(map[uint][]models.Disease)
	for _, disease := range diseases {
		diseasesByAppointment[disease.AppointmentID] = append(diseasesByAppointment[disease.AppointmentID], disease)
	}

	// Формуємо відповідь - список об'єктів, де кожен appointment з його хворобами
	type AppointmentWithDiseases struct {
		Appointment models.Appointment `json:"appointment"`
		Diseases    []models.Disease   `json:"diseases"`
	}

	responseData := make([]AppointmentWithDiseases, 0, len(appointments))
	for _, appt := range appointments {
		responseData = append(responseData, AppointmentWithDiseases{
			Appointment: appt,
			Diseases:    diseasesByAppointment[appt.ID],
		})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "Medical records retrieved successfully",
		"data":    responseData,
	})
}

// GetDiseasesByAppointment - отримання хвороб за appointment_id із query параметра
func GetDiseasesByAppointment(c *fiber.Ctx) error {
	appointmentIDStr := c.Query("appointment_id")
	if appointmentIDStr == "" {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "appointment_id query parameter is required",
		})
	}

	appointmentID, err := strconv.Atoi(appointmentIDStr)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "appointment_id must be a valid integer",
		})
	}

	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	var diseases []models.Disease
	if err := db.Where("appointment_id = ?", appointmentID).Find(&diseases).Error; err != nil {
		log.Println("Error fetching diseases by appointment_id:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error fetching diseases",
		})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "Diseases retrieved successfully",
		"data":    diseases,
	})
}
