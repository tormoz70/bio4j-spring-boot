package ru.bio4j.spring.commons.types;

/**
 * Имена системных query-параметров, которые обрабатываются автоматически и используются при wrapping'е запросов
 */
public interface HttpParamMap {
    /** Имя пользователя */
    default String username() {
        return "login";
    }
    /** Пароль */
    default String password() {
        return "passwd";
    }
    /** Кол-во записей на страницу */
    default String pageSize() {
        return "limit";
    }
    /** Номер страницы */
    default String page() {
        return "page";
    }
    /** Номер записи */
    default String offset() {
        return "offset";
    }
    /** Токен приложения */
    default String appToken() {
        return "apptoken";
    }
    /** Токен */
    default String securityToken() {
        return "stoken";
    }
    /** Уникальный идентификатор устройства (для входа с гостевым логином) */
    default String deviceuuid() {
        return "deviceuuid";
    }
    /** Флаг для подсчёта общего числа записей */
    default String calcTotal() {
        return "gcount";
    }

    default String pageSizeHeader() {
        return "X-Pagination-Pagesize";
    }
    default String pageHeader() {
        return "X-Pagination-Page";
    }
    default String offsetHeader() {
        return "X-Pagination-Offset";
    }
    default String deviceuuidHeader() {
        return "X-DEVICEUUID";
    }
    default String appTokenHeader() {
        return "X-AppToken";
    }
    default String securityTokenHeader() {
        return "X-SToken";
    }
    default String clientHeader() {
        return "X-Client";
    }
    default String clientVerHeader() {
        return "X-Client-Ver";
    }
}
