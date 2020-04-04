package ru.bio4j.spring.database.api;

public interface Wrappers {

    FilteringWrapper getFilteringWrapper();

    SortingWrapper getSortingWrapper();

    PaginationWrapper getPaginationWrapper();

    TotalsWrapper getTotalsWrapper();

    LocateWrapper getLocateWrapper();

    GetrowWrapper getGetrowWrapper();
}
