package ru.bio4j.spring.commons.types;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Сервис позволяет запускать однотипные процессы с задержкой и возможностью вытеснения.
 * @param <T> тип возвращаемого процессом результата
 */
public interface DelayedExecutor<T> {
    /**
     * Запускает новый процесс с указанной задержкой и вытесняет предыдущий процесс с таким же {@code requestHash}.
     * @param action      метод, который будет выполняться
     * @param requestHash идентификатор, при совпадении которого новый процесс будет вытеснять предыдущий
     * @param delay       величина задержки запуска нового процесса
     * @param timeUnit    единица измерения задержки
     * @return Результат, который возвращает процесс. Если процесс был вытеснен новым процессом, то возвращается null.
     * @throws ExecutionException в случае возникновения исключения в выполняемом процессе.
     */
    T execute(Callable<T> action, String requestHash, long delay, TimeUnit timeUnit) throws ExecutionException;

    /**
     * Запускает новый процесс с задержкой по умолчанию и вытесняет предыдущий процесс с таким же {@code requestHash}.
     * @param action      метод, который будет выполняться
     * @param requestHash идентификатор, при совпадении которого новый процесс будет вытеснять предыдущий
     * @return Результат, который возвращает процесс. Если процесс был вытеснен новым процессом, то возвращается null.
     * @throws ExecutionException в случае возникновения исключения в выполняемом процессе.
     */
    T execute(Callable<T> action, String requestHash) throws ExecutionException;
}
