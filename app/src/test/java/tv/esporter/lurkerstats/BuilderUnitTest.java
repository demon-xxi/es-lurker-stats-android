package tv.esporter.lurkerstats;

import android.content.Intent;

import junit.framework.Assert;

import org.junit.Test;

import tv.esporter.lurkerstats.util.Build;


public class BuilderUnitTest {

    @Test
    public void testSimpleIntent() throws Exception {

        final String action = "TEST_ACTION";
        final String extra = "TEST_EXTRA";
        final String value = "VALUE";

        Intent intent = Build.intent(action)
                .extra(extra, value)
                .build();

        Assert.assertEquals(action, intent.getAction());
        Assert.assertEquals(extra, intent.getStringExtra(extra));

    }

    @Test
    public void testKeyBuilder() throws Exception {

        final String key1 = "A";
        final String key2 = "BC";
        final String key = "A#BC";

        Assert.assertEquals(key, Build.key(key1, key2));

    }

}
