public class Main {

    Reception reception = new Reception();

    //Sätter igång programmet
    public Main(){
        while (true){
            String userInput = reception.getInput(reception.menuPrompt());
            reception.menuChoice(userInput);
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}
