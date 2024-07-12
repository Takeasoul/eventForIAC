# Этап сборки
FROM maven:3.8.7-openjdk-17-slim AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и исходники проекта
COPY pom.xml .
COPY src ./src

# Собираем проект, создавая исполняемый JAR файл
RUN mvn clean package -DskipTests

# Этап выполнения
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем исполняемый JAR файл из этапа сборки
COPY --from=build /app/target/events-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт, на котором работает ваше приложение
EXPOSE 8080

# Определяем переменные окружения, если нужно
ENV JAVA_OPTS=""

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
