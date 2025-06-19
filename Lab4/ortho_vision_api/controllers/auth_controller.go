package controllers

import (
	"fmt"
	"log"
	"time"

	"ortho_vision_api/models"

	"github.com/gofiber/fiber/v2"
	"github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

const JwtSecret = "super_mega_secret_token_228"

// RegisterUser - реєстрація нового користувача
func RegisterUser(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	var user models.User
	if err := c.BodyParser(&user); err != nil {
		log.Println("BodyParser error:", err)
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "Invalid request data",
		})
	}

	var existingUser models.User
	if err := db.Where("email = ?", user.Email).First(&existingUser).Error; err == nil {
		log.Println("User with this email already exists")
		return c.Status(fiber.StatusConflict).JSON(fiber.Map{
			"message": "User with this email already exists",
		})
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(user.Password), bcrypt.DefaultCost)
	if err != nil {
		log.Println("Password hashing error:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error hashing password",
		})
	}

	user.PasswordHash = string(hashedPassword)
	user.Password = ""

	if err := db.Create(&user).Error; err != nil {
		log.Println("Database save error:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error saving user to database",
		})
	}

	user.PasswordHash = ""
	return c.Status(fiber.StatusCreated).JSON(fiber.Map{
		"message": "User registered successfully",
		"user":    user,
	})
}

// LoginUser - авторизація користувача з JWT
func LoginUser(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	var loginData struct {
		Email    string `json:"email"`
		Password string `json:"password"`
	}

	if err := c.BodyParser(&loginData); err != nil {
		log.Println("BodyParser error:", err)
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "Invalid request data",
		})
	}

	var user models.User
	if err := db.Where("email = ?", loginData.Email).First(&user).Error; err != nil {
		log.Println("User not found:", err)
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"message": "Invalid email or password",
		})
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(loginData.Password)); err != nil {
		log.Println("Password mismatch")
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"message": "Invalid email or password",
		})
	}

	// Генеруємо JWT
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"user_id": user.ID,
		"email":   user.Email,
		"exp":     time.Now().Add(time.Hour * 72).Unix(),
	})

	tokenString, err := token.SignedString([]byte(JwtSecret))
	if err != nil {
		log.Println("JWT Signing error:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Could not login",
		})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "Login successful",
		"token":   tokenString,
		"user": map[string]interface{}{
			"id":     user.ID,
			"email":  user.Email,
			"name":   user.Name,
			"avatar": user.AvatarURL,
			"role":   user.Role,
		},
	})
}

// JwtProtected - middleware для перевірки JWT
func JwtProtected() fiber.Handler {
	return func(c *fiber.Ctx) error {
		authHeader := c.Get("Authorization")
		if authHeader == "" {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{"message": "Missing or malformed JWT"})
		}

		tokenString := ""
		if len(authHeader) > 7 && authHeader[:7] == "Bearer " {
			tokenString = authHeader[7:]
		} else {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{"message": "Invalid token format"})
		}

		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, fiber.ErrUnauthorized
			}
			return []byte(JwtSecret), nil
		})

		if err != nil || !token.Valid {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{"message": "Invalid or expired JWT"})
		}

		claims, ok := token.Claims.(jwt.MapClaims)
		if !ok {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{"message": "Invalid JWT claims"})
		}

		c.Locals("userID", claims["user_id"])

		return c.Next()
	}
}

