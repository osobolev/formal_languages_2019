package ru.mirea.lang;

public class NumberNode extends ExprNode {

    final Token number;

    public NumberNode(Token number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return number.text;
    }
}
