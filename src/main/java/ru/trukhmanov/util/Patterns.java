package ru.trukhmanov.util;

import java.util.regex.Pattern;

public class Patterns{
    private Patterns(){}
    public static final Pattern ENG_LETTERS = Pattern.compile("[A-Za-z]+");
    public static final Pattern ENG_LETTERS_AND_SPACES_BETWEEN_WORDS = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)*$");
}
