package models

import (
	"time"
)

// Модель для таблиці Users
type User struct {
	ID             uint   `gorm:"primaryKey"`
	Name           string `gorm:"not null"`
	Email          string `gorm:"unique;not null"`
	PasswordHash   string `gorm:"not null"`
	Role           string `gorm:"not null;check:role IN ('patient', 'admin', 'doctor')"`
	Specialization *string
	AvatarURL      *string
	CreatedAt      time.Time
	Password       string `gorm:"-"`
}
