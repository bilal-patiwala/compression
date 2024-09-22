package Compression;

import java.io.*;

public class ArithmeticDecompression {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream("arithout.txt");
        DataInputStream din = new DataInputStream(file);
        ObjectInputStream oin = new ObjectInputStream(file);

    }
}
