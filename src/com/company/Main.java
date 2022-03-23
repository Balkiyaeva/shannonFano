package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Path path = Paths.get("/Users/amira/Downloads/Text.txt");
        File file = new File(path.toString());
        String text = "";

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String string = sc.nextLine();
                text += string;
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ShannonFano shannonFano = new ShannonFano();

        //String text = "CERRIAARREEIICCEEAAIIEECCRRRCR";
        shannonFano.algorithm(text);
        System.out.println(shannonFano);
    }
}
