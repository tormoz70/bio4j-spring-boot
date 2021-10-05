package ru.bio4j.spring.commons.types;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        return context;
    }
    public static void setApplicationContextStatic(ApplicationContext ac) {
        context = ac;
    }

    /**
     *
     * @param beanClass класс бина, который нужно найти и получить
     * @param <T>       тип возвращаемого бина
     * @return The Spring managed bean instance of the given class type if it exists. Otherwise <code>null</code>.
     * @throws NoSuchBeanDefinitionException в случае, если бин не найден в контексте.
     * @throws IllegalArgumentException в случае, если контекст не инициализирован.
     */
    public static <T> T getBean(Class<T> beanClass) throws NoSuchBeanDefinitionException, IllegalArgumentException {
        if (context == null)
            throw new IllegalArgumentException("Cannot get bean. Context not initialized.");
        return context.getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) {
        context = ac;
    }
}
