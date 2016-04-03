package com.vukic.utr.mindfsm;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/***
 * @author Matija Vukić 2015
 */
public class MinDFSM {
    public static boolean DEBUG = true;
    private List<String> states;
    private List<String> reachableStates;
    private List<String> alphabet;
    private List<String> finalStates;
    private String initialState;
    private List<Transition> transitions;
    private List<List<String>> listeIstovjetnihStanja;
    private Set<Transition> newTransitions;
    public static List<String> finalOutput;

    public static void main(String[] args) {
        String testFileNumber = "02";
        String testInputFile = "src/testsMinDFSM/test"+testFileNumber+"/t.ul";
        MinDFSM mindfsm = MinDFSM.DEBUG ? new MinDFSM(testInputFile) : new MinDFSM("");
        mindfsm.start();
		 if(DEBUG) {
             boolean matchError=false;
             int i=0;
             try {
                 String line;
                 BufferedReader brr = new BufferedReader(new FileReader(new File("src/testsMinDFSM/test" + testFileNumber + "/t.iz")));
                 System.out.println("OUTPUT\t\t\tCORRECT OUTPUT ");
                 while ((line = brr.readLine()) != null) {
                     if(!(MinDFSM.finalOutput.get(i).equals(line))) matchError=true;
                     System.out.println(MinDFSM.finalOutput.get(i)+"\t<==>\t"+line);
                     i++;
                 }
             } catch (IOException e) {
                 System.out.println("Error");
             }
             System.out.println(matchError ? "Error in result." : "Correct result..");
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
            if(grupa.contains(this.initialState)){
                this.initialState = grupa.get(0);
                break;
            }
        }
    }

    private void eliminirajStanjaIzPrihvatljivih() {
        Set<String> tempStanja = new LinkedHashSet<>();
        for(String stanje:this.finalStates){
            for(List<String> grupa:this.listeIstovjetnihStanja){
                if(grupa.contains(stanje)){
                    tempStanja.add(grupa.get(0));
                    break;
                }
            }
        }
        this.finalStates.clear();
		this.finalStates.addAll(tempStanja.stream().sorted().collect(Collectors.toList()));
    }

    private void eliminirajStanjaIzDohvatljivih() {
        Set<String> tempStanja = new LinkedHashSet<>();
        for(List<String> grupa:this.listeIstovjetnihStanja){
            tempStanja.add(grupa.get(0));
        }
        this.reachableStates.clear();
		this.reachableStates.addAll(tempStanja.stream().sorted().collect(Collectors.toList()));
    }

