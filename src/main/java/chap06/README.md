#  Chapter 6 - 스트림으로 데이터 수집
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

## 6.1 컬렉터란 무엇인가?
함수형 프로그래밍에서는 ‘무엇’을 원하는지 명시, 어떤 방법으로 얻을지 신경 x

### 6.1.1 고급 리듀싱 기능을 수행하는 컬렉터
- 스트림에 collect를 호출하면 스트림 요소에 (컬렉터로 파라미터화된) 리듀싱 연산이 수행된다. -> 스트림의 각 요소를 방문하면서 컬렉터가 작업을 처리한다.
- 컬렉터를 적용하며 최종 결과를 저장하는 자료구조에 값을 누적한다.

### 6.1.2 미리 정의된 컬렉터
Collectors 제공 메서드 기능
1. 스트림 요소를 하나의 값으로 리듀스하고 요약
2. 요소 그룹화
3. 요소 분할

## 6.2 리듀싱과 요약
```java
long howManyDishes = menu.stream().collect(Collectors.counting());
long howManyDishes2 = menu.stream().count();
```

### 6.2.1 스트림값에서 최대값과 최솟값 검색
```java
Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);
Optional<Dish> mostCaloriesDish = menu.stream
	.collect(maxBy(dishCaloriesComparator));
```

### 6.2.2 요약 연산
```java
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));

double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));

// count, sum, min, average, max 계산
IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
```
### 6.2.3 문자열 연결
```java
menu.stream().map(Dish::getName).collect(joining(","))
```
### 6.2.4 범용 리듀싱 요약 연산
```java
// reducing(초깃값, 정수로의 변환 함수, 합계함수 BinaryOperator)
int totalCalories = menu.stream()
	.collect(reducing(0, Dish::getCalories, (i,j) -> i+j);

Optional<Dish> mostCalorieDish = menu.stream().
	.collect(reducing((d1, d2) -> d1.getCalories()>d2.getCalories() ? d1 : d2) );

int totalCalories = menu.stream()
	.map(Dish::getCalories).reduce(Integer::sun).get();

int totalCalories = menu.stream()
	.mapToInt(Dish::getCalories).sum();
```

## 6.3 그룹화
```java
Map<Dish.Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));

/*
Dishes grouped by type: {MEAT=[pork, beef, chicken], OTHER=[french fries, rice, season fruit, pizza], FISH=[prawns, salmon]}
*/

enum CaloricLevel { DIET, NORMAL, FAT };
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = 	menu.stream().collect(
    groupingBy(dish -> {
        if (dish.getCalories()<=400) return CaloricLevel.DIET;
        else if (dish.getCalories()<=700) return CaloricLevel.NORMAL;
        else return CaloricLevel.FAT;
    })
);
```

### 6.3.1 그룹화된 요소 조작
```java
// 비어있는 항목도 키로 추가
Map<Dish.Type, List<Dish>> caloricDishesByType = 
 menu.stream()
	.collect(groupBy(Dish::getType, 
			filtering(dish->dish.getCalories()>500, toList())));

// 그룹의 각 요리를 관련 이름 목록으로 변환
Map<Dish.Type, List<String>> dishNamesByType = 
	menu.stream()
		.collect(groupBy(Dish::getType, mapping(Dish::getName, toList())));

Map<Dish.Type, Set<String>> dishNamesByType = menu.stream()
	.collect(groupingBy(Dish::getType,
        flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
```

### 6.3.2 다수준 그룹화
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
```

### 6.3.3 서브그룹으로 데이터 수집
```java
// 요리의 종류를 분류하는 컬렉터로 메뉴에서 가장 높은 칼로리를 가진 요리 찾기
Map<Dish.Type, Optional<Dish>> mostCaloricByType = 
	menu.stream()
		.collect(groupBy(Dish::getType,
						maxBy(comparingInt(Dish::getCalories)));
// {FISH=Optional[salmon], OTHER=Optional[pizza], MEAT=Optional[pork]}
```

## 6.4 분할

## 6.5 Collector 인터페이스
## 6.6 커스텀 컬렉터를 구현해서 성능 개선하기
