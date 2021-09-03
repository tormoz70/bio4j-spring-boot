package ru.bio4j.spring.commons.grpc;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import ru.bio4j.spring.commons.utils.Strings;

import java.util.concurrent.ConcurrentHashMap;

public class GrpcClientBase<T> {
    private final EurekaClient client;
    private final ConcurrentHashMap<String, T> grpcStubPool;

    private T getActualGrpcService() {
        if (client == null || Strings.isNullOrEmpty(ssoServiceName)) {
            if (serviceGrpcStub != null)
                return serviceGrpcStub;
            /* Почему код 6000? Не знаю. Такой код по умолчанию в BioError. */
            throw BioError.build(6000, "SSO client not configured.");
        }

        final InstanceInfo instanceInfo = client.getNextServerFromEureka(ssoServiceName, false);
        String ssoServiceAddress = instanceInfo.getIPAddr();
        int ssoServicePort = instanceInfo.getPort();
        return grpcStubPool.computeIfAbsent(ssoServiceAddress + ":" + ssoServicePort, key -> {
            String[] addrPort = key.split(":");
            return SsoServiceGrpc.newFutureStub(NettyChannelBuilder.forAddress(addrPort[0], Integer.parseInt(addrPort[1]))
                    .usePlaintext()
                    .build());
        });
    }
}
