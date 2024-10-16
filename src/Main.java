public class Main {

    ReceptionHelper reception = new ReceptionHelper();

    public Main(){
        while (true){
            String menuChoice = reception.getInput(reception.menuPrompt());
            reception.menuChoice(menuChoice);
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}
