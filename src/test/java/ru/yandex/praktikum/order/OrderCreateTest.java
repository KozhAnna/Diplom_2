package ru.yandex.praktikum.order;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.service.IngredientsResponse;
import ru.yandex.praktikum.service.Service;
import ru.yandex.praktikum.user.UserAPI;
import ru.yandex.praktikum.user.UserCredentials;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class OrderCreateTest {

    UserAPI userAPI = new UserAPI();
    OrderAPI orderAPI = new OrderAPI();
    String AuthToken;
    IngredientsResponse ingredients;
    Order order;

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
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthorization() {
        ingredients = getAllIngredients();
        ArrayList<String> tempOrder = new OrderGenerator().createRandomOrder(ingredients);
        order = new Order(tempOrder);
        Response response = orderAPI.createOrder(AuthToken, order);
        checkResponseOfCreateOrder(response);
    }

    @Step("Получаем список возможных ингредиентов как класс")
    public IngredientsResponse getAllIngredients() {
        return  given()
                .when()
                .get("ingredients")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(IngredientsResponse.class);
    }

    @Step("Проверка ответа после создания заказа")
    public void checkResponseOfCreateOrder(Response response) {
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success",is(true))
                .and()
                .body("name", notNullValue())
                .body("order",notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthorization() {
        ingredients = getAllIngredients();
        ArrayList<String> tempOrder = new OrderGenerator().createRandomOrder(ingredients);
        order = new Order(tempOrder);
        Response response = letCreateOrderWithoutAuth();
        checkResponseOfCreateOrderWithoutAuth(response);
    }

    @Step("Отправка запроса на создание заказа (без авторизации)")
    public Response letCreateOrderWithoutAuth() {
        return given()
                .body(order)
                .when()
                .post(OrderAPI.ORDER_PATH);
    }

    @Step("Проверка ответа после создания заказа (без авторизации)")
    public void checkResponseOfCreateOrderWithoutAuth(Response response) {
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success",is(true))
                .and()
                .body("name", notNullValue())
                .body("order",notNullValue());
    }

    @Test
    @DisplayName("Создание заказа (без ингредиентов)")
    public void createOrderWithoutIngredients() {
        ArrayList<String> tempOrder = new ArrayList<>(List.of(new String[]{}));
        order = new Order(tempOrder);
        Response response = orderAPI.createOrder(AuthToken, order);
        checkResponseOfCreateOrderWithoutIngredients(response);
    }

    @Step("Проверка ответа после создания заказа (без ингредиентов)")
    public void checkResponseOfCreateOrderWithoutIngredients (Response response) {
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success",is(false))
                .and()
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа (с неправильными ингредиентами)")
    public void createOrderWithIncorrectIngredients() {
        ArrayList<String> tempOrder = new ArrayList<>(List.of(new String[]{"1", "2"}));
        order = new Order(tempOrder);
        Response response = orderAPI.createOrder(AuthToken, order);
        checkResponseOfCreateOrderWithIncorrectIngredients(response);
    }

    @Step("Проверка ответа после создания заказа (с неправильными ингредиентами)")
    public void checkResponseOfCreateOrderWithIncorrectIngredients(Response response) {
        response.then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
