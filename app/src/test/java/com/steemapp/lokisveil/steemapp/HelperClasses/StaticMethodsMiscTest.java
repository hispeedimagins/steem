package com.steemapp.lokisveil.steemapp.HelperClasses;

import android.content.Context;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StaticMethodsMiscTest {



    @Test
    public void FormatDateGmt() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            sdf.parse("2018-04-06T06:00:30"+"+0000");
            return;
        } catch (ParseException pa){
            String ecs = pa.getMessage();
            throw new RuntimeException(pa.getMessage());
            // int i = 0;
        }
    }

    /*private static final String TEST_STRING = "HELLO WORLD!";

    //As we don't have access to Context in our JUnit test classes, we need to mock it

    @Mock

    Context mMockContext;

    @Test

    public void readStringFromContext() {

        //Returns the TEST_STRING when getString(R.string.hello_world) is called

        when(mMockContext.getString(R.string.text_hello_word)).thenReturn(TEST_STRING);

        //Creates an object of the ClassUnderTest with the mock context

        StaticMethodsMiscTest objectUnderTest = new StaticMethodsMiscTest(mMockContext);

        //Stores the return value of getHelloWorldString() in result

        String result = objectUnderTest.getHelloWorldString();

        //Asserts that result is the value of TEST_STRING

        assertThat(result, is(TEST_STRING));

    }*/

}