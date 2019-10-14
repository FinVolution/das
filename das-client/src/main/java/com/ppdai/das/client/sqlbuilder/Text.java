package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.Segment;

public class Text implements Segment {
    private String text;
    public Text(String text) {
        this.text = text;
    }
    
    @Override
    public String build(BuilderContext helper) {
        return text;
    }

    public String getText() {
        return text;
    }
    
    public String toString() {
        return getText();
    }
}
