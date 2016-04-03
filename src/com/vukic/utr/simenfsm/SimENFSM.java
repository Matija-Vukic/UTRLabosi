package com.vukic.utr.simenfsm;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/***
 * @author Matija Vukić 2015
 */
public class SimENFSM {
    private static boolean DEBUG = true;
    private List<List<String>> inputArrays;
    private List<String> states;
    private List<String> alphabet;
    private List<String> finalStates;
    private List<String> initialStates;
    private List<Transition> transitions;
    private Set<String> currentStates;
    private List<String> linesForProcessing;

    public static void main(String[] args) {
        String testFileNumber = "03"; //See testsSimENFSM folder for details
        String testInputFile = "src/testsSimENFSM/test"+testFileNumber+"/test.a";
        SimENFSM se = SimENFSM.DEBUG ? new SimENFSM(testInputFile) : new SimENFSM("");
        se.startSimulation();
        if(DEBUG) {
            try {
                String line;
                BufferedReader brr = new BufferedReader(new FileReader(new File("src/testsSimENFSM/test" + testFileNumber + "/test.b")));
                while ((line = brr.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error while reading from test output file!");
            }
        }
    }

    /**
     * Starts the process of simulation.
     */
    public void startSimulation() {
        for (List<String> array : this.inputArrays) {
            parseSymbolArray(array);
            resetData();
        }
    }

    public SimENFSM(String testFileName) {
        this.linesForProcessing = new ArrayList<>();
        try {
            BufferedReader reader = SimENFSM.DEBUG ?
                    new BufferedReader(new FileReader(new File(testFileName)))
                    : new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();;
            while (line != null) {
                this.linesForProcessing.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        parseInputArrays();
        parseAllStates();
        parseAlphabet();
        parseFinalStates();
        parseInitialStates();
        parseTransitions();
        this.currentStates = new LinkedHashSet<>(this.initialStates);
    }

    private void resetData() {
        this.currentStates.clear();
        this.currentStates.addAll(this.initialStates);
    }

    /**
     * Parses transitions from string format to classes.
     */
    private void parseTransitions() {
        this.transitions = new ArrayList<>();
        for (String line : this.linesForProcessing) {
            if (this.linesForProcessing.indexOf(line) >= 5) {
                String left = (line.split("->"))[0];
                String right = (line.split("->"))[1];
                String[] leftSplit = left.split(",");
                String[] rightSplit = right.split(",");
                this.transitions.add(new Transition(leftSplit[0], leftSplit[1], rightSplit));
            }
        }
    }

    private void parseInitialStates() {
        this.initialStates = Arrays.asList(this.linesForProcessing.get(4).split(","));
    }

    private void parseFinalStates() {
        this.finalStates = Arrays.asList(this.linesForProcessing.get(3).split(","));
    }

    private void parseAlphabet() {
        this.alphabet = Arrays.asList(this.linesForProcessing.get(2).split(","));
    }

    private void parseInputArrays() {
        this.inputArrays = new ArrayList<>();
        for (String array : this.linesForProcessing.get(0).split("\\|")) {
            this.inputArrays.add(Arrays.asList(array.split(",")));
        }
    }

    private void parseAllStates() {
        this.states = Arrays.asList(this.linesForProcessing.get(1).split(","));
    }

    /**
     * Recursively find new states based on E-Transitions of given setOfStates
     * @param setOfStates
     */
    private void findETransitions(Set<String> setOfStates) {
        Set<String> newStates = new LinkedHashSet<>(setOfStates);
        for (String state : setOfStates) {
            for (Transition tran : this.transitions) {
                if (tran.state.equals(state) && tran.symbol.equals("$")) {
                    newStates.addAll(tran.nextStates);
                    break;
                }
            }
        }
        if (setOfStates.containsAll(newStates)) {
            //Nn new states were found
            this.currentStates.clear();
            this.currentStates.addAll(newStates);
            return;
        } else {
            //New states were found so we have found E-Transitions for them
            findETransitions(newStates);
        }
    }

    /**
     * Goes through list of symbols inputSymbols and finds transitions for every symbol.
     * Final output is written in array outputArray and then the array is concatenated
     * with | .
     * @param inputSymbols
     */
    public void parseSymbolArray(List<String> inputSymbols) {
        Set<String> newStates = new LinkedHashSet<>(this.currentStates);
        List<String> outputArray = new ArrayList<>();
        Boolean found;
        findETransitions(this.currentStates);
        outputArray.add(sortAddCommasAndRemoveHashes());
        outputArray.add("|");

        for (String symbol : inputSymbols) {
            found = false;
            newStates.clear();
            for (String state : this.currentStates) {
                found = false;
                for (Transition trans : this.transitions) {
                    if (trans.state.equals(state) && trans.symbol.equals(symbol)) {
                        newStates.addAll(trans.nextStates);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    //If no new states are found add #
                    newStates.add("#");
                }
            }
            //Finds E-Transitions for new set of states
            findETransitions(newStates);
            if (this.currentStates.size() > 1) {
                outputArray.add(sortAddCommasAndRemoveHashes());
            } else {
                outputArray.addAll(this.currentStates);
            }
            outputArray.add("|");

        }
        outputArray.remove(outputArray.size() - 1);
		outputArray.stream().forEach(s -> System.out.print(s));
        System.out.println();
    }

    /**
     *Sorts current states alphabetically, removes hash and joins everything with comma.
     * @return joined
     */
    private String sortAddCommasAndRemoveHashes() {
        // Sorted to Set because there could be more then one #
		List<String> sorted = this.currentStates.stream().sorted().collect(Collectors.toList());
        this.currentStates.clear();
        if (sorted.contains("#")) {
            if (sorted.size() > 1) sorted.remove("#");
        }
        this.currentStates.addAll(sorted);
        String joined = String.join(",",new ArrayList<>(this.currentStates));
        return joined;
    }

    /**
     * Simple class that describes transition for specific state and specific symbol.
     */
    private class Transition {
        public String state;
        public String symbol;
        public List<String> nextStates;

        public Transition(String a, String b, String[] c) {
            state = a;
            symbol = b;
            nextStates = Arrays.asList(c);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Transition that = (Transition) o;

            if (!state.equals(that.state)) return false;
            return symbol.equals(that.symbol);

        }

        @Override
        public int hashCode() {
            int result = state.hashCode();
            result = 31 * result + symbol.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return state + "," + symbol + "->" + nextStates;
        }
    }
}
