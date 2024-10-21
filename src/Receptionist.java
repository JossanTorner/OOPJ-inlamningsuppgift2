import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class Receptionist {

    //Receptionisten ska interagera med gymmets medlemslista
    private Gym bestGymEver;

    public Receptionist(){
        bestGymEver = new Gym("Best Gym Ever");
    }

    public Gym getBestGymEver() {
        return bestGymEver;
    }

    public void setBestGymEver(Gym bestGymEver) {
        this.bestGymEver = bestGymEver;
    }

    //Tittar om namn och ss stämmer överrens med en medlem i gymmets medlemslista
    public Member isMember(String name, String ss){
        for(Member member : bestGymEver.getMembers()){
            if (name.equalsIgnoreCase(member.getName()) && ss.equalsIgnoreCase(member.getSsNumber())){
                return member;
            }
        }
        return null;
    }

    //Tittar om en medlem är aktiv eller icke aktiv
    public boolean isActiveMember(Member member){
        Period period = Period.between(member.getLatestPaymentDate(), LocalDate.now());
        return period.getYears() < 1 || (period.getYears() == 1 && period.getMonths() == 0 && period.getDays() == 0);
    }

    //Returnerar vilken medlemstyp det är
    public VisitorType identifyVisitorType(String name, String ssNumber){
        Member loggedIn = isMember(name, ssNumber);
        if(loggedIn != null){
            if (isActiveMember(loggedIn)){
                return VisitorType.ACTIVE_MEMBER;
            }
            return VisitorType.NON_ACTIVE_MEMBER;
        }
        return VisitorType.NON_MEMBER;
    }

    //Adderar besök till en medlems besökslista samt skriver över till besöksfil
    public boolean logSession(Member member, boolean test) {
        if (!isActiveMember(member)){
            return false;
        }

        FileHandler fileHandler = new FileHandler();
        LocalDateTime currentTime = LocalDateTime.now();
        member.addSession(currentTime);
        try{
            if (!test){
                fileHandler.writeToVisitFile(bestGymEver.visitLog, member, currentTime);
            }
            return true;
        }
        catch(IOException e){
            System.out.println("Error: Failed to write session to the visit log file.");
            return false;
        }
    }

    //Skapar ny gymmedlem, lägger till i gymlistan och skriver till medlemsfil
    public boolean signUpNewMember(String name, String ssNumber, boolean test){
        FileHandler fileHandler = new FileHandler();
        Member newMember = isMember(name, ssNumber);
        if(newMember != null){
            return false;
        }

        newMember = new Member(name, ssNumber, LocalDate.now());
        bestGymEver.addMember(newMember);

        if (!test){
            try{
                fileHandler.writeToMembersFile(bestGymEver.gymData, newMember);
            }
            catch(IOException e){
                System.out.println("Error: Failed to write member with SS: " + ssNumber +  " and Name: " + name + " to the member file.");
                e.printStackTrace();
            }
        }
        return true;
    }
}