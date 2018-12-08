package com.github.dahaka934.jhocon;

import com.github.dahaka934.jhocon.annotations.ValidatorDoubleRange;
import com.github.dahaka934.jhocon.annotations.ValidatorRange;
import com.github.dahaka934.jhocon.fieldlhandler.FieldHandlerValidator;
import org.junit.Assert;
import org.junit.Test;

public class TestValidationRange extends Assert {
    static class SimpleClass {
        @ValidatorRange(min = 0, max = 50)
        public int value;
        @ValidatorDoubleRange(min = 0.0, max = 50.0)
        public float valueFloat;

        SimpleClass(int value) {
            this.value = value;
            valueFloat = value;
        }
    }

    public void test(int value) {
        JHocon jhocon = new JHoconBuilder().registerDefaultValidators().create();
        SimpleClass obj = new SimpleClass(value);

        jhocon.fromHocon(jhocon.toHocon("root", obj), "root", SimpleClass.class);
    }

    @Test
    public void testValid() {
        test(5);
    }

    @Test
    public void testInvalid() {
        boolean error = false;
        
        try {
            test(100);
        } catch (FieldHandlerValidator.Exception e) {
            error = true;
        }

        assertTrue(error);
    }
}
