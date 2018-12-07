package com.github.dahaka934.jhocon;

import com.github.dahaka934.jhocon.internal.TestTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
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

        SimpleClass reinit() {
            publicInt = 7;
            privateBoolean = false;
            intArray = new int[]{5, 2, 3, 8};
            stringIntMap.clear();
            stringIntMap.put("key2", 2);
            stringIntMap.put("key3", 3);
            objList.add(new SimpleSubClass());

            return this;
        }
    }

    static class SimpleSubClass {
        @JsonAdapter(TestTypeAdapter.class)
        String str = "some text";
    }

    private Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        JHoconHelper.initBuilder(builder);
        return builder.create();
    }

    @Test
    public void testNonGenericObject() {
        JHocon jhocon = new JHocon(createGson());
        SimpleClass obj = new SimpleClass();
        obj.reinit();

        String hocon = jhocon.toHocon("root", obj);

        System.out.println("Object to Hocon:");
        System.out.println(hocon);
        System.out.println();

        SimpleClass newObj = jhocon.fromHocon(hocon, "root", SimpleClass.class);

        assertEquals(obj.publicInt, newObj.publicInt);
        assertEquals(obj.privateBoolean, newObj.privateBoolean);
        assertArrayEquals(obj.intArray, newObj.intArray);
        assertEquals(obj.stringIntMap.size(), newObj.stringIntMap.size());
        assertEquals(obj.objList.size(), newObj.objList.size());
    }

    @Test
    public void testGenericObject() {
        JHocon jhocon = new JHocon(createGson());
        Map<String, SimpleClass> obj = new HashMap<>();
        obj.put("default", new SimpleClass());
        obj.put("reinited", new SimpleClass().reinit());

        String hocon = jhocon.toHocon("root", obj);

        System.out.println("Map of objects to Hocon:");
        System.out.println(hocon);
        System.out.println();

        Type type = new TypeToken<Map<String, SimpleClass>>() {
        }.getType();
        Map<String, SimpleClass> newObj = jhocon.fromHocon(hocon, "root", type);

        assertEquals(obj.size(), newObj.size());
    }
}
