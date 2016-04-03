package com.vukic.utr.simpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Deterministicki potisni utomat koji prihvaca niz
 * prizvatljivim stanjem.
 * @author Matija Vukić
 *
 */
public class SimPa {
    private static boolean DEBUG=true;
    private List<List<String>> ulazniNizovi;
    private List<String> stanja;
    private List<String> znakoviUlaz;
    private List<String> znakoviStog;
    private List<String> prihStanja;//Prihvatljiva stanja
    private String pocStanje;//Pocetno stanje automata
    private String pocStanjeStoga;//Pocetno stanje na stogu
    private List<Prijelaz> prijelazi;// S(trenutnoStanje,ulazniZnak,stanjeStoga)->sljedStanje,[sljedSadrzajStoga]
    private List<List<String>> zadnjiRezultati;

    public static void main(String[] args) {
        String broj = "7";
        String fileInputName = "src/testsSimPa/test"+broj+"/test.in";
        SimPa se = new SimPa(fileInputName);
        se.start();
        if(SimPa.DEBUG) {
            try {
                String line;
                BufferedReader br = new BufferedReader(new FileReader(new File("src/testsSimPa/test" + broj + "/test.out")));
                System.out.println("\nIzlaz iz datoteke: ");
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
            }
        }
    }

    private void start() {
        this.zadnjiRezultati = new ArrayList<>();
        for(List<String> niz:this.ulazniNizovi){
            obradiNiz(niz);
        }
        for(List<String> niz2:this.zadnjiRezultati){
            ispisiRezultatZaNiz(niz2);
        }
    }

