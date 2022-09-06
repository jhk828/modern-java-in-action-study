# Chapter 5 - 스트림 활용

정리: Yes
발표: 2022/09/06

# 5.1 필터링

## 5.1.1 프레디케이트로 필터링

- `filter` 메서드 : 프레디케이트 (불리언 반환 함수)를 인수로 받아 → 일치하는 요소를 포함하는 스트림 반환

```java
List<Dish> vegetarianMenu = menu.stream()
    .filter(Dish::isVegetarian)
    .collect(toList());
```

## 5.1.2 고유 요소 필터링

- `distinct` 메서드: 고유 요소 스트림 반환
- (고유 여부 : 스트림에서 만든 객체의 hashCode, equals)

```java
List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
numbers.stream()
    .filter(i -> i % 2 == 0)
    .distinct()
    .forEach(System.out::println);
```

# 5.2 스트림 슬라이싱

## 5.2.1 프레디케이트를 이용한 슬라이싱

자바9 - `takeWhile`, `dropWhile`

### takeWhile 활용

- 리스트가 이미 정렬되어 있음 → 320 칼로리보다 크거나 같은 요리가 나올 시 반복 중단

```java
List<Dish> sliceMenu1 = specialMenu.stream()
        .takeWhile(dish -> dish.getCalories() < 320)
        .collect(toList());
```

### dropWhile 활용

- 나머지 요소 (320칼로리보다 큰 요소)
- 무한스트림에서도 동작

```java
List<Dish> sliceMenu2 = specialMenu.stream()
        .dropWhile(dish -> dish.getCalories() < 320)
        .collect(toList());
```

## 5.2.2 스트림 축소

`limit(n)`

- 스트림이 정렬되어 있으면 최대 요소 n개 반환
- 정렬되어 있지 않으면 결과도 정렬x

```java
List<Dish> dishesLimit3 = specialMenu.stream()
    .filter(dish -> dish.getCalories()>300)
    .limit(3)
    .collect(toList());
```

## 5.2.3 요소 건너뛰기

`skip(n)`

- 처음 n개 제외
- 스트림 요소개 n개이하면 빈 스트림 연산

```java
List<Dish> dishes = specialMenu.stream()
        .filter(dish -> dish.getCalories()>300)
        .skip(2)
        .collect(toList());
```

### 퀴즈

처음 등장하는 두 고기 요소 필터링

```java
List<Dish> dishes = specialMenu.stream()
    .filter(d -> d.getType() == Dish.Type.MEAT)
    .limit(2)
    .collect(toList());
```

# 5.3 매핑

SQL로치면 테이블에서 특정 열만 선택

## 5.3.1 스트림의 각 요소에 함수 적용하기

map은 기존의 값을 고친다 보다는 ‘새로운 버전’을 만든다는 개념, 변환/매핑

```java
List<Integer> dishNameLengths = menu.stream() // Stream<Dish>
        .map(Dish::getName) // Stream<String>
        .map(String::length) // Stream<Integer>
        .collect(Collectors.toList());
```

## 5.3.2 스트림 평면화

```java
List<String> words = Arrays.asList("Hello", "World");
List<String[]> list1 = words.stream() // Stream<String>
        .map(word -> word.split("")) // Stream<String[]>
        .distinct()
        .collect(Collectors.toList());
```

### `map`과 `Arrays.stream` 활용

배열 스트림 대신 문자열 스트림이 필요

```java
List<Stream<String>> list2 = words.stream() // Stream<String>
        .map(word -> word.split("")) // Stream<**String[]**>
        .map(Arrays::stream) // Stream<**Stream<String>**>
        .distinct()
        .collect(Collectors.toList());
```

### flatMap 사용

- `flatMap`은 각 배열을 스트림이 아니라 스트림의 콘텐츠로 매핑

```java
List<String> uniqueCharacters = words.stream() // Stream<String>
        .map(word -> word.split("")) // Stream<String[]>
        .flatMap(Arrays::stream) // Stream<String>  H e l l o ,,
        .distinct()
        .collect(Collectors.toList());
System.out.println(uniqueCharacters); // [H, e, l, o, W, r, d]
```



