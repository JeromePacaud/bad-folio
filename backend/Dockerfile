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

# Créer l'utilisateur dédié AVANT de switcher dessus.
# Sur alpine, adduser/addgroup remplacent useradd/groupadd.
# -S = compte système (pas de home, pas de shell login)
# Sans cette étape, USER devfolio échoue car l'utilisateur n'existe pas dans l'image.
RUN addgroup -S devfolio && adduser -S devfolio -G devfolio
RUN chown devfolio:devfolio /app/app.jar

# Port applicatif Spring Boot (8080 par défaut, pas 8083)
EXPOSE 8080

# Lancer le conteneur avec l'utilisateur dédié, pas root
# CORRIGÉ DEV-08 : ajout USER devfolio, le conteneur ne tourne plus en root
USER devfolio

# Démarrage sans agent de debug JVM
# CORRIGÉ DEV-08 : -agentlib retiré, port de debug JVM fermé
CMD ["java", "-jar", "app.jar"]