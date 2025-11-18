package myframework.container;

import myframework.annotation.Autowired;
import myframework.annotation.Component;
import myframework.exception.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
        Set<String> initiatedComponents = scanCandidateComponentsAndInstantiate();
        injectDependencies(initiatedComponents);
    }

    public Object getBean(String beanName) {
        Object bean = beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanException(ErrorMessage.BEAN_NOT_FOUND.getMessage(beanName));
        }
        return bean;
    }

    public <T> T getBean(String beanName, Class<T> classType) {
        Object bean = getBean(beanName);
        if (classType.isInstance(bean)) {
            return classType.cast(bean);
        }
        String expected = classType.getName();
        String found = bean.getClass().getName();
        throw new NoSuchBeanException(ErrorMessage.BEAN_TYPE_NOT_MATCHED.getMessage(beanName, expected, found));
    }

    private Set<String> scanCandidateComponentsAndInstantiate() {
        Set<String> classNames = getClassNamesFromPackage(BASE_PACKAGE);
        Set<String> initiatedComponents = new HashSet<>();
        for (String className : classNames) {
            boolean isAnnotated = putBeanIfAnnotatedAsComponent(className);
            if (isAnnotated) {
                initiatedComponents.add(className);
            }
        }
        return initiatedComponents;
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

    private boolean putBeanIfAnnotatedAsComponent(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            boolean isAnnotatedAsComponent = clazz.isAnnotationPresent(Component.class);
            if (isAnnotatedAsComponent) {
                putAnnotatedClassInstanceAsBean(className, clazz);
                return true;
            }
            return false;
        } catch (ClassNotFoundException e) {
            throw new ComponentScanException(ErrorMessage.CLASS_NOT_FOUND.getMessage(className), e);
        }
    };

    private void putAnnotatedClassInstanceAsBean(String className, Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor(null);
            beans.put(className, constructor.newInstance());
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(ErrorMessage.NO_DEFAULT_CONSTRUCTOR.getMessage(className), e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new BeanCreationException(ErrorMessage.BEAN_INSTANTIATION_FAILED.getMessage(className), e);
        }
    }

    private void injectDependencies(Set<String> initiatedComponents) {
        for (String componentName : initiatedComponents) {
            Object bean = getBean(componentName);
            Class<?> clazz = bean.getClass();
            while(clazz != null && clazz != Object.class) {
                injectDependenciesIfAnnotatedAsAutowired(bean, clazz);
                clazz = clazz.getSuperclass();
            }
        }
    }

    private void injectDependenciesIfAnnotatedAsAutowired(Object bean, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                injectDependency(bean, field);
            }
        }
    }

    private void injectDependency(Object bean, Field field) {
        field.setAccessible(true);
        try {
            field.set(bean, getBean(field.getType().getName(), field.getType()));
        } catch (IllegalAccessException e) {
            String beanClassName = bean.getClass().getName();
            throw new DependencyInjectionException(
                    ErrorMessage.DEPENDENCY_INJECTION_FAILED.getMessage(beanClassName, field.getName()), e);
        }
    }
}
