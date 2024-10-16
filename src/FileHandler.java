import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public List<Member> getMembersFromFile(Path path) throws ParseException, IOException {
        List<Member> list = new ArrayList<Member>();
        if (Files.exists(path)) {
            try(BufferedReader br = Files.newBufferedReader(path)){
                String temp;
                while ((temp = br.readLine()) != null){

                    String id = temp.substring(0,temp.indexOf(',')).trim();
                    String name = temp.substring(temp.indexOf(',')+1).trim();
                    LocalDate date = (LocalDate) validateLocalDate(br.readLine());

                    list.add(new Member(name, id, date));
                }
            }
            catch (ParseException e){
                throw new ParseException("An exception was thrown due to reading invalid date from members-data file", 0);
            }
            catch (IOException e){
                throw new IOException("An I/O exception was thrown when reading from members file");
            }
        }
        return list;
    }

    public Temporal validateLocalDate(String time) throws ParseException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try{
            return LocalDateTime.parse(time, dateTimeFormatter);
        }
        catch(DateTimeParseException e){
            try{
                return LocalDate.parse(time, dateFormatter);
            }
            catch(DateTimeParseException e2){
                throw new ParseException("Invalid date format for string: " + time, 0);
            }
        }
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

    public void getVisitsFromFile(Path path, List<Member> memberList) throws IOException, ParseException {
        if (Files.exists(path)){
            try(BufferedReader br = Files.newBufferedReader(path)){
                String temp;
                while( (temp = br.readLine()) != null){
                    String[] data = temp.split(",");
                    for (Member member : memberList) {
                        if (member.getId().equals(data[1].trim()) && member.getName().equals(data[2].trim())) {
                            LocalDateTime dateTime = (LocalDateTime) validateLocalDate(data[0]);
                            member.addSession(dateTime);
                        }
                    }
                }
            }
            catch (ParseException e){
                throw new ParseException("An exception was thrown due to reading invalid date from visit-log file", 0);
            }
            catch (FileNotFoundException e){
                throw new FileNotFoundException("Visit-log file not found");
            }
            catch (IOException e){
                throw new IOException("An I/O exception was thrown when reading from visit-log file");
            }
        }
    }
}
