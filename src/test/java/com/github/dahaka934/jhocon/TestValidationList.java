package com.github.dahaka934.jhocon;

import com.github.dahaka934.jhocon.annotations.ValidatorStringList;
import com.github.dahaka934.jhocon.fieldlhandler.FieldHandlerValidator;
import org.junit.Assert;
import org.junit.Test;

public class TestValidationList extends Assert {
    static class SimpleClass {
        @ValidatorStringList({"foo", "bar"})
        public String value;

        SimpleClass(String value) {
            this.value = value;
        }
    }

    public void test(String value) {
        JHocon jhocon = new JHoconBuilder().registerDefaultValidators().create();
        SimpleClass obj = new SimpleClass(value);

        jhocon.fromHocon(jhocon.toHocon("root", obj), "root", SimpleClass.class);
    }

    @Test
    public void testValid() {
        test("foo");
    }

    @Test
    public void testInvalid() {
        boolean error = false;

        try {
            test("invalid");
        } catch (FieldHandlerValidator.Exception e) {
            error = true;
        }

        assertTrue(error);
    }
}
