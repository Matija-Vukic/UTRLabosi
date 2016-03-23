package com.vukic.utr.simenka;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimEnka {
    private static boolean DEBUG = true;
    private List<List<String>> ulazniNizovi;
    private List<String> stanja;
    private List<String> abeceda;
    private List<String> prihStanja;
    private List<String> pocStanja;
    private List<Prijelaz> prijelazi;
    private Set<String> trenStanja;
    private List<String> temp;

    public static void main(String[] args) {
        String broj = "03";
        String fileInputName = "src/testsSimEnka/test"+broj+"/test.a";
        SimEnka se;
        if(!DEBUG) se = new SimEnka("");
        else se = new SimEnka(fileInputName);
        se.start();
        if(DEBUG) {
            int i=0;
            try {
                String line;
                BufferedReader brr = new BufferedReader(new FileReader(new File("src/testsSimEnka/test" + broj + "/test.b")));
                while ((line = brr.readLine()) != null) {
                    System.out.println(line);
                    i++;
                }
            } catch (IOException e) {
                System.out.println("Error");
            }
        }

    }

    public void start() {
        for (List<String> lista : this.ulazniNizovi) {
            obradiNiz(lista);
            resetData();
        }
    }

    private class Prijelaz {
        public String stanje;
        public String znak;
        public List<String> sljedStanja;

        public Prijelaz(String a, String b, String[] c) {
            stanje = a;
            znak = b;
            sljedStanja = Arrays.asList(c);
        }

        @Override
        public String toString() {
            return (stanje + "," + znak + "->" + sljedStanja);
        }
    }

    public SimEnka(String fileInput) {
        this.temp = new ArrayList<>();
        try {
            BufferedReader reader;
            if(!SimEnka.DEBUG) reader = new BufferedReader(new InputStreamReader(System.in));
            else reader = new BufferedReader(new FileReader(new File(fileInput)));
            String s;
//			System.out.println("Upisi liniju datoteke:");
            s = reader.readLine();
            while (s != null) {
                this.temp.add(s);
//				System.out.println("Upisi liniju datoteke:");
                s = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        getUlazneNizove(temp.get(0));
        getStanja(temp.get(1));
        getAbeceda(temp.get(2));
        getPrihvatljivaStanja(temp.get(3));
        getPocetnaStanja(temp.get(4));
        getPrijelazi(temp);
        this.trenStanja = new LinkedHashSet<>(this.pocStanja);
    }

    private void resetData() {
        this.trenStanja.clear();
        this.trenStanja.addAll(this.pocStanja);
    }

    private void getPrijelazi(List<String> prijelaziAsLines) {
        this.prijelazi = new ArrayList<>();
        // System.out.println("Prijelazi:");
        for (String line : prijelaziAsLines) {
            if (prijelaziAsLines.indexOf(line) >= 5) {
                String left = (line.split("->"))[0];
                String right = (line.split("->"))[1];
                String[] leftSplit = left.split(",");
                String[] rightSplit = right.split(",");
                Prijelaz prijelaz = new Prijelaz(leftSplit[0], leftSplit[1], rightSplit);
                this.prijelazi.add(prijelaz);
                // System.out.println("    "+prijelaz.toString());
            }
        }
    }

    private void getPocetnaStanja(String pocStanjaAsString) {
        String[] temp = pocStanjaAsString.split(",");
        this.pocStanja = Arrays.asList(temp);
        // System.out.println("Pocetna stanja: "+this.pocStanja);
    }

    private void getPrihvatljivaStanja(String prihStanjaAsString) {
        String[] temp = prihStanjaAsString.split(",");
        this.prihStanja = Arrays.asList(temp);
        // System.out.println("Prihvaljiva stanja: "+this.prihStanja);

    }

    private void getAbeceda(String abecedaAsString) {
        String[] temp = abecedaAsString.split(",");
        this.abeceda = Arrays.asList(temp);
        // System.out.println("Abeceda: "+this.abeceda);
    }

    private void getUlazneNizove(String ulazniNizoviAsString) {
        String[] nizovi = ulazniNizoviAsString.split("\\|");
        this.ulazniNizovi = new ArrayList<>();
        // System.out.println("Ulazni nizovi:");
        for (String niz : nizovi) {
            List<String> temp = Arrays.asList(niz.split(","));
            this.ulazniNizovi.add(temp);
            // System.out.println("    "+temp.toString());
        }
    }

    private void getStanja(String stanjaAsString) {
        String[] temp = stanjaAsString.split(",");
        this.stanja = Arrays.asList(temp);
        // System.out.println("Stanja: "+this.stanja);
    }

    private void findEpsilonPrijelaze(Set<String> trenStanja) {
        Set<String> novaStanja = new LinkedHashSet<>(trenStanja);
        for (String stanje : trenStanja) {
            for (Prijelaz prijelaz : this.prijelazi) {
                if (prijelaz.stanje.equals(stanje) && prijelaz.znak.equals("$")) {
                    novaStanja.addAll(prijelaz.sljedStanja);
                    // System.out.println("  Prijelaz: "+prijelaz.toString());
                    break;
                }
            }
        }
        if (trenStanja.containsAll(novaStanja)) {
            this.trenStanja.clear();
            this.trenStanja.addAll(novaStanja);
            // System.out.println("nova stanja:"+this.trenStanja);
            return;
        } else {
            findEpsilonPrijelaze(novaStanja);
        }
    }

    public void obradiNiz(List<String> ulazniZnakovi) {
        Set<String> novaStanja = new LinkedHashSet<>(trenStanja);
        List<String> kraj = new ArrayList<>();
        Boolean found;

        // System.out.println("Pocetna stanja:" + this.trenStanja);
        findEpsilonPrijelaze(this.trenStanja);
        // System.out.println("Pocetna stanja nakon EPrijelaza:"+this.trenStanja+"\n");

        String sortedPocetak = sortAddCommasRemoveHash();
        kraj.add(sortedPocetak);
        kraj.add("|");

        for (String znak : ulazniZnakovi) {
            // System.out.println("ZNAK:                       "+znak);
            // System.out.println("Trenutna stanja: "+this.trenStanja);
            found = false;
            novaStanja.clear();
            for (String stanje : this.trenStanja) {
                found = false;
                for (Prijelaz prijelaz : this.prijelazi) {
                    if (prijelaz.stanje.equals(stanje) && prijelaz.znak.equals(znak)) {
                        // System.out.println("  Prijelaz: "+prijelaz.toString());
                        // System.out.println("Trenutna stanja: "+novaStanja);
                        novaStanja.addAll(prijelaz.sljedStanja);
                        // System.out.println("Dodana stanja: "+prijelaz.sljedStanja);
                        // System.out.println("Zavrsna stanja:"+novaStanja);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // System.out.println("Trenutna stanja:"+novaStanja);
                    novaStanja.add("#");
                    // System.out.println("Dodan prazan znak: "+novaStanja);
                }
            }
            findEpsilonPrijelaze(novaStanja);
            if (this.trenStanja.size() > 1) {
                String sorted = sortAddCommasRemoveHash();
                kraj.add(sorted);
            } else {
                kraj.addAll(this.trenStanja);
            }
            kraj.add("|");
            // System.out.println("Zavrsna stanja nakon EPrijelaza:"+this.trenStanja);

        }
        // System.out.println("\n\nZavrsna stanja:");
        kraj.remove(kraj.size() - 1);
		kraj.stream().forEach(s -> System.out.print(s));

        System.out.println();// Print empty space at the end of result
        // System.out.println();
        // System.out.println("\n\n\n");
    }

    private String sortAddCommasRemoveHash() {
        // sorted to Set because there could be more then one #
		List<String> sorted = this.trenStanja.stream().sorted().collect(Collectors.toList());// Sort

        this.trenStanja.clear();
        if (sorted.contains("#")) {
            if (sorted.size() > 1) {
                sorted.remove("#");
            }
        }
        this.trenStanja.addAll(sorted);
        String joined = "";
        List<String> joinedList = new ArrayList<>(this.trenStanja);
        joined = String.join(",",joinedList);
        return joined;
    }
}