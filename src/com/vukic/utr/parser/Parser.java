package com.vukic.utr.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Parser {
    private static boolean DEBUG = true;
    private List<String> niz;
    //	private Stack<String> niz;
    private int glava=0;
    private boolean Parsed=false;

    public static void main(String[] args) {
        String broj = "20";
        String fileInputName = "src/testsParser/test" + broj + "/test.in";
        Parser parser = new Parser(fileInputName);

		try {
			String line;
			BufferedReader brr = new BufferedReader(new FileReader(new File("src/testsParser/test" + broj + "/test.out")));
			System.out.println("\nIzlaz iz datoteke: ");
			while ((line = brr.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
		}

    }

    public Parser(String fileInputName) {
        BufferedReader br;
        String line = "";
        try {
            if (Parser.DEBUG) br = new BufferedReader(new FileReader(new File(fileInputName)));
            else  br = new BufferedReader(new InputStreamReader(System.in));
            line = br.readLine();
        } catch (IOException ex) {
        }
//		System.out.println("Ulazni niz: " + line);
        char[] temp = line.toCharArray();
        this.niz = new Stack<>();
        for (char ch : temp) {
            niz.add(String.valueOf(ch));
        }
//		System.out.println(ulaz);
//		System.out.println(this.niz);
//		sZnak();
        boolean yes = sZnak();
        if (this.glava >= niz.size() && yes) {
            System.out.println("\nDA");
        } else {
            System.out.println("\nNE");
        }
    }

    public boolean sZnak() {
        System.out.print("S");
        if(checkLength() && niz.get(glava).equals("a")){
            this.glava++;
            if(aZnak()){
                if(bZnak()){
                    return true;
                }
            }
            return false;
        }
        if(checkLength() && niz.get(glava).equals("b")){
            this.glava++;
            if(bZnak()){
                if(aZnak()){
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean aZnak() {
        System.out.print("A");
        if(checkLength() && niz.get(glava).equals("a")){
            this.glava++;
            return true;
        }
        if(checkLength() && niz.get(glava).equals("b")){
            this.glava++;
            return cZnak();
        }
        return false;
    }

    public boolean bZnak() {
        System.out.print("B");
        if(checkLength() && niz.get(glava).equals("c") && niz.get(glava+1).equals("c")){
            this.glava++;
            this.glava++;
            if(sZnak() && checkLength() && niz.get(glava).equals("b") && niz.get(glava+1).equals("c")){
                this.glava++;
                this.glava++;
                return true;
            }
        }else{
            if(checkLength() && !niz.get(glava).equals("c") || !checkLength()){
                return true;
            }
        }
        return false;
    }

    public boolean cZnak() {
        System.out.print("C");
        if(aZnak()){
            if(aZnak()){
                return true;
            }
        }
        return false;

    }

    public boolean checkLength2(){
        if(this.glava+2 < this.niz.size()) return true;
        return false;
    }
    public boolean checkLength(){
        if(this.glava < this.niz.size()) return true;
        return false;
    }
}