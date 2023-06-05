package ru.yandex.praktikum.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.service.AuthResponse;
import ru.yandex.praktikum.service.Service;
import org.apache.http.HttpStatus;
import static io.restassured.RestAssured.given;

public class UserAPI {

    private static final String USER_PATH = "auth/user";
    private static final String CREATE_USER_PATH = "auth/register";
    private static final String LOGIN_USER_PATH = "auth/login";
    public static final String DELETE_USER_PATH = "auth/user";
    private static final String EDIT_USER_PATH = "auth/user";

    @Step("Создание пользователя")
    public Response create(User user) {
        Service.setupSpecification();
        return given()
                .body(user)
                .when()
                .post(CREATE_USER_PATH);
    }

    @Step("Создание пользователя и возврат результата в виде класса")
    public AuthResponse createAsAuthResponse(User user) {
        Service.setupSpecification();
        return given()
                .body(user)
                .when()
                .post("auth/register")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(AuthResponse.class);
    }

    @Step("Удаление пользователя")
    public Response delete(String accessToken) {
        Service.setupSpecification();
        return given()
                .body(accessToken)
                .when()
                .post(DELETE_USER_PATH);
    }

    @Step("Авторизация пользователя")
    public Response login(UserCredentials user) {
        Service.setupSpecification();
        return given()
                .body(user)
                .when()
                .post(LOGIN_USER_PATH);
    }

    @Step("Авторизация и возврат ответа как класс")
    public AuthResponse loginAsAuthResponse(UserCredentials user) {
        return given()
                .body(user)
                .when()
                .post(LOGIN_USER_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(AuthResponse.class);
    }

    @Step("Получение информации о пользователе")
    public Response getUser(User user) {
        Service.setupSpecification();
        return given()
                .when()
                .post(USER_PATH);
    }

    @Step("Изменение данных пользователя")
    public Response editData(String accessToken, User user) {
        Service.setupSpecification();
        return given()
                .header("authorization", accessToken)
                .body(user)
                .when()
                .patch(EDIT_USER_PATH);
    }

    public Response editDataWithoutToken(User user){
        Service.setupSpecification();
        return given()
                .body(user)
                .when()
                .patch(EDIT_USER_PATH);
    }
}
