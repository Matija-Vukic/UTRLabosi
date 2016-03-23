package com.vukic.utr.simts;

import com.vukic.utr.simpa.SimPa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimTs {
    private static boolean DEBUG = true;
    private List<String> stanja;
    private List<String> znakoviUlazni;
    private List<String> znakoviTraka;
    private List<String> traka;
    private String znakPrazneCelije;
    private List<String> prihvatljivaStanja;
    private String pocetnoStanje;
    private int polozajGlave;
    private List<Prijelaz> prijelazi;

    public static void main(String[] args) {
        String broj = "05";
        String fileInputName = "src/testsSimTs/test" + broj + "/test.in";
        SimTs se = new SimTs(fileInputName);
        System.out.println("Parsirani izlaz dolje ");
        se.start();
        if (SimTs.DEBUG) {
            try {
                String line;
                BufferedReader brr = new BufferedReader(new FileReader(new File("src/testsSimTs/test" + broj + "/test.out")));
                while ((line = brr.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
            }
            System.out.println("Izlaz iz datoteke iznad ");

        }
    }

    private void start() {
        // inicijalizacija na prvi znak i polozaj
        Integer glava = this.polozajGlave;
        String trenutnoStanje = this.pocetnoStanje;
        boolean noTransitionsFound = true;
        while (true) {
            noTransitionsFound = true;
            if (glava >= this.traka.size() || glava < 0) {
                System.out.print(trenutnoStanje + "|");
                System.out.print(glava - 1 + "|");
                for (String str : this.traka) {
                    System.out.print(str);
                }
                if (this.prihvatljivaStanja.contains(trenutnoStanje)) {
                    System.out.println("|1");
                } else {
                    System.out.println("|0");
                }
                break;// exit while
            }
            String znakTrake = this.traka.get(glava);// ucitaj znak trake
            for (Prijelaz prijelaz : this.prijelazi) {
                if (prijelaz.trenutnoStanje.equals(trenutnoStanje) && prijelaz.znakNaTraci.equals(znakTrake)) {
                    if (prijelaz.pomakGlave.equals("L")) {
                        if (glava - 1 < 0) {
                            noTransitionsFound = true;
                            break;
                        }
                    }
                    if (prijelaz.pomakGlave.equals("R")) {
                        if (glava + 1 >= this.traka.size()) {
                            noTransitionsFound = true;
                            break;
                        }
                    }
                    // postoji prijelaz za stanje i znak trake
                    // System.out.print("Traka: ");
                    // for (String str : this.traka) {
                    // System.out.print(str);
                    // }
                    // System.out.println();
                    // System.out.println("Prijelaz: "+prijelaz);
                    // System.out.println(trenutnoStanje+","+znakTrake);
                    trenutnoStanje = prijelaz.novoStanje; // promjeni stanje
                    // System.out.println("Novo stanje: "+trenutnoStanje);
                    // System.out.println("Stari znak: "+znakTrake);
                    // System.out.println("Novi znak: "+prijelaz.noviZnak);
                    this.traka.set(glava, prijelaz.noviZnak); // zamjeni znak
                    // trake
                    if (prijelaz.pomakGlave.equals("R")) { // pomakni glavu
                        // System.out.println("Pomicem glavu u desno.");
                        glava++;
                    } else {
                        // System.out.println("Pomicem glavu u lijevo.");
                        glava--;
                    }
                    // System.out.println(this.traka);
                    noTransitionsFound = false;
                    break; // exit for
                }
            }
            if (noTransitionsFound) {
                // System.out.println("Ne postoji prijelaz: ");
                System.out.print(trenutnoStanje + "|");
                System.out.print(glava + "|");
                for (String str : this.traka) {
                    System.out.print(str);
                }
                if (this.prihvatljivaStanja.contains(trenutnoStanje)) {
                    System.out.println("|1");
                } else {
                    System.out.println("|0");
                }
                break; // exit while
            }
        }

    }

    public SimTs(String fileInputName) {
        List<String> temp = new ArrayList<>(); // Lines from file
        String line;
        try {
            BufferedReader br;
            if (SimTs.DEBUG) {
                br = new BufferedReader(new FileReader(new File(fileInputName)));
            } else {
                br = new BufferedReader(new InputStreamReader(System.in));
            }
            while ((line = br.readLine()) != null) {
                temp.add(line);
            }
            getStanja(temp.get(0));
            getUlazniZnakovi(temp.get(1));
            getZnakoviTrake(temp.get(2));
            getZnakPrazneCelije(temp.get(3));
            getTraka(temp.get(4));
            getPrihvatljivaStanja(temp.get(5));
            getPocetnoStanje(temp.get(6));
            getPolozajGlave(temp.get(7));
            getPrijelazi(temp);
        } catch (IOException e) {
        }
    }

    private void getPrijelazi(List<String> ulaz) {
        this.prijelazi = new ArrayList<>();
        for (String line : ulaz) {
            if (ulaz.indexOf(line) > 7) {
                String left = line.split("->")[0];
                String right = line.split("->")[1];
                String[] leftSplit = left.split(",");// left part of transition
                String[] rightSplit = right.split(",");// right part of
                // trnsition
                Prijelaz prijelaz = new Prijelaz(leftSplit[0], leftSplit[1], rightSplit[0], rightSplit[1], rightSplit[2]);
                this.prijelazi.add(prijelaz);
                // System.out.println(prijelaz);
            }
        }

    }

    private void getPolozajGlave(String polozajAsString) {
        this.polozajGlave = Integer.parseInt(polozajAsString);
        // System.out.println("Polozaj glave: "+this.polozajGlave);
    }

    private void getPocetnoStanje(String stanje) {
        this.pocetnoStanje = stanje;
        // System.out.println("Prihvatljiva stanja: " + this.pocetnoStanje);

    }

    private void getPrihvatljivaStanja(String prihStanjaWithCommas) {
        this.prihvatljivaStanja = new ArrayList<>();
        String[] temp = prihStanjaWithCommas.split(",");
        this.prihvatljivaStanja = Arrays.asList(temp);
        // System.out.println("Prihvatljiva stanja: " +
        // this.prihvatljivaStanja);
    }

    private void getTraka(String niz) {
        this.traka = new ArrayList<>();
        char[] temp = niz.toCharArray();
        for (char ch : temp) {
            this.traka.add(String.valueOf(ch));
        }
        // System.out.println("Traka: " + this.traka);
    }

    private void getZnakPrazneCelije(String znak) {
        this.znakPrazneCelije = znak;
        // System.out.println("Znak prazne celije: " + this.znakPrazneCelije);
    }

    private void getZnakoviTrake(String ulazniZnakoviWithCommas) {
        this.znakoviTraka = new ArrayList<>();
        String[] temp = ulazniZnakoviWithCommas.split(",");
        this.znakoviTraka = Arrays.asList(temp);
        // System.out.println("Znakovi trake: " + this.znakoviTraka);

    }

    private void getUlazniZnakovi(String ulazniZnakoviWithCommas) {
        this.znakoviUlazni = new ArrayList<>();
        String[] temp = ulazniZnakoviWithCommas.split(",");
        this.znakoviUlazni = Arrays.asList(temp);
        // System.out.println("Ulazni znakovi: " + this.znakoviUlazni);

    }

    private void getStanja(String stanjaWithCommas) {
        this.stanja = new ArrayList<>();
        String[] temp = stanjaWithCommas.split(",");
        this.stanja = Arrays.asList(temp);
        // System.out.println("Stanja: " + this.stanja);
    }

    private class Prijelaz {
        public String trenutnoStanje;
        public String znakNaTraci;
        public String novoStanje;
        public String noviZnak;
        public String pomakGlave;

        public Prijelaz(String stanje, String stariZnak, String novoStanje, String noviZnak, String pomak) {
            this.trenutnoStanje = stanje;
            this.znakNaTraci = stariZnak;
            this.novoStanje = novoStanje;
            this.noviZnak = noviZnak;
            this.pomakGlave = pomak;
        }

        @Override
        public String toString() {
            return this.trenutnoStanje + "," + "" + this.znakNaTraci + "->" + this.novoStanje + "," + this.noviZnak + "," + this.pomakGlave;
        }
    }
}