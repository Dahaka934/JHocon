package com.github.dahaka934.jhocon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReadmeUsage extends Assert {
    public static final class Person {
        public String name;
        public int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    public void testObject() {
        JHocon jhocon = new JHocon(new Gson());
        Person person = new Person("foo", 20);

        // Convert non-generic object to HOCON-string representation
        String hocon = jhocon.toHocon("person", person);

        // Create non-generic object from HOCON-string representation
        Person personNew = jhocon.fromHocon(hocon, "person", Person.class);

        System.out.println(hocon);
        assertEquals(person.name, personNew.name);
        assertEquals(person.age, personNew.age);
    }

    @Test
    public void testCollection() {
        JHocon jhocon = new JHocon(new Gson());
        List<Person> family = new ArrayList<>();
        family.add(new Person("foo", 20));
        family.add(new Person("bar", 25));

        // Convert generic object to HOCON-string representation.
        String hocon = jhocon.toHocon("family", family);

        // Create generic object from HOCON-string representation
        Type type = new TypeToken<List<Person>>() {
        }.getType();
        List<Person> familyNew = jhocon.fromHocon(hocon, "family", type);

        System.out.println(hocon);
        assertEquals(family.size(), familyNew.size());
    }
}
