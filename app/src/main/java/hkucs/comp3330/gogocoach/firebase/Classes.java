package hkucs.comp3330.gogocoach.firebase;

public class Classes {
    public String name;
    public String description;
    public String number;
    public String price;
    public String time;
    public String type;
    public String id;
    public String location;
    public String className;

    public Classes(){}

    public Classes(String name, String description, String number, String price, String time, String type, String id, String location){
        this.description = description;
        this.number = number;
        this.price = price;
        this.time = time;
        this.type = type;
        this.id = id;
        this.location = location;
    }
}
