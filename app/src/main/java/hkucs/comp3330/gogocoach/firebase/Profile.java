package hkucs.comp3330.gogocoach.firebase;

import java.io.Serializable;

public class Profile implements Serializable {
    public String sportTypes;
    public String bio;
    public String name;
    public String photourl;
    public String email;
    public String contactNumber;

    public Profile(){}

    public Profile(String sportTypes, String bio, String name, String photourl, String email, String contactNumber){
        this.sportTypes = sportTypes;
        this.bio = bio;
        this.name = name;
        this.photourl = photourl;
        this.email = email;
        this.contactNumber = contactNumber;
    }
}