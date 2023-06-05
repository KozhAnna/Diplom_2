package ru.yandex.praktikum.user;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.service.AuthResponse;
import ru.yandex.praktikum.service.Service;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserCreateTest {

    AuthResponse regResponse;
    UserAPI userAPI = new UserAPI();

    @Before
    public void setUp() {
        Service.setupSpecification();
    }

    @Test
    @DisplayName("Создать нового пользователя и проверить ответ")
    public void createNewUserAndCheckResponse() {
        regResponse = userAPI.createAsAuthResponse(UserCredentials.user);
        checkBodyOfResponse(regResponse);
        tearDown();
    }

    @Step("Проверка ответов")
    public void checkBodyOfResponse (AuthResponse response) {
        assertTrue(regResponse.isSuccess());
        assertEquals(UserCredentials.fakeEmail, regResponse.getUser().getEmail());
        assertEquals(UserCredentials.fakeName, regResponse.getUser().getName());
        assertFalse(regResponse.getAccessToken().isBlank());
        assertFalse(regResponse.getRefreshToken().isBlank());
    }

    @Step ("Завершение - удаляем созданного пользователя")
    public void tearDown() {
        // авторизация пользователя
        userAPI.login(UserCredentials.from(UserCredentials.user));
        // удаление пользователя
        given()
                .header("Authorization",regResponse.getAccessToken())
                .body(UserCredentials.user)
                .when()
                .delete(UserAPI.DELETE_USER_PATH)
                .then()
                .statusCode(HttpStatus.SC_ACCEPTED);
    }

    @Test
    @DisplayName("Создание дублирующихся пользователей и проверка ответов")
    public void createDoubleUserAndCheckResponse() {
        regResponse = userAPI.createAsAuthResponse(UserCredentials.user);
        checkBodyOfResponse(regResponse);
        Response newResponse = createDoubleUser();
        checkStatusCodeOfBadRequest(newResponse);
        checkBodyOfDoubleRequest(newResponse);
        tearDown();
    }

    @Step("Еще раз создаем пользователя")
    public Response createDoubleUser() {
        return userAPI.create(UserCredentials.user);
    }

    @Step("Проверка ответа (результат отрицательный)")
    public void checkStatusCodeOfBadRequest(Response response) {
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusCode());
    }

    @Step("Проверка ошибки создания пользоватля")
    public void checkBodyOfDoubleRequest(Response response) {
        response.then().body("success", is(false)).
                assertThat().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создаем пользователя без почты")
    public void createUserWithoutEmailAndCheckResponse () {
        Response response = sendPostRequestUserWithoutEmail();
        checkStatusCodeOfBadRequest(response);
        checkMessageToRequestWithoutAnyField(response);
    }

    @Step("Отправка запроса на создание юзера без почты")
    public Response sendPostRequestUserWithoutEmail () {
        return userAPI.create(UserCredentials.userWithoutEmail);
    }

    @Step("Проверка на отсутствие необходимого поля")
    public void checkMessageToRequestWithoutAnyField (Response response) {
        response.then().body("success", is(false))
                .and().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void createUserWithoutPasswordAndCheckResponse () {
        Response response = sendPostRequestUserWithoutPassword();
        checkStatusCodeOfBadRequest(response);
        checkMessageToRequestWithoutAnyField (response);
    }

    @Step("Отправка запроса на создание юзера без пароля")
    public Response sendPostRequestUserWithoutPassword () {
        return userAPI.create(UserCredentials.userWithoutPassword);
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void createUserWithoutNameAndCheckResponse () {
        Response response = sendPostRequestUserWithoutName();
        checkStatusCodeOfBadRequest(response);
        checkMessageToRequestWithoutAnyField(response);
    }

    @Step("Отправка запроса на создание юзера без имени")
    public Response sendPostRequestUserWithoutName () {
        return userAPI.create(UserCredentials.userWithoutName);
    }

}