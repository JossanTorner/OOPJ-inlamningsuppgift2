import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Member {

    private String name;
    private String id;
    private LocalDate latestPaymentDate;
    private List<LocalDateTime> sessions;

    public Member(String name, String id, LocalDate latestPaymentDate) {
        this.name = name;
        this.id = id;
        this.latestPaymentDate = latestPaymentDate;
        sessions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getLatestPaymentDate() {
        return latestPaymentDate;
    }

    public void setLatestPaymentDate(LocalDate latestPaymentDate) {
        this.latestPaymentDate = latestPaymentDate;
    }

    public List<LocalDateTime> getSessions() {
        return sessions;
    }

    public void addSession(LocalDateTime dateTime) {
        sessions.add(dateTime);
    }

    public String getMemberInfo(){
        return getId() + " " + getName() + "\nLatest payment made: " + getLatestPaymentDate() + "\nLogged sessions:\n" + allSessions();
    }

    public StringBuilder allSessions(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        for(LocalDateTime dateTime : sessions){
            sb.append(dateTime.format(formatter)).append("\n");
        }
        return sb;
    }

    @Override
    public String toString() {
        return id + ", " + name + "\n" + latestPaymentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id.equals(member.id) && name.equals(member.name) && latestPaymentDate.equals(member.latestPaymentDate);
    }

}
