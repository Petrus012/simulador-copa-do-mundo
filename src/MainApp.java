import br.ufla.copa.ui.terminal.MenuTerminal;

/**
 * Classe principal que inicia a aplicação
 */
public class MainApp {
    public static void main(String[] args) {
        System.out.println("\n\n--- Simulador da Copa Ativo ---");

        // O projeto está configurado para que a interface web inicie 
        // automaticamente. Basta acessar o endereço abaixo
        System.out.println("Acesse a interface web pelo endereco: http://localhost:8080");

        // O menu via terminal é executado também em paralelo
        MenuTerminal menu = new MenuTerminal();
        menu.iniciar();
    }
}