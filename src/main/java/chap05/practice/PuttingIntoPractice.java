package chap05.practice;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PuttingIntoPractice {
    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        // 질의 1: 2011년부터 발생한 모든 거래를 찾아 값으로 정렬(작은 값에서 큰 값).
        List<Transaction> tr2011 = transactions.stream()
                .filter(tr -> tr.getYear() == 2011)
                .sorted(comparing(Transaction::getValue))
                .collect(toList());
        System.out.println(">> q1: \n" + tr2011);

        // 질의 2: 거래자가 근무하는 모든 고유 도시는?
        List<String> cities = transactions.stream()
                .map(tr -> tr.getTrader().getCity())
                .distinct()
                .collect(toList());
        System.out.println(">> q2: \n" + cities);

        // 질의 3: Cambridge의 모든 거래자를 찾아 이름으로 정렬
        List<Trader> traders = transactions.stream()
                .map(Transaction::getTrader)
                .filter(td -> td.getCity().equals("Cambridge"))
                .distinct()
                .sorted(comparing(Trader::getName))
                .collect(toList());
        System.out.println(">> q3: \n" + traders);

        // 질의 4: 알파벳 순으로 정렬된 모든 거래자의 이름 문자열을 반환
        String traderStr = transactions.stream()
                .map(tr -> tr.getTrader().getName())
                .distinct()
                .sorted()
                .reduce("", (n1, n2) -> n1+n2);
        System.out.println(">> q4: \n" + traderStr);

        // 질의 5: Milan에 거주하는 거래자가 있는가?
        boolean milanBased = transactions.stream()
                .anyMatch(tr -> "Milan".equals(tr.getTrader().getCity()));
        System.out.println(">> q5: \n" + milanBased);

        // 질의 6: Cambridge에 사는 거래자의 모든 거래내역 출력
        System.out.println(">> q6:");
        transactions.stream()
                .filter(t -> "Cambridge".equals(t.getTrader().getCity()))
                .map(Transaction::getValue)
                .forEach(System.out::println);

        // 질의 7: 전체 트랜잭션 중 최대값
        int highestValue = transactions.stream()
                .map(Transaction::getValue)
                .reduce(0, Integer::max);
        System.out.println(">> q7:" + highestValue);

        // 질의 8 : 전체 트랜잭션 중 최소값
        Optional<Transaction> smallestTransaction = transactions.stream()
                .min(comparing(Transaction::getValue));
        // 거래가 없을 때 기본 문자열을 사용할 수 있도록발견된 거래가 있으면 문자열로 바꾸는 꼼수를 사용함(예, the Stream is empty)
        System.out.println(smallestTransaction.map(String::valueOf).orElse("No transactions found"));
    }
}
