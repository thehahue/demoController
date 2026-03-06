package at.bbrz.demo;

public class Hund {
    private int id;
    private String name;
    private int age;

    public Hund(String name, int age, int id) {
        this.name = name;
        this.age = age;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getId() {
        return id;
    }
}
