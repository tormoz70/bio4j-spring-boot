package ru.bio4j.spring.database.h2;

import org.h2.tools.Server;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.DbServer;
import ru.bio4j.spring.model.transport.BioSQLException;

import java.sql.SQLException;

public class H2ServerImpl implements DbServer {
    private static final LogWrapper LOG = LogWrapper.getLogger(H2ServerImpl.class);

    private final String tcpPort;
    private String actualTcpPort = null;
    private Server server;

    public H2ServerImpl(String port){
        tcpPort = port;
    }

    public synchronized void startServer() {
        if(server == null) {
            if (!Strings.isNullOrEmpty(tcpPort)) {
                final String[] args = new String[] {
                        "-tcp",
                        "-tcpPort", tcpPort,
                        "-tcpAllowOthers",
                };
                try {
                    server = Server.createTcpServer(args).start();
                } catch (SQLException e) {
                    throw new BioSQLException(e);
                }
                actualTcpPort = "" + server.getPort();
            }
        }
    }

    public String getActualTcpPort() {
        return actualTcpPort;
    }

    public synchronized void shutdownServer() {
        if(server != null) {
            try {
                Server.shutdownTcpServer(String.format("tcp://localhost:%s", actualTcpPort), "", true, true);
            } catch (SQLException e) {
                throw new BioSQLException(e);
            }
        }
    }

}
