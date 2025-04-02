package domain;

import domain.utils.AnimalType;

public class UserMenus {


    public UserMenus() {
    }

    public void displayMainMenu() {
        String mainMenu = """ 
                -------------- MAIN MENU --------------
                1. Register new animal
                2. Edit the data of a registered animal
                3. Delete a registered animal
                4. List all registered animals
                5. List animals by criteria (age, name, breed)
                6. Exit
                """;
        System.out.println(mainMenu);
    }

    public void displayListAnimalsMenu(AnimalType animalType){
        String findAnimalMenu = String.format("""
                -------------- FIND %S BY --------------
                1. First or last name
                2. Gender
                3. Age
                4. Weight
                5. Breed
                6. Address
                7. Return to main menu
                """, animalType.name());
        System.out.println(findAnimalMenu);
    }

}
