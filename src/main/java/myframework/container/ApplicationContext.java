package myframework.container;

import myframework.annotation.Component;
import myframework.exception.BeanCreationException;
import myframework.exception.ComponentScanException;
import myframework.exception.ErrorMessage;
import myframework.exception.NoSuchBeanException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    private static final String CLASS_FILE_NAME_SUFFIX = ".class";
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String FILE_PATH_SEPARATOR = "/";

    private final String BASE_PACKAGE;
    private final Map<String, Object> beans;

    public ApplicationContext(String basePackage) {
        this.BASE_PACKAGE = basePackage;
        this.beans = new ConcurrentHashMap<>();
        scanCandidateComponents();
    }

    public Object getBean(String beanName) {
        Object bean = beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanException(ErrorMessage.BEAN_NOT_FOUND.getMessage(beanName));
        }
        return bean;
    }

    private void scanCandidateComponents() {
        Set<String> classNames = getClassNamesFromPackage(BASE_PACKAGE);
        for (String className : classNames) {
            putBeanIfAnnotatedAsComponent(className);
        }
    }

    private Set<String> getClassNamesFromPackage(String basePackage) {
        String basePackagePath = basePackage.replace(PACKAGE_SEPARATOR, FILE_PATH_SEPARATOR);
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(basePackagePath);
            return getClassNamesFromResources(resources);
        } catch (IOException e) {
            throw new ComponentScanException(ErrorMessage.PACKAGE_SCAN_FAILED.getMessage(basePackage), e);
        }

    }

    private Set<String> getClassNamesFromResources(Enumeration<URL> resources) {
        List<File> files = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            files.add(new File(resource.getFile()));
        }
        return getClassNamesFromFiles(files);
    }

    private Set<String> getClassNamesFromFiles(List<File> files) {
        Set<String> classNames = new HashSet<>();
        for (File file : files) {
            findClassesFromDirectory(BASE_PACKAGE, file, classNames);
        }
        return classNames;
    }

    private void findClassesFromDirectory(String basePackage, File directory, Set<String> classNames) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            addClassNames(basePackage, file, classNames);
        }
    }

    private void addClassNames(String packagePrefix, File file, Set<String> classNames) {
        if (file.isDirectory()) {
            String subPackageName = file.getName();
            String newPackagePrefix = packagePrefix + PACKAGE_SEPARATOR + subPackageName;
            findClassesFromDirectory(newPackagePrefix, file, classNames);
            return;
        }

        String fileName = file.getName();
        if (fileName.endsWith(CLASS_FILE_NAME_SUFFIX)) {
            String className = fileName.replace(CLASS_FILE_NAME_SUFFIX, "");
            String classNameWithPackageInfo = packagePrefix + PACKAGE_SEPARATOR + className;
            classNames.add(classNameWithPackageInfo);
        }
    }

    private void putBeanIfAnnotatedAsComponent(String className) {
        try {
            Class<?> classInstance = Class.forName(className);
            boolean isAnnotatedAsComponent = classInstance.isAnnotationPresent(Component.class);
            if (isAnnotatedAsComponent) {
                putAnnotatedClassInstanceAsBean(className, classInstance);
            }
        } catch (ClassNotFoundException e) {
            throw new ComponentScanException(ErrorMessage.CLASS_NOT_FOUND.getMessage(className), e);
        }
    };

    private void putAnnotatedClassInstanceAsBean(String className, Class<?> classInstance) {
        try {
            Constructor<?> constructor = classInstance.getConstructor(null);
            beans.put(className, constructor.newInstance());
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(ErrorMessage.NO_DEFAULT_CONSTRUCTOR.getMessage(className), e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new BeanCreationException(ErrorMessage.BEAN_INSTANTIATION_FAILED.getMessage(className), e);
        }
    }
}
