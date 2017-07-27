package com.catherine.webservices;

import com.catherine.webservices.toolkits.Utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Catherine on 2017/7/24.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class UnitTestSample1 {
    @Test
    public void stringValidator_CorrectStringSimple_ReturnsTrue1() {
        assertThat(Utils.Companion.junitTestSample1(), is("CSOp40c"));
    }
    @Test
    public void stringValidator_CorrectStringSimple_ReturnsTrue2() {
        assertThat(Utils.Companion.junitTestSample1(), is("AC0VSVk"));
    }
}
