package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Count {
    public enum LexemeType{
        LEFT_BRACKET, RIGHT_BRACKET,
        OP_PLUS, OP_MINUS, OP_DOLLARS, OP_RUBLE,
        NUMBER,
        OP_MUL,OP_DIV,
        EOF;
    }
    private double result;
    private static String input;
    public Count(String input) {
        this.input = input;
    }
    public double res(){
        List<Lexeme> lexemes = new ArrayList<>();
        lexemes=stringRedact();
        LexemeBuffer leksbuf=new LexemeBuffer(lexemes);
        double result=expr(leksbuf);
        return result;
    }
    public static List<Lexeme> stringRedact(){
        List<Lexeme> lexemes = new ArrayList<>();
        StringTokenizer str= new StringTokenizer(input,"()+pр $*/",true);
        String test="";
        while (str.hasMoreTokens()){
             test=String.format(str.nextToken());
            System.out.println(test);
            switch (test){
                case "toDollars":
                    lexemes.add(new Lexeme(LexemeType.OP_DOLLARS,test));
                    continue;
                case  "toRubles":
                    lexemes.add(new Lexeme(LexemeType.OP_RUBLE,test));
                    continue;
                case "(" :
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET,test));
                    continue;
                case ")" :
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET,test));
                    continue;
                case "+" :
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS,test));
                    continue;
                case "-" :
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS,test));
                case "*":

                case "p":
                    continue;
                case "$":
                    continue;
                case " ":
                    continue;

                default:

                    if(Double.valueOf(test)>=0){
                        lexemes.add(new Lexeme(LexemeType.NUMBER,test));
                        continue;
                    }
                    if(test!=" "){
                        throw  new RuntimeException("Неверный символ " + test);
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF,"."));
        return lexemes;
    }
    public static class Lexeme {
        LexemeType type;
        String value;

        public Lexeme(LexemeType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
    public static class LexemeBuffer {
        private int pos;

        public List<Lexeme> lexemes;

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        public Lexeme next() {
            return lexemes.get(pos++);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }
    }
    public static double expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }
    public static double plusminus(LexemeBuffer lexemes) {
        double value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    value += multdiv(lexemes);
                    break;
                case OP_MINUS:
                    value -= multdiv(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }
    public static double multdiv(LexemeBuffer lexemes) {
        double value = factor(lexemes);

        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL:
                    value *= toVal(lexemes);
                    break;
                case OP_DIV:
                    value /= toVal(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case OP_PLUS:
                case OP_MINUS:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }
    public static double toVal(LexemeBuffer lexemes) {
        double value = factor(lexemes);
        double dollars=0.5;
        double ruble=0.2;
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_DOLLARS:
                    dollars*=factor(lexemes);
                case OP_RUBLE:
                    ruble*=factor(lexemes);
                case OP_MUL:
                case OP_DIV:
                case EOF:
                case RIGHT_BRACKET:
                case OP_PLUS:
                case OP_MINUS:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    public static double factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        double value=0;
        switch (lexeme.type) {
            case OP_RUBLE:
                value =toVal(lexemes);
            case OP_DOLLARS:
                value =toVal(lexemes);
            case NUMBER:
                return Double.parseDouble(lexeme.value);
            case LEFT_BRACKET:
                 value = plusminus(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
        }
    }
}

