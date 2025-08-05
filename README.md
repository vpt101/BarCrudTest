# BarCrudTest

## Areas of improvement
- The OpenApi generator is in a different project. Ideally it should have been part of this codebase
- Code in the delegate can be refactored and made more modular.

## Build
- Build and run the project using Maven

`mvn clean install`

`mvn spring-boot:run`
- The application will be available at http://localhost:8080
- Swagger UI will be available at http://localhost:8080/swagger-ui.html

### Auth
- `/v1/auth/login` and `/v1/users` are not authenticated endpoints
  The rest of the endpoints are authenticated.
- The user needs to add an `Authorization: Bearer <token>` header to their request to access them.
- The user may use `/v1/users` endpoint to create a new user
  *Note: The password is the same as the username for now*
- Then, the user may use `/v1/auth/login` endpoint to get an access token. This token can be used to authenticate other endpoints.
- For Swagger: the user can do this by clicking on the `Authorize` button and enter the token in the `Value` field.
