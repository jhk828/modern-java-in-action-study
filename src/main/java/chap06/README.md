# 6.1 컬렉터란 무엇인가?

```java
// 통화별로 트랜잭션을 그룹화한 코드 (명령어 버전)
Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>();
for(Transaction transaction : transactions) {
    Currency currency = transaction.getCurrency();
    List<Transaction> transactionForCurrency = transactionsByCurrencies.get(currency);
    if(transactionForCurrency == null) {
        transactionForCurrency = new ArrayList<>();
        transactionsByCurrencies.put(currency, transactionForCurrency);
    }
    transactionForCurrency.add(transaction);
}
// Collector 인터페이스 구현을 collect 메서드에 전달
Map<Currency, List<Transaction>> transactionsByCurrencies = transactions.stream()
        .collect(groupingBy(Transaction::getCurrency));
System.out.println(transactionsByCurrencies);
```

함수형 프로그래밍에서는 ‘무엇’을 원하는지 명시, 어떤 방법으로 얻을지 신경 x

## 6.1.1 고급 리듀싱 기능을 수행하는 컬렉터

- 스트림에 collect를 호출하면 스트림 요소에 (컬렉터로 파라미터화된) 리듀싱 연산이 수행된다. -> 스트림의 각 요소를 방문하면서 컬렉터가 작업을 처리한다.
- 컬렉터를 적용하며 최종 결과를 저장하는 자료구조에 값을 누적한다.

### 6.1.2 미리 정의된 컬렉터

Collectors 제공 메서드 기능

1. 스트림 요소를 하나의 값으로 리듀스하고 요약
2. 요소 그룹화
3. 요소 분할

# 6.2 리듀싱과 요약

```java
long howManyDishes = menu.stream().collect(Collectors.counting());
long howManyDishes2 = menu.stream().count();
```

## 6.2.1 스트림값에서 최대값과 최솟값 검색

```java
Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);
Optional<Dish> mostCaloriesDish = menu.stream
    .collect(maxBy(dishCaloriesComparator));
```

## 6.2.2 요약 연산

```java
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));

double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));

// count, sum, min, average, max 계산
IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
```

## 6.2.3 문자열 연결

```java
String shortMenu = menu.stream().map(Dish::getName).collect(joining(","));
```

## 6.2.4 범용 리듀싱 요약 연산

```java
// reducing(초깃값, 정수로의 변환 함수, 합계함수 BinaryOperator)
int totalCalories = menu.stream()
    .collect(**reducing**(0, Dish::getCalories, (i,j) -> i+j);

// 시작값 없음 - 빈 스트림이 올 수도 있으니 Optional
Optional<Dish> mostCalorieDish = menu.stream().
    .collect(
            **reducing**((d1, d2) -> d1.getCalories()>d2.getCalories() ? d1 : d2) );

// Integer클래스의 sum 메서드 참조
int totalCalories = menu.stream()
    .collect(**reducing**(0, Dish::getCalories, Integer::sum);

int totalCalories = menu.stream()
    .map(Dish::getCalories)
    .reduce(Integer::sum).get();

int totalCalories = menu.stream()
    .mapToInt(Dish::getCalories).sum();
```

- quiz
  
  ```java
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
  ```

# 6.3 그룹화

```java
Map<Dish.Type, List<Dish>> dishesByType = menu.stream()
    .collect(groupingBy(Dish::getType)); // 메서드 참조
/*
Dishes grouped by type: {MEAT=[pork, beef, chicken], OTHER=[french fries, rice, season fruit, pizza], FISH=[prawns, salmon]}
*/

// 람다 표현식
enum CaloricLevel { DIET, NORMAL, FAT };
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream()
    .collect(
    groupingBy(dish -> {
        if (dish.getCalories()<=400) return CaloricLevel.DIET;
        else if (dish.getCalories()<=700) return CaloricLevel.NORMAL;
        else return CaloricLevel.FAT;
    })
);
```

## 6.3.1 그룹화된 요소 조작

```java
// 비어있는 항목도 키로 추가
Map<Dish.Type, List<Dish>> caloricDishesByType =
 menu.stream()
    .collect(groupBy(Dish::getType,
            filtering(dish->dish.getCalories()>500, **toList()**)));

// 그룹의 각 요리를 관련 이름 목록으로 변환
Map<Dish.Type, List<String>> dishNamesByType =
    menu.stream()
        .collect(groupBy(Dish::getType, 
                                            **mapping**(Dish::getName, toList())));

// 중복 테스 제거
Map<Dish.Type, Set<String>> dishNamesByType = menu.stream()
    .collect(groupingBy(Dish::getType,
        flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
```

## 6.3.2 다수준 그룹화

