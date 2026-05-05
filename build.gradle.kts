plugins {
    id("java")
    id("war")
}

group = "ru.trukhmanov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.platform:jakarta.jakartaee-web-api:11.0.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.zaxxer:HikariCP:7.0.2")
}

tasks.test {
    useJUnitPlatform()
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.war{
    archiveFileName.set("ROOT.war")
}