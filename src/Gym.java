import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Gym {

    private List<Member> members;
    private final Path gymData = Paths.get("src/members-data.txt");
    protected final Path visitLog = Paths.get("src/visit-log.txt");

    public Gym() {
        loadGymMembers();
        loadGymMembersSessionLists();
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

    public void loadGymMembers(){
        FileHandler fileHandler = new FileHandler();
        try{
            members = fileHandler.getMembersFromFile(gymData);
        }
        catch(DateTimeParseException | IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadGymMembersSessionLists(){
        FileHandler fileHandler = new FileHandler();
        try{
            fileHandler.getVisitsFromFile(visitLog, members);
        }
        catch(DateTimeParseException | IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
