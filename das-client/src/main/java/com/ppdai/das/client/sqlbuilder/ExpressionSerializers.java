package com.ppdai.das.client.sqlbuilder;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.Segment;
import com.ppdai.das.strategy.OperatorEnum;

import java.util.List;

public class ExpressionSerializers {

    static List<Serializer> expressionSerializers() {
        return Lists.newArrayList(
                new NullExpressionSerializer(),
                new BooleanExpressionSerializer(),
                new BetweenExpressionSerializer(),
                new ColumnValueExpressionSerializer(),
                new InterColumnExpressionSerializer(),
                new InExpressionSerializer());
    }

    abstract static class ExpressionSerializer implements Serializer {

        boolean included = true;

        @Override
        public JsonObject serialize(Segment expression) {
            JsonObject element = new JsonObject();
            element.addProperty("included", ((Expression) expression).isIncluded());
            serializeOther(element, ((Expression) expression));
            return addBuildType(element);
        }

        abstract void serializeOther(JsonObject root, Expression expression);

        @Override
        public Segment deserialize(JsonObject jo) {
            included = jo.get("included").getAsBoolean();
            deserializeOther(jo);
            Expression expression = createExpression();
            writeField(expression, "included", included);
            return expression;
        }

        protected abstract void deserializeOther(JsonObject jo);

        protected abstract Expression createExpression();
    }

    abstract static class ColumnExpressionSerializer extends ExpressionSerializer {

        String template;
        AbstractColumn rightColumn;

        @Override
        void serializeOther(JsonObject root, Expression expression) {
            ColumnExpression ce = (ColumnExpression) expression;
            root.addProperty("template", ce.getTemplate());
            JsonObject cd = getSerializeFactory().serialize(ce.getRightColumn(), ce.getRightColumn().getClass());
            root.add("rightColumn", cd);
        }

        @Override
        protected void deserializeOther(JsonObject jo) {
            template = jo.get("template").getAsString();
            rightColumn = getSerializeFactory().deserialize(jo.getAsJsonObject("rightColumn"));
        }
    }

    static class NullExpressionSerializer extends ColumnExpressionSerializer {
        OperatorEnum operator;

        @Override
        protected Expression createExpression() {
            return new NullExpression(operator, rightColumn);
        }

        @Override
        protected void deserializeOther(JsonObject jo) {
            super.deserializeOther(jo);
            operator = OperatorEnum.valueOf(jo.get("operator").getAsString());
        }

        @Override
        void serializeOther(JsonObject root, Expression expression) {
            super.serializeOther(root, expression);
            Object op = readField(expression, "operator");
            JsonElement opElement = new Gson().toJsonTree(op);
            root.add("operator", opElement);
        }

        @Override
        public Class<NullExpression> getBuildType() {
            return NullExpression.class;
        }
    }

    static class BooleanExpressionSerializer extends ExpressionSerializer {

        String template;

        @Override
        protected Expression createExpression() {
            if (template.equalsIgnoreCase(Boolean.TRUE.toString())) {
                return BooleanExpression.TRUE;
            } else {
                return BooleanExpression.FALSE;
            }
        }

        @Override
        protected void deserializeOther(JsonObject jo) {
            template = jo.get("template").getAsString();
        }

        @Override
        void serializeOther(JsonObject root, Expression expression) {
            Object template = readField(expression, "template");//TODO
            JsonElement templateElement = new Gson().toJsonTree(template);
            root.add("template", templateElement);
        }

        @Override
        public Class getBuildType() {
            return BooleanExpression.class;
        }
    }

    static class BetweenExpressionSerializer extends ColumnExpressionSerializer {
        OperatorEnum operator;
        AbstractColumn column;
        Object firstValue;
        Object secondValue;

        @Override
        protected Expression createExpression() {
            if(operator.equals(OperatorEnum.BEWTEEN) ){
                return BetweenExpression.between(column, firstValue, secondValue);
            }else {
                return BetweenExpression.notBetween(column, firstValue, secondValue);
            }
        }

        @Override
        protected void deserializeOther(JsonObject jo) {
            super.deserializeOther(jo);
            operator = OperatorEnum.valueOf(jo.get("operator").getAsString());
            column = getSerializeFactory().deserialize(jo.getAsJsonObject("rightColumn"));
            firstValue = new Gson().fromJson(jo.get("firstValue"), Object.class);
            secondValue = new Gson().fromJson(jo.get("secondValue"), Object.class);
        }

