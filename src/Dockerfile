# Используем официальный образ Ubuntu как базовый образ
FROM ubuntu:latest

# Указываем автора образа
LABEL authors="odent"

# Устанавливаем необходимые пакеты
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get clean;

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл сборки JAR в контейнер
COPY target/your-app.jar app.jar

# Экспонируем порт, который использует ваше приложение
EXPOSE 8080

# Устанавливаем переменную окружения, чтобы указать JVM не буферизовать стандартные потоки
ENV JAVA_OPTS=""

# Команда для запуска вашего приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
