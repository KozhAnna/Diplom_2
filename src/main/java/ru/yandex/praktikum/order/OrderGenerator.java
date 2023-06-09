package ru.yandex.praktikum.order;

import ru.yandex.praktikum.service.IngredientsResponse;

import java.util.ArrayList;
import java.util.Random;

public class OrderGenerator {

    private ArrayList<String> data = new ArrayList<>();
    private Random random = new Random();
    private static int COUNT_INGREDIENTS = 15;

    public ArrayList <String> createRandomOrder(IngredientsResponse ingredients){
        int n = 1 + random.nextInt(COUNT_INGREDIENTS-1);
        for (int i = 0; i < n; i++) {
            int ingIndex = random.nextInt(COUNT_INGREDIENTS);
            data.add(ingredients.getData().get(ingIndex).getId());
        }
        return data;
    }
}
