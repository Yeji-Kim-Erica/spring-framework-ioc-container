package myframework.exception;

public enum ErrorMessage {
    // ComponentScanException
    PACKAGE_SCAN_FAILED("패키지 '%s' 스캔에 실패했습니다."),
    CLASS_NOT_FOUND("%s라는 이름의 클래스가 존재하지 않습니다."),

    // BeanException
    NO_DEFAULT_CONSTRUCTOR("%s 클래스에 기본 생성자가 존재하지 않습니다."),
    BEAN_INSTANTIATION_FAILED("%s 클래스의 빈 생성에 실패했습니다."),
    BEAN_NOT_FOUND("%s의 빈이 존재하지 않습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage(String cause) {
        return String.format(message, cause);
    }
}
