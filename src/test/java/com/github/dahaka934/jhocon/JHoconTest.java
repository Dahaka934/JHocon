package com.github.dahaka934.jhocon;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JHoconTest extends Assert {
    static class SimpleClass {
        public int publicInt = 5;
        private Boolean privateBoolean = true;

        int[] intArray = new int[]{1, 2, 3};

        Map<String, Integer> stringIntMap = new HashMap<>();

        List<SimpleSubClass> objList = new ArrayList<>();

        {
            stringIntMap.put("key1", 1);
            stringIntMap.put("key2", 2);
            objList.add(new SimpleSubClass());
            objList.add(new SimpleSubClass());
        }

        void reinit() {
            publicInt = 7;
            privateBoolean = false;
            intArray = new int[]{5, 2, 3, 8};
            stringIntMap.clear();
            stringIntMap.put("key2", 2);
            stringIntMap.put("key3", 3);
            objList.add(new SimpleSubClass());
        }
    }

    static class SimpleSubClass {
        String str = "some text";
    }

    @Test
    public void testNonGenericObject() {
        JHocon jhocon = new JHocon(new Gson());
        SimpleClass obj = new SimpleClass();
        obj.reinit();

        String hocon = jhocon.toHocon(obj);

        System.out.println("Object to Hocon:");
        System.out.println(hocon);
        System.out.println();

        SimpleClass newObj = jhocon.fromHocon(hocon, SimpleClass.class);

        assertEquals(obj.publicInt, newObj.publicInt);
        assertEquals(obj.privateBoolean, newObj.privateBoolean);
        assertArrayEquals(obj.intArray, newObj.intArray);
        assertEquals(obj.stringIntMap.size(), newObj.stringIntMap.size());
        assertEquals(obj.objList.size(), newObj.objList.size());
    }
}
