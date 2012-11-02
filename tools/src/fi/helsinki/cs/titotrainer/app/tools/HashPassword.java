package fi.helsinki.cs.titotrainer.app.tools;

import java.util.Scanner;

import fi.helsinki.cs.titotrainer.app.model.User;

public class HashPassword {
    public static void main(String[] args) {
        String usage = "This program hashes a password with the proper salt " +
                       "so that the hash can be stored in the database " +
                       "into \"user\".passwordsha1";
        System.out.println(usage);
        System.out.println();
        System.out.print("Password to hash (visible prompt!) > ");
        System.out.flush();
        
        String password = new Scanner(System.in).nextLine();
        String hash = User.hashPassword(password);
        System.out.println();
        System.out.println(hash);
        System.out.println();
    }
}
