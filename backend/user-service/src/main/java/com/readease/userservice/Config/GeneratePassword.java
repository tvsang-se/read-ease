package com.readease.userservice.Config;

import java.security.SecureRandom;

public  class GeneratePassword {


    public  String generateStrongPassword(int length) {
        String uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercaseChars = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_+=<>?";

        String allChars = uppercaseChars + lowercaseChars + digits + specialChars;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each character set
        password.append(uppercaseChars.charAt(random.nextInt(uppercaseChars.length())));
        password.append(lowercaseChars.charAt(random.nextInt(lowercaseChars.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Generate the rest of the password
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the characters in the password to make it more random
        String shuffledPassword = shuffleString(password.toString());

        return shuffledPassword;
    }

    // Helper function to shuffle a string
    public  String shuffleString(String input) {
        char[] charArray = input.toCharArray();
        SecureRandom random = new SecureRandom();
        for (int i = charArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = charArray[index];
            charArray[index] = charArray[i];
            charArray[i] = temp;
        }
        return new String(charArray);
    }
}
