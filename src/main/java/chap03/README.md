# Chapter 2 - 동작 파라미터화 코드 전달하기

# 2.1 변화하는 요구사항에 대응하기

### 동작 파라미터화 (behavior parameterization)

- 어떻게 실행할 것인지 결정하지 않은 코드 블록, 나중에 프로그램에서 호출함
- 나중에 실행될 메서드의 인수로 코드 블록을 전달할 수 있다 → 코드 블록에 따라 메서드의 동작이 파라미터화된다.
- 자주 바뀌는 요구사항에 효과적으로 대응할 수 있다.

## 2.2.1 첫 번째 시도 : 녹색 사과 필터링

```java
enum Color { RED, GREEN }

public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (**GREEN.equals(apple.getColor()**)) {
        result.add(apple);
      }
    }
    return result;
}
```

빨간 사과도 필터링하고 싶다면 메서드를 복사해 filterRedApples를 만들 수 있겠지만

거의 비슷한 코드가 반복 존재한다면 크 코드를 추상화한다.

### 2.2.2 두 번째 시도 : 색을 파라미터화

색을 파라미터화하여

```java
public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (**apple.getColor().equals(color**)) {
        result.add(apple);
      }
    }
    return result;
}
```

색 외의 무게도 필터링 하고 싶다면 weight 파라미터를 가진 `filterApplesByWeight` 함수를 정의하면 되겠지만

이는 소프트웨어 공학의 DRY (don’t repeat yourself 같은 것을 반복하지 말 것) 원칙을 어기는 것이다.

## 2.2.2 세 번째 시도 : 가능한 모든 속성으로 필터링

```java
public static List<Apple> filterApples(List<Apple> inventory, 
												Color color, int weight, boolean flag) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if ((flag && apple.getColor().equals(color)) || (!flag && apple.getWeight() > weight)) {
        result.add(apple);
      }
    }
    return result;
}
```

flag가 무엇을 의미하는지 알기 어렵고 앞으로 요구사항이 바뀔 때 유연하게 대응하기도 어렵다.

⇒ filterApples 메서드에 어떤 기준으로 사과를 필터링할 것인지 **동작 파라미터화**로 유연하게 전달할 수 있다.

# 2.2 동작 파라미터화

### 전략 디자인 패턴 (strategy design pattern)

```java
// 참 / 거짓을 반환하는 프레디케이트 함수로, **선택 조건을 결정**하는 인터페이스를 정의한다.
public interface ApplePredicate {
	boolean test (Apple apple);
}

// 다양한 선택 조건을 정의하는 여러 버전의 ApplePredicate를 정의한다.
public class AppleHeavyWeightPredicate implements ApplePredicate {
	public boolean test {
		return apple.getWeight() > 150;
	}
}

public class AppleGreenColorPredicate implements ApplePredicate {
	public boolean test {
		return GREEN.equals(apple.getColor());
	}
}
```

각 알고리즘 (=전략=AppleHeavyWeightPredicate)을 캡슐화하는 알고리즘 패밀리 (=ApplePredicate)를 정의해두고 런타임에서 알고리즘을 선택하는 기법 → 동작 파라미터화

## 2.2.1 네 번째 시도 : 추상적 조건으로 필터링

```java
// 2. ApplePredicate로 동작 파라미터화
public static List<Apple> filterApples(List<Apple> inventory, **ApplePredicate p**) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (p.test(apple)) { 
        result.add(apple);
      }
    }
    return result;
}
```

### 코드/동작 전달하기

```java
static class AppleRedAndHeavyPredicate implements ApplePredicate {
    @Override
    public boolean **test**(Apple apple) {
			// 1. ApplePredicate에서 새 동작 정의
      return RED.equals(apple.getColor()) && apple.getWeight() > 150;
    }
  }

//
List<Apple> redAndHeavyApples 
	= **filterApples**(inventory, **new AppleRedAndHeavyPredicate()**);
```

### 한 개의 파라미터, 다양한 동작

1. ApplePredicate를 implements하여 test 메소드에서 새 동작 정의
2. filterApples에 ApplePredicate 객체로 사과 검사 조건을 캡슐화하여 전달

# 2.3 복잡한 과정 간소화

## 2.3.1 익명 클래스

클래스 선언과 인스턴스화 동시에 수행 ⇒ 익명 클래스 ⇒ 즉석에서 필요한 구현을 만들어서 쓸 수 있다.

## 2.3.2 다섯 번째 시도 : 익명 클래스 사용

```java
List<Apple> redApples = filterApples(inventory, **new ApplePredicate()** {
	public boolean **test**(Apple a) {
		return RED.equals(a.getColor());
	}
});
```

객체를 만들고 명시적으로 새로운 동작을 정의해야 함은 여전함

## 2.3.3 여섯 번째 시도 : 람다 표현식 사용

```java
List<Apple> result = 
	filterApples(inventory, (Apple apple) -> RED.equals(a.getColor());
```

## 2.3.3 일곱 번째 시도 : 리스트 형식으로 추상화

cf) 제네릭(Generic)은 클래스 내부에서 지정하는 것이 아닌 외부에서 사용자에 의해 지정되는 것을 의미

```java
public interface ApplePredicate {
	boolean test (Apple apple);
}

public static <T> List<T> filter(List<T> list, **Predicate<T> p**) { // 형식 파라미터 T
	List<T> result = new ArrayList<>();
	for(T e: list) {
		if (p.test(e)) {
			 result.add(e);
		}
	}
	return result;
}

// 리스트에 필터 메서드 사용
List<Apple> redApples = 
	filter(inventory, (Apple apple) -> RED.equals(apple.getColor());

List<Integer> evenNumbers = 
	filter(numbers, (Integer i) -> i % 2 == 0);
```

# 2.4 실전 예제

## 2.4.1 Comparator로 정렬하기

```java
// Java 8 - List의 sort 메소드
// Comparator객첼 sort 동작을 파라미터화 할 수 ㅣㅇㅆ다.

// java.util.Comparator
public interface Comparator<T> {
	int compare(T o1, T o2);
}

// 익명 클래스 활용
inventory.sort(new Comparator<Apple>() {
	public int compare(Apple a1, Apple a2){
		return a1.getWeight().compareTo(a2.getWeight());
	}
});

// 람다 표현식 활용
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

## 2.4.2 Runnable로 코드 블록 실행하기

자바 스레드로 병렬로 코드 블록을 실행 → 나중에 실행할 수 있는 코드를 구현

자바 8까지는 Thread 생성자에 객체만을 전달 가능 → void run 메소드를 포함하는 익명 클래스가 Runnable 인터페이스를 구현하는 것이 일반적

```java
// java.lang.Runnable
public interface Runnable() {
	void run();
}

// Runnable로 다양한 동작을 스레드로 실행 가능
Thread t = new Thread(new Runnalbe() {
	public void run() {
		System.out.println("Hello World");
	}
});

// 람다 표현식 이용
Thread t = new Thread(() -> System.out.println("Hello World"));
```

## 2.4.3 Callable을 결과로 반환하기

ExecutorService : 태스크를 스레드 풀로 보내고 결과를 Future로 저장

## 2.4.4 GUI 이벤트 처리하기

버튼 클릭의 EventHandler는 setOnAction메서드의 동작을 파라미터 하는데 람다 표현식으로 구현 가능하다.