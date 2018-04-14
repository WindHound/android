package windshift.windhound.objects;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;


public class Championship implements Serializable {

    Long id;
    String name;
    Calendar startDate;
    Calendar endDate;
    HashSet<Long> admins;
    HashSet<Long> subordinates;
    HashSet<Long> managers;

    public Championship() {

    }

    private Championship(Long          a_id,
                         String        a_name,
                         Calendar a_startDate,
                         Calendar a_endDate,
                         HashSet<Long> a_admins,
                         HashSet<Long> a_events,
                         HashSet<Long> a_managers) {
        id = a_id;
        name = a_name;
        startDate = a_startDate;
        endDate = a_endDate;
        admins = a_admins;
        subordinates = a_events;
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
