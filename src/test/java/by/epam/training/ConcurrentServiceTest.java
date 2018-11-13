package by.epam.training;

import by.epam.training.cache.FileCache;
import by.epam.training.cache.RedisCache;
import by.epam.training.service.ConcurrentHtmlParsingServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class ConcurrentServiceTest {

    @Mock
    private FileCache fileCache;

    @Mock
    private RedisCache redisCache;

    @InjectMocks
    private ConcurrentHtmlParsingServiceImpl concurrentParsingService;

    private Set<String> testSet;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void concurrentHtmlParsingServiceWithNotEmptyFileCacheTest() {

        testSet = new HashSet<>(Arrays.asList("url", "url2"));

        when(fileCache.checkCache(anyString())).thenReturn(testSet);
        Set<String> result = concurrentParsingService.parse(anyString(), false);

        verify(redisCache, atLeastOnce()).checkCache(anyString());
        verify(fileCache, atLeastOnce()).checkCache(anyString());
        Assert.assertEquals(testSet, result);
    }

    @Test
    public void concurrentHtmlParsingServiceWithNotEmptyRedisCacheTest() {

        testSet = new HashSet<>(Arrays.asList("url", "url2"));

        when(redisCache.checkCache(anyString())).thenReturn(testSet);
        Set<String> result = concurrentParsingService.parse(anyString(), false);

        verify(redisCache, atLeastOnce()).checkCache(anyString());
        verify(fileCache, never()).checkCache(anyString());

        Assert.assertEquals(testSet, result);
    }

    @Test
    public void concurrentHtmlParsingServiceWithSkipCacheCheckTrueTest() {
        String testString = "teststring";
        concurrentParsingService.parse(testString, true);

        verify(redisCache, never()).checkCache(anyString());
        verify(fileCache, never()).checkCache(anyString());
    }
}
