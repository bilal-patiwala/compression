package Compression;

import java.io.*;
import java.util.*;

class CharacterModel implements Serializable{
    double low;
    double high;
    public CharacterModel(double low, double high){
        this.low = low;
        this.high = high;
    }

}

class MetaData implements Serializable{
    private ArrayList<Map.Entry<Character, Double>> characterProbability;
    private HashMap<Character, CharacterModel> characterScale;

    public ArrayList<Map.Entry<Character, Double>> getCharacterProbability() {
        return characterProbability;
    }

    public void setCharacterProbability(ArrayList<Map.Entry<Character, Double>> characterProbability) {
        this.characterProbability = characterProbability;
    }

    public HashMap<Character, CharacterModel> getCharacterScale() {
        return characterScale;
    }

    public void setCharacterScale(HashMap<Character, CharacterModel> characterScale) {
        this.characterScale = characterScale;
    }


}

public class ArithmeticCompression {
    public static void main(String[] args) throws IOException {
        FileInputStream fin = new FileInputStream("test.txt");
        HashMap<Character, Integer> map = new HashMap<>();
        int read_input;
        int totalSymbols=0;
        while((read_input = fin.read())!=-1){
            if(map.containsKey((char) read_input)){
                map.put((char) read_input, map.get((char) read_input)+1);
            }
            else{
                map.put((char) read_input,1);
            }
            totalSymbols+=1;
        }
        fin.close();
        System.out.println(totalSymbols);
        ArrayList<Map.Entry<Character,Integer>> list = sortMap(map);
        System.out.println(list);
        ArrayList<Map.Entry<Character,Integer>> character_rang_list = Intialize_model(list);
        System.out.println(character_rang_list);
        ArrayList<Map.Entry<Character, Double>> characterProbabilities = sortDoubleMap(calculateProbabilities(totalSymbols,list));
        System.out.println(characterProbabilities);
        HashMap<Character, CharacterModel> characterScale = scaleCharacter(characterProbabilities);
        ArrayList<Map.Entry<Character, CharacterModel>> character_list = new ArrayList<>(characterScale.entrySet());
//        for(Map.Entry<Character, CharacterModel> entry : character_list){
//            System.out.println(entry.getKey() + "-->" + entry.getValue().low + "," + entry.getValue().high);
//        }
        double encoded_number = encode(characterScale);
        System.out.println(encoded_number);
        MetaData metaData = new MetaData();
        metaData.setCharacterProbability(characterProbabilities);
        metaData.setCharacterScale(characterScale);
        File file = new File("out.txt");
        file.createNewFile();
        FileOutputStream fout = new FileOutputStream(file.getName());
        // ObjectOutputStream out = new ObjectOutputStream(fout);
        DataOutputStream dout = new DataOutputStream(fout);
        // out.writeObject(metaData);
        // out.close();
        dout.writeDouble( encoded_number);
        dout.close();

    }

    private static double encode(HashMap<Character, CharacterModel> characterScale) throws IOException {
        String decimal_number = "";
        FileInputStream file = new FileInputStream("test.txt");
        double low = 0.0;
        double high = 1.0;
        int c;
        while((c=file.read())!=-1){
            double range = high-low;
            high = low + range * highRange((char) c,characterScale);
            low = low + range * lowRange((char) c, characterScale);
        }

        return low;
    }

    private static double lowRange(char c, HashMap<Character, CharacterModel> characterScale) {
        CharacterModel character = characterScale.get(c);
        return character.low;
    }

    private static double highRange(char c, HashMap<Character, CharacterModel> characterScale) {
        CharacterModel character = characterScale.get(c);
        return character.high;
    }


    private static ArrayList<Map.Entry<Character, CharacterModel>> sortCharacterScale(HashMap<Character, CharacterModel> h) {
        ArrayList<Map.Entry<Character, CharacterModel>> l = new ArrayList<>(h.entrySet());

        Collections.sort(l, new Comparator<Map.Entry<Character, CharacterModel>>() {
            @Override
            public int compare(Map.Entry<Character, CharacterModel> o1, Map.Entry<Character, CharacterModel> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return  l;
    }

    private static HashMap<Character, CharacterModel> scaleCharacter(ArrayList<Map.Entry<Character, Double>> characterProbabilities) {
        double count = 0.0;
        HashMap<Character, CharacterModel> map = new HashMap<>();
        for(Map.Entry<Character, Double> entry: characterProbabilities){
            map.put(entry.getKey(),new CharacterModel(count, entry.getValue()));
            count = entry.getValue();
        }
        return map;
    }

    private static HashMap<Character, Double> calculateProbabilities(int totalSymbols, ArrayList<Map.Entry<Character, Integer>> list) {
        HashMap<Character, Double> probability= new HashMap<>();
        double cumulativeProbability = 0.0;

        for(Map.Entry<Character, Integer> entry: list){
            double prob = (double) entry.getValue() / totalSymbols;
            cumulativeProbability+=prob;
            probability.put(entry.getKey(),cumulativeProbability);
        }

        return probability;
    }

    public static ArrayList<Map.Entry<Character, Integer>> sortMap(HashMap<Character, Integer> h){
        ArrayList<Map.Entry<Character, Integer>> l = new ArrayList<>(h.entrySet());

        Collections.sort(l, new Comparator<Map.Entry<Character, Integer>>() {
            @Override
            public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        return l;
    }

    public static ArrayList<Map.Entry<Character, Double>> sortDoubleMap(HashMap<Character, Double> h){
        ArrayList<Map.Entry<Character, Double>> l = new ArrayList<>(h.entrySet());

        Collections.sort(l, new Comparator<Map.Entry<Character, Double>>() {
            @Override
            public int compare(Map.Entry<Character, Double> o1, Map.Entry<Character, Double> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        return l;
    }

    public static ArrayList<Map.Entry<Character, Integer>> Intialize_model(ArrayList<Map.Entry<Character,Integer>> list){
        ArrayList<Map.Entry<Character, Integer>> range_list = new ArrayList<>();
        int count = 0;
        for(Map.Entry<Character, Integer> entry: list){
            range_list.add(Map.entry(entry.getKey(), count));
            count+=entry.getValue();
        }
        return range_list;
    }
}
