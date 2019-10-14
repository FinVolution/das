package com.ppdai.das.strategy;

public enum OperatorEnum {
    EQUAL("%s = ?"),
    NOT_EQUAL("%s <> ?"),
    GREATER_THAN("%s > ?"),
    GREATER_THAN_OR_EQUAL("%s >= ?"),
    LESS_THAN("%s < ?"),
    LESS_THAN_OR_EQUAL("%s <= ?"),
    IN("%s IN ( ? )"),
    NOT_IN("%s NOT IN ( ? )"),
    BEWTEEN("%s BETWEEN ? AND ?"),
    NOT_BETWEEN("%s NOT BETWEEN ? AND ?"),
    LIKE("%s LIKE ?"),
    NOT_LIKE("%s NOT LIKE ?"),
    IS_NULL("%s IS NULL"),
    IS_NOT_NULL("%s IS NOT NULL");

    
    private String template;
    OperatorEnum(String template) {
        this.template = template;
    }
    
    public String getTemplate() {
        return template;
    }

    public OperatorEnum getReversed() {
        switch (this) {
        case EQUAL: return NOT_EQUAL;
        case NOT_EQUAL: return EQUAL;
        case GREATER_THAN: return LESS_THAN_OR_EQUAL;
        case GREATER_THAN_OR_EQUAL: return LESS_THAN;
        case LESS_THAN: return GREATER_THAN_OR_EQUAL;
        case LESS_THAN_OR_EQUAL: return GREATER_THAN;
        case IN: return NOT_IN;
        case NOT_IN: return IN;
        case BEWTEEN: return NOT_BETWEEN;
        case NOT_BETWEEN: return BEWTEEN;
        case LIKE: return NOT_LIKE;
        case NOT_LIKE: return LIKE;
        case IS_NULL: return IS_NOT_NULL;
        case IS_NOT_NULL: return IS_NULL;
        default:
            return null;
        }
    }
}
