import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class Receptionist {

    //Receptionisten ska interagera med gymmets medlemslista
    private Gym bestGymEver;

    public Receptionist(){
        bestGymEver = new Gym();
    }

    public Gym getBestGymEver() {
        return bestGymEver;
    }

    public void setBestGymEver(Gym bestGymEver) {
        this.bestGymEver = bestGymEver;
    }

    public Member isMember(String name, String id){
        for(Member member : bestGymEver.getMembers()){
            if (name.equalsIgnoreCase(member.getName()) && id.equalsIgnoreCase(member.getId())){
                return member;
            }
        }
        return null;
    }

    public boolean isActiveMember(Member member){
        return Period.between(member.getLatestPaymentDate(), LocalDate.now()).getYears() <= 1;
    }

    public String validateVisitor(String name, String id){
        Member loggedIn = isMember(name, id);
        if(loggedIn != null){
            if (isActiveMember(loggedIn)){
                logSession(loggedIn);
                return "Active member";
            }
            return "Non active member";
        }
        return "Not a member";
    }

    public void logSession(Member member){
        FileHandler fileHandler = new FileHandler();
        member.addSession(LocalDateTime.now());
        try{
            fileHandler.writeToVisitLog(member, bestGymEver.visitLog, LocalDateTime.now());
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

}
