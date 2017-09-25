package com.catherine.webservices;

import android.test.mock.MockContext;

import com.catherine.webservices.network.NetworkHelper;

import org.junit.Test;

import java.net.InetAddress;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Catherine on 2017/8/24.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class UT_NetworkHelper {

    @Test
    public void testHostNameBaidu() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getHostName("103.235.46.39");
        assertThat(s, is("103.235.46.39"));//无法解析
    }

    @Test
    public void testHostNameOracle() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getHostName("104.115.242.175");
        assertThat(s, is("a104-115-242-175.deploy.static.akamaitechnologies.com"));
    }

    @Test
    public void testHostNameCD() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getHostName("54.251.164.119");
        assertThat(s, is("ec2-54-251-164-119.ap-southeast-1.compute.amazonaws.com"));
    }

    @Test
    public void testDNSHostNameBaidu() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getDNSHostName("103.235.46.39");
        assertThat(s, is("103.235.46.39"));//无法解析
    }

    @Test
    public void testDNSHostNameOracle() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getDNSHostName("104.115.242.175");
        assertThat(s, is("a104-115-242-175.deploy.static.akamaitechnologies.com"));
    }

    @Test
    public void testHostNameNEU6() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getHostName("2001:da8:9000:0:0:0:0:7");
        assertThat(s, is("www.neu6.edu.cn"));
    }

    @Test
    public void testIpAddressCD() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getHostAddress("dictionary.cambridge.org");
        assertThat(s, is("54.251.164.119"));
    }

    @Test
    public void testIpAddressIBM() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getHostAddress("www.ibm.com");
        assertThat(s, is("104.115.242.175"));
    }

    @Test
    public void testIpAddressBaidu() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getHostAddress("www.baidu.com");
        assertThat(s, is("103.235.46.39"));
    }

    //ipv6
    @Test
    public void testIpAddressNEU6() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getHostAddress("www.neu6.edu.cn");
        assertThat(s, is("2001:da8:9000:0:0:0:0:7"));
    }

    @Test
    public void testGetURLInfo() {
        NetworkHelper helper = new NetworkHelper(new MockContext());
        String s = helper.getHostAddress("www.neu6.edu.cn");
        assertThat(s, is("54.251.164.119"));
    }
}
