package org.example;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String input="25+25*(14+17)";
        Count count = new Count(input);
        count.stringRedact();
        count.res();

    }
}