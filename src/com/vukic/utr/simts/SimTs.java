package com.vukic.utr.simts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimTs {
    private static boolean DEBUG = true;
    private List<String> states;
    private List<String> znakoviUlazni;
    private List<String> tapeSymbols;
    private List<String> tape;
    private String EmptyCellSymbol;
    private List<String> acceptableStates;
    private String initialState;
    private int headPosition;
    private List<Transition> transitions;

    public static void main(String[] args) {
        String testFileNumber = "01";
        String testInputFile = "src/testsSimTs/test"+testFileNumber+"/test.in";
        SimTs se = SimTs.DEBUG ? new SimTs(testInputFile) : new SimTs("");
        se.start();
        if(DEBUG) {
            try {
                String line;
                BufferedReader brr = new BufferedReader(new FileReader(new File("src/testsSimTs/test" + testFileNumber + "/test.out")));
                while ((line = brr.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error while reading from test output file!");
            }
        }
    }

    private void start() {
        Integer glava = this.headPosition;
        String trenutnoStanje = this.initialState;
        boolean noTransitionsFound;
        while (true) {
            noTransitionsFound = true;
            if (glava >= this.tape.size() || glava < 0) {
                System.out.print(trenutnoStanje + "|");
                System.out.print(glava - 1 + "|");
                for (String str : this.tape) {
                    System.out.print(str);
                }
                if (this.acceptableStates.contains(trenutnoStanje)) {
                    System.out.println("|1");
                } else {
                    System.out.println("|0");
                }
                break;// exit while
            }
            String znakTrake = this.tape.get(glava);// ucitaj znak trake
            for (Transition transition : this.transitions) {
                if (transition.currentState.equals(trenutnoStanje) && transition.currentSymbol.equals(znakTrake)) {
                    if (transition.headDirection.equals("L")) {
                        if (glava - 1 < 0) {
                            noTransitionsFound = true;
                            break;
                        }
                    }
                    if (transition.headDirection.equals("R")) {
                        if (glava + 1 >= this.tape.size()) {
                            noTransitionsFound = true;
                            break;
                        }
                    }
                    trenutnoStanje = transition.newState; // promjeni stanje
                    this.tape.set(glava, transition.newSymbol); // zamjeni znak
                    // trake
                    if (transition.headDirection.equals("R")) { // pomakni glavu
                        // System.out.println("Pomicem glavu u desno.");
                        glava++;
                    } else {
                        // System.out.println("Pomicem glavu u lijevo.");
                        glava--;
                    }
                    // System.out.println(this.tape);
                    noTransitionsFound = false;
                    break; // exit for
                }
            }
            if (noTransitionsFound) {
                // System.out.println("Ne postoji prijelaz: ");
                System.out.print(trenutnoStanje + "|");
                System.out.print(glava + "|");
                for (String str : this.tape) {
                    System.out.print(str);
                }
                if (this.acceptableStates.contains(trenutnoStanje)) {
                    System.out.println("|1");
                } else {
                    System.out.println("|0");
                }
                break; // exit while
            }
        }

    }

    public SimTs(String testFileName) {
        List<String> temp = new ArrayList<>();
        String line;
        try {
            BufferedReader reader = SimTs.DEBUG ?
                    new BufferedReader(new FileReader(new File(testFileName)))
                    : new BufferedReader(new InputStreamReader(System.in));
            while ((line = reader.readLine()) != null) {
                temp.add(line);
            }
            ParseStates(temp.get(0));
            getUlazniZnakovi(temp.get(1));
            ParseTapeSymbols(temp.get(2));
            ParseEmptyCellSymbol(temp.get(3));
            ParseTape(temp.get(4));
            ParseAcceptableStates(temp.get(5));
            ParseInitialState(temp.get(6));
            ParseHeadPosition(temp.get(7));
            ParseTransitions(temp);
        } catch (IOException e) {
        }
    }

    private void ParseTransitions(List<String> input) {
        this.transitions = new ArrayList<>();
        input.stream().filter(line -> input.indexOf(line) > 7).forEach(line -> {
            String left = line.split("->")[0];
            String right = line.split("->")[1];
            String[] leftSplit = left.split(",");
            String[] rightSplit = right.split(",");
            this.transitions.add(new Transition(leftSplit[0], leftSplit[1], rightSplit[0], rightSplit[1], rightSplit[2]));
        });

    }

    private void ParseHeadPosition(String positionAsString) {
        this.headPosition = Integer.parseInt(positionAsString);
    }

    private void ParseInitialState(String state) {
        this.initialState = state;

    }

    private void ParseAcceptableStates(String acceptableStatesAsString) {
        this.acceptableStates = new ArrayList<>();
        this.acceptableStates = Arrays.asList(acceptableStatesAsString.split(","));
    }

    private void ParseTape(String tapeAsString) {
        this.tape = new ArrayList<>();
        char[] temp = tapeAsString.toCharArray();
        for (char ch : temp) {
            this.tape.add(String.valueOf(ch));
        }
        System.out.println(tapeAsString);
        tapeAsString.chars().forEach(System.out::println);
    }

    private void ParseEmptyCellSymbol(String symbol) {
        this.EmptyCellSymbol = symbol;
    }

    private void ParseTapeSymbols(String inputSymbolsAsString) {
        this.tapeSymbols = new ArrayList<>();
        this.tapeSymbols = Arrays.asList(inputSymbolsAsString.split(","));

    }

    private void getUlazniZnakovi(String ulazniZnakoviWithCommas) {
        this.znakoviUlazni = new ArrayList<>();
        String[] temp = ulazniZnakoviWithCommas.split(",");
        this.znakoviUlazni = Arrays.asList(temp);

    }

    private void ParseStates(String statesAsString) {
        this.states = new ArrayList<>();
        this.states = Arrays.asList(statesAsString.split(","));
    }

    private class Transition {
        public String currentState;
        public String currentSymbol;
        public String newState;
        public String newSymbol;
        public String headDirection;

        public Transition(String state, String oldsymbol, String newState, String newSymbol, String direction) {
            this.currentState = state;
            this.currentSymbol = oldsymbol;
            this.newState = newState;
            this.newSymbol = newSymbol;
            this.headDirection = direction;
        }

        @Override
        public String toString() {
            return this.currentState + "," + "" + this.currentSymbol + "->" + this.newState + "," + this.newSymbol + "," + this.headDirection;
        }
    }
}