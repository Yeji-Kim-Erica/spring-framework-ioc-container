package myframework.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private Map<String, Object> beans;

    public ApplicationContext(String basePackage) {
        this.beans = new ConcurrentHashMap<>();
        // 컴포넌트 스캔
    }
}
