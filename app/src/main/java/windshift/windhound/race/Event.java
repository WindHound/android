package windshift.windhound.race;

import java.util.HashSet;

public class Event {
    Long id;
    String name;
    HashSet<Long> admins;
    HashSet<Long> subordinates;
    HashSet<Long> managers;

    public Event() {

    }

    public Event(Long a_id, String a_name, HashSet<Long> a_admins, HashSet<Long> a_races,
                 HashSet<Long> a_championships) {
        id = a_id;
        name = a_name;
        admins = a_admins;
        subordinates = a_races;
        managers = a_championships;
    }

    public String getName() {
        return name;
    }

}