- 퀴즈
  
  ```java
  // 1. 숫자를 인수로 받아 제곱근 반환
  List<Integer> numbers = Arrays.asList(1,2,3,4,5);
  List<Integer> squares
      = numbers.stream()
          .map(n -> n*n)
          .collect(toList());
  ```
  
  ```java
  // 2. 두 개의 리스트 -> 모든 숫자 쌍의 리스트 반환
  List<int[]> pairs = numbers1.stream() // Stream<Integer>
          .flatMap(x -> numbers2.stream()
                          .map(y -> new int[] {x, y})
          ) // Stream<int[]>
          .collect(toList());
  for(int[] p: pairs) System.out.println(Arrays.toString(p));
  
  List<Stream<int[]>> pairs2 = numbers1.stream() // Stream<Integer>
          .map(x -> numbers2.stream()
                  .map(y -> new int[] {x, y})
          ) // Stream<Stream<int[]>>
          .collect(toList());
  
  // 3. 합이 3으로 나누어 떨어지는 쌍만 반환
  List<int[]> pairs3 = numbers1.stream()
          .flatMap(x -> numbers2.stream()
                  .filter(y -> (x+y)%3==0)
                  .map(y -> new int[] {x, y})
          )
          .collect(toList());
  ```

# 5.4 검색과 매칭

anyMatch, allMatch, noneMatch → 스트리쇼트서킷 기법, 자바의 `&&`, `||`

## 5.4.1 프레디케이트가 적어도 한 요소와 일치하는지 확인

- `anyMatch`

```java
if (menu.stream.anyMatch(Dish::isVegetarian)) {
    System.out.println("The menu is (somewhat) vegerarian friendly!!");
}
```

## 5.4.2 프레디케이트가 모든 요소와 일치하는지 검사

- `allMatch`, `noneMatch`

```java
boolean isHealthy
    = menu.stream()
        .allMatch(dish -> dish.getCalories() < 1000);

boolean isHealthy2
    = menu.stream()
        .noneMatch(dish -> dish.getCalories() >= 1000);
```

### ❓쇼트서킷

- allMatch, noneMatch, findFirst, findAny, limit 연산은 모든 스트림 요소를 처리하지 않고도 결과 반환 가능

## 5.4.3 요소검색

- 임의요소 반환 : `findAny` → `ifPresent`

```java
 menu.stream()
    .filter(Dish::isVegetarian)
    .findAny() // 반환: Optional<Dish>
    .ifPresent(dish -> System.out.println(dish.getName());
```

### `Optional`

- isPresent()
- ifPresent(Comsumber<T> block) : 값이 있으면 실행
- T get() : 값이 존재하면 반환, 없으면 `NoSuchElementException`
- T orElse(T others) : 값이 있으면 반환, 없으면 기본값 반환

## 5.4.4 첫 번째 요소 찾기

- `findFirst`

```java
List<Integer> someNumbers = Arrays.asList(1,2,3,4,5);
Optional<Integer> firstSquareDivisibleByThree 
    = someNumbers.stream()
        .map(n -> n*n)
        .filter(n -> n%3 == 0)
        findFirst(); // 9
```

# 5.5 리듀싱

리듀싱 연산 : 모든 스트림 요소를 처리해서 값으로 도출

## 5.5.1 요소의합

```java
int sum = numbers.stream().reduce(0, (a,b) -> a+b);
int sum2 = numbers.stream().reduce(0, Integer::sum);
// reduce (초기값, 두 수를 조합해서 새로운 값 만드는 BinarayOperator<T>)
// 스트림이 하나의 값으로 줄어들 때 까지 람다는 각 요소를 반복해서 조합함

// 초기값 없음
Optinal<Integer> sum = numbers.stream().reduce((a,b) -> (a+b));
```

## 5.5.2 최대값과 최소값

```java
Optional<Integer> max = numbers.stream().reduce(Integer::max);
Optional<Integer> min = numbers.stream().reduce(Integer::min);
Optional<Integer> min2 = numbers.stream().reduce((x,y) -> x<y ? x:y);
```

- 퀴즈
  
  ```java
  // 스트림의 요리 개수
  // 스트림 각 요소를 1로 매핑, reduce로 합계
  int count
      = menu.stream()
          .map(d -> 1)
          .reduce(0, (a,b) -> a+b);
  long count = menub.stream().count();
  ```

