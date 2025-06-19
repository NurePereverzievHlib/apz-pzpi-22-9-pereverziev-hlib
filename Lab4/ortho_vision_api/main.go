package main

import (
	"log"
	"ortho_vision_api/config"
	"ortho_vision_api/routes"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
)

func main() {
	// Ініціалізація БД
	_ = config.InitDB()

	// Створення Fiber аплікації
	app := fiber.New()

	app.Use(cors.New(cors.Config{
		AllowOrigins:     "http://localhost:4000", // або твоя реальна фронт-URL
		AllowMethods:     "GET,POST,PUT,DELETE,OPTIONS",
		AllowHeaders:     "Authorization, Content-Type, ngrok-skip-browser-warning",
		AllowCredentials: true,
	}))

	app.Options("/*", func(c *fiber.Ctx) error {
    return c.SendStatus(200)
})


	// Передача з'єднання з БД в контекст
	app.Use(func(c *fiber.Ctx) error {
		c.Locals("db", config.DB)
		return c.Next()
	})

	// Підключення роутів
	routes.SetupRoutes(app)

	// Запуск сервера
	err := app.Listen(":3000")
	if err != nil {
		log.Fatal("Error starting server: ", err)
	}
}
