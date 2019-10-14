package com.ppdai.das.client.sqlbuilder;

/**
 * For user defined column which can be used in select list and order segment
 * 
 * @author hejiehui
 */
public class CustomizedColumn extends Text {
    public CustomizedColumn(String template) {
        super(template);
    }

    public Text asc() {
        return new Text(getText() + " ASC");
    }
    
    public Text desc() {
        return new Text(getText() + " DESC");
    }
    
    public Text as(String alias) {
        return new Text(getText() + " AS " + alias);
    }
}
