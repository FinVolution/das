package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.Segment;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Text)) return false;
        Text text1 = (Text) o;
        return Objects.equals(getText(), text1.getText());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getText());
    }
}
