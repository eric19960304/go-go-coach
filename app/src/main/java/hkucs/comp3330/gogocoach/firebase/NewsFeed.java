package hkucs.comp3330.gogocoach.firebase;

import java.io.Serializable;

public class NewsFeed implements Serializable, Comparable< NewsFeed >  {
    public String topic;
    public String content;
    public String time;
    public String id;
    public String name;
    public String photoUrl;
    public String imageUrl;

    public NewsFeed(){}

    public NewsFeed(String topic,String content,String time, String id, String name , String photoUrl, String imageUrl){
        this.topic =topic;
        this.content = content;
        this.time= time;
        this.id = id;
        this.name= name;
        this.photoUrl= photoUrl;
        this.imageUrl= imageUrl;
    }

    public int compareTo(NewsFeed o) {
        return o.time.compareTo(this.time);
    }

}