package Compression;

import java.io.*;
import java.util.*;

class Node {
    Map.Entry<String, Integer> m;
    Node leftNode;
    Node rightNode;

    public Node(Map.Entry<String, Integer> m){
        this.m = m;
        leftNode = null;
        rightNode=null;
    }
}

class BitOutputStream {
    private OutputStream outputStream;
    private int currentByte;
    private int numBitsFilled;

    public BitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.currentByte = 0;
        this.numBitsFilled = 0;
    }

    public void writeBit(int bit) throws IOException {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("Bit must be 0 or 1");
        }

        currentByte = (currentByte << 1) | bit;
        numBitsFilled++;

        if (numBitsFilled == 8) {
            outputStream.write(currentByte);
            currentByte = 0;
            numBitsFilled = 0;
        }
    }

    public void close() throws IOException {
        while (numBitsFilled != 0) {
            writeBit(0); // Pad with zeros if needed
        }
        outputStream.close();
    }
}

public class TextCompression {
    public static void main(String[] args) throws IOException {
        FileInputStream f = new FileInputStream("idea.txt");
        HashMap<Character, Integer> h = new HashMap<>();
        int c;
        while((c = f.read()) != -1){
            if(h.containsKey((char) c)){
                h.put((char) c, h.get((char) c)+1);
            }
            else{
                h.put((char) c,1);
            }
        }
        f.close();
        ArrayList<Map.Entry<Character, Integer>> list = sortMap(h);
        System.out.println(list);

        Node root = null;
        root = huffman(root, list);

        HashMap<Character, String> codeWord = new HashMap<>();
        inOrder(root,codeWord,"");
        System.out.println(codeWord);
        int k;
        f = new FileInputStream("idea.txt");
        File file = new File("ideaout.txt");
        file.createNewFile();
        FileOutputStream fout = new FileOutputStream(file.getName());
        BitOutputStream bitOutputStream = new BitOutputStream(fout);
        while((k=f.read())!=-1){
            for(char bit : codeWord.get((char) k).toCharArray()){
                bitOutputStream.writeBit(bit == '1' ? 1 : 0);
            }
        }
        bitOutputStream.close();
        HashMap<String, Character> inverseCodeWord = createInverseMap(codeWord);;
        decode(file.getName(), inverseCodeWord);
    }

    private static HashMap<String, Character> createInverseMap(HashMap<Character, String> codeWord) {
        HashMap<String, Character> inverseMap = new HashMap<>();
        for (Map.Entry<Character, String> entry : codeWord.entrySet()) {
            inverseMap.put(entry.getValue(), entry.getKey());
        }
        return inverseMap;
    }

    private static Node huffman(Node root, ArrayList<Map.Entry<Character, Integer>> list) {
        for(Map.Entry<Character, Integer> entry : list){
            if(root == null){
                Map.Entry<String, Integer> temp = Map.entry(String.valueOf(entry.getKey()),entry.getValue());
                root = new Node(temp);
            }
            else{
                int sum = root.m.getValue() + entry.getValue();
                Map.Entry<String, Integer> temp = Map.entry(String.valueOf(entry.getKey()),entry.getValue());
                Node right = new Node(temp);
                String key = root.m.getKey() + entry.getKey();
                temp = Map.entry(key, sum);
                Node new_root = new Node(temp);
                new_root.leftNode = root;
                new_root.rightNode = right;
                root = new_root;
            }
        }
        return root;
    }

    public static ArrayList<Map.Entry<Character, Integer>> sortMap(HashMap<Character, Integer> h){
        ArrayList<Map.Entry<Character, Integer>> l = new ArrayList<>(h.entrySet());

        Collections.sort(l, new Comparator<Map.Entry<Character, Integer>>() {
            @Override
            public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        return l;
    }

    public static void inOrder(Node root, HashMap<Character, String> codeWord, String code){

        if(root.leftNode == null && root.rightNode == null){
            codeWord.put(root.m.getKey().charAt(0),code);
            return;
        }

        inOrder(root.leftNode,codeWord,code+="0");
        code = code.substring(0,code.length()-1);
        inOrder(root.rightNode,codeWord,code+="1");
    }

    public static void decode(String file, HashMap<String, Character> codeWord) throws IOException {
        FileInputStream fin = new FileInputStream(file);
        byte c;
        String content = "";
        String leftOut = "";
        while((c= (byte) fin.read())!=-1){
            String code = "";
            if (leftOut.length()!=0){
                code+=leftOut;
                leftOut="";
            }
            for(int i=7;i>=0;i--){
                code += String.valueOf((c >> i) & 1);
                if (codeWord.containsKey(code)){
                    content += codeWord.get(code);
                    code="";
                }
                if(code.length()!=0 && i==0){
                    leftOut+=code;
                }
            }
        }
        System.out.println(content);

    }

}
