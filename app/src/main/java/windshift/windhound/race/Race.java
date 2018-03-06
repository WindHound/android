package windshift.windhound.race;

import java.util.HashSet;

public class Race {

    Long id;
    String name;
    HashSet<Long> admins;
    HashSet<Long> subordinates;
    HashSet<Long> managers;
    // time and date

    public Race() {

    }

    private Race(Long          a_id,
                 String        a_name,
                 HashSet<Long> a_admins,
                 HashSet<Long> a_boats,
                 HashSet<Long> a_events) {
        id = a_id;
        name = a_name;
        admins = a_admins;
        subordinates = a_boats;
        managers = a_events;
    }

    public Long getID() {
        return id;
    }

}
