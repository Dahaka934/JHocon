[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.dahaka934/jhocon/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.dahaka934/jhocon)
# JHocon

This is a [Gson](https://github.com/google/gson) wrapper using 
[Typesafe Config](https://github.com/lightbend/config) for supporting HOCON files.
JHocon overrides Gson' JsonWriter and JsonReader and a gives full compatibility with Gson.
JHocon providing simple helper functions to convert between Hocon and Java Objects.
Also, supported all Gson' TypeAdapters, TypeAdapterFactories and annotations.

## Usage
For example, we have some simple class.
```java
public final class Person {
    public String name;
    public int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```
### Converting non-generic object
```java
JHocon jhocon = new JHocon(new Gson());
Person person = new Person("foo", 20);

// Convert non-generic object to HOCON-string representation
String hocon = jhocon.toHocon("person", person);

// Create non-generic object from HOCON-string representation
Person personNew = jhocon.fromHocon(hocon, "person", Person.class);
```
Output hocon string:
```hocon
person {
    age=20
    name=foo
}
```

### Converting generic object
```java
JHocon jhocon = new JHocon(new Gson());
List<Person> family = new ArrayList<>();
family.add(new Person("foo", 20));
family.add(new Person("bar", 25));

// Convert generic object to HOCON-string representation.
String hocon = jhocon.toHocon("family", family);

// Create generic object from HOCON-string representation
Type type = new TypeToken<List<Person>>() {}.getType();
List<Person> familyNew = jhocon.fromHocon(hocon, "family", type);
```
Output hocon string:
```hocon
family=[
    {
        age=20
        name=foo
    },
    {
        age=25
        name=bar
    }
]
```
### Add comments in own TypeAdapter
```java
public class TestTypeAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter out, String value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        JHocon.setComment(out, "own comment");
        out.value(value);
    }
    // ...
}
```
