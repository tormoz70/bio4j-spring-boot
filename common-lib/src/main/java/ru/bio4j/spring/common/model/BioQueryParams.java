package ru.bio4j.spring.common.model;

import ru.bio4j.spring.common.model.jstore.Sort;
import ru.bio4j.spring.common.model.jstore.filter.Filter;

import java.util.List;

public class BioQueryParams {
    public static final String CS_UPLOADEXTPARAM = "upldprm";
    public static final String CS_UPLOADTYPE = "upldType";

    public String method;
    public String remoteIP;
    public String remoteClient;
    public String remoteClientVersion;

    @Mapper(name = "deviceuuid")
    public String deviceuuid;
    @Mapper(name = "stoken")
    public String stoken;
    @Mapper(name = "jsonData")
    public String jsonData;
    @Mapper(name = "login")
    public String login;
    @Mapper(name = CS_UPLOADTYPE)
    public String fcloudUploadType;

    @Mapper(name = "page")
    public String pageOrig;
    public Integer page;
    @Mapper(name = "offset")
    public String offsetOrig;
    public Integer offset;

    @Mapper(name = "pageSize")
    public String pageSizeOrig;

    public Integer pageSize;
    public Integer totalCount;

    @Mapper(name = "locate")
    public String location;

    @Mapper(name = "asorter")
    public String sortOrig;
    public List<Sort> sort;

    @Mapper(name = "afilter")
    public String filterOrig;
    public Filter filter;

    public String query;

    public String gcount;

    public List<Param> bioParams;
}
