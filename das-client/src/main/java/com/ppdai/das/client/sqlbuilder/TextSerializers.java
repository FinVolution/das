package com.ppdai.das.client.sqlbuilder;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppdai.das.client.Segment;

import java.util.List;

import static com.ppdai.das.client.SegmentConstants.AND;
import static com.ppdai.das.client.SegmentConstants.COMMA;
import static com.ppdai.das.client.SegmentConstants.NOT;
import static com.ppdai.das.client.SegmentConstants.OR;
import static com.ppdai.das.client.SegmentConstants.leftBracket;
import static com.ppdai.das.client.SegmentConstants.rightBracket;

public class TextSerializers {

    static List<Serializer> textSerializers() {
        return Lists.newArrayList(
                new TextSerializer(),
                new KeywordSerializer(),
                new OperatorSerializer(),
                new BracketSerializer());
    }

    static class TextSerializer implements Serializer {
        Text createText(String text) {
            return text.equals(COMMA.getText()) ? COMMA : new Text(text);
        }

        @Override
        public Segment deserialize(JsonObject jsonObject) {
            String text = jsonObject.get("text").getAsString();
            return createText(text);
        }

        @Override
        public JsonObject serialize(Segment text) {
            JsonObject element = (JsonObject) new Gson().toJsonTree(text);
            return addBuildType(element);
        }

        @Override
        public Class getBuildType() {
            return Text.class;
        }
    }

    static class KeywordSerializer extends TextSerializer {
        @Override
        Text createText(String text) {
            return new Keyword(text);
        }

        @Override
        public JsonObject serialize(Segment keyword) {
            JsonObject element = (JsonObject) new Gson().toJsonTree(keyword);
            return addBuildType(element);
        }

        @Override
        public Class getBuildType() {
            return Keyword.class;
        }
    }

    static class OperatorSerializer extends TextSerializer {
        @Override
        Text createText(String text) {
            if (text.equals(NOT.getText())){
                return NOT;
            }
            if(text.equals(OR.getText())){
                return OR;
            }
            if(text.equals(AND.getText())){
                return AND;
            }
            return new Operator(text);
        }

        @Override
        public JsonObject serialize(Segment operator) {
            JsonObject element = (JsonObject) new Gson().toJsonTree(operator);
            return addBuildType(element);
        }

        @Override
        public Class getBuildType() {
            return Operator.class;
        }
    }

    static class BracketSerializer extends TextSerializer {
        @Override
        public Segment deserialize(JsonObject jsonObject) {
            return jsonObject.get("left").getAsBoolean() ? leftBracket : rightBracket;
        }

        @Override
        public JsonObject serialize(Segment bracket) {
            JsonObject element = (JsonObject) new Gson().toJsonTree(bracket);
            return addBuildType(element);
        }

        @Override
        public Class getBuildType() {
            return Bracket.class;
        }
    }

}
