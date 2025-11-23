# spring-framework-ioc-container
우아한테크코스 웹 백엔드 프리코스 오픈미션: 스프링의 IoC 컨테이너 구현

---

## 🎯 프로젝트 개요
**Spring의 IoC 컨테이너 구조를 직접 구현**하며 **의존성 주입(DI)** 원리를 이해하고,  
**Static 메서드 기반 접근 방식**과 **DI 기반 IoC 컨테이너(Singleton Registry) 방식**의 **비교 분석**을 통해  
적합한 설계에 대한 결론을 도출하기 위한 프로젝트입니다.

---

## 🗂️ 프로젝트 패키지 구조
이 프로젝트는 **프레임워크 구현체**와 **실제 사용 예시**, **비교 실험**을 패키지로 분리하여 구성했습니다.  
```text
src/main/java
├── myframework     → 직접 구현한 IoC 컨테이너
├── example         → 프레임워크 검증 예제, 실험에서 DI를 대표
│   └── lotto       → 로또 미션 이식: Singleton Registry 기반 DI 컨테이너 사용하도록 리팩토링
└── experiment      → Static vs DI 비교 실험
    └── statics     → 실험에서 Static을 대표
        └── lotto   → 로또 미션 이식: Static 메서드 기반 유틸리티 클래스로 리팩토링
```

---

## 🛠️ 프레임워크 구현
> **Package:** `myframework`

### ✅ 구현 기능 목록

### 1. 어노테이션 기반 설정 정보 정의
- [x] `@Component`: 컴포넌트 스캔 대상을 식별하는 어노테이션 정의
- [x] `@Autowired`: 의존성 주입 대상을 식별하는 어노테이션 정의

### 2. IoC 컨테이너 구현
- 컴포넌트 스캔 및 인스턴스 생성
    - [x] **ApplicationContext (컨테이너) 클래스 정의**
        - `Map`을 사용한 **빈 저장소(Singleton Registry)** 구현
    - [x] **컴포넌트 스캔 및 빈 생성**
        - 특정 패키지 하위의 `@Component` 어노테이션이 붙은 클래스 스캔
        - **(예외 처리)** 패키지 스캔 실패 시 예외 발생: `ComponentScanException`
          - 스캔 중 I/O 오류가 발생할 경우
          - 클래스를 찾지 못할 경우
        - Reflection API를 사용해 스캔한 클래스들의 인스턴스 생성
        - **(예외 처리)** 빈 생성 실패 시 예외 발생: `BeanCreationException`
            - `@Component`로 등록된 클래스에 기본 생성자가 없는 경우
            - 추상 클래스 등 인스턴스화가 불가능한 경우
        - 생성된 인스턴스를 빈 저장소에 등록
- 의존성 주입 (Dependency Injection)
    - [x] **의존성 주입**
        - 빈 저장소에 등록된 객체들을 순회
        - `@Autowired` 어노테이션이 붙은 필드 식별
        - 빈 저장소에서 해당 필드 타입에 맞는 빈을 **조회**
        - **(예외 처리)** 주입할 빈을 찾지 못할 경우 예외 발생: `DependencyInjectionException`
            - [x] `@Component`가 누락된 경우
            - [x] `@Autowired`로 주입하려는 타입의 빈의 유일성이 보장되지 않는 경우
        - Reflection API를 사용해 필드에 의존 객체를 **주입**
    - [x] **컨테이너의 빈 조회**
        - 컨테이너에 등록된 빈 반환
        - **(예외 처리)** 빈 저장소에 해당 타입의 빈이 존재하지 않을 경우 예외 발생: `NoSuchBeanException`
          - 등록되지 않은 클래스의 빈을 얻으려고 할 경우
          - 빈의 이름은 존재하지만 요청한 타입과 다를 경우

