# Use a imagem oficial do OpenJDK como base
FROM openjdk:17-jdk-alpine

# Defina o diretório de trabalho dentro do container
WORKDIR /app

# Copie o arquivo JAR gerado no diretório libs para o diretório /app do container
COPY build/libs/ws-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta em que a aplicação estará rodando (ajuste de acordo com sua aplicação)
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
