package ru.bio4j.spring.database.commons.wrappers;

import ru.bio4j.spring.database.api.LocateWrapper;
import ru.bio4j.spring.database.api.WrapperInterpreter;
import ru.bio4j.spring.database.commons.AbstractWrapper;
import ru.bio4j.spring.model.transport.RestParamNames;

public class LocateWrapperBaseImpl extends AbstractWrapper implements LocateWrapper {

    private String template;

    public LocateWrapperBaseImpl(String template, WrapperInterpreter wrapperInterpreter) {
        super(template, wrapperInterpreter);
    }

    @Override
    protected void parseTemplate(String template){
        this.template = template;
    }

    public String wrap(String sql, String pkFieldName) {
//        Field pkCol = sqlDef.findPk();
//        if(pkCol == null)
//            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", sqlDef.getBioCode()));
        String whereclause = "(" + pkFieldName + " = :" + RestParamNames.LOCATE_PARAM_PKVAL + ") AND (rnum_num >= :" + RestParamNames.LOCATE_PARAM_STARTFROM + ")";
        String rslt = template.replace(QUERY, sql);
        return rslt.replace(WHERE_CLAUSE, whereclause);
    }
}
