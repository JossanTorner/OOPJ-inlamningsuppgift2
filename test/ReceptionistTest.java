import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReceptionistTest {

    Receptionist receptionist = new Receptionist();

    LocalDate fixedDate = LocalDate.of(2024, 1, 1);

    Member nonMember = new Member("aaa aaa", "200010102020", fixedDate);
    Member nonActiveMember = new Member("Ida Idylle", "8906138493", LocalDate.parse("2018-03-07"));
    Member validMember = new Member("Alhambra Aromes", "7703021234", LocalDate.parse("2024-07-01"));

    @Test
    void testValidateNonMember(){
        String result = receptionist.validateVisitor(nonMember.getName(), nonMember.getId());
        String expected = "Not a member";
        assertEquals(expected, result);
    }

    @Test
    void testValidateNonActiveMember(){
        String result = receptionist.validateVisitor(nonActiveMember.getName(), nonActiveMember.getId());
        String expected = "Non active member";
        assertEquals(expected, result);
    }

    @Test
    void testValidateActiveMember(){
        String result = receptionist.validateVisitor(validMember.getName(), validMember.getId());
        String expected = "Active member";
        assertEquals(expected, result);
    }

    @Test
    void testIsActiveMember(){
        assertFalse(receptionist.isActiveMember(nonActiveMember));
        assertTrue(receptionist.isActiveMember(validMember));
    }

    @Test
    void isMember(){
        Member validResult = receptionist.isMember(validMember.getName(), validMember.getId());
        assertEquals(validMember, validResult);
        Member nonValidResult = receptionist.isMember(nonMember.getName(), nonMember.getId());
        assertNull(nonValidResult);
    }
}