        @Override
        void serializeOther(JsonObject root, Expression expression) {
            super.serializeOther(root, expression);

            Object firstValue = readField(expression, "firstValue");//TODO
            JsonElement firstValueElement = new Gson().toJsonTree(firstValue);
            root.add("firstValue", firstValueElement);

            Object secondValue = readField(expression, "secondValue");//TODO
            JsonElement secondValueElement = new Gson().toJsonTree(secondValue);
            root.add("secondValue", secondValueElement);

            Object op = readField(expression, "operator");//TODO
            JsonElement opElement = new Gson().toJsonTree(op);
            root.add("operator", opElement);
        }

        @Override
        public Class<BetweenExpression> getBuildType() {
            return BetweenExpression.class;
        }
    }

    static class ColumnValueExpressionSerializer extends ColumnExpressionSerializer {
        OperatorEnum operator;
        Object value;

        @Override
        protected void deserializeOther(JsonObject jo) {
            super.deserializeOther(jo);
            operator = OperatorEnum.valueOf(jo.get("operator").getAsString());
            value = new Gson().fromJson(jo.get("value"), Object.class);
        }

        @Override
        protected Expression createExpression() {
            return new ColumnValueExpression(operator, rightColumn, value);
        }

        @Override
        void serializeOther(JsonObject root, Expression expression) {
            super.serializeOther(root, expression);

            Object value = readField(expression, "value");//TODO
            JsonElement valueElement = new Gson().toJsonTree(value);
            root.add("value", valueElement);

            Object op = readField(expression, "operator");//TODO
            JsonElement opElement = new Gson().toJsonTree(op);
            root.add("operator", opElement);
        }

        @Override
        public Class getBuildType() {
            return ColumnValueExpression.class;
        }
    }

    static class InterColumnExpressionSerializer extends ColumnExpressionSerializer {
        AbstractColumn leftColumn;
        OperatorEnum operatorEnum;

        @Override
        protected Expression createExpression() {
            return new InterColumnExpression(operatorEnum, rightColumn, leftColumn);
        }

        @Override
        public void deserializeOther(JsonObject jo) {
            super.deserializeOther(jo);
            leftColumn = getSerializeFactory().deserialize(jo.getAsJsonObject("leftColumn"));
            operatorEnum = OperatorEnum.valueOf(jo.get("operator").getAsString());
        }

        @Override
        void serializeOther(JsonObject root, Expression expression) {
            super.serializeOther(root, expression);

            AbstractColumn leftColumn = ((InterColumnExpression) expression).getLeftColumn();
            JsonElement left = getSerializeFactory().serialize(leftColumn, leftColumn.getClass());
            root.add("leftColumn", left);

            Object op = readField(expression, "operator");
            JsonElement opElement = new Gson().toJsonTree(op);
            root.add("operator", opElement);
        }

        @Override
        public Class getBuildType() {
            return InterColumnExpression.class;
        }
    }

    static class InExpressionSerializer extends ColumnExpressionSerializer {
        OperatorEnum operator;
        List<?> values;

        @Override
        protected Expression createExpression() {
            if(operator.equals(OperatorEnum.IN)) {
                return InExpression.in(rightColumn, values);
            }else {
                return InExpression.notIn(rightColumn, values);
            }
        }

        @Override
        public void deserializeOther(JsonObject jo) {
            super.deserializeOther(jo);
            operator = OperatorEnum.valueOf(jo.get("operator").getAsString());
            values = new Gson().fromJson(jo.get("values"), List.class);//TODO
        }

        @Override
        void serializeOther(JsonObject root, Expression expression) {
            super.serializeOther(root, expression);

            Object values = readField(expression, "values");//TODO
            JsonElement valueElement = new Gson().toJsonTree(values);
            root.add("values", valueElement);

            Object op = readField(expression, "operator");//TODO
            JsonElement opElement = new Gson().toJsonTree(op);
            root.add("operator", opElement);
        }

        @Override
        public Class getBuildType() {
            return InExpression.class;
        }
    }
}
