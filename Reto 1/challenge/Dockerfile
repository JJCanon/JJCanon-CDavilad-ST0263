FROM openjdk:22
WORKDIR /app
COPY . /app
RUN mvn clean install
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.challenge.FileTransferTracker"]
