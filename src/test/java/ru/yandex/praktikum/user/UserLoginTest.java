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

public class UserLoginTest {

    UserAPI userAPI = new UserAPI();
    AuthResponse loginResponse;
    Response response;

    @Before
    @Step("Начало - создание пользователя")
    public void setUp() {
        Service.setupSpecification();
        userAPI.create(UserCredentials.user);
    }

    @Step("Завершение - удаляем созданного пользователя")
    public void tearDown(){
        given()
                .header("Authorization", loginResponse.getAccessToken())
                .body(UserCredentials.user)
                .when()
                .delete(UserAPI.DELETE_USER_PATH)
                .then()
                .statusCode(HttpStatus.SC_ACCEPTED);
    }

    @Test
    @DisplayName("Проверка логина")
    public void checkLoginUser() {
        loginResponse = userAPI.loginAsAuthResponse(UserCredentials.from(UserCredentials.user));
        checkBodyOfResponse(loginResponse);
        tearDown();
    }

    @Step("Проверка ответов")
    public void checkBodyOfResponse(AuthResponse response) {
        assertTrue(loginResponse.isSuccess());
        assertFalse(loginResponse.getAccessToken().isBlank());
        assertFalse(loginResponse.getRefreshToken().isBlank());
        assertEquals(UserCredentials.fakeEmail, loginResponse.getUser().getEmail());
        assertEquals(UserCredentials.fakeName, loginResponse.getUser().getName());
    }

    @Test
    @DisplayName("Логин юзера с неправильными данными и проверка ответа")
    public void loginIncorrectUserAndCheckResponse() {
        response = loginIncorrectUser();
        checkStatusCodeWithIncorrectData(response);
        checkMessageWithIncorrectData(response);
    }

    @Step("Авторизация юзера с неправильными данными")
    public Response loginIncorrectUser() {
        return userAPI.login(UserCredentials.from(UserCredentials.newUser));
    }

    @Step("Проверка статуса ответа")
    public void checkStatusCodeWithIncorrectData(Response response) {
        response.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Step("Проверка тела ответа")
    public void checkMessageWithIncorrectData (Response response){
        response.then().body("success", is(false))
                .and().assertThat().body("message", equalTo("email or password are incorrect"));
    }

}
