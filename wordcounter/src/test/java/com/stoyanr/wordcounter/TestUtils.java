package com.stoyanr.wordcounter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.io.FileUtils;

public class TestUtils {

    public static void deleteDir(File dir) {
        while (dir.exists()) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
            }
        }
    }

    public static SortedMap<Integer, Set<String>> getHead(SortedMap<Integer, Set<String>> map,
        int top) {
        assert (top > 0 && top <= map.size());
        int key = 0;
        Set<Integer> keys = map.keySet();
        int count = 0;
        for (int keyx : keys) {
            if (count == top) {
                key = keyx;
                break;
            }
            count++;
        }
        return map.headMap(key);
    }

    public static void assertEqualMaps(Map<String, Integer> expected, Map<String, Integer> actual) {
        assertEquals(expected.size(), actual.size());
        for (Entry<String, Integer> e : actual.entrySet()) {
            assertEquals(expected.get(e.getKey()), e.getValue());
        }
    }

    public static void assertEqualSortedMaps(SortedMap<Integer, Set<String>> expected,
        SortedMap<Integer, Set<String>> actual) {
        assertEquals(expected.size(), actual.size());
        for (Entry<Integer, Set<String>> e : actual.entrySet()) {
            Set<String> es = expected.get(e.getKey());
            assertNotNull(es);
            Set<String> as = e.getValue();
            assertEquals(es.size(), as.size());
            for (String s : as) {
                assertTrue(es.contains(s));
            }
        }
    }
}