import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public List<Member> getMembersFromFile(Path path) throws DateTimeParseException, IOException {
        List<Member> list = new ArrayList<Member>();
        if (Files.exists(path)) {
            try(BufferedReader br = Files.newBufferedReader(path)){
                String temp;
                while ((temp = br.readLine()) != null){

                    String id = temp.substring(0,temp.indexOf(',')).trim();
                    String name = temp.substring(temp.indexOf(',')+1).trim();
                    LocalDate latestPaymentDate = LocalDate.parse(br.readLine());
                    list.add(new Member(name, id, latestPaymentDate));
                }
            }
            catch (DateTimeParseException e){
                throw new DateTimeParseException("An exception was thrown when reading from file due to invalid date format: ", e.getParsedString(), 0);
            }
            catch (IOException e){
                throw new IOException("An I/O exception was thrown when reading from member data file");
            }
        }
        return list;
    }

    public void writeToVisitLog(Member member, Path path, LocalDateTime dateTime) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try(BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE)){
            bw.write(dateTime.format(formatter) +", " + member.getId() + ", " + member.getName());
            bw.newLine();
        }
    }

    public void writeToMembersFile(List<Member> members, Path path, LocalDate dateTime) throws IOException {

    }

    public void getVisitsFromFile(Path path, List<Member> memberList) throws DateTimeParseException, IOException {
        if (Files.exists(path)){
            try(BufferedReader br = Files.newBufferedReader(path)){
                String temp;
                while( (temp = br.readLine()) != null){
                    String[] data = temp.split(",");
                    for (Member member : memberList) {
                        if (member.getId().equals(data[1].trim()) && member.getName().equals(data[2].trim())) {
                            LocalDateTime dateTime = LocalDateTime.parse(data[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                            member.addSession(dateTime);
                        }
                    }
                }
            }
            catch (DateTimeParseException e){
                throw new DateTimeParseException("An exception was thrown when reading from file due to invalid date format: ", e.getParsedString(), 0);
            }
            catch (IOException e){
                throw new IOException("An I/O exception was thrown when reading from visit log file");
            }
        }
    }
}
