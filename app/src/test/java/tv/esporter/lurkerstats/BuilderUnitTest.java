package tv.esporter.lurkerstats;

import junit.framework.Assert;

import org.junit.Test;

import tv.esporter.lurkerstats.util.Build;


public class BuilderUnitTest {

    @Test
    public void testKeyBuilder() throws Exception {

        final String key1 = "A";
        final String key2 = "BC";
        final String key = "A#BC";

        Assert.assertEquals(key, Build.key(key1, key2));

    }

}
