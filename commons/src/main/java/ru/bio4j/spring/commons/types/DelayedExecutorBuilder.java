package ru.bio4j.spring.commons.types;

import java.util.concurrent.TimeUnit;

/**
 * Билдер для создания экземпляров сервиса {@link DelayedExecutor}.
 */
public interface DelayedExecutorBuilder {
    /**
     * Создаёт экземпляр со значением задержки по умолчанию (600 мсек.).
     * @param <T> тип возвращаемого процессом результата
     * @return Экземпляр сервиса.
     */
    <T> DelayedExecutor<T> build();

    /**
     * Создаёт экземпляр с указанным значением задержки по умолчанию.
     * @param delay    величина задержки запуска нового процесса, принимаемая по умолчанию
     * @param timeUnit единица измерения задержки, принимаемая по умолчанию
     * @param <T>      тип возвращаемого процессом результата
     * @return Экземпляр сервиса.
     */
    <T> DelayedExecutor<T> build(long delay, TimeUnit timeUnit);
}
