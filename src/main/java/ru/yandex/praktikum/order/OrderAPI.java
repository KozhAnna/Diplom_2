package ru.yandex.praktikum.order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.service.Service;

import static io.restassured.RestAssured.given;

public class OrderAPI {

    public static final String ORDER_PATH = "orders";

    @Step("Создание заказа")
    public Response createOrder(String accessToken, Order order) {
        return given()
                .header("authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Получение заказов пользователя")
    public Response getOrdersByCurrentUser(String accessToken) {
        return given()
                .header("authorization", accessToken)
                .when()
                .get(ORDER_PATH);
    }

    @Step("Получение заказов без авторизации")
    public Response getOrdersWithoutAuth() {
        return given()
                .when()
                .get(ORDER_PATH);
    }

}
