import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Member {

    private String name;
    private String ssNumber;
    private LocalDate latestPaymentDate;
    private List<LocalDateTime> GymSessions;

    public Member(String name, String ssNumber, LocalDate latestPaymentDate) {
        this.name = name;
        this.ssNumber = ssNumber;
        this.latestPaymentDate = latestPaymentDate;
        GymSessions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsNumber() {
        return ssNumber;
    }

    public void setSsNumber(String ssNumber) {
        this.ssNumber = ssNumber;
    }

    public LocalDate getLatestPaymentDate() {
        return latestPaymentDate;
    }

    public void setLatestPaymentDate(LocalDate latestPaymentDate) {
        this.latestPaymentDate = latestPaymentDate;
    }

    public List<LocalDateTime> getGymSessions() {
        return GymSessions;
    }

    public void addSession(LocalDateTime dateTime) {
        GymSessions.add(dateTime);
    }

    public String getMemberInfo(){
        return getSsNumber() + " " + getName() + "\nLatest payment made: " + getLatestPaymentDate() + "\nLogged gym sessions:\n" + allSessions();
    }

    public StringBuilder allSessions(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        for(LocalDateTime dateTime : GymSessions){
            sb.append(dateTime.format(formatter)).append("\n");
        }
        return sb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return ssNumber.equals(member.ssNumber) && name.equals(member.name) && latestPaymentDate.equals(member.latestPaymentDate);
    }
}