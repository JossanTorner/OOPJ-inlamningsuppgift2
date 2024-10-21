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

    //Returnerar en lista med medlemmar från en fil
    public List<Member> getMembersFromFile(Path path) throws DateTimeParseException, StringIndexOutOfBoundsException, IOException {
        List<Member> list = new ArrayList<>();
        if (Files.exists(path)) {
            try(BufferedReader br = Files.newBufferedReader(path)){
                String temp;
                while ((temp = br.readLine()) != null){

                    String ssNumber = temp.substring(0,temp.indexOf(',')).trim();
                    String name = temp.substring(temp.indexOf(',')+1).trim();
                    LocalDate latestPaymentDate = LocalDate.parse(br.readLine());
                    list.add(new Member(name, ssNumber, latestPaymentDate));
                }
            }
        }
        return list;
    }

    //Tar emot en lista med medlemmar, fyller på medlemmarnas besökslistor med registrerade besök i en fil
    public void getVisitsFromFile(Path path, List<Member> memberList) throws DateTimeParseException, ArrayIndexOutOfBoundsException, IOException {
        if (Files.exists(path)){
            try(BufferedReader br = Files.newBufferedReader(path)){
                String temp;
                while( (temp = br.readLine()) != null){
                    String[] data = temp.split(",");
                    for (Member member : memberList) {
                        if (member.getSsNumber().equals(data[1].trim()) && member.getName().equals(data[2].trim())) {
                            LocalDateTime dateTime = LocalDateTime.parse(data[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                            member.addSession(dateTime);
                        }
                    }
                }
            }
        }
    }

    public void writeToVisitFile(Path path, Member member, LocalDateTime dateTime) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try(BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE)){
            String line = dateTime.format(formatter) +", " + member.getSsNumber() + ", " + member.getName();
            bw.write(line);
            bw.newLine();
        }
    }

    public void writeToMembersFile(Path path, Member member) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try(BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE)){
            String line = member.getSsNumber() + ", " + member.getName() + System.lineSeparator() + member.getLatestPaymentDate().format(formatter);
            bw.write(line);
            bw.newLine();
        }
    }
}