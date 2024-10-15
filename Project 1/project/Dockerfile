FROM maven:3.8.4-openjdk-11-slim

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean install

EXPOSE 50052 50053 50054

CMD ["mvn", "exec:java", "-Dexec.mainClass=com.project.DataBase"]