```java
Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel =
    menu.stream().collect(
    groupingBy(Dish::getType, // 첫 번째 수준의 분류 함수
        groupingBy((Dish dish) -> { // 두 번째 수준의 분류 함수
          if(dish.getCalories()<=400) { return CaloricLevel.DIET; }
          else if(dish.getCalories()<=700) { return CaloricLevel.NORMAL; }
          else{ return CaloricLevel.FAT; }
        })
    )
    );
// {MEAT={NORMAL=[beef], FAT=[pork], DIET=[chicken]}, 
// OTHER={NORMAL=[french fries, pizza], DIET=[rice, season fruit]}, 
// FISH={NORMAL=[salmon], DIET=[prawns]}}
```

## 6.3.3 서브그룹으로 데이터 수집

```java
// 요리의 종류를 분류하는 컬렉터로 메뉴에서 가장 높은 칼로리를 가진 요리 찾기
Map<Dish.Type, Optional<Dish>> mostCaloricByType =
    menu.stream()
        .collect(groupBy(Dish::getType,
                        maxBy(comparingInt(Dish::getCalories)));
// {FISH=Optional[salmon], OTHER=Optional[pizza], MEAT=Optional[pork]}

// 컬렉터 결과를 다른 형식에 적용
// collectAndThen : 적용할 컬렉터와 변환 함수를 인수로 받아 다른 컬렉터를 반환
Map<Dish.Type, Dish> mostCaloricByType = 
    menu.stream()
        .collect(groupBy(Dish::getType, // 분류 함수
                            collectingAndThen(maxBy(comparingInt(Dish::getCalories)), // 감싸인 컬렉터
    **Optional::get**))); // 변환 함수
```

### groupingBy와 함께 사용하는 다른 컬렉터 예제

```java
// mapping은 입력 요소 누적 전에 매핑 함수 적용 - 다양한 형식의 객체를 주어진 형식의 컬렉터에 맞게 변환
Map<Dish.Type, Set<CaloricLevel>> caloricLevlsByType = 
menu.stream().collect(
        groupingBy(Dish::getType, mapping(
            dish -> {
              if (dish.getCalories() <= 400) {  return CaloricLevel.DIET; }
              else if (dish.getCalories() <= 700) { return CaloricLevel.NORMAL; }
              else { return CaloricLevel.FAT; }
            },
            toSet()
        ))
      );
```

# 6.4 분할

분할 함수 프리디케이트를 분류 함수로 사용

```java
// 채식 / 채식 아닌 요리 분류
Map<Boolean, List<Dish>> partitionedMenu = 
    menu.stream()
    .collect(partitioningBy(Dish::isVegetarian)); // 분할 함수
// 모든 채식 요리 얻기
List<Dish> vegetarianDishes = partitionedMenu.get(true);
```

## 6.4.1 분할의 장점

```java
// 참, 거짓 두가지 모두의 스트림 리스트 유지
// 컬렉터를 두 번째 인수로 전달하는 오버로드된 버전 partitionedBy
Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = 
    menu.stream()
        .collect(
            partitionedBy(Dish::isVegetarian, // 분할 함수
                                        **groupingBy(Dish::getType)**) // 두 번째 컬렉터
        );

// 채식 요리와 각각의 그룹에서 가장 칼로리가 높은 요리 찾기
Map<Boolean, Dish> mostCaloricPartitionedByVegetarian = 
    menu.stream()
        .collect(
            partitionedBy(Dish::isVegetarian,
                    collectingAndThen(maxBy(comparingInt(Dish::getCalories)),
                                                        Optional::get)
            )
        );
```

# 6.5 Collector 인터페이스

Collector 인터페이스 메소드

```java
public interface Collector<T, A, R> {
    // 1. 새로운 결과 컨테이너 만들기
    Supplier<A> supplier(); 
    // 2. 리듀싱 연산 수행 함수 반환
    BiConsumer<A, T> accumulator(); 
    // 3. 최종 결과 변환
    Function<A, R> finisher(); 
    // 4. 스트림 두 번째 수집파트에서 수집한 항목 리스트를 첫 번째 서브파트 결과 리스트의 뒈에 추가
    BinaryOperator<A> combiner(); 
    // 5. 스트림을 병렬로 리듀스 할 것인지, 어던 최적화 선택할 지 힌트 제공
    Set<Characteristics> characteristics(); 
}
// T : 수집될 스트림 항목의 제네릭 형식
// A : 누적자, 수집 중간 결과 누적 객체 형식
// R : 수집 연산 결과 객체 형식
```

## 6.5.2 응용하기

- [커스텀 ToListCollection 구현하기](ToListCollector.java)

# 6.6 커스텀 컬렉터를 구현해서 성능 개선하기

- [소수/비소수 판별](PartitionPrimeNumbers.java)

