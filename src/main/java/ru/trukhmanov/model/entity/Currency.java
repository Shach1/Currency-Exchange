package ru.trukhmanov.model.entity;

import ru.trukhmanov.util.Patterns;

/**
 * Было принято решение сделать валидацию при создании по аналогии c Jakarta Validation, а не в отдельном классе
 */
public record Currency(
         Integer id,
         String code,
         String fullName,
         String sign
){
    public Currency{
        if(code == null) throw new IllegalArgumentException("Code cannot be null");
        if(code.length() != 3) throw new IllegalArgumentException("Code length must be equal 3");
        if(!Patterns.ENG_LETTERS.matcher(code).matches()) throw new IllegalArgumentException("Code must consist entirely of letters");

        if(fullName == null) throw new IllegalArgumentException("Full name cannot be null");
        if(fullName.length() < 3 || fullName.length() > 20) throw new IllegalArgumentException("Full name length cannot be less than 3 and more than 20");
        if(!Patterns.ENG_LETTERS_AND_SPACES_BETWEEN_WORDS.matcher(fullName).matches()) throw new IllegalArgumentException("Full name can contain only letters and spaces between words");

        if(sign == null) throw new IllegalArgumentException("Sign cannot be null");
        if(sign.isEmpty() || sign.length()> 4) throw new IllegalArgumentException("Sign length cannot be less than 1 and more than 4");
    }
}


