package ru.bio4j.spring.common.model;

public enum RestParamNames {
    PAGINATION_PARAM_PAGE("pagination$page"),
    PAGINATION_PARAM_PAGESIZE("pagination$pageSize"),
    PAGINATION_PARAM_OFFSET("pagination$offset"),
    PAGINATION_PARAM_LIMIT("pagination$limit"),
    PAGINATION_PARAM_TOTALCOUNT("pagination$totalcount"),
    GETROW_PARAM_PKVAL("getrow$pkvalue"),
    LOCATE_PARAM_PKVAL("locate$pkvalue"),
    DELETE_PARAM_PKVAL("delete$pkvalue"),
    LOCATE_PARAM_STARTFROM("locate$startfrom"),
    QUERY_PARAM_VALUE("query$value");

    public static final String RAPI_PARAM_FILEHASHCODE = "file$hashcode";
    private String paramNameInDB = null;

    RestParamNames(String paramNameInDB) {
        this.paramNameInDB = paramNameInDB;
    }

    public String getParamNameInDB() {
        return paramNameInDB;
    }
}
