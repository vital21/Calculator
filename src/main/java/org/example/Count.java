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
        result=evaluateExpression(leksbuf);
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
        public boolean hasNext() {
            return pos < lexemes.size();
        }
    }



    private static double evaluateAddSub(LexemeBuffer buffer) {
        double value = evaluateMulDiv(buffer);
        while (buffer.hasNext()) {
            Lexeme lexeme = buffer.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    value += evaluateMulDiv(buffer);
                    break;
                case OP_MINUS:
                    value -= evaluateMulDiv(buffer);
                    break;
                case OP_DOLLARS:
                    value = toDollars(value);
                    break;
                case OP_RUBLE:
                    value = toRubles(value);
                    break;
                case RIGHT_BRACKET:
                    buffer.back();
                    return value;
                case EOF:
                    return value;
                default:
                    throw new RuntimeException("Неверный формат выражения");
            }
        }
        return value;
    }


    private static double evaluateFactor(LexemeBuffer buffer) {
        if (buffer.hasNext()) {
            Lexeme lexeme = buffer.next();
            switch (lexeme.type) {
                case NUMBER:
                    return Double.parseDouble(lexeme.value);
                case OP_PLUS:
                    return evaluateFactor(buffer);
                case OP_MINUS:
                    return -evaluateFactor(buffer);
                case OP_RUBLE:
                    if (buffer.hasNext() && buffer.next().type == LexemeType.LEFT_BRACKET) {
                        double dollars = evaluateExpression(buffer);
                        if (buffer.hasNext() && buffer.next().type == LexemeType.RIGHT_BRACKET) {
                            return toRubles(dollars);
                        }
                        throw new RuntimeException("Неправильный формат выражения");
                    }
                    throw new RuntimeException("Неправильный формат выражения");
                case OP_DOLLARS:
                    if (buffer.hasNext() && buffer.next().type == LexemeType.LEFT_BRACKET) {
                        double rubles = evaluateExpression(buffer);
                        if (buffer.hasNext() && buffer.next().type == LexemeType.RIGHT_BRACKET) {
                            return toDollars(rubles);
                        }
                        throw new RuntimeException("Неправильный формат выражения");
                    }
                    throw new RuntimeException("Неправильный формат выражения");
                case LEFT_BRACKET:
                    double result = evaluateExpression(buffer);
                    if (buffer.hasNext() && buffer.next().type == LexemeType.RIGHT_BRACKET) {
                        return result;
                    }
                    throw new RuntimeException("Неправильный формат выражения");
                default:
                    throw new RuntimeException("Неправильный формат выражения");
            }
        }
        throw new RuntimeException("Неправильный формат выражения");
    }
    private static double evaluateMulDiv(LexemeBuffer buffer) {
        double left = evaluateUnary(buffer);

        while (buffer.hasNext()) {
            Lexeme lexeme = buffer.next();
            switch (lexeme.type) {
                case OP_MUL:
                    left *= evaluateUnary(buffer);
                    break;
                case OP_DIV:
                    left /= evaluateUnary(buffer);
                    break;
                case OP_DOLLARS:
                    if (!buffer.hasNext()) {
                        throw new RuntimeException("Неверный формат выражения");
                    }
                    Lexeme dollarValue = buffer.next();
                    if (dollarValue.type != LexemeType.NUMBER) {
                        throw new RuntimeException("Неверный формат выражения");
                    }
                    left *= toRubles(Double.parseDouble(dollarValue.value));
                    break;
                case OP_RUBLE:
                    if (!buffer.hasNext()) {
                        throw new RuntimeException("Неверный формат выражения");
                    }
                    Lexeme rubleValue = buffer.next();
                    if (rubleValue.type != LexemeType.NUMBER) {
                        throw new RuntimeException("Неверный формат выражения");
                    }
                    left = toDollars(left) + toDollars(toRubles(Double.parseDouble(rubleValue.value)));
                    break;
                case EOF:
                case OP_PLUS:
                case OP_MINUS:
                case RIGHT_BRACKET:
                    buffer.back();
                    return left;
                default:
                    throw new RuntimeException("Неверный формат выражения");
            }
        }
        return left;
    }


    private static double evaluateExpression(LexemeBuffer buffer) {
        double result = evaluateAddSub(buffer);
        while (buffer.hasNext()) {
            Lexeme lexeme = buffer.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    result += evaluateAddSub(buffer);
                    break;
                case OP_MINUS:
                    result -= evaluateAddSub(buffer);
                    break;
                case OP_DOLLARS:
                    result = toDollars(result);
                    break;
                case OP_RUBLE:
                    result = toRubles(result);
                    break;
                case RIGHT_BRACKET:
                    return result;
                case EOF:
                    buffer.back();
                    break;
                default:
                    throw new RuntimeException("Неверный формат выражения");
            }
        }
        return result;
    }

    private static double evaluateUnary(LexemeBuffer buffer) {
        if (buffer.hasNext()) {
            Lexeme lexeme = buffer.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    return evaluateUnary(buffer);
                case OP_MINUS:
                    return -evaluateUnary(buffer);
                case NUMBER:
                    return Double.parseDouble(lexeme.value);
                case LEFT_BRACKET:
                    double result = evaluateExpression(buffer);
                    if (!buffer.hasNext() || buffer.next().type != LexemeType.RIGHT_BRACKET) {
                        throw new RuntimeException("Неверный формат выражения: пропущена закрывающая скобка");
                    }
                    return result;
                case OP_DOLLARS:
                    /*if (!buffer.hasNext() || buffer.next().type != LexemeType.LEFT_BRACKET) {
                        throw new RuntimeException("Неверный формат выражения: пропущена открывающая скобка после toDollars");
                    }*/
                    double dollars = evaluateExpression(buffer);
                 /*   if (!buffer.hasNext() || buffer.next().type != LexemeType.RIGHT_BRACKET) {
                        throw new RuntimeException("Неверный формат выражения: пропущена закрывающая скобка после toDollars");
                    }*/
                    return Count.toDollars(dollars);
                case OP_RUBLE:
                    /*if (!buffer.hasNext() || buffer.next().type != LexemeType.LEFT_BRACKET) {
                        throw new RuntimeException("Неверный формат выражения: пропущена открывающая скобка после toRubles");
                    }*/
                    double rubles = evaluateExpression(buffer);
                   /* if (!buffer.hasNext() || buffer.next().type != LexemeType.RIGHT_BRACKET) {
                        throw new RuntimeException("Неверный формат выражения: пропущена закрывающая скобка после toRubles");
                    }*/
                    return Count.toRubles(rubles);
                default:
                    throw new RuntimeException("Неверный формат выражения");
            }
        }
        throw new RuntimeException("Неверный формат выражения");
    }
    public static double toDollars(double rubles) {
        return rubles / 75.0;
    }

    public static double toRubles(double dollars) {
        return dollars * 75.0;
    }

}

