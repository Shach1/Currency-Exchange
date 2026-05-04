# Проект “Обмен валют”
Третий проект [Java Роадмапа](https://zhukovsd.github.io/java-backend-learning-course/#требуемые-знания-и-технологии) Сергея Жукова.

Фронтенд взят [отсюда](https://github.com/zhukovsd/currency-exchange-frontend).

---
Для самостоятельного запуска проекта необходимо наличие [JDK 21](https://www.oracle.com/asean/java/technologies/downloads/#java21) и и [Apache Tomcat 11](https://tomcat.apache.org/download-11.cgi)
## Этапы запуска
1) Собрать проект
```shell
./gradlew clean war
```
После успешной сборки в проекте появится артефакт .`/build/libs/ROOT.war`

2) Удалить все из папки `{tomcat_home}/webapps` и переместить туда собранный артефакт

3) Отредактировать конфигурационный файл `{tomcat_home}/conf/server.xml`.
Нужно найти строки:
```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```
и заменить на
```xml
    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443"
               parseBodyMethods="POST,PUT,PATCH" />
```

4) Остается запустить сервер:
```shell
{tomcat_home}/bin/catalina.sh run
```
Сервер доступен по адресу `localhost:8080`