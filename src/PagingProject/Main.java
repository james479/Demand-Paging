package PagingProject;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Enter number of frames: ");
        Scanner in = new Scanner(System.in);
        int frames = in.nextInt();
        VirtualMemory virtualMemory = new VirtualMemory(frames);
        int option = getMenuOption();
        while(option != 0) {
            switch (option) {
                case 1:
                    virtualMemory.readReferenceString();
                    break;
                case 2:
                    virtualMemory.generateReferenceString();
                    break;
                case 3:
                    virtualMemory.displayReferenceString();
                    break;
                case 4:
                    virtualMemory.simulateFIFO();
                    break;
                case 5:
                    virtualMemory.simulateOptAndLru(1);
                    break;
                case 6:
                    virtualMemory.simulateOptAndLru(2);
                    break;
                case 7:
                    virtualMemory.simulateLfu();
                    break;
                default:
                    break;
            }
            option = getMenuOption();
        }
        in.close();
    }

    //method to get user option
    static int getMenuOption() {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            displayOptions();
            System.out.print("Choose an option: ");
            while(!scanner.hasNextInt()) {
                scanner.next();
                System.out.println("Invalid entry. Please enter an integer");
                displayOptions();
                System.out.print("Choose an option: ");
            }
            option = scanner.nextInt();
            if(option < 0 || option > 7) {
                System.out.println("Please enter an integer between 0 and 7");
            }

        } while (option < 0 || option > 7);
        return option;
    }

    //method to display options for user
    static void displayOptions() {
        System.out.println("0 - Exit");
        System.out.println("1 - Read reference string");
        System.out.println("2 - Generate reference string");
        System.out.println("3 - Display current reference string");
        System.out.println("4 - Simulate FIFO");
        System.out.println("5 - Simulate OPT");
        System.out.println("6 - Simulate LRU");
        System.out.println("7 - Simulate LFU");
    }
}
