package service;

import controller.AnimalController;
import domain.UserMenus;
import domain.utils.AnimalType;
import domain.utils.BiologicalSex;

import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;


public class UserInterfaceService {
     private final FileReaderService fileReaderService;
     private final AnimalController animalController;
     private final Scanner sc;
     private final UserMenus userMenus;


    public UserInterfaceService(FileReaderService fileReaderService, AnimalController animalController, UserMenus userMenus) {
        this.fileReaderService = fileReaderService;
        this.animalController = animalController;
        this.userMenus = userMenus;
        this.sc = new Scanner(System.in);

    }

    public void start(String filePath) throws IOException {
        Scanner sc = new Scanner(System.in);
        int userChoice;

        do {
            userMenus.displayMainMenu();
            System.out.print("Enter the number of your option: ");

            try {
                userChoice = sc.nextInt();
                sc.nextLine();

                if (userChoice >= 1 && userChoice <= 6) {
                    handleMainMenuOption(userChoice, filePath);
                } else {
                    System.out.println("Please choose a number between 1 and 6!");
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid option! Please enter a valid number.");
                sc.nextLine();
                continue;
            }

            if (userChoice != 6) {
                System.out.println("\nPress enter to return to main menu.");
                sc.nextLine();
            }

        }
        System.out.println("Exiting program...");

    }

    private void handleMainMenuOption(int userChoice, String filePath) throws IOException {
        switch (userChoice) {
            case 1:
                //Register new animal
                System.out.println("\nREGISTER NEW ANIMAL:");
                collectRegisterResponses(filePath);
               // Map<String, String> collectedRegisterResponses = collectRegisterResponses(filePath);
                break;
            case 2:
                //Edit animal

                break;
            case 3:
                //Delete registered animal

                break;
            case 4:
                animalController.listAll();
                break;
            case 5:
                //Find animals by criteria
                sc.nextLine();
                animalController.listAnimalsCriteria(handleFilterAnimalMenu());
                break;

            case 6:
                break;

        }
    }

    public void collectRegisterResponses(String filePath) throws IOException {
        Map<String, String>  responses = new HashMap<>();
        List<String> questions = fileReaderService.readFileToList(filePath);

        for (String question : questions) {
            if (question.contains("animal's first and last name")) {
                System.out.println(question);
                System.out.print("Animal's first name: ");
                responses.put("first name", sc.nextLine().trim());

                System.out.print("Animal's last name: ");
                responses.put("last name", sc.nextLine().trim());
            }
            else if (question.contains("address it was found")) {
                System.out.println(question);
                System.out.print("Number: ");
                //Integer addressNumber = parseInt(sc.nextLine());
                responses.put("address number", sc.nextLine().trim());

                System.out.print("St./Ave./Rd./Pl./Sq. name: ");
                responses.put("address name", sc.nextLine().trim());

                System.out.print("City: ");
                responses.put("address city", sc.nextLine().trim());
            } else {
                System.out.println(question);
                String response = sc.nextLine().trim();
                responses.put(question, response);
            }

        }
        animalController.registerAnimal(responses);
    }

    private Map<String, Object> handleFilterAnimalMenu() {
        System.out.println("--- FIND ANIMAL ---\n" +
                "Select animal type to search: 1 - Cat | 2 - Dog");

        AnimalType animalType = null;
        while (animalType == null) {
            try {
                int typeChoice = Integer.parseInt(sc.nextLine());
                animalType = AnimalType.fromValue(typeChoice);
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter 1 (Cat) or 2 (Dog)");
            }
        }
        Map<String, Object> filters = new HashMap<>();
        filters.put("type", animalType);
        int crit1 = getValidCriterion(animalType, "Choose a criterion (1-6) to search: ");
        if (crit1 > 0) {
            addFilter(crit1, filters);

            int crit2 = getValidCriterion(animalType, "Choose a second criterion or press 0 to continue: ");
            while (crit2 == crit1 && crit2 != 0) {
                System.out.println("You cannot use the same criterion! Choose a different one.");
                crit2 = getValidCriterion(animalType, "Choose a different criterion or press 0 to continue: ");
            }
            if (crit2 > 0) {
                addFilter(crit2, filters);
            }
        }

        return filters;
    }

    private int getValidCriterion(AnimalType animalType, String prompt) {
        while (true) {
            try {
                userMenus.displayListAnimalsMenu(animalType);
                System.out.print(prompt);
                int choice = Integer.parseInt(sc.nextLine());
                if (choice == 0 || (choice >= 1 && choice <= 6)) {
                    return choice;
                }
                System.out.println("Please enter a number between 1-6 or 0 to continue!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }


    private void addFilter(int criterionCode, Map<String, Object> filters) {
        switch (criterionCode) {
            case 1: //name
                System.out.println("Enter animal's first or last name to search: ");
                filters.put("name", sc.nextLine().toLowerCase());
                break;

            case 2: //gender
                BiologicalSex gender = null;
                while (gender == null) {
                    System.out.println("Select animal's gender (1 - Female | 2 - Male): ");
                    try {
                        gender = BiologicalSex.fromValue(Integer.parseInt(sc.nextLine()));
                    } catch (Exception e) {
                        System.out.println("Invalid gender! Please enter 1 or 2");
                    }
                }
                filters.put("gender", gender);
                break;

            case 3: //age
                double expectedAge = -1;
                while (expectedAge <= 0) {
                    System.out.println("Enter animal's age: ");
                    try {
                        expectedAge = Double.parseDouble(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid age! Please enter a positive number until 20");
                    }}
                filters.put("age", expectedAge);
                break;

            case 4: //weight
                double expectedWeight = -1;
                while (expectedWeight <= 0) {
                    System.out.println("Enter animal's weight: ");
                    try {
                        expectedWeight = Double.parseDouble(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid weight! Please enter a positive number");
                    }
                }
                filters.put("weight", expectedWeight);
                break;

            case 5: //breed
                System.out.println("Enter animal's breed: ");
                filters.put("breed", sc.nextLine());
                break;

            case 6: //address
                System.out.println("Enter the address where animal was found at: ");
                filters.put("address", sc.nextLine());
                break;
            case 0:
                break;

            default:
                System.out.println("Invalid criterion code!");
        }
    }

    private String readLine() {
        String input = sc.nextLine();
        while (input.trim().isEmpty()) {
            input = sc.nextLine();
        }
        return input;
    }

    private int readInt() {
        while (true) {
            try {
                String input = readLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

}







