import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    LocalDate fixedDate = LocalDate.of(2024, 1,1);
    Member peter = new Member("Peter Johansson", "0010102323", fixedDate);
    LocalDateTime peterSession1 = LocalDateTime.of(2024, 1, 1, 10, 0);
    LocalDateTime peterSession2 = LocalDateTime.of(2024, 3, 3, 12, 0);


    @Test
    void testAddSession(){
        peter.addSession(peterSession1);
        assertEquals(peter.getSessions().getFirst(), peterSession1);
        assertEquals(1, peter.getSessions().size());
    }

    @Test
    void testGetMemberInfo() {
        String expected = peter.getId() + " " + peter.getName() + "\nLatest payment made: " + peter.getLatestPaymentDate() + "\nLogged sessions:\n" + peter.allSessions();
        String actual = peter.getMemberInfo();
        assertEquals(expected, actual);
    }

    @Test
    void testAllSessions() {

        peter.addSession(peterSession1);
        peter.addSession(peterSession2);
        String actual = peter.allSessions().toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String expected = peterSession1.format(formatter) + "\n" + peterSession2.format(formatter) + "\n";

        assertEquals(expected, actual);
    }

    @Test
    void testToString() {
        String actual = peter.toString();
        String expected = peter.getId() + ", " + peter.getName() + "\n" + peter.getLatestPaymentDate();
        assertEquals(expected, actual);
    }

    @Test
    void testEquals() {
        assertTrue(peter.equals(new Member("Peter Johansson", "0010102323", fixedDate)));
    }
}