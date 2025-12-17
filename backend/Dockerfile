# 构建阶段
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app

# 时区可根据需要调整
ENV TZ=Asia/Shanghai

COPY --from=build /app/target/cailanzi-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
