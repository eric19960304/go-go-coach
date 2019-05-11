package hkucs.comp3330.gogocoach.firebase;

import java.io.Serializable;

public class Classes implements Serializable {
    public String id;
    public String name;
    public String description;
    public String number;
    public String price;
    public String time;
    public String type;
    public String location;
    public Double latitude;
    public Double longitude;
    public String className;
    public String photoUrl;

    public Classes(){}

    public Classes(String id, String name, String description, String number, String className, String price,
                   String time, String type, String location, Double latitude, Double longitude, String photoUrl){
        this.id = id;
        this.name = name;
        this.description = description;
        this.number = number;
        this.className = className;
        this.price = price;
        this.time = time;
        this.type = type;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoUrl = photoUrl;
    }
}