    private void obradiNiz(List<String> ulazniNiz) {
        //Ulazni niz znakova
        List<String> niz = new ArrayList<>(ulazniNiz);
        //Set start state
        String trenutnoStanje = new String(this.pocStanje);
        //Stack class
        Stack<String> stack = new Stack<>();
        //Push start symbol on stack
        stack.push(this.pocStanjeStoga);
        //Index variable for input array
        int i=0;
        Boolean loop=true,found;
        String znakStack,znakUlaz;
        //finished product
        List<String> kraj = new ArrayList<>();
        kraj.add(trenutnoStanje);
        kraj.add("#");
        kraj.add(stack.peek());
        kraj.add("|");
        while(loop){
            if(this.prihStanja.contains(trenutnoStanje) && i >= niz.size()){
                kraj.add("1");
//				kraj.stream().forEach(s -> System.out.print(s));
//				ispisiRezultatZaNiz(kraj);
                this.zadnjiRezultati.add(kraj);
                break;
            }
            //Check if stack is empty
            if(stack.empty()){
                znakStack="$";
            }else{
                znakStack=stack.peek();
            }
            //Check if index is out of bounds
            if(i < niz.size()){
                znakUlaz = niz.get(i);
            }else{
                znakUlaz = "$";
            }
            found=false;
            for(Prijelaz prijelaz:this.prijelazi){
                if(prijelaz.stanje.equals(trenutnoStanje) && prijelaz.znakStog.equals(znakStack) && prijelaz.znakUlaz.equals(znakUlaz)){
//						System.out.println("Prijelaz: "+prijelaz);
//						System.out.println("Skidam znak: "+stack.peek());
                    stack.pop();
                    if(!(prijelaz.nizZnakovaStoga.contains("$") && prijelaz.nizZnakovaStoga.size()==1)){
                        //Add to stack
                        for(String znak:prijelaz.nizZnakovaStoga){
//								System.out.println("Dodajem znak: "+znak);
                            stack.push(znak);
                        }
                    }
                    //Set new current state
                    trenutnoStanje=prijelaz.sljedStanje;
                    kraj.add(trenutnoStanje);
                    kraj.add("#");
                    //Print stack content
//						System.out.println("Sljedece stanje: "+trenutnoStanje);
                    List<String> tempNewStack = new ArrayList<>(stack);
                    if(tempNewStack.isEmpty()){
                        kraj.add("$");
                    }else{
                        Collections.reverse(tempNewStack);
                        String joined="";
                        StringBuilder sb = new StringBuilder();
                        for(int o=0;o<tempNewStack.size();o++){
                            sb.append(tempNewStack.get(o));
                        }
                        joined = sb.toString();
                        kraj.add(joined);
                    }
                    kraj.add("|");
//						System.out.print("Stack content: ");
//                    for(String st:stack){
//							System.out.print(st+" ");
//                    }
//						System.out.println();
//						System.out.println("Kraj: ");
//						kraj.stream().forEach(s -> System.out.print(s));
//						System.out.println();
                    found=true;
                    break;
                }
            }
            if(found == false){
                boolean found1=false;
                if(i < niz.size()){
                    for(Prijelaz prijelaz:this.prijelazi){
                        if(prijelaz.stanje.equals(trenutnoStanje) && prijelaz.znakStog.equals(znakStack) && prijelaz.znakUlaz.equals("$")){
//						System.out.println("Ne postoji prijelaz -> Provjeravam za $ znak: ");
//						System.out.println("Prijelaz: "+prijelaz);
//						System.out.println("Skidam znak: "+stack.peek());
                            stack.pop();
                            if(!(prijelaz.nizZnakovaStoga.contains("$") && prijelaz.nizZnakovaStoga.size()==1)){
                                //Add to stack
                                for(String znak:prijelaz.nizZnakovaStoga){
//								System.out.println("Dodajem znak: "+znak);
                                    stack.push(znak);
                                }
                            }
                            //Set new current state
                            trenutnoStanje=prijelaz.sljedStanje;
                            kraj.add(trenutnoStanje);
                            kraj.add("#");
                            //Print stack content
//						System.out.println("Sljedece stanje: "+trenutnoStanje);
                            List<String> tempNewStack = new ArrayList<>(stack);
                            if(tempNewStack.isEmpty()){
                                kraj.add("$");
                            }else{
                                Collections.reverse(tempNewStack);
//							kraj.add(tempNewStack.stream().collect(Collectors.joining()));
                                String joined="";
                                StringBuilder sb = new StringBuilder();
                                for(int o=0;o<tempNewStack.size();o++){
                                    sb.append(tempNewStack.get(o));
                                }
                                joined = sb.toString();
                                kraj.add(joined);
                            }
                            kraj.add("|");
//						System.out.print("Stack content: ");
                            for(String st:stack){
//							System.out.print(st+" ");
                            }
//						System.out.println();
//						System.out.println("Kraj: ");
//						kraj.stream().forEach(s -> System.out.print(s));
//						System.out.println();
                            found1=true;
                            i--; //Do not increase current input array index
                            break;
                        }
                    }
                    if(!found1){
                        kraj.add("fail|0");
                        //Print ending result
//					ispisiRezultatZaNiz(kraj);
                        this.zadnjiRezultati.add(kraj);
                        break;
                    }
                }else{
                    kraj.add("0");
                    //Print ending result
//				ispisiRezultatZaNiz(kraj);
                    this.zadnjiRezultati.add(kraj);
                    break;
                }
            }
            i++;
        }
    }
    public void ispisiRezultatZaNiz(List<String> niz){
//		System.out.println("  REZULTAT  ");
        for(String str:niz){
            System.out.print(str);
        }
        System.out.println();
    }
    public SimPa(String fileInputName){
        List<String> temp = new ArrayList<>(); // Lines from file
        String line;
        try {
            BufferedReader br;
            if(SimPa.DEBUG) br = new BufferedReader(new FileReader(new File(fileInputName)));
            else br = new BufferedReader(new InputStreamReader(System.in));
            while ((line = br.readLine()) != null) {
                temp.add(line);
            }
            getUlazneNizove(temp.get(0));
            getStanja(temp.get(1));
            getZnakoveUlazne(temp.get(2));
            getZnakoveStoga(temp.get(3));
            getPrihStanja(temp.get(4));
            this.pocStanje = temp.get(5);
//			System.out.println("Pocetno stanje automta: "+this.pocStanje);
            this.pocStanjeStoga = temp.get(6);
//			System.out.println("Pocetno stanje stoga: "+this.pocStanjeStoga);
            getPrijelaze(temp);
        } catch (IOException e) {
        }
    }

