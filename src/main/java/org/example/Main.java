package org.example;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String input="toDollars(737p + toRubles($85.4))";
        Count count = new Count(input);
        count.stringRedact();
        count.res();

    }
}