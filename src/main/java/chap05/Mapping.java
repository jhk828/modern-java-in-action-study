package chap05;

import chap04.Dish;

import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

import static chap04.Dish.menu;

public class Mapping {
    public static void main(String[] args) {
        // 5-3. 매핑
        // map
        List<Integer> dishNameLengths = menu.stream()
                .map(Dish::getName)
                .map(String::length)
                .collect(toList());
        System.out.println(dishNameLengths);

        // 스트림 평면화
        List<String> words = Arrays.asList("Hello", "World");
        List<String[]> list1 = words.stream()
                .map(word -> word.split(""))
                .distinct()
                .collect(toList());

        // map과 Arrays.steam 활용
        System.out.println(">> map, Arrays.stream");
        List<Stream<String>> list2 = words.stream()
                .map(word -> word.split(""))
                .map(Arrays::stream)
                .distinct()
                .collect(toList());

        words.stream()
                .map(word -> word.split(""))
                .map(Arrays::stream)
                .forEach(System.out::println);
        // java.util.stream.ReferencePipeline$Head@566776ad
        // java.util.stream.ReferencePipeline$Head@6108b2d7

        // flatMap
        System.out.println(">> flatMap");
        List<String> uniqueCharacters = words.stream()
                .map(word -> word.split(""))
                .flatMap(Arrays::stream)
                // H e l l o ,,
                .distinct()
                .collect(toList());
        System.out.println(uniqueCharacters); // [H, e, l, o, W, r, d]

        // <퀴즈>
        // 1. 숫자를 인수로 받아 제곱근 반환
        List<Integer> numbers = Arrays.asList(1,2,3,4,5);
        List<Integer> squares = numbers.stream()
                .map(n -> n*n)
                .collect(toList());
        System.out.println(">> q1. squares: " + squares);

        // 2. 두 개의 숫자 리스트 -> 모든 숫자 쌍의 리스트 반환
        List<Integer> numbers1 = Arrays.asList(1,2,3);
        List<Integer> numbers2 = Arrays.asList(3,4);

        System.out.println(">> q2. flatMap");
        List<int[]> pairs = numbers1.stream()
                .flatMap(x -> numbers2.stream()
                                .map(y -> new int[] {x, y})
                )
                .collect(toList());
        for(int[] p: pairs) System.out.println(Arrays.toString(p));

        System.out.println(">> q2. map");
        List<Stream<int[]>> pairs2 = numbers1.stream()
                .map(x -> numbers2.stream()
                        .map(y -> new int[] {x, y})
                )
                .collect(toList());

        // 3. 합이 3으로 나누어 떨어지는 쌍만 반환
        List<int[]> pairs3 = numbers1.stream()
                .flatMap(x -> numbers2.stream()
                        .filter(y -> (x+y)%3==0)
                        .map(y -> new int[] {x, y})
                )
                .collect(toList());
    }
}