    private void getPrijelaze(List<String> temp) {
        this.prijelazi = new ArrayList<>();
        for(String line:temp){
            if(temp.indexOf(line) > 6){
//				System.out.println(line);
                String left = line.split("->")[0];
                String right = line.split("->")[1];
                String[] leftSplit = left.split(",");//left part of transition
                String[] rightSplit = right.split(",");//right part of trnsition
//				String[] sljedNiz = rightSplit[1].split("");
                char[] sljedNiz = rightSplit[1].toCharArray();
//				List<String> revrsedForStack = new ArrayList<>(Arrays.asList(sljedNiz));
                List<String> revrsedForStack = new ArrayList<>();
                //java 7 compatibility
                for(char ch:sljedNiz){
                    revrsedForStack.add(String.valueOf(ch));
                }
                //
                Collections.reverse(revrsedForStack);
                Prijelaz prijelaz = new Prijelaz(leftSplit[0],leftSplit[1],leftSplit[2],rightSplit[0],revrsedForStack);
                this.prijelazi.add(prijelaz);
//				System.out.println(prijelaz);
            }
        }
//		System.out.println("Prijelazi: ");
//		this.prijelazi.stream().forEach(p -> System.out.println("    "+p));
    }

    private class Prijelaz{
        private String stanje;
        private String znakUlaz;
        private String znakStog;
        private String sljedStanje;
        private List<String> nizZnakovaStoga;

        public Prijelaz(String stanje,String znakUlaz,String znakStog,String sljedStanje,List<String> nizZnakovaStoga){
            this.nizZnakovaStoga = new ArrayList<>(nizZnakovaStoga);
            this.stanje=stanje;
            this.znakUlaz=znakUlaz;
            this.znakStog=znakStog;
            this.sljedStanje=sljedStanje;
        }
        @Override
        public String toString(){
            return this.stanje+","+this.znakUlaz+","+this.znakStog+"->"+this.sljedStanje+","+this.nizZnakovaStoga;
        }
    }

    private void getPrihStanja(String prihStanjaAsString) {
        String[] temp = prihStanjaAsString.split(",");
        this.prihStanja = Arrays.asList(temp);
//		System.out.println("Prihvatljiva stanja: "+this.prihStanja);

    }

    private void getZnakoveStoga(String znakoviAsString) {
        String[] temp = znakoviAsString.split(",");
        this.znakoviStog = Arrays.asList(temp);
//		System.out.println("Znakovi stoga: "+this.znakoviStog);

    }

    private void getZnakoveUlazne(String znakoviAsString) {
        String[] temp = znakoviAsString.split(",");
        this.znakoviUlaz = Arrays.asList(temp);
//		System.out.println("Znakovi ulaza: "+this.znakoviUlaz);
    }

    private void getStanja(String stanjaAsString) {
        String[] temp = stanjaAsString.split(",");
        this.stanja = Arrays.asList(temp);
//		System.out.println("Stanja: "+this.stanja);
    }

    private void getUlazneNizove(String ulazniNizoviAsString) {
        String[] nizovi = ulazniNizoviAsString.split("\\|");
        this.ulazniNizovi = new ArrayList<>();
		 System.out.println("Ulazni nizovi:");
        for (String niz : nizovi) {
            List<String> temp = Arrays.asList(niz.split(","));
            this.ulazniNizovi.add(temp);
//			 System.out.println("    "+temp.toString());
        }

    }
}
