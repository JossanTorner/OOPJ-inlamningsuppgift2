import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Gym {

    private String GymName;
    private List<Member> members;
    protected final Path gymData = Paths.get("src/members-data.txt");
    protected final Path visitLog = Paths.get("src/visit-log.txt");

    public Gym(String gymName) {
        this.GymName = gymName;
        loadGymMembersList();
        loadGymMembersSessionLists();
    }

    public void setGymName(String gymName) {
        this.GymName = gymName;
    }

    public String getGymName() {
        return GymName;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }


    public void addMember(Member member){
        members.add(member);

    }

    public void removeMember(Member member){
        members.remove(member);

    }

    public void loadGymMembersList(){
        FileHandler fileHandler = new FileHandler();
        try{
            members = fileHandler.getMembersFromFile(gymData);
        }
        catch (StringIndexOutOfBoundsException e){
            System.out.println("Error: Invalid format detected in the member-data file: " + gymData);
            e.printStackTrace();
        }
        catch(DateTimeParseException e){
            System.out.println("Error: Failed to parse date in the member-data file: " + gymData);
            e.printStackTrace();
        }
        catch (IOException e){
            System.out.println("Error: Unable to read from the member-data file: " + gymData);
            e.printStackTrace();
        }
    }

    public void loadGymMembersSessionLists(){
        FileHandler fileHandler = new FileHandler();
        try{
            fileHandler.getVisitsFromFile(visitLog, members);
        }
        catch(DateTimeParseException e){
            System.out.println("Error: Failed to parse date in the visit-log file: " + visitLog);
            e.printStackTrace();
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Error: Invalid format detected in the visit-log file: " + visitLog);
            e.printStackTrace();
        }
        catch (IOException e){
            System.out.println("Error: Unable to read from the visit-log file: " + visitLog);
            e.printStackTrace();
        }
    }

    public void printMembers(){
        for(Member member: getMembers()){
            System.out.println(member.getMemberInfo());
        }
    }
}