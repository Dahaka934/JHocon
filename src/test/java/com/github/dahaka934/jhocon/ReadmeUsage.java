package com.github.dahaka934.jhocon;

import com.github.dahaka934.jhocon.annotations.Comment;
import com.github.dahaka934.jhocon.annotations.ValidatorRange;
import com.github.dahaka934.jhocon.annotations.ValidatorStringList;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReadmeUsage extends Assert {
    public static final class Person {
        @Comment("person name")
        @ValidatorStringList(value = {"reserved"}, invert = true)
        public String name;
        @Comment
        @ValidatorRange(min = 0, max = 150)
        public int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    public void testObject() {
        JHocon jhocon = new JHoconBuilder().withComments().registerDefaultValidators().create();
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
        JHocon jhocon = new JHoconBuilder().withComments().registerDefaultValidators().create();
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
