package org.example.socialnetworkfx.socialnetworkfx.domain.event;

import org.example.socialnetworkfx.socialnetworkfx.domain.Message;

public class MessageEntityChange extends jdk.jfr.Event implements Event {
    private ChangeEventType type;
    private Message data;

    public MessageEntityChange(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }


    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

}
