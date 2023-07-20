FROM openjdk:17

COPY . .

RUN ./gradlew build -x test

CMD ["./gradlew", "run"]