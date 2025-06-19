package models

import (
	"database/sql/driver"
	"encoding/json"
	"fmt"
	"time"
)

// CustomTime - кастомний тип для роботи з часом.
type CustomTime time.Time

// Scan реалізує інтерфейс sql.Scanner.
func (ct *CustomTime) Scan(value interface{}) error {
	if value == nil {
		*ct = CustomTime(time.Time{})
		return nil
	}
	t, ok := value.(time.Time)
	if !ok {
		return fmt.Errorf("failed to scan CustomTime")
	}
	*ct = CustomTime(t)
	return nil
}

// Value реалізує інтерфейс driver.Valuer для збереження в БД.
func (ct CustomTime) Value() (driver.Value, error) {
	return time.Time(ct).Format("2006-01-02 15:04:05"), nil
}

// MarshalJSON — кастомне серіалізування в JSON.
func (ct CustomTime) MarshalJSON() ([]byte, error) {
	t := time.Time(ct)
	formatted := fmt.Sprintf("\"%s\"", t.Format("2006-01-02T15:04:05"))
	return []byte(formatted), nil
}

// UnmarshalJSON — розпарсити строку у CustomTime при десеріалізації з JSON.
func (ct *CustomTime) UnmarshalJSON(b []byte) error {
	var s string
	if err := json.Unmarshal(b, &s); err != nil {
		return err
	}
	t, err := time.Parse("2006-01-02T15:04:05", s)
	if err != nil {
		return err
	}
	*ct = CustomTime(t)
	return nil
}

// AppointmentTimes — модель доступних записів до лікаря.
type AppointmentTimes struct {
	ID            uint       `gorm:"primaryKey" json:"id"`
	DoctorID      uint       `gorm:"not null;index" json:"doctor_id"`
	Doctor        User       `gorm:"foreignKey:DoctorID" json:"-"`
	ClinicID      uint       `gorm:"not null;index" json:"clinic_id"`
	Clinic        Clinic     `gorm:"foreignKey:ClinicID" json:"-"`
	AvailableTime CustomTime `gorm:"not null" json:"available_time"`
	IsBooked      bool       `gorm:"column:is_booked;default:false" json:"is_booked"`
	CreatedAt     time.Time  `json:"created_at"`
	UpdatedAt     time.Time  `json:"updated_at"`
}
