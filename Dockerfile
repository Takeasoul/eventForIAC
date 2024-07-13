# Этап сборки
FROM ubuntu:latest AS build

# Устанавливаем необходимые пакеты
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven && \
    apt-get clean

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и исходники проекта
COPY pom.xml .
COPY src ./src

# Собираем проект, создавая исполняемый JAR файл
RUN mvn clean package -DskipTests

# Этап выполнения
FROM ubuntu:latest

# Устанавливаем необходимые пакеты
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get clean

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем исполняемый JAR файл из этапа сборки
COPY --from=build /app/target/events-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources /app/resources
# Экспонируем порт, который использует ваше приложение
EXPOSE 8080

# Устанавливаем переменную окружения, чтобы указать JVM не буферизовать стандартные потоки
ENV JAVA_OPTS=""

# Команда для запуска вашего приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