### 3. 생성자 주입 (Constructor Injection)
- [x] **생성자 주입 방식 지원**
    - `@Autowired`가 붙은 생성자를 통한 의존성 주입 우선 처리
    - 생성자 파라미터로 인터페이스/추상클래스가 올 경우 구현체를 재귀적으로 탐색해 주입
    - **(예외 처리)** 생성자 주입 실패 시 예외 발생: `DependencyInjectionException`
      - `@Autowired` 생성자가 2개 이상 존재하는 경우
      - 주입할 파라미터 타입의 빈이 존재하지 않는 경우
      - 생성자 간의 직접/간접 순환 참조가 발생하는 경우
      - 자기 자신을 의존하는 경우

---

## 🔍 실전 적용 및 검증
> **Package:** `example.lotto`

직접 구현한 프레임워크의 동작을 검증하기 위해,  
기존 우테코 프리코스 3주차 미션 [**로또(java-lotto-8)**](https://github.com/Yeji-Kim-Erica/java-lotto-8/tree/Yeji-Kim-Erica) 프로젝트를 이식해 실행합니다.

- [x] **기존 프로젝트 코드 이식**
    - 기존 로또 프로젝트의 코드를 `example.lotto` 패키지로 분리해 이식
- [x] **IoC 컨테이너 기반 리팩토링**
    - 직접 객체를 생성(`new`)하고 주입하던 코드 제거
    - `@Component`, `@Autowired`를 적용하여 **제어의 역전(IoC)** 적용
    - `ApplicationContext`를 통한 애플리케이션 실행 및 정상 동작 확인

---

## 🔬 실험
> **Package:** `experiment`

**Static** 기반 유틸리티 접근 방식 VS **DI** 기반 IoC 컨테이너(Singleton Registry) 방식

동일한 기능을 수행하는 로또 애플리케이션을 **Static 메서드**와 **DI 컨테이너**의 두 가지 방식으로 구현합니다.  
실제 코드를 작성하고 테스트하는 과정에서 발생하는 **구조적 차이**와 **제약 사항**을 직접 비교하고 분석합니다.

### 🧪 실험 수행 및 검증 결과
- [x] **Static 아키텍처 구현**: Static 방식의 로또 프로젝트 대조군 생성
- [x] **테스트 용이성 비교**: **Static** vs **DI** 각 환경에서의 단위 테스트 작성 난이도 및 가능 여부 검증
  - [x] Static 환경(`experiment.statics.lotto`): Controller, Service 단위 테스트
    - **검증 한계**: 내부 의존성(`RandomLottoNumberGenerator`) 제어 불가로 인해 정확한 결과는 예측 불가, 흐름 확인 정도만 가능
    - **환경 종속**: `System.in/out`과 같은 전역 입출력 자원을 직접 조작하는 코드를 작성해야 했음
  - [x] DI 환경(`example.lotto`): Controller, Service 단위 테스트
    - **검증 성공**: 생성자 주입을 통해 가짜 로또 번호 생성기를 주입해, 원하는 시나리오에 대한 테스트가 자유롭게 구현 가능함을 확인
- [x] **동시성 환경 분석**: 멀티스레드 상황에서 각 방식의 동작 차이 및 안정성 확인
  - [x] Static 환경(`experiment.statics.lotto`): 멀티스레드에서의 Controller 동작 테스트
    - **동기화 미적용**(`LottoController`): 전역 자원 경합으로 인한 실행 오류 발생 확인
    - **동기화 적용**(`SynchronizedLottoController`): `synchronized`로 데이터 무결성은 확보되나, 전역 Lock으로 인한 심각한 성능 저하 확인
  - [x] DI 환경(`example.lotto`): 멀티스레드에서의 Controller 동작 테스트
    - **성능 우수성**: 입출력 객체의 인스턴스 격리(테스트용 가짜 입출력 객체 주입)를 통한 데이터 무결성 및 병렬 처리 성능 검증
  
📂 상세 실험 보고서는 [`experiments/`](./experiments/) 디렉토리에서 확인하실 수 있습니다.
