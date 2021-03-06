package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ShannonFano {
    private String originalText;

    private Map<Character, Integer> appearance = new LinkedHashMap<>();
    private Map<Character, Float> probability = new LinkedHashMap<>();

    private Map<List<Character>, Float> recmap = new LinkedHashMap<>();
    //private Map<List<Character>, Character> fordec = new LinkedHashMap<>();

    private Map<List<Character>, String> recBits = new LinkedHashMap<>();

    private Map<Character, String> encode = new LinkedHashMap<>();

    private void prob(){
        int len = originalText.length();

        //Map<Character, Integer> appearance = new LinkedHashMap<>();

        for (char el: originalText.toCharArray()) {
            if(appearance.containsKey(el)){
                appearance.replace(el, appearance.get(el) + 1);
                probability.replace(el, (float)(appearance.get(el))/len);
            } else {
                appearance.put(el, 1);
                probability.put(el, (float)(appearance.get(el))/len);
            }
        }

        rec(probability);
    }

    private <Key, Value extends Comparable<? super Value>> Map<Key, Value> sortByValue(Map<Key, Value> map) {
        List<Map.Entry<Key, Value>> temp = new ArrayList<>(map.entrySet());
        //temp.sort(Map.Entry.comparingByValue());
        temp.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<Key, Value> result = new LinkedHashMap<>();
        for (Map.Entry<Key, Value> entry : temp) {
            result.put(entry.getKey(), entry.getValue());
            //System.out.print("'" + entry.getKey() + "': " + entry.getValue() + "; ");
        }
        //System.out.println();

        return result;
    }

    public void algorithm(String text){
        this.originalText = text;
        prob();
        getEncode();

       //System.out.println(getCompressedText(text));

        try {
            FileWriter writer = new FileWriter("/Users/amira/Downloads/New.txt");
            writer.write(getCompressedText(text));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rec(Map<Character, Float> map){
        List<Map.Entry<Character, Float>> temp = new ArrayList<>(sortByValue(map).entrySet());

        List<Character> fkeys = new ArrayList<>();
        List<Character> skeys = new ArrayList<>();

        Float first = 0f, second = 0f;

        int k = 0;
        for(int i = temp.size()-1, j = 0 ; i >= 0; i--, j++){

            if((j+k == temp.size()) || (j+k > temp.size())){break;}

            second += temp.get(i).getValue();
            skeys.add(temp.get(i).getKey());


            if ((k != i) && ((first < second) || (first.equals(second)))){ //if the first half less than/equals to second
                first += temp.get(k).getValue();
                fkeys.add(temp.get(k).getKey());

                k++;
            }
        }

        recmap.put(fkeys, first);
        recmap.put(skeys, second);

        Map<Character, Float> mapF = new LinkedHashMap<>();
        Map<Character, Float> mapS = new LinkedHashMap<>();

        for (Map.Entry<Character, Float> entry : temp) {
           if (fkeys.contains(entry.getKey())){
               mapF.put(entry.getKey(), entry.getValue());
           } else {
               mapS.put(entry.getKey(), entry.getValue());
           }
        }

        if (mapF.size() == 1){
            produceb(mapF, fkeys, true);
        } else {
            produceb(mapF, fkeys, true);
            rec(mapF);
        }

        if (mapS.size() == 1){
            produceb(mapS, skeys, false);
        } else {
            produceb(mapS, skeys, false);
            rec(mapS);
        }
    }

    public void produceb(Map<Character, Float> map, List<Character> characterList, boolean first){
        String bit = "";
        if (!map.isEmpty()){
            if (first){
                bit = "0";
            } else { bit = "1";}
        }

        for (Character c: characterList) {
            String recBit;
            if (encode.get(c) == null){
                recBit = "";
            } else {
                recBit = encode.get(c);
            }

            encode.put(c, recBit + bit);
//            fordec.put(characterList, b);
            recBits.put(characterList, recBit + bit);
        }
    }

    public void getSums(){
        int i = 1;
        for (List<Character> characters : recmap.keySet()){
            System.out.println("Node" + i + ": (" + recBits.get(characters) + ")");
            System.out.println("'" + characters + "': " + recmap.get(characters) + "; ");
            i++;
        }
    }

    public void getEncode(){
        getSums();
        System.out.println();

        for (Map.Entry<Character, String> entry : encode.entrySet()) {
            StringBuilder s = new StringBuilder();
            s.append(entry.getValue());
            s.reverse();
            entry.setValue(s.toString());
            //System.out.print("'" + entry.getKey() + "': " + entry.getValue() + "; ");
        }

        for (Character character : encode.keySet()){
            System.out.println(character + " - " + probability.get(character) + " - " + encode.get(character));
        }

        System.out.println();

    }

    public void decode(){
        StringBuilder compressed = new StringBuilder(getCompressedText(originalText));
        StringBuilder reversed = new StringBuilder(getCompressedText(originalText));

        reversed.reverse();

        String decoded = "", s = "";
        //String compressedLoop = "";

        Map<Character, String> encodeSorted;

        encodeSorted = sortByValue(encode);

        for (Character character : reversed.toString().toCharArray()){
            s = character + s;

            for (Map.Entry<Character, String> map : encodeSorted.entrySet()){
                if (s.equals(map.getValue())) {
                    decoded += map.getKey();
                    //compressedLoop += " " + s ;
                    s = "";
                    break;
                }
            }
        }
        StringBuilder builder = new StringBuilder(decoded);
        builder.reverse();

        System.out.println(builder);
        System.out.println();
        //System.out.println(decoded);
        System.out.println(originalText);
//        System.out.println(compressedLoop);
//        System.out.println(compressed);
    }

    /*public void recDec(int count, String elBit, List<Character> characterList, Character character){
        String s = elBit;
        Character key = character;

        for (List<Character> characters : fordec.keySet()  ){
            if (characters.size() == count){
                if (characterList == null && count == 1){
                    key = characters.get(0);
                    s = "";
                }
                if (characterList == null || (characters.containsAll(characterList))) {
                    s += String.valueOf(fordec.get(characters));
                    recDec(count+1, s, characters, key);
                }
            }
        }
        if (!decode.containsKey(key)){
            decode.put(key, s);
        }
    }*/

    public String getCompressedText(String text){
        String compressed = "";

        for (char el: text.toCharArray()) {
            compressed += encode.get(el);
        }

        return compressed;
    }

    public int originalBit(){
        int result = 0;
        for (Map.Entry<Character, Integer> frequency : appearance.entrySet()) {
            result += 8 * frequency.getValue();
        }
        return result;
    }

    public int compressedBit(){
        int result = 0;

        for (Character character: encode.keySet()) {
            String compressed = encode.get(character);
            int len = compressed.length();

            result += len * appearance.get(character);
        }
        return result;
    }

    public float averageCodeLength(){
        float sumFrequencyCL = 0, sumFrequency = 0;

        for (Character character: encode.keySet()) {
            String compressed = encode.get(character);
            int len = compressed.length();

            sumFrequencyCL += len * appearance.get(character);
            sumFrequency += appearance.get(character);
        }

        return sumFrequencyCL/sumFrequency;
    }



    @Override
    public String toString() {
        String str;
        int oBit = originalBit(), cBit = compressedBit();
        float ratio = (float) oBit/cBit;

        str = "\nNumber of bits in the original text: " + oBit + " bits " +
        "\nNumber of bits in the compressed text: " + cBit + " bits " +
        "\nCompression ratio = " + ratio +
        "\nAverage code length = " + averageCodeLength() + " bits/symbol";

        return str;
    }
}
