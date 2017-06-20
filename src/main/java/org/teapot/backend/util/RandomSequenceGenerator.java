package org.teapot.backend.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public final class RandomSequenceGenerator {

    public static final String CHARACTER_STRING = "0123456789abcdefghijklmnopqrstuvwxyz";
    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 256;
    public static final int MIN_NOTATION = 2;
    public static final int MAX_NOTATION = CHARACTER_STRING.length();

    public String generateSequence(int length, int notation, boolean caseSensitive) {
        if (length > MAX_LENGTH) {
            length = MAX_LENGTH;
        } else if (length < MIN_LENGTH) {
            length = MIN_LENGTH;
        }

        if (notation > MAX_NOTATION) {
            notation = MAX_NOTATION;
        } else if (notation < MIN_NOTATION) {
            notation = MIN_NOTATION;
        }

        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            String c = String.valueOf(CHARACTER_STRING.charAt(random.nextInt(notation)));

            if (caseSensitive) {
                c = random.nextBoolean() ? c.toUpperCase() : c;
            }

            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    public String generateSequence(int length) {
        return generateSequence(length, MAX_NOTATION, true);
    }
}
