package ru.mirea.lang;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Parser {

    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private void error(String message) {
        if (pos < tokens.size()) {
            Token t = tokens.get(pos);
            throw new RuntimeException(message + " в позиции " + t.pos);
        } else {
            throw new RuntimeException(message + " в конце файла");
        }
    }

    private Token match(TokenType... expected) {
        if (pos < tokens.size()) {
            Token c = tokens.get(pos);
            if (Arrays.asList(expected).contains(c.type)) {
                pos++;
                return c;
            }
        }
        return null;
    }

    private Token require(TokenType... expected) {
        Token t = match(expected);
        if (t == null)
            error("Ожидается " + Arrays.toString(expected));
        return t;
    }

    private ExprNode parseElem() {
        Token num = match(TokenType.NUMBER);
        if (num != null)
            return new NumberNode(num);
        Token id = match(TokenType.ID);
        if (id != null)
            return new VarNode(id);
        error("Ожидается число или переменная");
        return null;
    }

    public ExprNode parseExpression() {
        ExprNode e1 = parseElem();
        while (pos < tokens.size()) {
            Token op = require(TokenType.ADD);
            ExprNode e2 = parseElem();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    public static int eval(ExprNode node) {
        if (node instanceof NumberNode) {
            NumberNode num = (NumberNode) node;
            return Integer.parseInt(num.number.text);
        } else if (node instanceof BinOpNode) {
            BinOpNode binOp = (BinOpNode) node;
            int l = eval(binOp.left);
            int r = eval(binOp.right);
            return l + r;
        } else if (node instanceof VarNode) {
            VarNode var = (VarNode) node;
            System.out.println("Введите значение " + var.id.text + ":");
            String line = new Scanner(System.in).nextLine();
            return Integer.parseInt(line);
        } else {
            throw new IllegalStateException();
        }
    }

    public static void main(String[] args) {
        String text = "10 + 20 + x";

        Lexer l = new Lexer(text);
        List<Token> tokens = l.lex();
        tokens.removeIf(t -> t.type == TokenType.SPACE);

        Parser p = new Parser(tokens);
        ExprNode node = p.parseExpression();

        int result = eval(node);
        System.out.println(result);
    }
}
