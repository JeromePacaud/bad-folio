# Étape 1 : build de l'application avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# Étape 2 : image finale allégée (alpine = image minimale, réduit la surface d'attaque)
# CORRIGÉ DEV-07 : remplacé eclipse-temurin:21-jre par eclipse-temurin:21-jre-alpine
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copie uniquement le jar compilé, pas les sources
# CORRIGÉ DEV-09 : COPY ciblé sur le jar uniquement, pas COPY . .
COPY --from=build /app/target/*.jar app.jar

# Port applicatif uniquement
# CORRIGÉ : port debug 5005 retiré
EXPOSE 8083

# Lancer le conteneur avec l'utilisateur dédié, pas root
# CORRIGÉ DEV-08 : ajout USER devfolio, le conteneur ne tourne plus en root
USER devfolio

# Démarrage sans agent de debug JVM
# CORRIGÉ DEV-08 : -agentlib retiré, port de debug JVM fermé
CMD ["java", "-jar", "app.jar"]