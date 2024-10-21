import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileHandlerTest {

    FileHandler fileHandler = new FileHandler();

    LocalDateTime fixedDateTime1 = LocalDateTime.of(2000, 1, 1, 10,0);
    LocalDateTime fixedDateTime2 = LocalDateTime.of(2000, 1, 2, 10,0);

    Member peter = new Member("Peter Johansson", "121212", LocalDate.of(2000, 1, 1));
    Member johan = new Member("Johan Petersson", "141414", LocalDate.of(2000, 2, 2));
    Member anna = new Member("Anna Persson", "151515", LocalDate.of(2000, 3, 3));

    List<Member> expectedMemberList = Arrays.asList(peter, johan, anna);

    //helpers
    String memberStringFormat(Member member) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return member.getSsNumber() + ", " + member.getName() + System.lineSeparator() + member.getLatestPaymentDate().format(formatter) + System.lineSeparator();
    }

    String visitLogStringFormat(Member member, LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter) + ", " + member.getSsNumber() + ", " + member.getName() + System.lineSeparator();
    }

    Path visitLogTempFile() throws IOException {
        Path expectedFile = Paths.get("test/temp-file.txt");
        Files.writeString(expectedFile, visitLogStringFormat(peter, fixedDateTime1), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        Files.writeString(expectedFile, visitLogStringFormat(johan, fixedDateTime1), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        Files.writeString(expectedFile, visitLogStringFormat(peter, fixedDateTime2), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        return expectedFile;
    }

    //Här testas getMembersFromFile
    //Skriver först över den förväntade listan till en temporär fil
    //Hämtar ut en lista från denna fil med getMembersfromFile
    //Jämför den hämtade listan med den förväntade listan
    @Test
    void testGetMembersFromFile() throws IOException {
        Path tempFile = Paths.get("test/temp-file.txt");
        for(Member member : expectedMemberList) {
            Files.writeString(tempFile, memberStringFormat(member), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }
        List<Member> actual = fileHandler.getMembersFromFile(tempFile);

        Files.delete(tempFile);

        assertEquals(expectedMemberList, actual);
        assertEquals(expectedMemberList.size(), actual.size());
        assertEquals(expectedMemberList.get(0).getName(), actual.get(0).getName());
        assertEquals(expectedMemberList.get(1), actual.get(1));
    }

    //Skriver en sträng med ogiltigt datumformat till en fil
    //läser in denna fil med getMembersFromFile, kollar att exception slängs
    @Test
    void testGetMembersFromFileDateTimeException() throws IOException {
        Path tempFile = Paths.get("test/temp-file.txt");
        String tempData = "12942198, Peter\n2000-03-2312\n";
        Files.write(tempFile, tempData.getBytes(), StandardOpenOption.CREATE);

        assertThrows(DateTimeException.class, () -> fileHandler.getMembersFromFile(tempFile));

        Files.delete(tempFile);
    }

    //Skriver en sträng med ogiltigt format till en fil
    //läser in denna fil med getMembersFromFile, kollar att exception slängs
    @Test
    void testGetMembersFromFileStringIndexOutOfBoundsException() throws IOException {
        Path tempFile = Paths.get("test/temp-file.txt");
        String tempData = "12942198 Peter\n2000-03-23\n";
        Files.write(tempFile, tempData.getBytes(), StandardOpenOption.CREATE);

        assertThrows(StringIndexOutOfBoundsException.class, () -> fileHandler.getMembersFromFile(tempFile));

        Files.delete(tempFile);
    }

    //Ger metoden en fil som inte existerar, kontrollerar att listan som returneras är tom
    @Test
    void testGetMembersFromFileNonExistentFile() throws IOException {
        Path tempFile = Paths.get("non-existent-file.txt");
        assertTrue(fileHandler.getMembersFromFile(tempFile).isEmpty());
    }

    //Här testas getVisitsFromFile
    //Hämtar en temporär visit-log fil från en helper-metod
    //Sätter in filen och expectedMemberList i metoden getVisitsFromFile
    //Tittar att medlemmarnas listor har fyllts på med de besök jag skrev över
    //till filen i helperVisitLopTempFile
    @Test
    void testGetVisitsFromFile() throws IOException {
        Path tempFile = visitLogTempFile();
        fileHandler.getVisitsFromFile(tempFile, expectedMemberList);

        assertTrue(peter.getGymSessions().contains(fixedDateTime1));
        assertTrue(peter.getGymSessions().contains(fixedDateTime2));
        assertEquals(2, peter.getGymSessions().size());

        assertTrue(johan.getGymSessions().contains(fixedDateTime1));
        assertEquals(1, johan.getGymSessions().size());

        assertTrue(anna.getGymSessions().isEmpty());

        Files.delete(tempFile);
    }

    //Skriver över ett ogiltigt datum till en fil
    //Kontrollerar att DateTimeException slängs när getVisitsFromFIle läser in filen
    @Test
    void testGetVisitsFromFileDateTimeException() throws IOException {
        Path tempFile = Paths.get("test/temp-file.txt");
        String tempData = "Invalid date" + ", " + peter.getSsNumber() + ", " + peter.getName() + System.lineSeparator();
        Files.write(tempFile, tempData.getBytes(), StandardOpenOption.CREATE);

        assertThrows(DateTimeException.class, () -> fileHandler.getVisitsFromFile(tempFile, expectedMemberList));

        Files.delete(tempFile);
    }

    //Skriver över ett ogiltigt format i en temporär fil
    //Kontrollerar att ArrayIndexOutOfBoundsException slängs när getVisitsFromFile läser in filen
    @Test
    void testGetVisitsFromFileStringException() throws IOException {
        Path tempFile = Paths.get("test/temp-file.txt");
        String tempData = "12942198 Peter";
        Files.write(tempFile, tempData.getBytes(), StandardOpenOption.CREATE);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> fileHandler.getVisitsFromFile(tempFile, expectedMemberList));

        Files.delete(tempFile);
    }


    //Skapar upp en temporär fil med förväntat innehåll, skriver över till filen i formatet memberStringFormat
    //Skapar därefter upp en annan temporär fil, som jag skriver till med hjälp writeToMembersFile
    //Jämför filernas innehåll
    @Test
    void testWriteToMembersFile() throws IOException {
        Path expectedFile = Paths.get("test/expectedFile-temp-file.txt");
        Files.writeString(expectedFile, memberStringFormat(peter), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        Files.writeString(expectedFile, memberStringFormat(johan), StandardOpenOption.APPEND);
        Files.writeString(expectedFile, memberStringFormat(anna), StandardOpenOption.APPEND);

        Path actualFile = Paths.get("test/actualFile-temp-file.txt");
        fileHandler.writeToMembersFile(actualFile, peter);
        fileHandler.writeToMembersFile(actualFile, johan);
        fileHandler.writeToMembersFile(actualFile, anna);

        List<String> expectedLines = Files.readAllLines(expectedFile);
        List<String> actualLines = Files.readAllLines(actualFile);
        assertEquals(expectedLines, actualLines);

        Files.delete(expectedFile);
        Files.delete(actualFile);
    }

    //Skapar upp en temporär fil med förväntat innehåll med visitLogTempFile
    //Skapar upp en annan temporär fil som jag skriver till med hjälp av writeToVisitFile
    //Jämför filernas innehåll
    @Test
    void testWriteToVisitFile() throws IOException {

        Path expectedFile = visitLogTempFile();

        Path actualFile = Paths.get("test/actual-temp-file.txt");
        fileHandler.writeToVisitFile(actualFile, peter, fixedDateTime1);
        fileHandler.writeToVisitFile(actualFile, johan, fixedDateTime1);
        fileHandler.writeToVisitFile(actualFile, peter, fixedDateTime2);

        List<String> expectedLines = Files.readAllLines(expectedFile);
        List<String> actualLines = Files.readAllLines(actualFile);
        assertEquals(expectedLines, actualLines);

        Files.delete(expectedFile);
        Files.delete(actualFile);
    }

}
