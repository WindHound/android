package windshift.windhound.objects;

import java.io.Serializable;

public class Event implements Serializable {

    private Long   id;
    private String name;
    private Long   startDate;
    private Long   endDate;
    private Long[] admins;
    private Long[] races;
    private Long[] championships;

    public Event() {

    }

    public Event(Long   a_id,
                 String a_name,
                 Long   a_startDate,
                 Long   a_endDate,
                 Long[] a_admins,
                 Long[] a_races,
                 Long[] a_championships) {
        id            = a_id;
        name          = a_name;
        startDate     = a_startDate;
        endDate       = a_endDate;
        admins        = a_admins;
        races         = a_races;
        championships = a_championships;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Long[] getAdmins() {
        return admins;
    }

    public void setAdmins(Long[] admins) {
        this.admins = admins;
    }

    public Long[] getRaces() {
        return races;
    }

    public void setRaces(Long[] races) {
        this.races = races;
    }

    public Long[] getChampionships() {
        return championships;
    }

    public void setChampionships(Long[] championships) {
        this.championships = championships;
    }

}