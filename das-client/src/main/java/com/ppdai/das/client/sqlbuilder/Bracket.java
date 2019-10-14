package com.ppdai.das.client.sqlbuilder;

public class Bracket extends Keyword {
    private boolean left;

    public Bracket(boolean left) {
        super(left ? "(" : ")");
        this.left = left;
    }

    public boolean isLeft() {
        return left;
    }
}
