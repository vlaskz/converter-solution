# Use the official OpenJDK base image
FROM openjdk:8-jdk

# Install libfreetype6
RUN apt-get update && apt-get install -y libfreetype6


# Set the working directory inside the image
WORKDIR /app

# Copy the JAR file into the image
COPY /target/ConverterSolution-0.0.1-SNAPSHOT.jar /app/ConverterSolution-0.0.1-SNAPSHOT.jar

# Set the startup command to execute your application
CMD ["java", "-jar", "/app/ConverterSolution-0.0.1-SNAPSHOT.jar"]