package org.example;

import org.example.api.Dto.ParachuteDTO;
import org.example.api.Factory.ParachuteFactory;
import org.example.api.Misc.Archiver;

import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var storage = new ParachuteFactory();
        Scanner scanner = new Scanner(System.in);

        boolean t1 = false;

        do {
            System.out.println("Из какого файла прочитать данные? (txt, xml, json)");
            String fileToRead = scanner.nextLine();
            fileToRead = fileToRead.toLowerCase();
            switch (fileToRead) {
                case "txt":
                    storage.readFromFile("parachute.txt");
                    t1 = true;
                    break;

                case "xml":
                    storage.setListStorage(storage.readFromXml("parachute.xml"));
                    t1 = true;
                    break;

                case "json":
                    storage.setListStorage(storage.readDataFromJsonFile("parachute.json"));
                    t1 = true;
                    break;

                default:
                    System.out.println("Неправильный формат файла. Попробуйте снова.");
                    break;
            }
        } while (!t1);

        System.out.println("Список дельтапланов получен.");
        for (ParachuteDTO dto : storage.getList()) {
            System.out.println(dto.toString());
        }
        System.out.println();

        int id = -1;
        String name = "";
        String description = "";
        boolean t = true;
        do {
            System.out.println("Введите данные о делтаплане в формате cost,name,description (обязательно через запятую):");
            try {
                String input = scanner.nextLine();
                String[] parts = input.split(",");
                id = Integer.parseInt(parts[0]);
                name = parts[1];
                description = parts[2];

                int finalId = id;
                String finalDescription = description;
                String finalName = name;
                if (storage.getList().stream().anyMatch(parachuteDTO -> parachuteDTO.getCost() == finalId) &&
                        storage.getList().stream().anyMatch(ParachuteDTO -> ParachuteDTO.getDescription().equals(finalDescription)) &&
                        storage.getList().stream().anyMatch(CategoryDto -> CategoryDto.getName().equals(finalName))
                ) {
                    System.out.println("Такой дельтаплан уже есть!");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Попробуйте снова");
                t = false;
            }
        } while (!t);

        // Use the Builder pattern to create the new ParachuteDTO
        ParachuteDTO newParachute = ParachuteDTO.builder()
                .Cost(id)
                .Name(name)
                .Description(description)
                .build();

        storage.addToListStorage(newParachute);
        storage.addToMapStorage(id, newParachute);

        storage.writeToFile("parachute.txt");
        storage.writeToXml("parachute.xml", storage.getList());
        storage.writeDataToJsonFile("parachute.json", storage.getList());

        System.out.println("Обновленный список дельтапланов: ");
        for (ParachuteDTO dto : storage.getList()) {
            System.out.println(dto.toString());
        }
        boolean ans = false;

        do {
            System.out.println("Выберете поле для сортировки(cost,name,description):");
            String typeSort = scanner.nextLine();
            typeSort = typeSort.toLowerCase();

            switch (typeSort) {

                case "cost":
                    storage.getList().sort(Comparator.comparing(ParachuteDTO::getCost));
                    System.out.println("Дельтапланы сортированные по cost: ");
                    for (ParachuteDTO dto : storage.getList()) {
                        System.out.println(dto.toString());
                    }
                    ans = true;
                    break;

                case "name":
                    storage.getList().sort(Comparator.comparing(ParachuteDTO::getName));
                    System.out.println("Дельтапланы сортированные по названию: ");
                    for (ParachuteDTO dto : storage.getList()) {
                        System.out.println(dto.toString());
                    }
                    ans = true;
                    break;

                case "description":
                    storage.getList().sort(Comparator.comparing(ParachuteDTO::getDescription));
                    System.out.println("Дельтапланы сортированные по описанию: ");
                    for (ParachuteDTO dto : storage.getList()) {
                        System.out.println(dto.toString());
                    }
                    ans = true;
                    break;

                default:
                    System.out.println("Введено неверное поле");
                    break;
            }
        } while (!ans);

        String[] files = new String[]{
                "parachute.txt",
                "parachute.json",
                "parachute.xml"
        };

        Archiver archiver = new Archiver();
        try {
            archiver.createZipArchive("zipResult.zip", files);
            archiver.createJarArchive("jarResult.jar", files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
