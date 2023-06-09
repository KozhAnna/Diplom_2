package ru.yandex.praktikum.service;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.RestAssured;

public class Service {

    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";
    public static final String BASE_PATH = "api";

    public static void setupSpecification() {
        RestAssured.requestSpecification = getRequestSpecification();
        RestAssured.responseSpecification = getResponseSpecification();
    }

    private static RequestSpecification getRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath(BASE_PATH)
                .setContentType(ContentType.JSON)
                .build();
    }
    private static ResponseSpecification getResponseSpecification() {
        return new ResponseSpecBuilder()
                .build();
    }

}
