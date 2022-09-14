# Chapter 4 - 스트림 소개
# 4.1 스트림이란 무엇인가?

- Java 8
- 선언형 - 데이터를 SQL처럼 질의로 표현 가능 (↔  루프, if 등 제어 블록)
- 멀티스레드 - `parallelStream()`
- 고수준 빌딩 블록 (filter, sorted, map, collect)
    - 데이터 처리 과정을 병렬화하면서 스레드와 락 걱정 x

# 4.2 스트림 시작하기

1. 연속된 요소
    - 컬렉션은 자료 구조 - 시간, 공간, 복잡성, 요소 저장 및 접근 연산이 주
    - 스트림은 표현 계산식이 주
2. 소스
    - 스트림은 컬렉션, 배열, I/O 등 데이터 제공 소스로부터 데이터를 소비
3. 데이터 처리 연산
    - 함수형 언어 연산, 데이터베이스와 비슷한 연산
4. 파이프라이닝 : 스트림끼리 연산, 파이프 구성할 수 있도록 스트림 자신을 반환 → 게으름, 쇼트서킷 최적화
5. 내부 반복

```java
import static java.util.stream.Collectors.toList;

List<String> threeHighCaloricDishNames = menu.stream()
    .filter(dish -> dish.getCalories() > 300)
    .map(Dish::getName)
    .limit(3)
    .collect(Collectors.toList());
```

# 4.3 스트림과 컬렉션

1. 데이터를 언제 계산하느냐
    - 컬렉션은 현재 자료구조가 포함한 모든 값을 메모리에 저장 → 컬렉션 추가 전에 모든 값이 계산되어야 함
    - 스트림은 요청할 때만 요소를 계산 → 스트림에 요소 추가, 제거 가능
2. 스트림은 딱 한번만 탐색할 수 있다
3. 외부 반복 vs 내부 반복
    - 컬렉션은 사용자가 직접 요소를 반복 → 외부 반복 → 병렬성 스스로 관리
    - 스트림은 반복을 알아서 처리, 결과 스트림값을 어딘가에 저장해주는 내부반복

    ```java
    // 컬렉션 : 내부적으로 숨겨졌던 반복자를 사용한 외부 반복
    List<String> names = new ArrayList<>();
    Iterator<Dish> iterator = menu.iterator();
    while (iterator.hasNext()) { // 명시적 반복
        names.add(iterator.next().getName());
    }
    
    // 스트림 : 내부 반복
    List<String> names2 = menu.stream()
            .map(Dish::getName) // map 메서드를 getName메서드로 파라미터화해서 요리명을 추출한다.
            .collect(Collectors.toList());
    ```


# 4.4 스트림 연산

## 4.4.1 중간 연산

- 단말 연산을 스트림 파이프라인에서 실행하기 전까지는 아무 연산도 수행하지 않는다 → 게으르다 (lazy)
- 중간 연산을 합친 다음에 합쳐진 중간 연산을 최종 연산으로 한 번에 처리하기 때문

```java
// lazy
List<String> names3 = menu.stream()
        .filter(dish -> {
            System.out.println("fitering: " + dish.getName());
            return dish.getCalories()>300;
        })
        .map(dish -> {
            System.out.println("mapping: " + dish.getName());
            return dish.getName();
        })
        .limit(3)
        .collect(toList());
System.out.println(names3);
```

1. 300칼로리가 넘는 요리 중 처음 3개만 선택됨 → 쇼트서킷
2. filter, map은 다른 연산이지만 한 과정으로 병합됨 → 루프퓨전

## 4.4.2 최종 연산

- 보통 List, Integer, void 등 스트림 이외의 결과 반환
- forEach, count

```java
// 최종 연산
menu.stream().forEach(System.out::println);
```

## 4.4.3 스트림 이용하기

중간연산 : filter, map, limit, sorted, distinct

최종연산 : forEach, count, collect