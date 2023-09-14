package su.svn.config;

import su.svn.entity.Organization;
import su.svn.entity.Person;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.springdata.repository.config.EnableIgniteRepositories;
import org.apache.ignite.springframework.boot.autoconfigure.IgniteConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableIgniteRepositories(value = "su.svn.*")
public class IgniteCacheConfiguration {
    @Bean(name = "igniteInstance")
    public Ignite igniteInstance(Ignite ignite) {
        return ignite;
    }

    @Bean
    public IgniteConfigurer configurer() throws UnknownHostException {
        var host = InetAddress.getByName("client").getHostAddress();
        return igniteConfiguration -> {
            igniteConfiguration.setClientMode(true);

            TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
            TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
            // TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
            ipFinder.setAddresses(Arrays.asList("127.0.0.1:47500..47509", host + ":47500..47509"));
            tcpDiscoverySpi.setIpFinder(ipFinder);
            tcpDiscoverySpi.setLocalAddress(host);
            tcpDiscoverySpi.setLocalPort(47500);
            // Changing local port range. This is an optional action.
            tcpDiscoverySpi.setLocalPortRange(9);
            tcpDiscoverySpi.setNetworkTimeout(10000);
            tcpDiscoverySpi.setAckTimeout(10000);
            tcpDiscoverySpi.setForceServerMode(false);
            //tcpDiscoverySpi.setLocalAddress("localhost");
            igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
//
//            TcpCommunicationSpi communicationSpi = new TcpCommunicationSpi();
//            communicationSpi.setLocalAddress("192.168.22.134");
//            communicationSpi.setLocalPort(10800);
//            communicationSpi.setSlowClientQueueLimit(10000);
//            communicationSpi.setMaxConnectTimeout(TcpCommunicationSpi.DFLT_MAX_CONN_TIMEOUT);
//            communicationSpi.setMessageQueueLimit(1024);
//            igniteConfiguration.setCommunicationSpi(communicationSpi);

            igniteConfiguration.setCacheConfiguration(cacheConfiguration());
        };
    }
//
//    @Bean
//    public Ignite igniteInstance() {
//        return Ignition.start(igniteConfiguration());
//    }
//
//    @Bean(name = "igniteConfiguration")
//    public IgniteConfiguration igniteConfiguration() {
//        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
//        // igniteConfiguration.setIgniteInstanceName("testIgniteInstance");
//        // igniteConfiguration.setIgniteInstanceName("ignite");
//        // igniteConfiguration.setClientMode(true);
//        // igniteConfiguration.setPeerClassLoadingEnabled(true);
//        igniteConfiguration.setLocalHost("127.0.0.1");
//        igniteConfiguration.setPeerClassLoadingEnabled(false);
//
//        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
//        // TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
//        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
//        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
//        tcpDiscoverySpi.setIpFinder(ipFinder);
//        tcpDiscoverySpi.setLocalPort(47500);
//        // Changing local port range. This is an optional action.
//        tcpDiscoverySpi.setLocalPortRange(9);
//        //tcpDiscoverySpi.setLocalAddress("localhost");
//        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
//
//        TcpCommunicationSpi communicationSpi = new TcpCommunicationSpi();
//        communicationSpi.setLocalAddress("localhost");
//        communicationSpi.setLocalPort(48100);
//        communicationSpi.setSlowClientQueueLimit(1000);
//        igniteConfiguration.setCommunicationSpi(communicationSpi);
//
//
//        igniteConfiguration.setCacheConfiguration(cacheConfiguration());
//
//        return igniteConfiguration;
//
//    }

    @Bean(name = "cacheConfiguration")
    public CacheConfiguration[] cacheConfiguration() {
        List<CacheConfiguration> cacheConfigurations = new ArrayList<>();
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        cacheConfiguration.setCacheMode(CacheMode.REPLICATED);
        cacheConfiguration.setName("employee");
        cacheConfiguration.setStatisticsEnabled(true);

        CacheConfiguration cacheConfiguration1 = new CacheConfiguration();
        cacheConfiguration1.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        cacheConfiguration1.setCacheMode(CacheMode.REPLICATED);
        cacheConfiguration1.setName("student");
        cacheConfiguration1.setStatisticsEnabled(true);

        // Defining and creating a new cache to be used by Ignite Spring Data
        // repository.
        CacheConfiguration<Long, Person> ccfg = new CacheConfiguration("PersonCache");
        // Setting SQL schema for the cache.
        ccfg.setIndexedTypes(Long.class, Person.class);

        CacheConfiguration<Long, Organization> orgCacheCfg = new CacheConfiguration<>("ORG_CACHE");
        orgCacheCfg.setCacheMode(CacheMode.REPLICATED); // Default.
        orgCacheCfg.setIndexedTypes(Long.class, Organization.class);

        cacheConfigurations.add(ccfg);
        cacheConfigurations.add(orgCacheCfg);
        cacheConfigurations.add(cacheConfiguration);
        cacheConfigurations.add(cacheConfiguration1);

        return cacheConfigurations.toArray(new CacheConfiguration[cacheConfigurations.size()]);
    }
}
