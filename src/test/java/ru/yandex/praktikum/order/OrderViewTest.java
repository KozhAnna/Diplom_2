package ru.yandex.praktikum.order;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.service.Service;
import ru.yandex.praktikum.user.UserAPI;
import ru.yandex.praktikum.user.UserCredentials;

import static org.hamcrest.CoreMatchers.*;

public class OrderViewTest {

    UserAPI userAPI = new UserAPI();
    OrderAPI orderAPI = new OrderAPI();
    String authToken;

    @Before
    @Step("Начало - создание пользователя")
    public void setUp() {
        Service.setupSpecification();
        Response loginResponse = userAPI.create(UserCredentials.user);
        authToken = loginResponse.then().extract().path("accessToken");
    }

    @After
    @Step("Завершение - удаляем созданного пользователя")
    public void tearDown() {
        userAPI.delete(authToken, UserCredentials.user);
    }

    @Test
    @DisplayName("Получить список заказов пользователя")
    public void getOrdersByUser() {
        Response response = orderAPI.getOrdersByCurrentUser(authToken);
        checkResponseWhenGetListOfOrders(response);
    }

    @Step("Проверка ответа получения списка заказов")
    public void checkResponseWhenGetListOfOrders (Response response) {
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success",is(true))
                .and()
                .body("orders",notNullValue())
                .body("total",notNullValue())
                .body("totalToday",notNullValue());
    }

    @Test
    @DisplayName("Получить список заказов без авторизации")
    public void getUserOrderWithoutAuthorization() {
        Response response = orderAPI.getOrdersWithoutAuth();
        checkResponseWhenGetListOfOrdersWithoutAuthorization(response);
    }

    @Step("Проверка ответа получения списка заказов (без авторизации)")
    public void checkResponseWhenGetListOfOrdersWithoutAuthorization(Response response) {
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success",is(false))
                .and()
                .body("message",equalTo("You should be authorised"));
    }

}
