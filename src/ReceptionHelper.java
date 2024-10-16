import java.util.Scanner;

public class ReceptionHelper {

    Receptionist receptionist = new Receptionist();

    public String getInput(String prompt){
        Scanner userInput = new Scanner(System.in);
        System.out.print(prompt);
        return userInput.nextLine();
    }

    public String namePrompt(){
        return "Name: ";
    }
    public String idPrompt(){
        return "Id: ";
    }

    public String menuPrompt(){
        return "[1] Receptionist" + "\n[2] Personal Trainer" + "\n[3] Quit" + "\nFrom menu: ";
    }

    public void menuChoice(String choice){
        switch (choice){
            case "1" -> registerVisitor();
            case "2" -> memberInfo();
            case "3" -> System.exit(0);
        }
    }

    public void registerVisitor(){
        System.out.println("REGISTER VISITOR");
        String name = getInput(namePrompt());
        String id = getInput(idPrompt());
        String getValidation = receptionist.validateVisitor(name, id);
        System.out.println(getValidation);
    }

    public void memberInfo(){
        for(Member member: receptionist.getBestGymEver().getMembers()){
            System.out.println(member.getMemberInfo());
        }
    }
}
