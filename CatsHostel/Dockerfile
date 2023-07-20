FROM openjdk:17 as builder

COPY . .

RUN ./gradlew jar

FROM openjdk:17

COPY --from=builder /build/libs/cats-app.jar ./cats-app.jar

CMD ["java", "-jar", "cats-app.jar"]
