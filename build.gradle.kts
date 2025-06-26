plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
}

group = "team.black-hole.bot.asky"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // Тестирование
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    // Внедрение зависимостей (DI)
    implementation(libs.guice)

    // GraalVM Polyglot
    implementation(libs.polyglot.core)
    implementation(libs.polyglot.js)

    // Логирование
    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j)
    implementation(libs.slf4j.api)

    // Telegram Bots
    implementation(libs.telegrambots.client)
    implementation(libs.telegrambots.abilities) {
        // У нас своя реализация webhook
        exclude(group = "org.telegram", module = "telegrambots-webhook")
    }

    // Базы данных и ORM
    implementation(libs.hibernate.core)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
    implementation(libs.hikariCP)
    implementation(libs.postgresql)

    // Конфигурация (Typesafe Config)
    implementation(libs.typesafe.config)

    // Http сервер
    implementation(libs.jooby.jetty)

    // Redis
    implementation(libs.redis)

    // Библиотеки апача
    implementation(libs.apache.commons.text)
    implementation(libs.apache.commons.io)

    // Фильтрация
    implementation(libs.data.filter)
}

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        manifest {
            val group = project.group.toString().replace(rootProject.group.toString() + ".", "");
            val prefix = (if (group != project.name) "${group}-" else "") + project.name;
            archiveFileName.set("${prefix}-${project.version}.jar")
        }
    }

    register("dir", Copy::class.java) {
        destinationDir = project.layout.buildDirectory.dir("distributions").get().dir("asky-bot-${project.version}").asFile
        includeEmptyDirs = true
        duplicatesStrategy = DuplicatesStrategy.FAIL

        into("lib") {
            from(jar)
            from(configurations.runtimeClasspath)
        }

        into(".") {
            from(project.file("distribution")) {
                exclude("config/application.conf")
                exclude("logs/*.log")
            }
            from(project.file("LICENSE"))
        }
    }
}