package br.ufla.copa.ui.terminal;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// Único ponto de leitura do teclado; resolve o encoding corretamente no Windows
public class LeitorConsole {
    private static final Scanner scanner;

    static {
        Charset cs = (System.console() != null) ? System.console().charset() : StandardCharsets.UTF_8;
        scanner = new Scanner(System.in, cs);
    }

    public static String lerLinha() {
        return scanner.nextLine().trim();
    }

    public static int lerInteiro() throws NumberFormatException {
        return Integer.parseInt(lerLinha());
    }
}
