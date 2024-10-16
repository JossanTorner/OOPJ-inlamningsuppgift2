import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {

    FileHandler fileHandler = new FileHandler();

    LocalDate fixedDate = LocalDate.of(2024, 1,1);
    LocalTime fixedTime = LocalTime.of(1,1);
    LocalDateTime fixedDateTime = LocalDateTime.of(fixedDate,fixedTime);

    Member peter = new Member("Peter Johansson", "0010102323", fixedDate);
    Member anna = new Member("Anna Karlsson", "0010102424", fixedDate);
    Member johan = new Member("Johan Persson", "0011112525", fixedDate);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    LocalDateTime peterSession1 = LocalDateTime.of(2024, 1, 1, 10, 0);
    LocalDateTime annaSession1 = LocalDateTime.of(2024, 2, 2, 11, 0);
    LocalDateTime peterSession2 = LocalDateTime.of(2024, 3, 3, 12, 0);

    List<Member> expectedMembersList = Arrays.asList(peter, anna, johan);

    String visitLogExpectedContent =
            peterSession1.format(formatter) + ", " + peter.getId() + ", " + peter.getName() + System.lineSeparator() +
            annaSession1.format(formatter) + ", " + anna.getId() + ", " + anna.getName() + System.lineSeparator() +
            peterSession2.format(formatter) + ", " + peter.getId() + ", " + peter.getName() + System.lineSeparator();

    @TempDir
    Path tempDir;

    //Här har vi en temporär visit-log
    Path expectedVisitLogFile() throws IOException {
        Path tempFile = tempDir.resolve("expected-visit-log.txt");
        Files.writeString(tempFile, visitLogExpectedContent);
        return tempFile;
    }

    //Här fyller jag en fil med metoden writeToVisitLog
    void fillActualVisitLogFile(Path path) throws IOException {
        fileHandler.writeToVisitLog(peter, path, peterSession1);
        fileHandler.writeToVisitLog(anna, path, annaSession1);
        fileHandler.writeToVisitLog(peter, path, peterSession2);
    }

    public boolean helperCompareFiles(Path file1, Path file2) throws IOException {
        byte[] content1 = Files.readAllBytes(file1);
        byte[] content2 = Files.readAllBytes(file2);
        return Arrays.equals(content1, content2);
    }

    @Test
    void testVisitLogFileContent() throws IOException {
        Path expected = expectedVisitLogFile();
        Path actual = tempDir.resolve("visit-log-from-method.txt");
        fillActualVisitLogFile(actual);
        assertTrue(helperCompareFiles(expected, actual));
    }

    List<Member> resultOfGetMembersFromFile(Path tempFile) throws IOException, ParseException {
        for(Member member: expectedMembersList){
            String memberString = member.toString() + System.lineSeparator();
            Files.write(tempFile, memberString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        return fileHandler.getMembersFromFile(tempFile);
    }

    @Test
    void testGetMembersFromFile() throws IOException, ParseException {
        Path tempFile = tempDir.resolve("temp-members.txt");
        List<Member> readInList = resultOfGetMembersFromFile(tempFile);
        assertEquals(expectedMembersList,readInList);
    }

    @Test
    void testGetMembersFromFileNonExistentFile() throws ParseException, IOException {
        Path tempFile = tempDir.resolve("non-existent-file.txt");
        assertTrue(fileHandler.getMembersFromFile(tempFile).isEmpty());
    }

    @Test
    void testGetMembersFromFileParseException() throws IOException {
        Path tempFile = tempDir.resolve("wrong-members.txt");
        Files.write(tempFile, "Id, Name\nInvalid date".getBytes());
        Throwable message = assertThrows(ParseException.class, () -> {fileHandler.getMembersFromFile(tempFile);});
        assertEquals(message.getMessage(),"An exception was thrown due to reading invalid date from members-data file");
    }

    @Test
    void validateLocalDate() {
        assertThrows(ParseException.class, () -> {fileHandler.validateLocalDate("10-2411-1");});
        assertThrows(ParseException.class, () -> {fileHandler.validateLocalDate("100000");});
        assertThrows(ParseException.class, () -> {fileHandler.validateLocalDate("abc");});
    }

    @Test
    void testWriteToVisitLogIOException() throws IOException {
        Path tempFile = tempDir.resolve("temp-visit-log.txt");
        Files.createFile(tempFile);
        tempFile.toFile().setReadOnly();

        assertThrows(IOException.class, () -> {fileHandler.writeToVisitLog(expectedMembersList.get(0), tempFile, fixedDateTime);});

        tempFile.toFile().setWritable(true);
    }

    @Test
    void testGetVisitsFromFile() throws IOException, ParseException {
        Path tempFile = expectedVisitLogFile();

        fileHandler.getVisitsFromFile(tempFile, expectedMembersList);

        assertEquals(2, expectedMembersList.get(0).getSessions().size());
        assertEquals(1, expectedMembersList.get(1).getSessions().size());
        assertTrue(expectedMembersList.get(2).getSessions().isEmpty());

        assertTrue(expectedMembersList.get(0).getSessions().contains(LocalDateTime.of(2024, 1, 1, 10, 0)));
        assertTrue(expectedMembersList.get(1).getSessions().contains(LocalDateTime.of(2024, 2, 2, 11, 0)));
        assertFalse(expectedMembersList.get(2).getSessions().contains(LocalDateTime.of(2024, 2, 2, 11, 0)));
    }

    @Test
    void testGetVisitsFromFileParseException() throws IOException {
        Path tempFile = tempDir.resolve("temp-sessions.txt");
        String fileContent = "InvalidDate, " + expectedMembersList.get(1).getId() + ", " + expectedMembersList.get(1).getName();
        Files.writeString(tempFile, fileContent);
        assertThrows(ParseException.class, () -> {fileHandler.getVisitsFromFile(tempFile, expectedMembersList);});
    }

}