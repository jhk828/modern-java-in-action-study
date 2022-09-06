package chap05;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NumericStreams {
    public static void main(String[] args) {
        // 피타고라스 수
        Stream<int[]> pythagoreanTriples = IntStream.rangeClosed(1, 100).boxed()
            .flatMap(a ->
                IntStream.rangeClosed(a, 100)
                    .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0).boxed()
                    .map(b -> new int[] { a, b, (int) Math.sqrt(a * a + b * b) }));
        pythagoreanTriples.forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));

    }
}
