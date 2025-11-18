# spring-framework-ioc-container
우아한테크코스 웹 백엔드 프리코스 오픈미션: 스프링의 IoC 컨테이너 구현

---

## 🎯 프로젝트 개요
**Spring의 IoC 컨테이너 구조를 직접 구현**하며 **의존성 주입(DI)** 원리를 이해하고,  
**Static 메서드 기반 접근 방식과 IoC 컨테이너의 Singleton Registry 관리 방식의 차이점 비교**를 통해
적합한 설계에 대한 결론을 도출하기 위한 프로젝트입니다.

---

## 📝 구현 기능 목록

### 1. 어노테이션 기반 설정 정보 정의
- [x] `@Component`: 컴포넌트 스캔 대상을 식별하는 어노테이션 정의
- [x] `@Autowired`: 의존성 주입 대상을 식별하는 어노테이션 정의

### 2. IoC 컨테이너 구현
- 컴포넌트 스캔 및 인스턴스 생성
    - [x] **ApplicationContext (컨테이너) 클래스 정의**
        - `Map`을 사용한 **빈 저장소(Singleton Registry)** 구현
    - [x] **컴포넌트 스캔 및 빈 생성**
        - 특정 패키지 하위의 `@Component` 어노테이션이 붙은 클래스 스캔
        - **(예외 처리)** 패키지 스캔 실패 시 예외 발생
          - 스캔 중 I/O 오류가 발생할 경우: `ComponentScanException`
          - 클래스를 찾지 못할 경우: `ComponentScanException`
        - Reflection API를 사용해 스캔한 클래스들의 인스턴스 생성
        - **(예외 처리)** 빈 생성 실패 시 예외 발생
            - `@Component`로 등록된 클래스에 기본 생성자가 없는 경우: `BeanCreationException`
            - 추상 클래스 등 인스턴스화가 불가능한 경우: `BeanCreationException`
        - 생성된 인스턴스를 빈 저장소에 등록
- 의존성 주입
    - [x] **의존성 주입 (Dependency Injection)**
        - 빈 저장소에 등록된 객체들을 순회
        - `@Autowired` 어노테이션이 붙은 필드 식별
        - 빈 저장소에서 해당 필드 타입에 맞는 빈을 **조회**
        - **(예외 처리)** 주입할 빈을 찾지 못할 경우 예외 발생
            - [x] `@Component`가 누락된 경우: `DependencyInjectionException`
            - [x] `@Autowired`로 주입하려는 타입의 빈의 유일성이 보장되지 않는 경우: `DependencyInjectionException`
        - Reflection API를 사용해 필드에 의존 객체를 **주입**
    - [x] **컨테이너의 빈 조회**
        - 컨테이너에 등록된 빈 반환
        - **(예외 처리)** 빈 저장소에 해당 타입의 빈이 존재하지 않을 경우 예외 발생
          - 등록되지 않은 클래스의 빈을 얻으려고 할 경우: `NoSuchBeanException`
          - 빈의 이름은 존재하지만 요청한 타입과 다를 경우: `NoSuchBeanException`

### 3. (선택 과제) 기능 추가
- [ ] 생성자 주입 방식 지원
    - **(예외 처리)** 순환 참조 발생 시 예외 발생

---

## 🔬 실험 계획
IoC 컨테이너 구현을 완료한 이후,
- **Static 메서드 기반 유틸리티 접근 방식**과
- **Singleton Registry 기반 객체 관리 방식**을 비교 실험하여  
  DI의 확장성과 테스트 용이성 측면에서 어떤 차이가 있는지를 검증할 예정입니다.

> 📁 실험 코드는 추후 [`experiments/`](./experiments/) 디렉토리에 정리될 예정입니다.

---

## 🧩 학습 포인트
- Reflection API를 활용한 런타임 의존성 주입
- Singleton Registry 설계 원리
- Static 메서드와 싱글톤 인스턴스의 사용 시점 비교
- Spring DI의 내부 동작 원리 체험

---

이번 미션을 통해 **스프링 DI의 핵심 구조를 직접 구현**하고,  
**Static vs Singleton의 설계 선택 기준**을 명확히 이해하는 것을 목표로 합니다.
