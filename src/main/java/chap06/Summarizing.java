package chap06;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.function.BinaryOperator;

import static chap06.Dish.menu;
import static java.util.stream.Collectors.*;

public class Summarizing {
    // 6.2 리듀싱과 요약
    public static void main(String[] args) {
        System.out.println("Nr. of dishes: " + howManyDishes());
        System.out.println("The most caloric dish is: " + findMostCaloricDish());
        System.out.println("The most caloric dish is: " + findMostCaloricDishUsingComparator());
        System.out.println("Total calories in menu: " + calculateTotalCalories());
        System.out.println("Average calories in menu: " + calculateAverageCalories());
        System.out.println("Menu statistics: " + calculateMenuStatistics());
        System.out.println("Short menu comma separated: " + getShortMenuCommaSeparated());
    }

    private void quiz() {
        String shortMenu1 = menu.stream().map(Dish::getName).collect(joining(", "));

        String shortMenu2 = menu.stream()
                .map(Dish::getName)
                .collect(reducing((s1,s2) -> s1+s2))
                .get();

        // reduce는 두 인수를 받아 같은 형식을 반환하는 함수를 반환
//        String shortMenu3 = menu.stream()// Stream<Dish>
//                .collect(reducing((d1,d2) -> d1.getName() + d2.getName())) // Optional<Dish>
//                .get()

        // 빈 문자열을 포함하는 누적자로 리듀싱
        // - Stream Dish를 방문하면서 Dish를 요리명으로 변환한 후 누적자로 추가
        String shortMenu4 = menu.stream()
                .collect(reducing("", Dish::getName, (s1, s2) -> s1 + s2));
    }

    private static long howManyDishes() {
        return menu.stream().collect(counting());
    }

    private static Dish findMostCaloricDish() {
        return menu.stream().collect(reducing((d1, d2) -> d1.getCalories()>d2.getCalories() ? d1 : d2)).get();
    }

    private static Dish findMostCaloricDishUsingComparator() {
        Comparator<Dish> dishCaloriesComparator = Comparator.comparing(Dish::getCalories);
        BinaryOperator<Dish> moreCaloricOf = BinaryOperator.maxBy(dishCaloriesComparator);
        return menu.stream().collect(reducing(moreCaloricOf)).get();
    }

    private static int calculateTotalCalories() {
        return menu.stream().collect(summingInt(Dish::getCalories));
    }

    private static Double calculateAverageCalories() {
        return menu.stream().collect(averagingInt(Dish::getCalories));
    }

    private static IntSummaryStatistics calculateMenuStatistics() {
        return menu.stream().collect(summarizingInt(Dish::getCalories));
    }

    private static String getShortMenuCommaSeparated() {
        return menu.stream().map(Dish::getName).collect(joining(","));
    }

}
