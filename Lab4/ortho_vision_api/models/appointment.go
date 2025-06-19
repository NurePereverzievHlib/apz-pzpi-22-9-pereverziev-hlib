package models

import (
	"time"
)

type Appointment struct {
	ID                uint              `gorm:"primary_key"`
	AppointmentTimeID uint              `gorm:"not null"`
	AppointmentTime   *AppointmentTimes `gorm:"foreignKey:AppointmentTimeID;references:ID"`
	PatientID         uint              `gorm:"not null"`
	Status            string            `gorm:"not null;default:'pending';check:status IN ('pending', 'confirmed', 'cancelled', 'completed')"`
	Reason            string
	CreatedAt         time.Time
}
