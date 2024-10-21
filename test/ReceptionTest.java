import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReceptionTest {

    Reception reception = new Reception();

    @Test
    void testNamePrompt() {
        String expected = "Name: ";
        assertEquals(expected, reception.namePrompt());
        assertNotEquals("Name", reception.namePrompt());
    }

    @Test
    void testSsPrompt() {
        String expected = "Social Security number: ";
        assertEquals(expected, reception.ssPrompt());
        assertNotEquals("Id: ", reception.ssPrompt());
    }

    @Test
    void testMenuPrompt() {
        String expected = "\n" + reception.receptionist.getBestGymEver().getGymName().toUpperCase() +
                "\n[1] Register visitor" +
                "\n[2] View members" +
                "\n[3] Sign up new member" +
                "\n[4] Quit" +
                "\nFrom menu: ";
        assertEquals(expected, reception.menuPrompt());
    }

    @Test
    void testLogPrompt(){
        Member member = new Member("Anna", "1000", LocalDate.now());
        String expected = "Do you want to log a session for Anna? [YES/NO]: ";
        assertEquals(expected, reception.logPrompt(member));
    }
}