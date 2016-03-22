package com.vukic.utr.mindka;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MinDKA {
    public static boolean DEBUG = true;
    private List<String> ulaznaStanja;
    private List<String> dohvatljivaStanja;
    private List<String> abeceda;
    private List<String> prihStanja;
    private String pocStanje;
    private List<Prijelaz> prijelazi;
    private List<List<String>> listeIstovjetnihStanja;
    private Set<Prijelaz> noviPrijelazi;
    public static List<String> listaZaUsporedbu;

    public static void main(String[] args) {
        String broj = "02";
        String fileInputName = "src/testsMinDKA/test"+broj+"/t.ul";
        MinDKA se;
        if(!DEBUG) se = new MinDKA("");
		else se = new MinDKA(fileInputName);
        se.start();
		 if(DEBUG) {
             boolean kriviRezultat=false;
             int i=0;
             try {
                 String line;
                 BufferedReader brr = new BufferedReader(new FileReader(new File("src/testsMinDKA/test" + broj + "/t.iz")));
                 System.out.println("IZLAZ\t\t\tDATOTEKA ");
                 while ((line = brr.readLine()) != null) {
                     if(!(MinDKA.listaZaUsporedbu.get(i).equals(line))) kriviRezultat=true;
                     System.out.println(MinDKA.listaZaUsporedbu.get(i)+"  <==>  "+line);
                     i++;
                 }
             } catch (IOException e) {
                 System.out.println("Error");
             }
             System.out.println(kriviRezultat ? "Greska u rezultatu." : "Rezultat je točan.");
         }
    }

    public void start() {
        makniNedohvatljivaStanja();
        makniNepotrebnePrijelaze();
        minimizirajDka();
        makniPrijelazeSaIstimStanjima();
        eliminirajStanjaIzDohvatljivih();
        eliminirajStanjaIzPrihvatljivih();
        srediPocetnoStanje();
        ispisRezultata();
    }

    private void srediPocetnoStanje() {
        for(List<String> grupa: this.listeIstovjetnihStanja){
            if(grupa.contains(this.pocStanje)){
                this.pocStanje = grupa.get(0);
                break;
            }
        }
    }

    private void eliminirajStanjaIzPrihvatljivih() {
        Set<String> tempStanja = new LinkedHashSet<>();
        for(String stanje:this.prihStanja){
            for(List<String> grupa:this.listeIstovjetnihStanja){
                if(grupa.contains(stanje)){
                    tempStanja.add(grupa.get(0));
                    break;
                }
            }
        }
        this.prihStanja.clear();
//		this.prihStanja.addAll(tempStanja.stream().sorted().collect(Collectors.toList()));
        List<String> sorted = new ArrayList<>();
        sorted.addAll(tempStanja);
        Collections.sort(sorted);
        this.prihStanja.addAll(sorted);
    }

    private void eliminirajStanjaIzDohvatljivih() {
        Set<String> tempStanja = new LinkedHashSet<>();
        for(List<String> grupa:this.listeIstovjetnihStanja){
            tempStanja.add(grupa.get(0));
        }
        this.dohvatljivaStanja.clear();
//		this.dohvatljivaStanja.addAll(tempStanja.stream().sorted().collect(Collectors.toList()));
        List<String> sorted = new ArrayList<>();
        sorted.addAll(tempStanja);
        Collections.sort(sorted);
        this.dohvatljivaStanja.addAll(sorted);

    }

    private void ispisRezultata() {
        MinDKA.listaZaUsporedbu = new ArrayList<>();
        MinDKA.listaZaUsporedbu.add(spojiListuSaZarezima(this.dohvatljivaStanja));
        MinDKA.listaZaUsporedbu.add(spojiListuSaZarezima(this.abeceda));
        MinDKA.listaZaUsporedbu.add(spojiListuSaZarezima(this.prihStanja));
        MinDKA.listaZaUsporedbu.add(this.pocStanje);
        System.out.println(this.pocStanje);
        for(Prijelaz p:this.noviPrijelazi){
            MinDKA.listaZaUsporedbu.add(p.toString());
            System.out.println(p);
        }
    }
    private String spojiListuSaZarezima(List<String> lista){
        String joined = "";
        if(lista.size() == 1){
            System.out.println(lista.get(0));
            return lista.get(0);
        }else{
            List<String> joinedList = new ArrayList<>(lista);
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<joinedList.size();i++){
                sb.append(joinedList.get(i));
                if(i <joinedList.size()-1){
                    sb.append(",");
                }
            }
            joined = sb.toString();
            System.out.println(joined);
        }
        return joined;
    }

    private void makniPrijelazeSaIstimStanjima() {
        this.noviPrijelazi = new LinkedHashSet<>();
        for(Prijelaz prijelaz:this.prijelazi){
//			System.out.println("Prijelaz: "+prijelaz);
            Prijelaz noviPrijelaz = new Prijelaz(prijelaz.stanje,prijelaz.znak,prijelaz.sljedStanje);
            for(List<String> grupa:this.listeIstovjetnihStanja){
                if(grupa.contains(prijelaz.stanje)){
//					System.out.println("Stanje "+prijelaz.stanje+" je u grupi: "+grupa);
//					System.out.println("Mjenjam stanje "+prijelaz.stanje+" sa "+grupa.get(0));
                    noviPrijelaz.stanje = grupa.get(0);
                    break;
                }
            }
//			System.out.println("Novi prijelaz: "+noviPrijelaz);
            for(List<String> grupa:this.listeIstovjetnihStanja){
                if(grupa.contains(prijelaz.sljedStanje)){
//					System.out.println("Stanje "+prijelaz.sljedStanje+" je u grupi: "+grupa);
//					System.out.println("Mjenjam stanje "+prijelaz.sljedStanje+" sa "+grupa.get(0));
                    noviPrijelaz.sljedStanje = grupa.get(0);
                    break;
                }
            }
//			System.out.println("Novi prijelaz: "+noviPrijelaz);
            noviPrijelazi.add(noviPrijelaz);
//			noviPrijelazi.stream().forEach(p -> System.out.println("   "+p));
        }
    }

    private void minimizirajDka() {
        List<String> prihStanja = new ArrayList<>(this.prihStanja);
        List<String> neprihStanja = new ArrayList<>();
        List<List<String>> grupe = new ArrayList<>();
        // razdvoji prihvtljiva i neprihvatljiva stanja u liste
        for (String stanje : dohvatljivaStanja) {
            if (!(this.prihStanja.contains(stanje))) {
                neprihStanja.add(stanje);
            }
        }
        //provjeri ako je neka od listi prazna
        if(prihStanja.isEmpty() | neprihStanja.isEmpty()){
            if(!(prihStanja.isEmpty())){
                grupe.add(prihStanja);
            }
            if(!(neprihStanja.isEmpty())){
                grupe.add(neprihStanja);
            }
        }else{
            grupe.add(prihStanja);
            grupe.add(neprihStanja);
        }
        List<List<String>> noveGrupe = new ArrayList<>(grupe);
//		System.out.println("Prihvatljiva stanja: " + prihStanja);
//		System.out.println("Neprihvatljiva stajna: " + neprihStanja);
        while (true) {
            grupe.clear(); // ocisti buffer za nove grupe
            grupe.addAll(noveGrupe);// ponovi sve za nove grupe
            noveGrupe.clear();
            for (List<String> grupa : grupe) { // za svaku trenutnu grupu
                List<String> novaGrupa = new ArrayList<>();
                String stanje1 = grupa.get(0); // uzmi prvo stanje u grupi
                for (String stanje2 : grupa) { // usporeduj ga sa svakim drugim
                    // stanjem u grupi
                    if (!(stanje1.equals(stanje2))) { // ako su stanja razlicita
                        //provjeri ako stanja idu u stanja koja su u istim grupama
                        boolean check = ChechIfTheyTransitionToSameGroup(stanje1, stanje2, grupe);
                        if (!check) { // ako ne prelaze
                            // nova grupa koja sadrzi stanja koja se ne slazu s pocetnim
                            novaGrupa.add(stanje2);
                        }
                    }
                }
                if (novaGrupa.size() > 0) {
//					System.out.println("Nova grupa: " + novaGrupa);
                    List<String> temp = new ArrayList<>(removeStatesFromListInAnotherList(grupa, novaGrupa));
//					System.out.println("Stara grupa: " + grupa);
                    noveGrupe.add(new ArrayList<>(temp));
                    noveGrupe.add(new ArrayList<>(novaGrupa));
//					System.out.println("Nova lista grupa: " + noveGrupe);
//					System.out.println("Stare grupe: " + grupe);
                } else {
//					System.out.println("Nije bilo promjene unutar grupe!");
//					System.out.println("Nova lista grupa: " + noveGrupe);
//					System.out.println("Stare grupe: " + grupe);
                    noveGrupe.add(grupa);
                }

            }// after this repeat check if there were made any new groups
            if (grupe.size() == noveGrupe.size()) {
                //if not then break
                break;
            }
        }
//		System.out.println("\nPronadena istovjetna stanja:");
        this.listeIstovjetnihStanja = new ArrayList<>(grupe);
//		System.out.println("Grupe: "+this.listeIstovjetnihStanja);
    }

    private List<String> removeStatesFromListInAnotherList(List<String> grupa, List<String> novaGrupa) {
        List<String> temp = new ArrayList<>();
        for (String stanje : grupa) {
            if (!(novaGrupa.contains(stanje))) {
                temp.add(stanje);
            }
        }
        return temp;
    }

    /**
     * checks if transitions for given states and symbols lead to states that
     * are in same groups
     */
    private boolean ChechIfTheyTransitionToSameGroup(String stanje1, String stanje2, List<List<String>> grupe) {
        int counter = 0;
        for (String znak : this.abeceda) {
            Prijelaz prijelaz1 = getPrijelazZaZnakIStanje(stanje1, znak);
            Prijelaz prijelaz2 = getPrijelazZaZnakIStanje(stanje2, znak);
            for (List<String> grupa : grupe) {
                if (grupa.contains(prijelaz1.sljedStanje) && grupa.contains(prijelaz2.sljedStanje)) {
                    counter++;
                }
            }
        }
        if (counter == this.abeceda.size()) {
            return true;
        } else {
            return false;
        }
    }

    // Returns transition for given state and sign

    private Prijelaz getPrijelazZaZnakIStanje(String stanje, String znak) {
        Prijelaz temp = null;
        for (Prijelaz prijelaz : this.prijelazi) {
            if (prijelaz.stanje.equals(stanje) && prijelaz.znak.equals(znak)) {
                temp = prijelaz;
                break;
            }
        }
        return temp;
    }

    /**
     * Removes unwanted transitions and removes unreachable states from final
     * states
     */
    private void makniNepotrebnePrijelaze() {
        //makni nedohvatljiva stanja iz prijelaza
        List<Prijelaz> tempPrijelazi = new ArrayList<>();
        for (Prijelaz prijelaz : this.prijelazi) {
            if (this.dohvatljivaStanja.contains(prijelaz.stanje)) {
                tempPrijelazi.add(prijelaz);
            }
        }
        this.prijelazi.clear();
        this.prijelazi.addAll(tempPrijelazi);
        //makni nedohvatljiv stanja iz prihvatljivih stanja
        List<String> tempPrihStanja = new ArrayList<>();
        for (String stanje : this.prihStanja) {
            if (this.dohvatljivaStanja.contains(stanje)) {
                tempPrihStanja.add(stanje);
            }
        }
        this.prihStanja.clear();
        this.prihStanja.addAll(tempPrihStanja);
    }

    /**
     * Removes unreachable states
     */
    private void makniNedohvatljivaStanja() {
        Set<String> dohvatljivaStanja = new LinkedHashSet<>();
        List<String> svaStanjaIzTrenutnog;
        this.dohvatljivaStanja = new ArrayList<>();
        dohvatljivaStanja.add(this.pocStanje);

        while (true) {
            // while we can found new reachable state
            Set<String> novaDohvatljivaStanja = new LinkedHashSet<>();
            // for every currently reachable state
            for (String stanje : dohvatljivaStanja) {
                // find all reachable states for current state
                svaStanjaIzTrenutnog = FindDohvatljivaStanja(stanje);
//				 System.out.println("Dohvatljiva stanje iz "+stanje+" su: "+svaStanjaIzTrenutnog);
                // add new states to set
                novaDohvatljivaStanja.addAll(svaStanjaIzTrenutnog);
//				 System.out.println("Nova Dohvatljiva stanja: "+novaDohvatljivaStanja);
            }
            // if no new reachable states were found break while loop
            if (dohvatljivaStanja.containsAll(novaDohvatljivaStanja)) {
                break;
            }
            // else populate list with reachable states with new reachable
            // states
            dohvatljivaStanja.addAll(novaDohvatljivaStanja);
//			System.out.println("Nova dohvatljiva stanja za sva trenutno dohvatljiva stanja: "+dohvatljivaStanja+"\n");
        }
        // save reachable states to class variable and sort them alphabetically
        List<String> sorted = new ArrayList<>();
        sorted.addAll(dohvatljivaStanja);
        Collections.sort(sorted);
        this.dohvatljivaStanja.addAll(sorted);
//		 System.out.println("Dohvatljiva stanja: "+this.dohvatljivaStanja);
    }

    /**
     * Finds all reachable states for current state.
     *
     * @param stanje
     * @return
     */
    private List<String> FindDohvatljivaStanja(String stanje) {
        Set<String> stanjaUKojaMozeOvoStanje = new LinkedHashSet<>();
        for (Prijelaz prijelaz : this.prijelazi) {
            if (prijelaz.stanje.equals(stanje)) {
                stanjaUKojaMozeOvoStanje.add(prijelaz.sljedStanje);
            }
        }
        List<String> lst = new ArrayList<>(stanjaUKojaMozeOvoStanje);
        return lst;
    }

    public MinDKA(String fileInputString) {
        List<String> temp = new ArrayList<>(); // Lines from file
        String line;
        try {
            BufferedReader br;
            if(!MinDKA.DEBUG)  br = new BufferedReader(new InputStreamReader(System.in));
            else               br = new BufferedReader(new FileReader(new File(fileInputString)));
            while ((line = br.readLine()) != null) {
                temp.add(line);
            }
            getUlaznaStanja(temp.get(0));
            getAbeceda(temp.get(1));
            getPrihvatljivaStanja(temp.get(2));
            getPocetnoStanje(temp.get(3));
            getPrijelazi(temp);
        } catch (IOException e) {
        }
    }

    public class Prijelaz{
        public String stanje;
        public String znak;
        public String sljedStanje;

        public Prijelaz(String a, String b, String c) {
            stanje = a;
            znak = b;
            sljedStanje = c;
        }

        @Override
        public String toString() {
            return (stanje + "," + znak + "->" + sljedStanje);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((stanje == null) ? 0 : stanje.hashCode());
            result = prime * result + ((znak == null) ? 0 : znak.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Prijelaz other = (Prijelaz) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (stanje == null) {
                if (other.stanje != null)
                    return false;
            } else if (!stanje.equals(other.stanje))
                return false;
            if (znak == null) {
                if (other.znak != null)
                    return false;
            } else if (!znak.equals(other.znak))
                return false;
            return true;
        }

        private MinDKA getOuterType() {
            return MinDKA.this;
        }

    }

    private void getPrijelazi(List<String> prijelaziAsLines) {
        this.prijelazi = new ArrayList<>();
        // System.out.println("Prijelazi: ");
        for (String line : prijelaziAsLines) {
            if (prijelaziAsLines.indexOf(line) >= 4) {
                String left = (line.split("->"))[0];
                String right = (line.split("->"))[1];
                String[] leftSplit = left.split(",");
                Prijelaz prijelaz = new Prijelaz(leftSplit[0], leftSplit[1], right);
                this.prijelazi.add(prijelaz);
                // System.out.println("     "+prijelaz.toString());
            }
        }
    }

    private void getPocetnoStanje(String stanje) {
        this.pocStanje = stanje;
    }

    private void getPrihvatljivaStanja(String prihStanjaAsString) {
        this.prihStanja = new ArrayList<>();
        this.prihStanja.addAll(Arrays.asList(prihStanjaAsString.split(",")));
        // System.out.println("Prihvatljiva: "+this.prihStanja);
    }

    private void getAbeceda(String abecedaSaString) {
        this.abeceda = new ArrayList<>();
        this.abeceda = Arrays.asList(abecedaSaString.split(","));
        // System.out.println("Abeceda: "+this.abeceda);
    }

    private void getUlaznaStanja(String stanjaAsString) {
        this.ulaznaStanja = new ArrayList<>();
        this.ulaznaStanja = Arrays.asList(stanjaAsString.split(","));
        // System.out.println("Ul. stanja: "+this.ulaznaStanja);
    }

}