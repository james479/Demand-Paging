package PagingProject;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

//this class will simulate memory and its various paging algorithms
public class VirtualMemory {
    private ArrayList<Integer> referenceString;
    private int numberOfPhysicalFrames;
    private final int virtualMemory = 10;

    //constructor
    public VirtualMemory(int numberOfPhysicalFrames) {
        this.referenceString = new ArrayList<>();
        this.numberOfPhysicalFrames = numberOfPhysicalFrames;
    }

    public void readReferenceString() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Integer> newString = new ArrayList<>();
        System.out.println("Please enter a new reference string seperated by a single space");
        System.out.println("Only enter integers between 0 and " + (virtualMemory-1) + " to match the number of virtual memory frames");
        String[] input = scanner.nextLine().split(" ");
        boolean isValid = isValidReferenceString(input);

        if(isValid) {
            for(String item: input) {
                newString.add(Integer.parseInt(item));
            }
            referenceString = newString;
            System.out.println("The new reference string is:");
            displayReferenceString();
        }
    }

    //method to generate random reference string
    public void generateReferenceString() {
        Scanner scanner = new Scanner(System.in);
        boolean isNumeric = false;
        int length = 0;
        //getting reference string length from user, will check if it is numeric
        while(!isNumeric) {
            System.out.print("Enter the length of the reference string: ");
            String answer = scanner.nextLine();
            if(!answer.chars().allMatch(Character::isDigit)) {
                System.out.println("Invalid entry");
            }
            else {
                length = Integer.parseInt(answer);
                isNumeric = true;
            }
        }
        ArrayList<Integer> newString = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < length; i++) {
            int n = rand.nextInt(virtualMemory);
            newString.add(n);
        }
        referenceString = newString;
        displayReferenceString();
    }

    public void simulateFIFO() throws IOException {
        //queue to simulate FIFO
        Queue<Integer> index = new LinkedList<>();
        //scanner object to prompt user to press enter to continue
        Scanner scanner = new Scanner(System.in);
        int victim = -1;
        int stepNumber;
        int pageFaults = 0;

        //looping through the reference string if there is a reference string
        if(!referenceString.isEmpty()) {
            for(int i = 0; i < referenceString.size(); i++) {
                //if queue is full
                if(index.size() == numberOfPhysicalFrames) {
                    //checking to see if index is already in queue
                    if(!index.contains(referenceString.get(i))) {
                        //add the index to the queue
                        victim = index.remove();
                        index.add(referenceString.get(i));
                        pageFaults++;
                    }
                    else {
                        victim = -1;   //-1 is a flag for no page faults at all
                    }
                }
                else {
                    index.add(referenceString.get(i));
                    victim = -2;   //-2 is a flag for a page fault but no victim frame
                    pageFaults++;
                }
                System.out.println("Press enter for next step");
                scanner.nextLine();
                stepNumber = i + 1;
                System.out.println("STEP " + stepNumber);
                displayStep(index, victim, referenceString.get(i));
            }
            System.out.println("Page Faults: " + pageFaults);
        }
        //if not reference string
        else {
            System.out.println("There is no reference string.");
            System.out.println("Please enter or generate a reference string an try again");
        }
    }

    //method to simulate OPT algorithm
    public void simulateOptAndLru(int flag) {
        //arraylist to hold physical frames
        ArrayList<Integer> frames = new ArrayList<>();
        int victim = -1;
        int pageFaults = 0;
        Scanner scanner = new Scanner(System.in);
        int stepNumber;

        //looping through the reference string if there is a reference string
        if(!referenceString.isEmpty()) {
            for(int i = 0; i < referenceString.size(); i++) {
                //if physical frames are full
                if(frames.size() == numberOfPhysicalFrames) {
                    if(!frames.contains(referenceString.get(i))) {
                        switch (flag) {
                            case 1:
                                victim = getOptVictim(frames, i+1);
                                break;
                            case 2:
                                victim = getLruVictim(frames, i-1);
                                break;
                            default:
                                break;
                        }
                        int replacementIndex = frames.indexOf(victim);
                        frames.set(replacementIndex, referenceString.get(i));
                        pageFaults++;

                    }
                    else {
                        victim = -1;
                    }
                }
                else {
                    frames.add(referenceString.get(i));
                    victim = -2;   //-2 is a flag for a page fault but no victim frame
                    pageFaults++;
                }
                System.out.println("Press enter for next step");
                scanner.nextLine();
                stepNumber = i + 1;
                System.out.println("STEP " + stepNumber);
                displayStep(frames, victim, i);
            }

            System.out.println("Page Faults: " + pageFaults);
        }
        //if there is no reference string
        else {
            System.out.println("There is no reference string.");
            System.out.println("Please enter or generate a reference string an try again");
        }
    }

    //method to simulate LFU algorithm
    public void simulateLfu() {
        //arraylist to hold physical frames
        ArrayList<Integer> frames = new ArrayList<>();
        //hashmap to keep track how many instances a reference has
        HashMap<Integer, Integer> instances = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        int victim = -1;
        int pageFaults = 0;
        int stepNumber;

        //populating the hashmap
        for(int i = 0; i < virtualMemory; i++) {
            instances.put(i, 0);
        }

        //looping through the reference string if it is not empty
        if(!referenceString.isEmpty()) {
            for(int i = 0; i < referenceString.size(); i++) {
                if(frames.size() == numberOfPhysicalFrames) {
                    if (!frames.contains(referenceString.get(i))) {
                        victim = getLfuVictim(frames, instances, i);
                        instances.put(referenceString.get(i), instances.get(referenceString.get(i)) + 1);

                        //replace the victim with the new reference keeping in top down order
                        int replacementIndex = frames.indexOf(victim);
                        frames.set(replacementIndex, referenceString.get(i));
                        pageFaults++;

                    } else {
                        victim = -1;
                        //incrementing the value of instances
                        instances.put(referenceString.get(i), instances.get(referenceString.get(i)) + 1);
                    }
                }
                else {
                    frames.add(referenceString.get(i));
                    instances.put(referenceString.get(i), instances.get(referenceString.get(i)) + 1);
                    victim = -2;   //-2 is a flag for a page fault but no victim frame
                    pageFaults++;
                }
                System.out.println("Press enter for next step");
                scanner.nextLine();
                stepNumber = i + 1;
                System.out.println("STEP " + stepNumber);
                displayStep(frames, victim, i);
            }
            System.out.println("Page Faults: " + pageFaults);
        }
        //if there is no reference string
        else {
            System.out.println("There is no reference string.");
            System.out.println("Please enter or generate a reference string an try again");
        }
    }

    //method perform lfu operations and return victim frame
    public int getLfuVictim(ArrayList<Integer> frames, HashMap<Integer, Integer> instances, int index) {
        int victim = frames.get(0);
        for(int i = 1; i < frames.size(); i++) {
            if(instances.get(victim) > instances.get(frames.get(i))) {
                victim = frames.get(i);
            }
        }
        //setting victim frame instances back to zero
        instances.put(victim, 0);
        return victim;
    }

    //method to perform lru operations and return the victim frame
    public int getLruVictim(ArrayList<Integer> frames, int index) {
        int victim = 0;
        //using hashset to eliminate potential victims
        ArrayList<Integer> potentialVictims = new ArrayList<>();

        for(int i = index; i >= 0; i--) {
            if(frames.contains(referenceString.get(i))) {
                potentialVictims.add(referenceString.get(i));
            }
            if(potentialVictims.size() == numberOfPhysicalFrames -1) {
                break;
            }
        }
        //storing victim in variable
        for(Integer item : frames) {
            if(!potentialVictims.contains(item)) {
                victim = item;
            }
        }
        return victim;
    }

    //this method will perform opt operations and return the victim frame
    public int getOptVictim(ArrayList<Integer> frames, int index) {
        int victim = 0;
        //using hashset to eliminate potential victim frames
        ArrayList<Integer> potentialVictims = new ArrayList<>();

        for(int i = index; i < referenceString.size(); i++) {
            int reference = referenceString.get(i);
            if(frames.contains(reference)) {
                potentialVictims.add(referenceString.get(i));
            }
            if(potentialVictims.size() == numberOfPhysicalFrames - 1) {
                break;
            }
        }

        //storing victim in variable
        for(Integer item : frames) {
            if(!potentialVictims.contains(item)) {
                victim = item;
            }
        }
        return victim;
    }

    public void displayStep(Queue<Integer> index, int victim, int stringIndex) {
        int frameNumber = 0;

        System.out.println("Reference String:     " + stringIndex);
        System.out.println("***************");
        //printing queue
        for(Integer item: index) {
            System.out.println("Physical Frame " + frameNumber + ":     " + item);
            frameNumber++;
        }
        //checking for page faults
        if(victim == -1) {
            System.out.println("Page Faults: None");
        }
        else {
            System.out.println("Page Faults:          F");
            if(victim != -2) {
                System.out.println("Victim Frames:        " + victim);
            }

        }
        System.out.println("***************");
    }

    public void displayStep(ArrayList<Integer> frames, int victim, int stringIndex) {
        int frameNumber = 0;

        System.out.println("Reference String:     " + referenceString.get(stringIndex));
        System.out.println("***************");
        //printing physical frames
        for(Integer item: frames) {
            System.out.println("Physical Frame " + frameNumber + ":     " + item);
            frameNumber++;
        }
        //checking for page faults
        if(victim == -1) {
            System.out.println("Page Faults: None");
        }
        else {
            System.out.println("Page Faults:          F");
            if(victim != -2) {
                System.out.println("Victim Frames:        " + victim);
            }

        }
        System.out.println("***************");
    }

    //method to display reference string
    public void displayReferenceString() {
        String reference = "";
        if(!referenceString.isEmpty()) {
            for(Integer item: referenceString) {
                reference += item.toString() + " ";
            }
            System.out.println(reference);
        }
        else {
            System.out.println("There is no reference string");
        }
    }

    //method to check if reference string entered is valid
    public boolean isValidReferenceString(String[] referenceString) {
        //checking to make sure all entries are integers
        for(String item : referenceString) {
            if(!item.chars().allMatch(Character::isDigit)) {
                System.out.println("Invalid integer entry");
                return false;
            }
        }
        //checking to make sure all integer entries are within the range of virtual frames
        int[] intReference = new int[referenceString.length];
        for(int i = 0; i < referenceString.length; i++) {
            intReference[i] = Integer.parseInt(referenceString[i]);
        }
        for(Integer item: intReference) {
            if(item < 0 || item > virtualMemory -1) {
                System.out.println("Invalid entry. Not within range of virutal frames");
                return false;
            }
        }
        return true;
    }



}
