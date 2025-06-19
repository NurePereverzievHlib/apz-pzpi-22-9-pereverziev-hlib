package controllers

import (
	"ortho_vision_api/config"
	"ortho_vision_api/models"
	"sort"
	"time"

	"fmt"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

// AddSmartGlassesData - Функція для додавання нових даних смарт-окулярів
func AddSmartGlassesData(c *fiber.Ctx) error {
	// Завжди використовуємо user_id = 4
	userID := 4

	// Отримуємо дані з тіла запиту
	var data models.SmartGlassesData
	if err := c.BodyParser(&data); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"error": "Invalid data",
		})
	}

	// Перевірка наявності користувача в базі даних
	var user models.User
	if err := config.DB.First(&user, userID).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
				"error": "User not found",
			})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"error": "Failed to fetch user",
		})
	}

	// Встановлюємо дані для запису
	data.UserID = uint(userID)
	data.Timestamp = time.Now()

	// Додаємо новий запис у таблицю
	if err := config.DB.Create(&data).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"error": "Failed to create smart glasses data",
		})
	}

	return c.Status(fiber.StatusCreated).JSON(data)
}

func GetSmartGlassesStatistics(c *fiber.Ctx) error {
	userID := 4

	// Читаємо дату з query
	dateParam := c.Query("date")
	date, err := time.Parse("2006-01-02", dateParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"error": "Invalid date format. Use YYYY-MM-DD.",
		})
	}

	// Отримуємо всі записи користувача
	var data []models.SmartGlassesData
	if err := config.DB.Where("user_id = ?", userID).Find(&data).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"error": "DB error",
		})
	}

	// Сортуємо по часу
	sort.Slice(data, func(i, j int) bool {
		return data[i].Timestamp.Before(data[j].Timestamp)
	})

	// Перевірка скільки взагалі записів
	fmt.Printf("Total entries for user %d: %d\n", userID, len(data))

	// Змінні для підрахунку часу
	var timeHeadTiltExceeds45 float64
	var timeLowLight float64
	var timeHighLight float64

	var startHeadTiltExceeds45 time.Time
	var startLowLight time.Time
	var startHighLight time.Time

	// Перебір записів
	for _, entry := range data {
		// Дебаг лог
		fmt.Printf("Entry: %v | Angle: %.2f | Light: %.2f\n", entry.Timestamp, entry.PostureAngle, entry.EyeStrain)

		if entry.Timestamp.IsZero() {
			continue
		}

		// Перевірка, що дата співпадає (по роках, місяцях, днях)
		eYear, eMonth, eDay := entry.Timestamp.Date()
		dYear, dMonth, dDay := date.Date()
		if eYear != dYear || eMonth != dMonth || eDay != dDay {
			continue
		}

		// Голова нахилена > 45
		if entry.PostureAngle > 45 {
			if startHeadTiltExceeds45.IsZero() {
				startHeadTiltExceeds45 = entry.Timestamp
			}
		} else {
			if !startHeadTiltExceeds45.IsZero() {
				duration := entry.Timestamp.Sub(startHeadTiltExceeds45).Seconds()
				timeHeadTiltExceeds45 += duration
				startHeadTiltExceeds45 = time.Time{}
			}
		}

		// Освітлення < 100
		if entry.EyeStrain < 100 {
			if startLowLight.IsZero() {
				startLowLight = entry.Timestamp
			}
		} else {
			if !startLowLight.IsZero() {
				duration := entry.Timestamp.Sub(startLowLight).Seconds()
				timeLowLight += duration
				startLowLight = time.Time{}
			}
		}

		// Освітлення > 1000
		if entry.EyeStrain > 1000 {
			if startHighLight.IsZero() {
				startHighLight = entry.Timestamp
			}
		} else {
			if !startHighLight.IsZero() {
				duration := entry.Timestamp.Sub(startHighLight).Seconds()
				timeHighLight += duration
				startHighLight = time.Time{}
			}
		}
	}

	// Формуємо відповідь
	stats := fiber.Map{
		"time_head_tilt_exceeds_45": timeHeadTiltExceeds45,
		"time_low_light":            timeLowLight,
		"time_high_light":           timeHighLight,
	}

	return c.Status(fiber.StatusOK).JSON(stats)
}
