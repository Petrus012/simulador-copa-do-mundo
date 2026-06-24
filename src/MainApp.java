import br.ufla.copa.ui.terminal.MenuTerminal;

// Ponto de entrada da aplicação — inicia a interface web e o menu terminal em paralelo
public class MainApp {
    public static void main(String[] args) {
        System.out.println("\n\n--- Simulador da Copa Ativo ---");
        System.out.println("Acesse a interface web pelo endereco: http://localhost:8080");

        MenuTerminal menu = new MenuTerminal();
        menu.iniciar();
    }
}
