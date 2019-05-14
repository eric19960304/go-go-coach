/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hkucs.comp3330.gogocoach.firebase;

public class UserChatItem {

    private String uid;
    private String name;
    private String sender;
    private String senderPhotoUrl;
    private String photoUrl;
    private String lastMessage;
    private String time;


    public UserChatItem() {
    }

    public UserChatItem(String uid, String name, String sender, String senderPhotoUrl, String photoUrl, String lastMessage, String time) {
        this.uid = uid;
        this.name = name;
        this.sender = sender;
        this.senderPhotoUrl = senderPhotoUrl;
        this.time = time;
        this.photoUrl = photoUrl;
        this.lastMessage = lastMessage;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {return name;}

    public String getSender() {return sender;}

    public String getSenderPhotoUrl() {return senderPhotoUrl;}

    public String getPhotoUrl() {return photoUrl;}

    public String getlastMessage() {return lastMessage;}

    public String getTime() {return time;}
}
