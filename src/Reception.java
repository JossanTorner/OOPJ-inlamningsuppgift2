import java.util.Scanner;

public class Reception {

    Receptionist receptionist = new Receptionist();

    //Tar input från användaren beroende på parameter
    public String getInput(String prompt){
        Scanner userInput = new Scanner(System.in);
        System.out.print(prompt);
        return userInput.nextLine();
    }

    //Olika förfrågningar för programmet
    public String namePrompt(){
        return "Name: ";
    }
    public String ssPrompt(){
        return "Social Security number: ";
    }

    public String logPrompt(Member member){
        return "Do you want to log a session for " + member.getName() + "? [YES/NO]: ";
    }

    public String menuPrompt(){
        return "\n" + receptionist.getBestGymEver().getGymName().toUpperCase() +
                "\n[1] Register visitor" +
                "\n[2] View members" +
                "\n[3] Sign up new member" +
                "\n[4] Quit" +
                "\nFrom menu: ";
    }

    //Tar användaren vidare beroende på menyval
    public void menuChoice(String choice){
        switch (choice){
            case "1" -> registerVisitor();
            case "2" -> printAllGymMembers();
            case "3" -> signUp();
            case "4" -> System.exit(0);
        }
    }

    //Skriver ut alla medlemmar till skärmen
    public void printAllGymMembers(){
        System.out.println("\n" + receptionist.getBestGymEver().getGymName().toUpperCase() + " MEMBERS:");
        receptionist.getBestGymEver().printMembers();
    }

    //Låter användaren registrera en besökare och skriver vilken typ av besökare det är
    public void registerVisitor() {
        System.out.println("\nREGISTER VISITOR");
        String name = getInput(namePrompt());
        String ssNumber = getInput(ssPrompt());

        VisitorType visitor = receptionist.identifyVisitorType(name, ssNumber);
        System.out.println(visitor.type);

        if (visitor == VisitorType.ACTIVE_MEMBER){
            Member loggedIn = receptionist.isMember(name, ssNumber);
            handleSession(loggedIn);
        }
    }

    //Kollar om medlemmen vill registrera besök
    public void handleSession(Member loggedIn){
        String answer = getInput(logPrompt(loggedIn));

        if (answer.equalsIgnoreCase("yes")){
            boolean sessionLogged = receptionist.logSession(loggedIn, false);
            if (sessionLogged){
                System.out.println("Session logged successfully");
            }
            else{
                System.out.println("Session not logged due to error");
            }
        }
        else{
            System.out.println("Session logging skipped");
        }
    }

    //Skapar medlem
    public void signUp(){
        System.out.println("\nSIGN UP");
        String name = getInput(namePrompt());
        String ssNumber = getInput(ssPrompt());

        boolean signUpConfirmation = receptionist.signUpNewMember(name, ssNumber, false);
        if (signUpConfirmation){
            System.out.println("New member with social security number " + ssNumber + "  was successfully signed up");
        }
        else{
            System.out.println("Member with social security number " + ssNumber + " already exists");
        }
    }
}
