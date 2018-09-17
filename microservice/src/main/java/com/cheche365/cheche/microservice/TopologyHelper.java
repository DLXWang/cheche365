package com.cheche365.cheche.microservice;

import com.cheche365.cheche.core.util.ProfileProperties;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Created by liqiang on 7/24/15.
 */
public class TopologyHelper {

    private static Logger logger = LoggerFactory.getLogger(TopologyHelper.class);
    private static final List<String> members = new ArrayList<>();
    private static final int port;
    private static final int portCount;


    static {
        Properties properties = new Properties();
        try {
            properties.load(TopologyHelper.class.getResourceAsStream("/topology.properties"));
        } catch (IOException e) {
            logger.error("load topology property file failure!", e);
        }
        ProfileProperties profileProperties = new ProfileProperties(properties);
        String memberString = profileProperties.getProperty("members");
        members.addAll(Arrays.asList(memberString.split(",")));
        port = Integer.parseInt(profileProperties.getProperty("port"));
        portCount = Integer.parseInt(profileProperties.getProperty("portCount"), 10);
    }


    public static void setTopology(Config hazelcastConfig) {
        JoinConfig join = hazelcastConfig.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true).setMembers(members);
        NetworkConfig networkConfig = hazelcastConfig.getNetworkConfig();
        networkConfig.setPort(port).setPortCount(portCount);
        networkConfig.getInterfaces().setEnabled(true).addInterface(getLocalInterface());

    }

    public static String getLocalInterface() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress address = inetAddresses.nextElement();
                    if (members.contains(address.getHostAddress())) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            logger.error("read network info failure.", e);
        }

        return null;
    }
}
