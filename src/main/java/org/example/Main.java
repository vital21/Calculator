package org.example;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)  {
        String input="toDollars(777p + toRubles($85.4))";
        Count count = new Count(input);
      //  count.stringRedact();
        count.res();

    }
}