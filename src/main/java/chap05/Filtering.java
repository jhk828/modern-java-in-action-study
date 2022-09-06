package chap05;

import chap04.Dish;

import java.util.Arrays;
import java.util.List;

import static chap04.Dish.menu;
import static java.util.stream.Collectors.toList;

public class Filtering {
    public static void main(String[] args) {
        // 5-1. 필터링
        // 프레디케이트로 필터링
        System.out.println(">> Filtering with a predicate");
        List<Dish> vegetarianMenu = menu.stream()
                .filter(Dish::isVegetarian)
                .collect(toList());
        vegetarianMenu.forEach(System.out::println);

        // 고유 요소로 필터링
        System.out.println(">> Filtering unique elements:");
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
            .filter(i -> i % 2 == 0)
            .distinct()
            .forEach(System.out::println);

        // 5-2. 스트림 슬라이스
        List<Dish> specialMenu = Arrays.asList(
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER));

        // 프레디케이트로 슬라이싱
        System.out.println(">> Sorted menu sliced with takeWhile():");
        List<Dish> slicedMenu1 = specialMenu.stream()
                .takeWhile(dish -> dish.getCalories() < 320)
                .collect(toList());
        slicedMenu1.forEach(System.out::println);

        System.out.println(">> Sorted menu sliced with dropWhile():");
        List<Dish> slicedMenu2 = specialMenu.stream()
                .dropWhile(dish -> dish.getCalories() < 320)
                .collect(toList());
        slicedMenu2.forEach(System.out::println);

        // 스트림 축소
        List<Dish> dishesLimit3 = menu.stream()
            .filter(dish -> dish.getCalories()>300)
            .limit(3)
            .collect(toList());
        System.out.println(">> Truncating a stream:");
        dishesLimit3.forEach(System.out::println);

        // 요소 건너뛰기
        List<Dish> dishesSkip2 = menu.stream()
            .filter(dish -> dish.getCalories()>300)
            .skip(2)
            .collect(toList());
        System.out.println(">> Skipping elements:");
        dishesSkip2.forEach(System.out::println);

        // <퀴즈>
        List<Dish> dishes = specialMenu.stream()
                .filter(d -> d.getType() == Dish.Type.MEAT)
                .limit(2)
                .collect(toList());
    }
}
