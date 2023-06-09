package ru.yandex.praktikum.service;

import ru.yandex.praktikum.order.Ingredient;

import java.util.ArrayList;

public class IngredientsResponse {

    private boolean success;
    private ArrayList<Ingredient> data;

    public IngredientsResponse(){
    };

    public IngredientsResponse(boolean success, ArrayList<Ingredient> data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }
    public ArrayList<Ingredient> getData() {
        return data;
    }
}
