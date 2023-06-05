package ru.yandex.praktikum.user;

import ru.yandex.praktikum.service.Service;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserEditTest {

    UserAPI userAPI = new UserAPI();
    String AuthToken;

    @Before
    @Step("Начало - создание пользователя")
    public void setUp() {
        Service.setupSpecification();
        Response loginResponse = userAPI.create(UserCredentials.user);
        AuthToken = loginResponse.then().extract().path("accessToken");
    }

    @After
    @Step("Завершение - удаляем созданного пользователя")
    public void tearDown() {
        given()
                .header("Authorization", AuthToken)
                .body(UserCredentials.user)
                .when()
                .delete(UserAPI.DELETE_USER_PATH)
                .then()
                .statusCode(HttpStatus.SC_ACCEPTED);
    }

    @Test
    @DisplayName("Изменение данных пользователя (корректная авторизация)")
    public void editDataWithAuthorization() {
        userAPI.loginAsAuthResponse(UserCredentials.from(UserCredentials.user));
        Response response = userAPI.editData(AuthToken, UserCredentials.newUser);
        response.then().assertThat().body("success", is( true))
                .body ("user.name", equalTo(UserCredentials.newFakeName))
                .body ("user.email", equalTo(UserCredentials.newFakeEmail));
    }

    @Test
    @DisplayName("Изменение данных пользователя (без авторизации)")
    public void editDataWithoutAuthorization() {
        Response response = userAPI.editDataWithoutToken(UserCredentials.newUser);
        response.then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

}
