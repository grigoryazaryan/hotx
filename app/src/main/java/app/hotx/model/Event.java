package app.hotx.model;

import java.util.Date;

/**
 * Created by Grigory Azaryan on 10/7/18.
 */

public class Event {
    private Date dateTime;
    private String name;

    public Event() {
        this.dateTime = new Date();
    }

    public Event(String name) {
        this();
        this.name = name;
    }
}
