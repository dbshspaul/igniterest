package configuration;

import cachestore.HelloWorldCacheStore;
import org.apache.ignite.*;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.logger.java.JavaLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.FactoryBuilder;
import java.util.Arrays;

@Configuration
public class IgniteConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(IgniteConfiguration.class);

    @Value("${persistence.node.ip}")
    private String ip;

    @Bean(name = "igniteCache")
    public IgniteCache<String, String> getIgniteCache() {
        Ignition.setClientMode(true);
        org.apache.ignite.configuration.IgniteConfiguration igniteConfiguration = new org.apache.ignite.configuration.IgniteConfiguration();

        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
        dataRegionConfiguration.setName("Default_Region");
//        dataRegionConfiguration.setMaxSize(4L * 1024 * 1024 * 1024);
        dataRegionConfiguration.setPersistenceEnabled(true);
        storageCfg.setDataRegionConfigurations(dataRegionConfiguration);
        igniteConfiguration.setDataStorageConfiguration(storageCfg);


        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Arrays.asList(ip + ":47500..47502"));
        tcpDiscoverySpi.setIpFinder(ipFinder);
        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);

        IgniteLogger log = new JavaLogger(true);
        igniteConfiguration.setGridLogger(log);


        Ignite ignite = Ignition.start(igniteConfiguration);
        IgniteCluster cluster = ignite.cluster();
        cluster.active();
        LOGGER.info(">>>>>>>>>>>>>>>>Ignite Cache Started successfully");

//        IgniteCache<String, String> cache=ignite.getOrCreateCache("hello-world");

        CacheConfiguration contractCacheConfig = new CacheConfiguration();
        contractCacheConfig.setName("hello-world");
        contractCacheConfig.setBackups(1);
        contractCacheConfig.setCacheMode(CacheMode.PARTITIONED);
        contractCacheConfig.setCacheStoreFactory(FactoryBuilder.factoryOf(HelloWorldCacheStore.class));
        IgniteCache<String, String> cache = ignite.getOrCreateCache(contractCacheConfig);
        LOGGER.info("hello-world client cache created.");
        return cache;
    }
}
