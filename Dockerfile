# Используем базовый образ с установленным JDK
FROM openjdk:17-jdk-slim AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем исполняемый JAR файл Maven
COPY --from=build /target/events-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт, на котором работает ваше приложение
EXPOSE 8080

# Определяем переменные окружения, если нужно
ENV JAVA_OPTS=""

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
