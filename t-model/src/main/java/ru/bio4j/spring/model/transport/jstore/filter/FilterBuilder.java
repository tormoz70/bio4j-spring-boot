package ru.bio4j.spring.model.transport.jstore.filter;

/**
 * Фильтр...
 * Ну так вот например:
 *      and(
 *          eq(f1, 1),
 *          or(eq(r, 1), eq(r, 2))
 *      )
 *    это значит: (f1=1) and ((r=1) or (r=2))
 */
public class FilterBuilder {

    public static Expression filter(Expression ... exps){
        return new Filter(exps);
    }

    /**
     * Логическое 'или'
     * @param exps Выражения, которые нужно объединить с пом OR
     * @return Выражение логическое "ИЛИ"
     */
    public static Expression or(Expression ... exps){
        return new Or(exps);
    }


    /**
     * Логическое 'и'
     * @param exps Выражения, которые нужно объединить с пом AND
     * @return Выражение логическое "И"
     */
    public static Expression and(Expression ... exps){
        return new And(exps);
    }

    /**
     * Значение поля равно значению
     * @param column имя поля слева
     * @param value значение справа
     * @return результат операции
     */
    public static Expression eq(String column, Object value){
        return new Eq(column, value);
    }

    /**
     * Значение поля равно строке
     * @param column имя поля слева
     * @param value значение справа
     * @param ignoreCase не учитывать регистр при странении
     * @return результат операции
     */
    public static Expression eq(String column, String value, boolean ignoreCase){
        return new Eq(column, value, ignoreCase);
    }

    /**
     * инверсия
     * @param arg выражение
     * @return результат операции
     */
    public static Expression not(Expression arg){
        return new Not(arg);
    }

    /**
     * Значение поля IS NULL
     * @param column имя поля
     * @return результат операции
     */
    public static Expression isNull(String column){
        return new IsNull(column);
    }

    /**
     * Значение поля БОЛЬШЕ значения
     * @param column имя поля
     * @param value значение
     * @return результат операции
     */
    public static Expression gt(String column, Object value){
        return new Gt(column, value);
    }

    /**
     * Значение поля БОЛЬШЕ ИЛИ РАВНО значения
     * @param column имя поля
     * @param value значение
     * @return результат операции
     */
    public static Expression ge(String column, Object value){
        return new Ge(column, value);
    }

    /**
     * Значение поля МЕНЬШЕ значения
     * @param column имя поля
     * @param value значение
     * @return результат операции
     */
    public static Expression lt(String column, Object value){
        return new Lt(column, value);
    }

    /**
     * Значение поля МЕНЬШЕ ИЛИ РАВНО значения
     * @param column имя поля
     * @param value значение
     * @return результат операции
     */
    public static Expression le(String column, Object value){
        return new Le(column, value);
    }

    /**
     * Значение поля НАЧИНАЕТСЯ С строки...
     * @param column имя поля
     * @param value значение
     * @param ignoreCase не учитывать регистр при странении
     * @return результат операции
     */
    public static Expression bgn(String column, String value, boolean ignoreCase){
        return new Bgn(column, value, ignoreCase);
    }

    /**
     * Значение поля ЗАКАНСИВАЕТСЯ НА строку...
     * @param column имя поля
     * @param value значение
     * @param ignoreCase не учитывать регистр при странении
     * @return результат операции
     */
    public static Expression end(String column, String value, boolean ignoreCase){
        return new End(column, value, ignoreCase);
    }

    /**
     * Значение поля СОДЕРЖИТ строку...
     * @param column имя поля
     * @param value значение
     * @param ignoreCase не учитывать регистр при странении
     * @return результат операции
     */
    public static Expression contains(String column, String value, boolean ignoreCase){
        return new Contains(column, value, ignoreCase);
    }
}
