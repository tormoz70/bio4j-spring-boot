package ru.bio4j.spring.model.transport;

/**
 * Имена системных параметров, которые используются в запросах при wrapping'е
 */
public class Rest2sqlParamNames {
    public static final String PAGINATION_PARAM_PAGE = "pagination_page";
    public static final String PAGINATION_PARAM_LIMIT = "pagination_limit";
    public static final String PAGINATION_PARAM_OFFSET = "pagination_offset";
    public static final String PAGINATION_PARAM_TOTALCOUNT = "pagination_totalcount";
    public static final String GETROW_PARAM_PKVAL = "getrow_pkvalue";
    public static final String LOCATE_PARAM_PKVAL = "locate_pkvalue";
    public static final String DELETE_PARAM_PKVAL = "delete_pkvalue";
    public static final String LOCATE_PARAM_STARTFROM = "locate_startfrom";
    public static final String QUERY_PARAM_VALUE = "query_value";
}
