package service;

import domain.Animal;
import domain.utils.AnimalType;
import domain.utils.BiologicalSex;
import repository.AnimalRepositoryImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AnimalService {


    private final AnimalRepositoryImpl animalRepository;
    private final FileWriterService fileWriterService;

    public AnimalService(AnimalRepositoryImpl animalRepository, FileWriterService fileWriterService) {
        this.animalRepository = animalRepository;
        this.fileWriterService = fileWriterService;
    }

    //TODO test saveAnimal()
    public void saveAnimal(String firstName, String lastName, AnimalType animalType, BiologicalSex biologicalSex, Integer addressNumber, String addressName, String addressCity, Double age, Double weight, String breed) throws IOException {
        //Validate name and surname content
        if (containsInvalidCharacters(firstName) || containsInvalidCharacters(lastName)) {
                throw new IllegalArgumentException("First and last names must contain only A-Z letters.");
        }

        //validate breed content
        if (containsInvalidCharacters(breed)) {
            throw new IllegalArgumentException("Breed must contain only A-Z letters.");
        }

        //validate input and maximum age
        if (isValidDecimal(String.valueOf(age))) {
            throw new IllegalArgumentException("Age must contain only numbers!");
        }
        if (age > 20 || age == 0) {
            throw new IllegalArgumentException("Age must be between 0.1 and 20");
        }

        //validate input and min and max weight
        if (isValidDecimal(String.valueOf(weight))) {
            throw new IllegalArgumentException("Weight must contain only numbers!");
        }
        if (weight > 60 || weight < 0.1) {
            throw new IllegalArgumentException("Weight must be between 0.1 and 60");
        }

        Animal animal = new Animal(firstName,
                lastName,
                animalType,
                biologicalSex,
                addressNumber,
                addressName,
                addressCity,
                age,
                weight,
                breed);
        animalRepository.save(animal);
        System.out.println("Animal created in Service" + animal);
        fileWriterService.createAnimalFile(animal);

    }

    public List<Animal> findAll() {
        List<Animal> animals = animalRepository.findAll();
        if (animals.isEmpty()) {
            System.out.println("No animals registered yet.");
        } else {
            animals.forEach(System.out::println);
        }
        return animals;
    }

    public List<Animal> findByFilters(Map<String, Object> filters){
        List<Animal> animals = animalRepository.findAll();

        if (filters == null || filters.isEmpty()) {return animals;}
        if (filters.size() > 2) {throw new IllegalArgumentException("Max 2 filters allowed");}

        return animals.stream()
                .filter(animal -> {
                    for (Map.Entry<String, Object> entry : filters.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        if (!passesFilter(animal, key, value)) {
                            return false;
                        }
                    }
                    return true;
                })
                .toList();
    }

    private boolean containsInvalidCharacters(String text) {
        return text == null || !text.matches("[a-zA-Z ]+");
    }

    private boolean isValidDecimal(String input) {
        return !input.matches("^[+-]?\\d+(?:[.,]\\d+)?$");
    }

    private boolean passesFilter(Animal animal, String key, Object value) {
        switch (key) {
            case "name":
                return value instanceof String &&
                        animal.getFullName().equalsIgnoreCase((String) value);
            case "gender":
                return value instanceof BiologicalSex &&
                        animal.getBiologicalSex().name().equalsIgnoreCase((String) value);
            case "age":
                return value instanceof Double &&
                        Objects.equals(animal.getAge(), (Double) value);
            case "weight":
                return value instanceof Double &&
                        Objects.equals(animal.getWeight(), (Double) value);
            case "breed":
                return value instanceof String &&
                        animal.getBreed().equalsIgnoreCase((String) value);
            case "address":
                return value instanceof String &&
                        animal.getFullAddress().equalsIgnoreCase((String) value);
            default:
                return false;

        }
    }


}