    private void ispisRezultata() {
        MinDFSM.finalOutput = new ArrayList<>();
        MinDFSM.finalOutput.add(spojiListuSaZarezima(this.reachableStates));
        MinDFSM.finalOutput.add(spojiListuSaZarezima(this.alphabet));
        MinDFSM.finalOutput.add(spojiListuSaZarezima(this.finalStates));
        MinDFSM.finalOutput.add(this.initialState);
        System.out.println(this.initialState);
        for(Transition p:this.newTransitions){
            MinDFSM.finalOutput.add(p.toString());
            System.out.println(p);
        }
    }
    private String spojiListuSaZarezima(List<String> lista){
        String joined = "";
        if(lista.size() == 1){
            System.out.println(lista.get(0));
            MinDFSM.finalOutput.add(lista.get(0));
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
        this.newTransitions = new LinkedHashSet<>();
        for(Transition prijelaz:this.transitions){
//			System.out.println("Prijelaz: "+prijelaz);
            Transition noviPrijelaz = new Transition(prijelaz.state,prijelaz.symbol,prijelaz.nextState);
            for(List<String> grupa:this.listeIstovjetnihStanja){
                if(grupa.contains(prijelaz.state)){
//					System.out.println("Stanje "+prijelaz.stanje+" je u grupi: "+grupa);
//					System.out.println("Mjenjam stanje "+prijelaz.stanje+" sa "+grupa.get(0));
                    noviPrijelaz.state = grupa.get(0);
                    break;
                }
            }
            for(List<String> grupa:this.listeIstovjetnihStanja){
                if(grupa.contains(prijelaz.nextState)){
                    noviPrijelaz.nextState = grupa.get(0);
                    break;
                }
            }
            newTransitions.add(noviPrijelaz);
        }
    }

    private void minimizirajDka() {
        List<String> prihStanja = new ArrayList<>(this.finalStates);
        List<String> neprihStanja = new ArrayList<>();
        List<List<String>> grupe = new ArrayList<>();
        // razdvoji prihvtljiva i neprihvatljiva stanja u liste
        for (String stanje : reachableStates) {
            if (!(this.finalStates.contains(stanje))) {
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
        for (String znak : this.alphabet) {
            Transition prijelaz1 = getPrijelazZaZnakIStanje(stanje1, znak);
            Transition prijelaz2 = getPrijelazZaZnakIStanje(stanje2, znak);
            for (List<String> grupa : grupe) {
                if (grupa.contains(prijelaz1.nextState) && grupa.contains(prijelaz2.nextState)) {
                    counter++;
                }
            }
        }
        if (counter == this.alphabet.size()) {
            return true;
        } else {
            return false;
        }
    }

    // Returns transition for given state and sign

    private Transition getPrijelazZaZnakIStanje(String stanje, String znak) {
        Transition temp = null;
        for (Transition prijelaz : this.transitions) {
            if (prijelaz.state.equals(stanje) && prijelaz.symbol.equals(znak)) {
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
        List<Transition> tempPrijelazi = new ArrayList<>();
        for (Transition prijelaz : this.transitions) {
            if (this.reachableStates.contains(prijelaz.state)) {
                tempPrijelazi.add(prijelaz);
            }
        }
        this.transitions.clear();
        this.transitions.addAll(tempPrijelazi);
        //makni nedohvatljiv stanja iz prihvatljivih stanja
        List<String> tempPrihStanja = new ArrayList<>();
        for (String stanje : this.finalStates) {
            if (this.reachableStates.contains(stanje)) {
                tempPrihStanja.add(stanje);
            }
        }
        this.finalStates.clear();
        this.finalStates.addAll(tempPrihStanja);
    }

    /**
     * Removes unreachable states
     */
    private void makniNedohvatljivaStanja() {
        Set<String> dohvatljivaStanja = new LinkedHashSet<>();
        List<String> svaStanjaIzTrenutnog;
        this.reachableStates = new ArrayList<>();
        dohvatljivaStanja.add(this.initialState);

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
        this.reachableStates.addAll(sorted);
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
        for (Transition prijelaz : this.transitions) {
            if (prijelaz.state.equals(stanje)) {
                stanjaUKojaMozeOvoStanje.add(prijelaz.nextState);
            }
        }
        List<String> lst = new ArrayList<>(stanjaUKojaMozeOvoStanje);
        return lst;
    }

    public MinDFSM(String fileInputString) {
        List<String> temp = new ArrayList<>(); // Lines from file
        String line;
        try {
            BufferedReader br = MinDFSM.DEBUG ? new BufferedReader(new FileReader(new File(fileInputString))):new BufferedReader(new InputStreamReader(System.in));
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

    public class Transition{
        public String state;
        public String symbol;
        public String nextState;

        public Transition(String state, String symbol, String nextState) {
            this.state = state;
            this.symbol = symbol;
            this.nextState = nextState;
        }

        @Override
        public String toString() {
            return (state + "," + symbol + "->" + nextState);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((state == null) ? 0 : state.hashCode());
            result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
            Transition other = (Transition) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (state == null) {
                if (other.state != null)
                    return false;
            } else if (!state.equals(other.state))
                return false;
            if (symbol == null) {
                if (other.symbol != null)
                    return false;
            } else if (!symbol.equals(other.symbol))
                return false;
            return true;
        }

        private MinDFSM getOuterType() {
            return MinDFSM.this;
        }

    }

    private void getPrijelazi(List<String> prijelaziAsLines) {
        this.transitions = new ArrayList<>();
        // System.out.println("Prijelazi: ");
        for (String line : prijelaziAsLines) {
            if (prijelaziAsLines.indexOf(line) >= 4) {
                String left = (line.split("->"))[0];
                String right = (line.split("->"))[1];
                String[] leftSplit = left.split(",");
                Transition prijelaz = new Transition(leftSplit[0], leftSplit[1], right);
                this.transitions.add(prijelaz);
                // System.out.println("     "+prijelaz.toString());
            }
        }
    }

    private void getPocetnoStanje(String stanje) {
        this.initialState = stanje;
    }

    private void getPrihvatljivaStanja(String prihStanjaAsString) {
        this.finalStates = new ArrayList<>();
        this.finalStates.addAll(Arrays.asList(prihStanjaAsString.split(",")));
        // System.out.println("Prihvatljiva: "+this.prihStanja);
    }

    private void getAbeceda(String abecedaSaString) {
        this.alphabet = new ArrayList<>();
        this.alphabet = Arrays.asList(abecedaSaString.split(","));
        // System.out.println("Abeceda: "+this.abeceda);
    }

    private void getUlaznaStanja(String stanjaAsString) {
        this.states = new ArrayList<>();
        this.states = Arrays.asList(stanjaAsString.split(","));
        // System.out.println("Ul. stanja: "+this.ulaznaStanja);
    }

}