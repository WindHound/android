package windshift.windhound.objects;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;

public class Event implements Serializable {

    Long id;
    String name;
    Calendar startDate;
    Calendar endDate;
    HashSet<Long> admins;
    HashSet<Long> subordinates;
    HashSet<Long> managers;

    public Event() {

    }

    public Event(Long a_id,
                 String a_name,
                 Calendar a_startDate,
                 Calendar a_endDate,
                 HashSet<Long> a_admins,
                 HashSet<Long> a_races,
                 HashSet<Long> a_championships) {
        id = a_id;
        name = a_name;
        startDate = a_startDate;
        endDate = a_endDate;
        admins = a_admins;
        subordinates = a_races;
        managers = a_championships;
    }

    public Long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

}