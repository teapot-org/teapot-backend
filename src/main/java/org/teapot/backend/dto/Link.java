package org.teapot.backend.dto;

import java.io.Serializable;
import java.util.Objects;

public class Link implements CharSequence, Serializable {

    private String value;

    public Link() {
    }

    public Link(String link) {
        if (link != null) {
            value = link;
        }
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }
}
