package com.catherine.webservices;

import android.content.Context;

import com.catherine.webservices.toolkits.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Catherine on 2017/7/24.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitTestSample2 {

    private static final String FAKE_STRING = "MB890CPC";

    @Mock
    Context mMockContext;

    @Test
    public void readStringFromMethod_LocalizedString() {
        Utils utils = new Utils();

        // ...when the string is returned from the object under test...
        String result = utils.junitTestSample2();

        // ...then the result should be the expected one.
        assertThat(result, is(FAKE_STRING));
    }

    @Test
    public void readStringFromContext_LocalizedString() {
        Utils utils = new Utils();

        // ...when the string is returned from the object under test...
        String result = utils.getJUNIT_TEST();

        // ...then the result should be the expected one.
        assertThat(result, is(FAKE_STRING));
    }
}