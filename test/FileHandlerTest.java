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
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {

    FileHandler fileHandler = new FileHandler();

    LocalDate fixedDate = LocalDate.of(2024, 1,1);
    LocalDateTime fixedDateTime = LocalDateTime.of(fixedDate,LocalTime.of(1,1));

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


    public boolean helperCompareFiles(Path file1, Path file2) throws IOException {
        byte[] content1 = Files.readAllBytes(file1);
        byte[] content2 = Files.readAllBytes(file2);
        return Arrays.equals(content1, content2);
    }

    @Test
    void testVisitLogFileContent() throws IOException {
        Path expected = expectedVisitLogFile();

        Path actual = tempDir.resolve("visit-log-from-method.txt");
        fileHandler.writeToVisitLog(peter, actual, peterSession1);
        fileHandler.writeToVisitLog(anna, actual, annaSession1);
        fileHandler.writeToVisitLog(peter, actual, peterSession2);

        assertTrue(helperCompareFiles(expected, actual));
    }

    List<Member> resultOfGetMembersFromFile(Path tempFile) throws IOException {
        for(Member member: expectedMembersList){
            String memberString = member.toString() + System.lineSeparator();
            Files.write(tempFile, memberString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        return fileHandler.getMembersFromFile(tempFile);
    }

    //getMembersFromFile returnerar en lista
    //testa en returnerad lista, mot en förväntad lista
    @Test
    void testGetMembersFromFile() throws IOException, ParseException {
        Path tempFile = tempDir.resolve("temp-members.txt");
        List<Member> result = resultOfGetMembersFromFile(tempFile);
        assertEquals(expectedMembersList, result);
    }

    @Test
    void testGetMembersFromFileNonExistentFile() throws IOException {
        Path tempFile = tempDir.resolve("non-existent-file.txt");
        assertTrue(fileHandler.getMembersFromFile(tempFile).isEmpty());
    }

    @Test
    void testGetMembersFromFileDateTimeParseException() throws IOException {
        Path tempFile = tempDir.resolve("fake-date.txt");
        Files.write(tempFile, "Id, Name\nInvalid date".getBytes());
        DateTimeParseException exceptionMessage = assertThrows(DateTimeParseException.class, () -> {fileHandler.getMembersFromFile(tempFile);});
        assertTrue(exceptionMessage.getMessage().contains("An exception was thrown when reading from file due to invalid date format"));
        assertEquals("Invalid date", exceptionMessage.getParsedString());
    }

    @Test
    void testWriteToVisitLogIOException() throws IOException {
        Path tempFile = tempDir.resolve("temp-visit-log.txt");
        Files.createFile(tempFile);
        tempFile.toFile().setReadOnly();

        assertThrows(IOException.class, () -> {fileHandler.writeToVisitLog(peter, tempFile, fixedDateTime);});

        tempFile.toFile().setWritable(true);
    }

    @Test
    void testGetVisitsFromFile() throws IOException {
        Path tempFile = expectedVisitLogFile();

        fileHandler.getVisitsFromFile(tempFile, expectedMembersList);

        assertEquals(2, peter.getSessions().size());
        assertEquals(1, anna.getSessions().size());
        assertTrue(johan.getSessions().isEmpty());

        assertTrue(peter.getSessions().contains(LocalDateTime.of(2024, 1, 1, 10, 0)));
        assertTrue(anna.getSessions().contains(LocalDateTime.of(2024, 2, 2, 11, 0)));
        assertFalse(johan.getSessions().contains(LocalDateTime.of(2024, 2, 2, 11, 0)));
    }

    @Test
    void testGetVisitsFromFileDateTimeParseException() throws IOException {
        Path tempFile = tempDir.resolve("temp-sessions.txt");
        String fileContent = "Invalid date, " + peter.getId() + ", " + peter.getName();
        Files.writeString(tempFile, fileContent);
        DateTimeParseException exceptionMessage = assertThrows(DateTimeParseException.class, () -> {fileHandler.getVisitsFromFile(tempFile, expectedMembersList);});
        assertTrue(exceptionMessage.getMessage().contains("An exception was thrown when reading from file due to invalid date format:"));
        assertEquals("Invalid date", exceptionMessage.getParsedString());
    }
}