# EmployeeManagementVaadinDemo
A simple employee management tool - Demo project for a combination of Vaadin and Keycloak.
This demo project is based on spring-boot-oauth2-resource-server-client (https://github.com/GilbertHofstaetter/spring-boot-oauth2-resource-server-client)

Vaadin (v23) has been added as web front end and postgres as database.

**See Turorial.pdf for further infos.** \
**See Turorial Docker setup.pdf for further infos about running the project with Docker.**

**Build project**

* mvn clean install -P production

**Endpoints**

* http://localhost:8080/api/document/
* http://localhost:8080/api/picture/
* http://localhost:8080/api/customer/list
* http://localhost:8080/api/employee/list
* http://localhost:8080/userJwt
* http://localhost:8080/userOAuth2
* http://localhost:8080/logout

**Swagger-Ui**

* http://localhost:8080/swagger-ui.html

**Vaadin front end**

* http://localhost:8080/ui

![List Employee in Vaadin](https://github.com/GilbertHofstaetter/spring-boot-oauth2-resource-server-client-vaadin/blob/main/Employee_Vaadin.png)


