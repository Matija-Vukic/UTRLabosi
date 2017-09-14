package com.mvukic.utr.simdpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Deterministic pushdown automaton (DPA).
 * Acceptance by final state.
 * @author mvukic 2016.
 * TODO: Refactor
 */
public class SimDPA {
    private static boolean DEBUG=true;
    private List<List<String>> InputArrays;
    private List<String> States;
    private List<String> InputSymbols;
    private List<String> SymbolsStack;
    private List<String> AcceptableStates;//Prihvatljiva stanja
    private String initState;//Pocetno stanje automata
    private String initStackState;//Pocetno stanje na stogu
    private List<Transition> transitions;// S(currentState,ulazniZnak,stanjeStoga)->sljedStanje,[sljedSadrzajStoga]
    private List<List<String>> LastResult;

    public static void main(String[] args) {
        String broj = "6";
        String fileInputName = "src/testsSimPa/test"+broj+"/test.in";
        SimDPA se = new SimDPA(fileInputName);
        se.start();
        if(SimDPA.DEBUG) {
            try {
                String line;
                BufferedReader br = new BufferedReader(new FileReader(new File("src/testsSimPa/test" + broj + "/test.out")));
                System.out.println("\nCORRECT OUTPUT: ");
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.err.println("Error: "+e);
            }
        }
    }

    private void start() {
        this.LastResult = new ArrayList<>();
        InputArrays.forEach(this::ParseArray);
        LastResult.stream()
                .map(s -> String.join("",s))
                .forEach(System.out::println);
    }

    // What!
    private void ParseArray(List<String> inputArray) {
        List<String> array = new ArrayList<>(inputArray);
        String currentState = new String(this.initState);
        Stack<String> stack = new Stack<>();
        stack.push(this.initStackState);
        int i=0;
        Boolean found;
        String symbolStack,symbolInput;
        List<String> result = new ArrayList<>(Arrays.asList(currentState,"#",stack.peek(),"|"));
        while(true){
            if(this.AcceptableStates.contains(currentState) && i >= array.size()){
                result.add("1");
                this.LastResult.add(result);
                break;
            }
            //Check if stack is empty
            if(stack.empty()){
                symbolStack="$";
            }else{
                symbolStack=stack.peek();
            }
            if(i < array.size()){
                symbolInput = array.get(i);
            }else{
                symbolInput = "$";
            }
            found=false;
            for(Transition transition :this.transitions){
                if(transition.state.equals(currentState) && transition.symbolStack.equals(symbolStack) && transition.symbolInput.equals(symbolInput)){
                    currentState=transition.nextState;
                    UpdateStack(stack,transition,result);
                    found=true;
                    break;
                }
            }
            if(!found){
                boolean found1=false;
                if(i < array.size()){
                    for(Transition transition :this.transitions){
                        if(transition.state.equals(currentState) && transition.symbolStack.equals(symbolStack) && transition.symbolInput.equals("$")){
                            currentState = transition.nextState;
                            UpdateStack(stack,transition,result);
                            found1=true;
                            i--;
                            break;
                        }
                    }
                    if(!found1){
                        result.add("fail|0");
                        this.LastResult.add(result);
                        break;
                    }
                }else{
                    result.add("0");
                    this.LastResult.add(result);
                    break;
                }
            }
            i++;
        }
    }

    private void UpdateStack(Stack stack, Transition transition, List<String> result){
        stack.pop();
        if(!(transition.newStackSymbols.contains("$") && transition.newStackSymbols.size()==1)){
            transition.newStackSymbols.forEach(stack::push);
        }
        result.add(transition.nextState);
        result.add("#");
        if(stack.isEmpty()){
            result.add("$");
        }else{
            result.add(new StringBuilder(String.join("",stack)).reverse().toString());
        }
        result.add("|");
    }

    public void PrintResult(List<String> array){
        array.forEach(System.out::print);
        System.out.println();
    }

    public SimDPA(String fileInputName){
        List<String> temp = new ArrayList<>(); // Lines from file
        String line;
        try {
            BufferedReader br = DEBUG ? new BufferedReader(new FileReader(new File(fileInputName)))
                                        :new BufferedReader(new InputStreamReader(System.in));
            while ((line = br.readLine()) != null) {
                temp.add(line);
            }
            ParseInputArrays(temp.get(0));
            getStates(temp.get(1));
            ParseInputSymbols(temp.get(2));
            ParseStackSymbols(temp.get(3));
            ParseAcceptableStates(temp.get(4));
            this.initState = temp.get(5);
            this.initStackState = temp.get(6);
            ParseTransitions(temp);
        } catch (IOException e) {
            System.err.println("Error: "+e);
        }
    }

    private void ParseTransitions(List<String> temp) {
        this.transitions = new ArrayList<>();
        for(String line:temp){
            if(temp.indexOf(line) > 6){
                String left = line.split("->")[0];
                String right = line.split("->")[1];
                String[] leftSplit = left.split(",");//left part of transition
                String[] rightSplit = right.split(",");//right part of transition
                Transition transition = new Transition(
                        leftSplit[0],
                        leftSplit[1],
                        leftSplit[2],
                        rightSplit[0],
                        Arrays.asList((new StringBuilder(rightSplit[1]).reverse().toString()).split(""))
                );
                this.transitions.add(transition);
            }
        }
    }

    private class Transition {
        private String state;
        private String symbolInput;
        private String symbolStack;
        private String nextState;
        private List<String> newStackSymbols;

        public Transition(String state, String symbolInput, String symbolStack, String nextState, List<String> newStackSymbols){
            this.newStackSymbols = new ArrayList<>(newStackSymbols);
            this.state=state;
            this.symbolInput=symbolInput;
            this.symbolStack=symbolStack;
            this.nextState=nextState;
        }
        @Override
        public String toString(){
            return this.state+","+this.symbolInput+","+this.symbolStack+"->"+this.nextState+","+this.newStackSymbols;
        }
    }

    private void ParseAcceptableStates(String acceptableStatesAsString) {
        this.AcceptableStates = Arrays.asList(acceptableStatesAsString.split(","));
    }

    private void ParseStackSymbols(String symbolsAsString) {
        this.SymbolsStack = Arrays.asList(symbolsAsString.split(","));
    }

    private void ParseInputSymbols(String symbolsAsString) {
        this.InputSymbols = Arrays.asList(symbolsAsString.split(","));
    }

    private void getStates(String stanjaAsString) {
        this.States = Arrays.asList(stanjaAsString.split(","));
    }

    private void ParseInputArrays(String InputArraysAsString) {
        this.InputArrays = Arrays.asList(InputArraysAsString.split("\\|"))
                .stream()
                .map(array -> Arrays.asList(array.split(",")))
                .collect(Collectors.toList());
    }
}
