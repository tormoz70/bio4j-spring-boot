package ru.bio4j.spring.commons.grpc;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bio4j.spring.commons.utils.Strings;

import java.util.concurrent.ConcurrentHashMap;

public class GrpcEurekaClientBase<T> {
    @Autowired
    private EurekaClient client;
    private final ConcurrentHashMap<String, T> grpcStubPool;

    public GrpcEurekaClientBase() {
        this.grpcStubPool = new ConcurrentHashMap<>();
    }

    protected String getServiceName() {
        return null;
    }

    protected T getActualGrpcService(StubSupplier<ManagedChannel, T> stubCreator) {
        String ssoServiceName = getServiceName();
        if (client == null || Strings.isNullOrEmpty(ssoServiceName))
            throw new RuntimeException("SSO client not configured.");

        final InstanceInfo instanceInfo = client.getNextServerFromEureka(ssoServiceName, false);
        String ssoServiceAddress = instanceInfo.getIPAddr();
        int ssoServicePort = instanceInfo.getPort();
        return grpcStubPool.computeIfAbsent(ssoServiceAddress + ":" + ssoServicePort, key -> {
            String[] addrPort = key.split(":");
            ManagedChannel channel = NettyChannelBuilder.forAddress(addrPort[0], Integer.parseInt(addrPort[1]))
                    .usePlaintext()
                    .build();
            return stubCreator.get(channel);
        });
    }
}
