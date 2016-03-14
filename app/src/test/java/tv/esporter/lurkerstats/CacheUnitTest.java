package tv.esporter.lurkerstats;

import org.junit.Test;

import tv.esporter.lurkerstats.service.Cache;

import static org.junit.Assert.assertTrue;

public class CacheUnitTest {
    @Test
    public void cache_test() throws Exception {


        Cache<String> cache = new Cache<>(String.class);

        cache.put("test", "Yo!");

        assertTrue(cache.exists("test"));

    }


}
