package config

import (
	"fmt"
	"log"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

var DB *gorm.DB

func InitDB() *gorm.DB {
	dbUser := "postgres"
	dbPass := "31415"
	dbName := "orthovisiondb"
	dbHost := "localhost"
	dbPort := "5432"

	connectionString := fmt.Sprintf(
		"host=%s user=%s dbname=%s password=%s port=%s sslmode=disable",
		dbHost, dbUser, dbName, dbPass, dbPort,
	)

	var err error
	DB, err = gorm.Open(postgres.Open(connectionString), &gorm.Config{})
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}

	sqlDB, err := DB.DB()
	if err != nil {
		log.Fatal("Failed to get DB connection:", err)
	}

	if err := sqlDB.Ping(); err != nil {
		log.Fatal("Failed to ping database:", err)
	} else {
		log.Println("Successfully connected to the database.")
	}

	DB.AutoMigrate()
	return DB
}
