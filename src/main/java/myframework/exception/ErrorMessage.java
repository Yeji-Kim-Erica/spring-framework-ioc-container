package myframework.exception;

public enum ErrorMessage {
    // ComponentScanException
    PACKAGE_SCAN_FAILED("패키지 '%s' 스캔에 실패했습니다."),
    CLASS_NOT_FOUND("%s라는 이름의 클래스가 존재하지 않습니다."),

    // BeanCreationException
    NO_DEFAULT_CONSTRUCTOR("%s 클래스에 기본 생성자가 존재하지 않습니다."),
    BEAN_INSTANTIATION_FAILED("%s 클래스의 빈 생성에 실패했습니다."),

    // NoSuchBeanException
    BEAN_NOT_FOUND("%s의 빈이 존재하지 않습니다."),
    BEAN_TYPE_NOT_MATCHED("'%s'라는 이름의 %s 타입 빈이 존재하지 않습니다. (실제 타입: %s)"),

    // DependencyInjectionException
    DEPENDENCY_INJECTION_FAILED("%s 클래스의 '%s' 필드 의존성 주입에 실패했습니다."),
    DEPENDENCY_BEAN_NOT_FOUND("'%s' 필드에 주입될 수 있는 빈이 존재하지 않습니다."),
    TOO_MANY_DEPENDENCY_BEANS("'%s' 필드에 주입될 수 있는 빈이 유일하지 않습니다."),

    // TestDependencyInjectionException
    TEST_FIELD_INJECTION_FAILED("테스트 도중 '%s' 필드 주입에 실패했습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage(Object... cause) {
        return String.format(message, cause);
    }
}
