package com.vukic.utr.mindfsm;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/***
 * @author Matija Vukić 2016
 */
public class MinDFSM {
    public static boolean DEBUG = true;
    private List<String> states;
    private List<String> reachableStates;
    private List<String> alphabet;
    private List<String> finalStates;
    private String initialState;
    private List<Transition> transitions;
    private List<List<String>> listOfEquivalentStates;
    private Set<Transition> newTransitions;
    public static List<String> finalOutput;

    public static void main(String[] args) {
        String testFileNumber = "06";
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
                     System.out.println(MinDFSM.finalOutput.get(i)+"\t\t<==>\t"+line);
                     i++;
                 }
             } catch (IOException e) {
                 System.out.println("Error");
             }
             System.out.println(matchError ? "Error in result." : "Correct result..");
         }
    }

    public void start() {
        RemoveUnreachableStates();
        RemoveRedundantTransitionsAndUnreachableStates();
        MinimizeDFSM();
        RemoveEquivalentTransitions();
        RemoveEquivalentStatesFromReachable();
        RemoveEquivalentStatesFromAcceptable();
        FixInitialState();
        PrintResult();
    }

    /***
     * Swaps initial state with the one that represents him in his group
     * of equivalent states.
     */
    private void FixInitialState() {
        this.initialState = this.listOfEquivalentStates.stream()
                .filter(g->g.contains(this.initialState))
                .map(g->g.get(0))
                .findFirst()
                .get();
    }

    /***
     * Removes equivalent states from acceptable states and leaves only one
     * state that represents that group of equivalent states.
     */
    private void RemoveEquivalentStatesFromAcceptable() {
        Set<String> temp = new LinkedHashSet<>();
        for(String state:this.finalStates){
            for(List<String> group:this.listOfEquivalentStates){
                if(group.contains(state)){
                    temp.add(group.get(0));
                    break;
                }
            }
        }
        this.finalStates.clear();
		this.finalStates.addAll(temp.stream()
                .sorted()
                .collect(Collectors.toList()));
    }

    /***
     * From every group of equivalent states removes all states but the first
     * because first state represents all the other sates.
     */
    private void RemoveEquivalentStatesFromReachable() {
        this.reachableStates = this.listOfEquivalentStates.stream()
                .map(g -> g.get(0)) // Get first state from group of states
                .sorted()   //sort states alphabetically
                .collect(Collectors.toList());
    }

    private void PrintResult() {
        MinDFSM.finalOutput = new ArrayList<>();
        MinDFSM.finalOutput.add(String.join(",",this.reachableStates));
        MinDFSM.finalOutput.add(String.join(",",this.alphabet));
        MinDFSM.finalOutput.add(String.join(",",this.finalStates));
        MinDFSM.finalOutput.add(this.initialState);
        for(Transition p:this.newTransitions){
            MinDFSM.finalOutput.add(p.toString());
        }
        if(!MinDFSM.DEBUG){
            MinDFSM.finalOutput.stream().forEach(System.out::println);
        }
    }

    /***
     * Removes transitions that have the same state and next state.
     * Leaves only transition that for those states has states that are first in groups
     * in which those states were found.
     */
    private void RemoveEquivalentTransitions() {
        this.newTransitions = new LinkedHashSet<>();
        for(Transition trans:this.transitions){
            Transition newTransition = new Transition(trans.state,trans.symbol,trans.nextState);
            newTransition.state = this.listOfEquivalentStates.stream()
                    .filter(g->g.contains(trans.state)) // Find group that contains state
                    .map(g->g.get(0)) // Get first state in that group
                    .findFirst()
                    .get();
            newTransition.nextState = this.listOfEquivalentStates.stream()
                    .filter(g->g.contains(trans.nextState)) // Find group that contains state
                    .map(g->g.get(0)) // Get first state in that group
                    .findFirst()
                    .get();
            newTransitions.add(newTransition);
        }
    }

    /***
     * Uses Hopcroft's algorithm for minimization.
     * Link: https://en.wikipedia.org/wiki/DFA_minimization#Hopcroft.27s_algorithm
     */
    private void MinimizeDFSM() {
        List<String> acceptableStates = new ArrayList<>(this.finalStates);
        List<String> unAcceptableStates;
        // List groups will contain lists of states that are equivalent.
        List<List<String>> groups = new ArrayList<>();
        unAcceptableStates = this.reachableStates.stream()
                .filter(s -> !this.finalStates.contains(s))
                .collect(Collectors.toList());
        if(acceptableStates.isEmpty() | unAcceptableStates.isEmpty()){
            // If we have some acceptable sates add them all to group
            if(!(acceptableStates.isEmpty())) groups.add(acceptableStates);
            // If we have unacceptable states add them all to group
            if(!(unAcceptableStates.isEmpty())) groups.add(unAcceptableStates);
        }else{
            // Creates two groups of states and adds them to groups
            groups.add(acceptableStates);
            groups.add(unAcceptableStates);
        }
        List<List<String>> newGroups = new ArrayList<>(groups);
        while (true) {
            groups.clear(); //Clear old groups.
            groups.addAll(newGroups); // Add new groups.
            newGroups.clear();
            for (List<String> group : groups) { // For every group of states.
                List<String> newGroup = new ArrayList<>();
                String s1 = group.get(0); // Take first state in group
                // Add all states from group to new group that don't transition to the same group as first state
                newGroup.addAll(group.stream()
                        .filter(s -> !((s1.equals(s)) | CheckTransition(s1,s,groups)))
                        .collect(Collectors.toList()));
                // If there were states that have different transition group
                if (newGroup.size() > 0) {
                    newGroups.add(findDifferences(group, newGroup));
                    newGroups.add(newGroup);
                } else {
                    newGroups.add(group);
                }

            }
            // If there were any new groups repeat process for them, else break
            if (groups.size() == newGroups.size()) break;

        }
        this.listOfEquivalentStates = groups;
    }

    /**
     * Returns list of states that are in list group but not in list newGroup.
     * @param group
     * @param newGroup
     * @return
     */
    private List<String> findDifferences(List<String> group, List<String> newGroup) {
        return group.stream()
                .filter(s -> !newGroup.contains(s))
                .collect(Collectors.toList());
    }

    /**
     * Checks if transitions for given states and symbols lead to states that
     * are in same groups.
     */
    private boolean CheckTransition(String state1, String state2, List<List<String>> groups) {
        int counter = 0;
        for (String symbol : this.alphabet) {
            Transition t1 = GetTransition(state1, symbol);
            Transition t2 = GetTransition(state2, symbol);
            counter += groups.stream()
                    .filter(g -> g.contains(t1.nextState) && g.contains(t2.nextState))
                    .collect(Collectors.toList())
                    .size();
        }
        if (counter == this.alphabet.size()) {
            return true;
        } else {
            return false;
        }
    }

    /***
     *  Returns transition for specific state and symbol.
     * @param state
     * @param symbol
     * @return
     */
    private Transition GetTransition(String state, String symbol) {
        return this.transitions.stream()
                .filter(t -> t.state.equals(state) && t.symbol.equals(symbol))
                .findAny()
                .orElse(null);
    }

    /**
     * Removes redundant transitions and removes unreachable states from final
     * states. Redundant transitions are the ones that have unreachable state
     * as current state.
     */
    private void RemoveRedundantTransitionsAndUnreachableStates() {
        // Remove redundant transitions
        List<Transition> temp1 = this.transitions.stream()
                .filter(t -> this.reachableStates.contains(t.state))
                .collect(Collectors.toList());
        this.transitions.clear();
        this.transitions.addAll(temp1);
        // Remove unreachable sates
        List<String> temp2 = this.finalStates.stream()
                .filter(s -> this.reachableStates.contains(s))
                .collect(Collectors.toList());
        this.finalStates.clear();
        this.finalStates.addAll(temp2);
    }

    /**
     * Removes unreachable states. For every initial state goes trough transitions and finds
     * all transitions that have that state as starting state. New reachable states are saved
     * to list and compared to starting list of states.If there was no change that means we
     * found all reachable states.
     */
    private void RemoveUnreachableStates() {
        Set<String> reachableStates = new LinkedHashSet<>();
        List<String> reachableStatesFromCurrentState;
        this.reachableStates = new ArrayList<>();
        reachableStates.add(this.initialState);

        while (true) {
            // while we can found new reachable state
            Set<String> newReachableStates = new LinkedHashSet<>();
            // for every currently reachable state
            for (String state : reachableStates) {
                // find all reachable states
                reachableStatesFromCurrentState = FindReachableStates(state);
                // add new states to set (removes duplicates)
                newReachableStates.addAll(reachableStatesFromCurrentState);
            }
            // if no new reachable states were found break loop
            if (reachableStates.containsAll(newReachableStates)) {
                break;
            }
            // else populate list with reachable states with new reachable
            // states
            reachableStates.addAll(newReachableStates);
        }
        this.reachableStates.addAll(reachableStates.stream().sorted().collect(Collectors.toList()));
    }

    /**
     * Finds all reachable states for current state.
     *
     * @param stanje
     * @return
     */
    private List<String> FindReachableStates(String stanje) {
        return new ArrayList<>(
                this.transitions.stream()
                .filter(t->t.state.equals(stanje))
                .map(t->t.nextState)
                .collect(Collectors.toSet())
        );
    }

    public MinDFSM(String fileInputString) {
        List<String> temp = new ArrayList<>(); // Lines from file
        String line;
        try {
            BufferedReader br = MinDFSM.DEBUG ? new BufferedReader(new FileReader(new File(fileInputString))):new BufferedReader(new InputStreamReader(System.in));
            while ((line = br.readLine()) != null) {
                temp.add(line);
            }
            ParseStates(temp.get(0));
            ParseAlphabet(temp.get(1));
            ParseAcceptableStates(temp.get(2));
            ParseInitialState(temp.get(3));
            ParseTransitions(temp);
        } catch (IOException e) {
            System.err.println("Error when reading from test file.");
            System.out.println(e);
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

    private void ParseTransitions(List<String> TranstionsAsString) {
        this.transitions = new ArrayList<>();
        TranstionsAsString.stream()
                .filter(line -> TranstionsAsString.indexOf(line) >= 4)
                .forEachOrdered(line -> {
                    String left = (line.split("->"))[0];
                    String right = (line.split("->"))[1];
                    String[] leftSplit = left.split(",");
                    this.transitions.add(new Transition(leftSplit[0], leftSplit[1], right));
                    });
    }

    /***
     * Sets initial state.
     * @param state
     */
    private void ParseInitialState(String state) {
        this.initialState = state;
    }

    /***
     * Sets acceptable states.
     * @param AcceptableStatesAsString
     */
    private void ParseAcceptableStates(String AcceptableStatesAsString) {
        this.finalStates = new ArrayList<>();
        this.finalStates.addAll(Arrays.asList(AcceptableStatesAsString.split(",")));
    }

    /***
     * Sets alphabet.
     * @param AlphabetAsString
     */
    private void ParseAlphabet(String AlphabetAsString) {
        this.alphabet = new ArrayList<>();
        this.alphabet = Arrays.asList(AlphabetAsString.split(","));
    }

    /***
     * Sets states.
     * @param StatesAsString
     */
    private void ParseStates(String StatesAsString) {
        this.states = new ArrayList<>();
        this.states = Arrays.asList(StatesAsString.split(","));
    }

}