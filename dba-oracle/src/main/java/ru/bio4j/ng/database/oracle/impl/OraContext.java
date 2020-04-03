package ru.bio4j.ng.database.oracle.impl;

import org.springframework.stereotype.Component;
import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.commons.*;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
public class OraContext extends DbContextAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(OraContext.class);

    public OraContext(final DataSource cpool, final SQLContextConfig config) {
        super(cpool, config);

        if(this.config.getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    new SQLConnectionConnectedEvent() {
                        @Override
                        public void handle(SQLContext sender, Attributes attrs) {
                            if(attrs.getConnection() != null) {
                                String curSchema = sender.getConfig().getCurrentSchema().toUpperCase();
                                LOG.debug("onAfterGetConnection - start setting current_schema="+curSchema);
                                DbUtils.execSQL(attrs.getConnection(), "alter session set current_schema="+curSchema);
                                LOG.debug("onAfterGetConnection - OK. current_schema now is "+curSchema);
                            }
                        }
                    }
            );
        }

        wrappers = new OraWrappersImpl(this.getDBMSName());
        DbUtils.getInstance().init(
                new OraTypeConverterImpl(),
                new OraUtilsImpl()
        );
    }

    @Override
    public String getDBMSName() {
        return "oracle";
    }

    @Override
    public SQLReader createReader(){
        return new OraReader();
    }

}