// UpdateUser - оновлення користувача (авторизований юзер або адмін)
func UpdateUser(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	userIDFromToken := c.Locals("userID")
	role := c.Locals("role")

	if userIDFromToken == nil || role == nil {
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"message": "Unauthorized",
		})
	}

	// ID з параметру маршруту
	paramID := c.Params("id")
	if paramID == "" {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "User ID is required in path",
		})
	}

	// Перевірка прав доступу
	if role != "admin" && fmt.Sprintf("%v", userIDFromToken) != paramID {
		return c.Status(fiber.StatusForbidden).JSON(fiber.Map{
			"message": "Access denied",
		})
	}

	var user models.User
	if err := db.First(&user, paramID).Error; err != nil {
		log.Println("User not found:", err)
		return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
			"message": "User not found",
		})
	}

	var updatedData struct {
		Name     string `json:"name"`
		Email    string `json:"email"`
		Password string `json:"password"`
	}

	if err := c.BodyParser(&updatedData); err != nil {
		log.Println("BodyParser error:", err)
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"message": "Invalid request data",
		})
	}

	if updatedData.Name != "" {
		user.Name = updatedData.Name
	}
	if updatedData.Email != "" {
		user.Email = updatedData.Email
	}
	if updatedData.Password != "" {
		hashedPassword, err := bcrypt.GenerateFromPassword([]byte(updatedData.Password), bcrypt.DefaultCost)
		if err != nil {
			log.Println("Password hashing error:", err)
			return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
				"message": "Error hashing password",
			})
		}
		user.PasswordHash = string(hashedPassword)
	}

	if err := db.Save(&user).Error; err != nil {
		log.Println("Database save error:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error updating user",
		})
	}

	user.PasswordHash = ""
	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "User updated successfully",
		"user":    user,
	})
}

// DeleteUser - видалення користувача (юзер авторизований)
func DeleteUser(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	userIDFromToken := c.Locals("userID")
	if userIDFromToken == nil {
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{"message": "Unauthorized"})
	}
	userID := fmt.Sprintf("%v", userIDFromToken)

	var user models.User
	if err := db.First(&user, userID).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			log.Println("User not found:", err)
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
				"message": "User not found",
			})
		}
		log.Println("Error finding user:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error finding user",
		})
	}

	if err := db.Delete(&user).Error; err != nil {
		log.Println("Error deleting user:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error deleting user",
		})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{
		"message": "User deleted successfully",
	})
}

func GetDoctorByID(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	doctorID := c.Params("id")
	var user models.User
	if err := db.Where("id = ? AND role = ?", doctorID, "doctor").First(&user).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			log.Println("Doctor not found:", err)
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
				"message": "Doctor not found",
			})
		}
		log.Println("Error finding doctor:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error finding doctor",
		})
	}

	user.PasswordHash = ""
	return c.Status(fiber.StatusOK).JSON(user)
}

func GetAllDoctors(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	var doctors []models.User
	if err := db.Where("role = ?", "doctor").Find(&doctors).Error; err != nil {
		log.Println("Error fetching doctors:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error fetching doctors",
		})
	}

	// Забираємо паролі
	for i := range doctors {
		doctors[i].PasswordHash = ""
	}

	return c.Status(fiber.StatusOK).JSON(doctors)
}

func UpdateUserByAdmin(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}
	id := c.Params("id")

	var user models.User
	if err := db.First(&user, id).Error; err != nil {
		return c.Status(404).JSON(fiber.Map{"message": "User not found"})
	}

	var updatedData struct {
		Name     string `json:"name"`
		Email    string `json:"email"`
		Password string `json:"password"`
	}

	if err := c.BodyParser(&updatedData); err != nil {
		return c.Status(400).JSON(fiber.Map{"message": "Invalid data"})
	}

	if updatedData.Name != "" {
		user.Name = updatedData.Name
	}
	if updatedData.Email != "" {
		user.Email = updatedData.Email
	}
	if updatedData.Password != "" {
		hashed, _ := bcrypt.GenerateFromPassword([]byte(updatedData.Password), bcrypt.DefaultCost)
		user.PasswordHash = string(hashed)
	}

	if err := db.Save(&user).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"message": "DB error"})
	}

	return c.JSON(fiber.Map{"message": "Updated", "user": user})
}

func GetAllPatients(c *fiber.Ctx) error {
	db, ok := c.Locals("db").(*gorm.DB)
	if !ok || db == nil {
		log.Println("Database connection not found in context")
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Database connection error",
		})
	}

	var patients []models.User
	if err := db.Where("role = ?", "patient").Find(&patients).Error; err != nil {
		log.Println("Error fetching patients:", err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"message": "Error fetching patients",
		})
	}

	// Забираємо паролі
	for i := range patients {
		patients[i].PasswordHash = ""
	}

	return c.Status(fiber.StatusOK).JSON(patients)
}
