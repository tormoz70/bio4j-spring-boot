package ru.bio4j.spring.commons.eureka;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bio4j.spring.commons.utils.Strings;

import java.util.concurrent.ConcurrentHashMap;

public class EurekaClientBase<T> {
    @Autowired
    private EurekaClient client;
    private final ConcurrentHashMap<String, T> stubPool;

    public EurekaClientBase() {
        this.stubPool = new ConcurrentHashMap<>();
    }

    protected String getServiceName() {
        return null;
    }

    protected T getActualService(StubSupplier<String, Integer, T> stubCreator) {
        String serviceName = getServiceName();
        if (client == null || Strings.isNullOrEmpty(serviceName))
            throw new RuntimeException("Eureka client not configured properly.");

        final InstanceInfo instanceInfo = client.getNextServerFromEureka(serviceName, false);
        String serviceAddress = instanceInfo.getIPAddr();
        int servicePort = instanceInfo.getPort();
        return stubPool.computeIfAbsent(serviceAddress + ":" + servicePort, key -> {
            String[] addrPort = key.split(":");
            return stubCreator.get(addrPort[0], Integer.parseInt(addrPort[1]));
        });
    }
}
