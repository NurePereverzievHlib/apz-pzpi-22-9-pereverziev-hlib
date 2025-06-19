package routes

import (
	"ortho_vision_api/controllers"
	middlewares "ortho_vision_api/middleware"

	"github.com/gofiber/fiber/v2"
)

func SetupRoutes(app *fiber.App) {
	// Публічні маршрути (без токена)
	app.Post("/register", controllers.RegisterUser)
	app.Post("/login", controllers.LoginUser)

	// Група захищених маршрутів
	protected := app.Group("/", middlewares.JwtProtected())

	protected.Put("/users/:id", controllers.UpdateUser)
	protected.Delete("/admin/users/:id", controllers.DeleteUser)
	protected.Get("/doctors/:id", controllers.GetDoctorByID)
	protected.Get("/admin/doctors", controllers.GetAllDoctors)
	protected.Get("/admin/patients", controllers.GetAllPatients)
	protected.Put("/admin/users/:id", controllers.UpdateUserByAdmin)

	protected.Post("/admin/clinics", controllers.AddClinic)
	protected.Get("/clinics", controllers.GetAllClinics)
	protected.Get("/clinics/:name", controllers.GetClinicByName)
	protected.Delete("/admin/clinics/:id", controllers.DeleteClinic)
	protected.Put("/clinics/:id", controllers.UpdateClinic)

	protected.Post("/doctor/:doctor_id/appointment_times", controllers.CreateAppointmentTime)
	protected.Put("/doctor/:doctor_id/appointment_times/:appointment_time_id", controllers.UpdateAppointmentTime)
	protected.Get("/doctor/:doctor_id/appointment_times", controllers.CreateAppointmentTime)
	protected.Delete("/doctor/:doctor_id/appointment_times/:appointment_time_id", controllers.DeleteAppointmentTime)

	protected.Get("/appointment-times/search", controllers.SearchAppointmentTimes)

	protected.Post("/appointments", controllers.CreateAppointment)
	protected.Delete("/appointments/:id", controllers.DeleteAppointment)
	protected.Get("/appointments/patient/:patientID", controllers.GetAppointmentsByPatientID)
	protected.Get("/doctors/:id/appointments", controllers.GetAppointmentsByDoctor)
	protected.Put("/doctors/:doctor_id/appointments/:appointment_id", controllers.UpdateAppointment)

	protected.Post("/diseases", controllers.CreateDisease)
	protected.Delete("/diseases/:id", controllers.DeleteDisease)
	protected.Put("/diseases/:id", controllers.UpdateDisease)
	protected.Get("/medical-record/:userID", controllers.GetUserMedicalRecord)
	protected.Get("/clinics/:clinic_id/doctors", controllers.GetDoctorsByClinic)
	protected.Get("/diseases", controllers.GetDiseasesByAppointment)

	protected.Get("/clinic-stats", controllers.GetClinicDiseaseStats)

	protected.Post("/smart-glasses", controllers.AddSmartGlassesData)
	protected.Get("/smart-glasses/statistics", controllers.GetSmartGlassesStatistics)

	app.Options("/*", func(c *fiber.Ctx) error {
		return c.SendStatus(200)
	})
}
