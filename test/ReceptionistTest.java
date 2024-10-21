import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReceptionistTest {

    Receptionist receptionist = new Receptionist();

    Member nonMember = new Member("aaa aaa", "200010102020", LocalDate.now());
    Member nonActiveMember1 = new Member("Anna Karlsson", "0010102424", LocalDate.now().minusYears(2));
    Member nonActiveMember2 = new Member("Peter Johansson", "0010102323", LocalDate.now().minusMonths(13));
    Member activeMember = new Member("Peter Johansson", "0010102323", LocalDate.now());

    //Tre metoder som testar metoden identifyVisitorType
    //Testar att alla tre typer returneras beroende på parametrar
    @Test
    void testIdentifyNonMember(){
        VisitorType result = receptionist.identifyVisitorType(nonMember.getName(), nonMember.getSsNumber());
        VisitorType expected = VisitorType.NON_MEMBER;
        assertEquals(expected, result);
    }

    //Lägger till medlem innan och tar bort i slutet
    @Test
    void testIdentifyNonActiveMember(){
        receptionist.getBestGymEver().addMember(nonActiveMember1);

        VisitorType result = receptionist.identifyVisitorType(nonActiveMember1.getName(), nonActiveMember1.getSsNumber());
        VisitorType expected = VisitorType.NON_ACTIVE_MEMBER;
        assertEquals(expected, result);

        receptionist.getBestGymEver().removeMember(nonActiveMember1);
    }

    @Test
    void testIdentifyActiveMember(){
        receptionist.getBestGymEver().addMember(activeMember);

        VisitorType result = receptionist.identifyVisitorType(activeMember.getName(), activeMember.getSsNumber());
        VisitorType expected = VisitorType.ACTIVE_MEMBER;
        assertEquals(expected, result);

        receptionist.getBestGymEver().removeMember(activeMember);
    }

    //Testar att isActiveMember returnerar false eller true beroende på typ av medlem som sätts in
    @Test
    void testIsActiveMember(){
        assertFalse(receptionist.isActiveMember(nonActiveMember1));
        assertTrue(receptionist.isActiveMember(activeMember));
        assertFalse(receptionist.isActiveMember(nonActiveMember2));
    }

    //Testar om rätt medlem returneras beroende på namn och personnummer som sätts in
    @Test
    void testIsMember(){
        receptionist.getBestGymEver().addMember(activeMember);

        //Här borde vi få tillbaka den aktiva medlemmen
        Member validResult = receptionist.isMember(activeMember.getName(), activeMember.getSsNumber());
        assertEquals(activeMember, validResult);

        //Här borde vi få tillbaka null
        Member nonValidResult = receptionist.isMember(nonMember.getName(), nonMember.getSsNumber());
        assertNull(nonValidResult);

        receptionist.getBestGymEver().removeMember(activeMember);
    }

    //Testar att signa upp en medlem sedan innan
    @Test
    void testSignUpActiveMember(){
        receptionist.getBestGymEver().addMember(activeMember);
        boolean signedUp = receptionist.signUpNewMember(activeMember.getName(), activeMember.getSsNumber(), true);
        //Borde returnera false eftersom personen redan är medlem, ej kan bli signad
        assertFalse(signedUp);
        receptionist.getBestGymEver().removeMember(activeMember);
    }

    //Testar signa upp en icke medlem
    @Test
    void testSignUpNonMember(){
        boolean signedUp = receptionist.signUpNewMember(nonMember.getName(), nonMember.getSsNumber(), true);
        //Borde returnera true eftersom personen inte redan är medlem
        assertTrue(signedUp);
    }

    //Testar att registrera besök för aktiv medlem
    @Test
    void testLogSessionActiveMember(){
        //sätter testparameter till true för att inte skriva till fil
        boolean sessionLogged = receptionist.logSession(activeMember, true);
        //borde returnera true samt medlemmen borde fått ett besök till sin besökslista
        assertTrue(sessionLogged);
        assertEquals(1, activeMember.getGymSessions().size());
        activeMember.getGymSessions().clear();
    }

    //Testar att registrerar besök för icke aktiv medlem
    @Test
    void testLogSessionNonActiveMember(){
        boolean sessionLogged = receptionist.logSession(nonActiveMember1, true);
        //borde returnera false eftersom besöket ej ska registreras då
        assertFalse(sessionLogged);
    }
}