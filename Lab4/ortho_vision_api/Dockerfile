# Вихідний образ з Go
FROM golang:1.23.4-alpine AS build


# Робоча директорія всередині контейнера
WORKDIR /app

# Копіюємо go.mod та go.sum і завантажуємо залежності
COPY go.mod ./
COPY go.sum ./
RUN go mod download

# Копіюємо увесь проєкт
COPY . .

# Збираємо Go застосунок
RUN go build -o main .

# Фінальний образ
FROM alpine:latest

WORKDIR /root/

# Копіюємо зібраний файл
COPY --from=build /app/main .

# Відкриваємо порт (залежно від твого сервера)
EXPOSE 3000

# Запускаємо
CMD ["./main"]
