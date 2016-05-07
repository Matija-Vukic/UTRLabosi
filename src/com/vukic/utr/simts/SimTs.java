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
    private List<String> alphabet;
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
        Integer head = this.headPosition;
        String currentState = this.initialState;
        boolean noTransitionsFound;
        while (true) {
            noTransitionsFound = true;
            if (head >= this.tape.size() || head < 0) {
                PrintResult(currentState,head);
                break;
            }
            String tapeSymbol = this.tape.get(head);// read from tape
            for (Transition transition : this.transitions) {
                if (transition.currentState.equals(currentState) && transition.currentSymbol.equals(tapeSymbol)) {
                    if (transition.headDirection.equals("L")) {
                        if (head - 1 < 0) {
                            noTransitionsFound = true;
                            break;
                        }
                    }
                    if (transition.headDirection.equals("R")) {
                        if (head + 1 >= this.tape.size()) {
                            noTransitionsFound = true;
                            break;
                        }
                    }
                    currentState = transition.newState; // change state
                    this.tape.set(head, transition.newSymbol); // replace symbol on tape
                    if (transition.headDirection.equals("R")) { // Move head
                        head++;
                    } else {
                        head--;
                    }
                    noTransitionsFound = false;
                    break;
                }
            }
            if (noTransitionsFound) {
                PrintResult(currentState,head);
                break;
            }
        }

    }
    private void PrintResult(String currentState,Integer head){
        System.out.print(currentState + "|");
        System.out.print(head + "|");
        this.tape.forEach(System.out::print);
        if (this.acceptableStates.contains(currentState)) {
            System.out.println("|1");
        } else {
            System.out.println("|0");
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
            ParseAlphabet(temp.get(1));
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
    }

    private void ParseEmptyCellSymbol(String symbol) {
        this.EmptyCellSymbol = symbol;
    }

    private void ParseTapeSymbols(String inputSymbolsAsString) {
        this.tapeSymbols = new ArrayList<>();
        this.tapeSymbols = Arrays.asList(inputSymbolsAsString.split(","));

    }

    private void ParseAlphabet(String AlphabetWithCommas) {
        this.alphabet = new ArrayList<>();
        this.alphabet = Arrays.asList(AlphabetWithCommas.split(","));

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