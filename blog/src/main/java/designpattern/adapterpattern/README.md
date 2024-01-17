### 어댑터 사용 방법 알아보기
Duck 객체가 모자라서 Turkey를 대신 사용해야할 경우, 인터페이스가 다르기에 바로 사용할 수 없다.
이때 어댑터가 필요하다.

```java
public class TurkeyAdapter implements Duck { // 적응시킬 형식의 인터페이스를 구현.

    private Turkey turkey;

    public TurkeyAdapter(Turkey turkey) {
        this.turkey = turkey; // 기존 형식 객체의 레퍼런스를 주입
    }
    
    // 해당 인터페이스에 맞게 구현
    @Override
    public void quack() {
        turkey.gobbble();
    }

    @Override
    public void fly() {
        for (int i = 0; i < 5; i++) {
            turkey.fly();
        }
    }
}
```

### 클라이언트에서 어댑터를 사용하는 방법
```java
public class DuckTestDrive {

    public static void main(String[] args) {
        Duck duck = new MallardDuck();

        Turkey turkey = new WildTurkey();
        Duck turkeyAdapter = new TurkeyAdapter(turkey);

        System.out.println("칠면조가 말하길");
        turkey.gobbble();
        turkey.fly();

        System.out.println("\n오리가 말하길");
        testDuck(duck);

        System.out.println("\n칠면조 어댑터가 말하길");
        testDuck(turkeyAdapter);
    }

    static void testDuck(Duck duck) {
        duck.quack();
        duck.fly();
    }
}
```

1. 클라이언트에서 타깃 인터페이스(`Duck`)로 메소드를 호출해서 어댑터(`TurkeyAdapter`)에 요청을 보낸다.
2. 어댑터는 그 요청을 어댑티 인터페이스(`Turkey`)의 메서드 호출로 변환한다.
3. 클라이언트는 호출 결과를 받지만 중간에 어댑터가 있다는 사실을 모른다.

### 어댑터 패턴 정의
어댑터 패턴 : 특정 클래스 인터페이스를 클라이언트에서 요구하는 다른 인터페이스로 변환한다. 
인터페이스가 호횐되지 않아 같이 쓸 수 없었던 클래스를 사용할 수 있게 도와준다.

어댑터 패턴은 여러 객체지향 원칙을 반영하고 있다.
- 어댑티를 새로 바뀐 인터페이스로 감쌀 때 composition(구성)을 사용한다.
- 클라이언트는 특정 구현이 아닌 **인터페이스**에 의존한다. 
- 따라서 여러 어댑터를 사용할 수 있으며, 타깃 인터페이스를 제대로 유지한다면 다른 구현을 추가하는 것도 가능하다.

### 객체 어댑터와 클래스 어댑터
- 객체 어댑터 : Target 인터페이스를 구현하고 Adaptee를 composition하는 어댑터
  - 장점 : 어댑티 클래스와 그 서브클래스에 대해서도 어댑터 역할을 할 수 있다. 
- 클래스 어댑터 : Target과 Adaptee를 둘다 상속하는 어댑터
  - 장점 : 어댑티 전체를 다시 구현하지 않아도 된다. 어댑티를 오버라이드 할 수 있다. 
> 자바에서는 다중상속을 지원하지 않으므로 클래스 어댑터는 사용할 수 없다.

### 실전에서의 어댑터 패턴
`Enumeration`
- 컬렉션 항목에 접근할 수 있다.
- hasMoreElements(), nextElement()

`Iterator`
- 컬렉션 항목에 접근하고 제거할 수 있다.
- hasNext(), next(), remove()

```java
Stack<Integer> stack = new Stack<>();
Enumeration<Integer> elements = stack.elements();
Iterator<Integer> iterator = stack.iterator();
```

#### Enumeration에 Iterator 적응시키기

- Target : Iterator
  - 겉에서 봤을 때 Iterator처럼 생겨야함.
- Adaptee : Enumeration
- Adapter : 새로운 EnumerationIterator 생성 필요

```java
public class EnumerationIterator implements Iterator<Object> {

    private Enumeration<?> enumeration;

    public EnumerationIterator(Enumeration<?> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    @Override
    public Object next() {
        return enumeration.nextElement();
    }
    
    @Override
    public void remove() {
        // Enumeration는 remove를 지원하지 않으므로 예외를 던짐.
        throw new UnsupportedOperationException();
    }
}
```
> 다행히도 remove()메서드는 애초에 설계될 때 default 메서드로 UnsupportedOperationException을 던진다.
```java
public interface Iterator<E> {
 
    ...
    default void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
```