# 5.6 실전 연습

- 실전 연습
  
  ```java
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
  ```

# 5.7 숫자형 스트림

## 5.7.1 기본형 특화 스트림

### 숫자 스트림으로 매핑

- mapToDouble, mapToDouble, mapToLong

```java
int calories = menu.strean()
            .mapToInt(Dish::getCalories) // IntStream 반환, **Stream<Integer>가 아님**
            .sum(); // max,min,average
```

### 객체 스트림으로 복원하기

```java
IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
Stream<Integer> stream = intStream.boxed();
```

### 기본값 : OptionalInt

```java
OptionalInt maxCalories = menu.stream()
        .mapToint(Dish::getCalories)
        .max();
// 최대값이 없는 상황에서 사용할 기본값
int max = maxCalories.orElse(1);
```

## 5.7.2 숫자범위

```java
IntStream evenNumbers 
= IntStream.rangeClosed(1,100) // 1<= x<= 100 cf) range: 1<x<100
    .filter(n -> n%2 == 0);
System.out.println(evenNumbers.count)); // 50
```

## 5.7.3 숫자 스트림 활용 : 피타고라스 수

- 5.7.3 숫자 스트림 활용 : 피타고라스 수
  
  ```java
  // 1. 세수 표현하기
  new int[] {3,4,5};
  
  // 2. 좋은 필터링 조합
  filter(b-> Math.sqrt(a*a + b*b) % 1 == 0)
  
  // 3. 집합 생성
  // a,b와 함께 피타고라수 수를 구성하는 세 번째 수 찾기
  stream.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
      .map(b -> new int[] {a, b, (int) Math.sqrt(a*a + b*b)}); 
  
  // 4. b값 생성 : mapToObj (개체값 스트림 반환)
  InStream.rangeClose(1, 10)
      .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
      .mapToObj(b -> new int[] {a, b, (int) Math.sqrt(a*a + b*b)}); 
  
  // 5. a 값 생성과 완성
  Stream<int[]> pythagoreanTriples = IntStream.rangeClosed(1, 100).boxed()
      .flatMap(a ->
          IntStream.rangeClosed(a, 100)
              .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0).boxed()
              .map(b -> new int[] { a, b, (int) Math.sqrt(a * a + b * b) }));
  pythagoreanTriples.forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
  ```

# 5.8 스트림 만들기

```java
// 5.8.1 값으로 스트림 만들기
Stream<String> stream = Stream.of("Modern","Java","In","Action");
stream.map(String::toUpperCase).forEach(System.out::println);
Stream<String> emptyStream = Stream.empty();

// 5.8.2 null이 될 수 있는 객체로 스트림 만들기
// 원래
String homeValue = System.getProperty("home");
Stream<String> homeValueStream
    = homeValue == null ? Stream.empty() : String.of(value);
// 자바9
Stream<String> homeValueStream
    = Stream.ofNullable(System.getProperty("home"));

// 응용 -> 이게왜 유용하다는거죵
Stream<String> values
 = Stream.of("config","home","user")
        .flatMap(key -> Stream.o

// 5.8.3 배열로 스트림 만들기
int[] numbers = {2,3,5,7,11,13};
int sum = Arrays.stream(numbers).sum();

// 5.8.4 파일로 스트림 만들기
```

## 5.8.5 함수로 무한 스트림 만들기

### `iterate` 메서드

```java
// <퀴즈> iterate를 이용한 피보나치
Stream.iterate(new int[] { 0, 1 }, t -> new int[] { t[1], t[0] + t[1] })
  .limit(10)
  .forEach(t -> System.out.printf("(%d, %d)", t[0], t[1]));
```

### `generate` 메서드

- generate는 Supplier<T>를 인수로 받아서 새로운 값 생산

```java
// 다음 피보나치 요소 반환
IntSupplier fib = new IntSupplier() {
    private int previous = 0;
    private int current = 1;
    @Override
    public int getAsInt() {
        int nextValue = previous + current;
        previous = current;
        current = nextValue;
        return previous;
    }
};

IntStream.generate(fib)
    .limit(10)
    .forEach(System.out::println);
```