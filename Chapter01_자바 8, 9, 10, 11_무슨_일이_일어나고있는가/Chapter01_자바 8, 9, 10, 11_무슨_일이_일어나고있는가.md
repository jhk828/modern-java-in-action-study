# Chapter 1 - 자바 8, 9, 10, 11 : 무슨 일이 일어나고 있는가

정리: Yes
발표: 2022/08/17

# 1.1 역사의 흐름은 무엇인가

자바 8부터 간결한 코드, 멀티코어 프로세스 쉬운 활용 (병렬 실행) 두가지 위주로 큰 변화가 일어남

# 1.2 왜 아직도 자바는 변화하는가?

### 1.2.1 프로그래밍 언어 생태계에서 자바의 위치

빅데이터 → 병렬 프로세싱을 활용하기 위해 새로운 개념들이 도입됨

## 1.2.2 스트림 처리

스트림

- 한번에 한 개씩 만들어지는 연속적인 데이터 항목들의 모임
- 유닉스 명령어 연결과 비슷함
- Java 8 - `[java.util.stream](http://java.util.stream)`
  - `Stream<T>` : T 형식으로 구성된 일련의 항목
  - 하려는 작업을 (데이터베이스 질의처럼) 고수준으로 추상화 - 일련의 스트림으로 만들어 처리
  - 스트림 파이프라인으로 입력 부분을 여러 CPU에 쉽게 할당 → 병렬성도 얻을 수 있다.

## 1.2.3 동작 파라미너화로 메서드에 코드 전달하기

어떠헥 정렬 시킬 것인지 sort 메서드에 명령을 내려야 한다.

1. Comparaotr 객체를 만들어서 sort에 넘겨주기 → 복잡
2. `comparingUsingCustomerId` 같은 메서드를 구현하여  다른 메서드의 인수로 넘겨주기 (Java 8) ⇒ 동작 파라미터화 (behavior parameterization)

스트림 API는 연산의 동작을 파라미터화할 수 있는 코드를 전달한다는 사상에 기초한다.

## 1.2.4 병렬성과 공유 가변 데이터

- “병렬성을 공짜로 얻는다" ⇒ 대신 스트림 메서드로 전달하는 코드의 동작 방식을 안전하게 바꿔야 함
- 공유된 가변 데이터 (shared mutable data)에 접근하지 않아야 함
  1. 순수 (pure) 함수
  2. 부작용 없는 (side-effect-free) 함수
  3. 상태 없는 (stateless) 함수
- 기존의 `synchronized`로 공유된 가변 데이터 보호 → 다중 프로세싱 코어에 악영향
- 공유되지 않은 가변 데이터 (no-shared mutable data), 메서드, 함수 코드를 다른 메서드로 전달하는 기능은 함수형 프로그래밍 패러다임의 핵심
- 명령형 프로그래밍 (imperative programming) 패러다임에서는 일련의 가변 상태로 프로그램을 정의

# 1.3 자바 함수

자바에서 조작할 수 있는 값 → 일급 (first-class, citizens)

1. 기본값
2. 객체 (객체의 참조) - new, 팩토리 메서드, 라이브러리함수로 객체 값 얻을 수 있음 → 클래스의 인스턴스
3. 메서드 → 자바 이급 시민이었으나, 자바 8부터는 일급 시민으로 만듦

## 1.3.1 메서드와 람다를 일급 시민으로

자바 8에서 메서드를 값으로 취급할 수 있는 기능 → 스트림 같은 다른 자바 8의 기능의 토대 제공

메서드 참조 (method reference)

```java
File[] hiddenFiles = new File(".").listFiles(new FileFilter() {
    public boolean accept(Filfe file) {
        return file.isHidden();
    }
});

// 메서드 참조 -> 함수 대신 메서드라는 용어 사용
File[] hiddenFiles = new File(".").listFiles(**Files::isHidden**);
```

### 람다 : 익명 함수

- 자바 8에서는 (기명, named) 메서드와 **람다 (익명 함수, annonymous function)**도 값으로 취급
- 람다로 구현된 프로그램은 함수형 프로그램, 즉 ‘함수를 일급값을 넘겨주는 프로그램'

## 1.3.2 코드 넘겨주기 : 예제

```java
public interface Predicate<T> {
    boolean test(T t);
}

static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple: inventory) {
        if (p.test(apple)) {
            result.add(apple);
        }
    }
}

// 메소드 호출
filterApples(inventory, Apple::isGreenApple);
filterApples(inventory, Apple::isHeavyApple);
```

### 프리디케이트 (predicate)

- `Apple::isGreenApple`메서드를 `Predicate<Apple>`를 파라미터로 받아 넘겨주었다.
- 수학에서는 인수를 값으로 받아 true, false로 반환하는 함수를 프레디케이트라고 한다.
- `Function<Apple, Boolean>`처럼 코드로 구현하는 것 보다 표준적인 방식

## 1.3.3 메서드 전달에서 람다로

```java
filterApple(inventory, (Apple a) -> a.getWeight<80 || RED.equals(e.getColor()) ); 
```

# 1.4 스트림

- 컬렉션 : 반복 과정 직접 처리 → 외부 반복 (external iteration)
- 스트림 API : 라이브러리 내부에서 모든 데이터가 처리됨 → 내부 반복 (internal iteration)
  - 컬렉션의 모호함, 반복적 코드 문제 해결

## 1.4.1 멀티스레딩은 어렵다

- 스트림 API는 멀티코어 활용 어려움 문제 해결 (구. 스레드 API, synchronized)
  - 포킹 단계 : 한 CPU는 리스트의 앞 부분을 처리하고 다른 CPU는 리스트의 뒷 부분 처리
- 컬렉션, 스트림 둘 다 순차적 데이터 항목 접근 방법 제공
  - 컬렉션은 어떻게 데이터를 저장하고 접근할지에 중점
  - 스트림은 데이터에 어떤 계산을 할 것인지 묘사에 중점
  - 스트림은 내부 요소 병렬로 쉽게 처리 할 수 있는 환경 제공

# 1.5 디폴트 메서드와 자바 모듈

- 자바 8에서는 인터페이스를 쉽게 바꿀 수 있도록 디폴트 메소드 제공
- 구현 클래스에서 구현하지 않아도 되는 메소드를 인터페이스에 추가 가능 → 디폴트 메소드 `default`

# 1.6 함수형 프로그래밍에서 가져온 다른 유용한 아이디어

- null 처리 방법 → Optional
- 패턴 매칭 활용