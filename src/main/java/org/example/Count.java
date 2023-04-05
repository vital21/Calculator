package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Count {
    public enum LexemeType{
        LEFT_BRACKET, RIGHT_BRACKET,
        OP_PLUS, OP_MINUS, OP_DOLLARS, OP_RUBLE,
        NUMBER,
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
        //  double result=factor(leksbuf);
        return result;
    }
    public static List<Lexeme> stringRedact(){
        List<Lexeme> lexemes = new ArrayList<>();
        StringTokenizer str= new StringTokenizer(input,"()+pр $",true);
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





}

