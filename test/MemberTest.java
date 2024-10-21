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
        assertEquals(peter.getGymSessions().getFirst(), peterSession1);
        assertEquals(1, peter.getGymSessions().size());
    }

    @Test
    void testGetMemberInfo() {
        String expected = peter.getSsNumber() + " " + peter.getName() +
                "\nLatest payment made: " + peter.getLatestPaymentDate() +
                "\nLogged gym sessions:\n" + peter.allSessions();
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
    void testEquals() {
        Member anna = new Member("anna", "3", LocalDate.of(2000, 1, 3));
        assertTrue(peter.equals(new Member("Peter Johansson", "0010102323", fixedDate)));
        assertTrue(peter.equals(peter));
        assertFalse(peter.equals(anna));
    }
}