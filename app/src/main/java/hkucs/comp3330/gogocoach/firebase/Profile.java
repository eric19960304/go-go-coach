package hkucs.comp3330.gogocoach.firebase;

public class Profile{
    public String sportTypes;
    public String bio;
    public String email;
    public String contactNumber;

    public Profile(){}

    public Profile(String sportTypes, String bio, String email, String contactNumber){
        this.sportTypes = sportTypes;
        this.bio = bio;
        this.email = email;
        this.contactNumber = contactNumber;
    }
}