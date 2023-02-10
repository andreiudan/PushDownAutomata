import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static List<Character> nonterminals = new ArrayList<>();

    public static List<Character> terminals = new ArrayList<>();

    public static LinkedHashMap<String, ArrayList<String>> productions = new LinkedHashMap<>();

    public static String start = null;

    static int temporaryCounter = 0;

    static Stack attributeStack = new Stack();

    public static void main(String[] args) {

        File input = new File("inputProductions");
        Scanner scan = null;
        try {
            scan = new Scanner(input).useDelimiter("}");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ProductionsReader productionsReader = new ProductionsReader();

        while (scan.hasNext()) {
            String[] scanned = scan.next().split("=");
            scanned[0] = scanned[0].replaceAll("\\s+", "");
            switch (scanned[0]) {
                case "P" -> productions = productionsReader.readProductions(scanned);
                case "N" -> nonterminals = productionsReader.readNonterminals(scanned);
                case "T" -> terminals = productionsReader.readTerminals(scanned);
                case "S" -> start = scanned[1].substring(scanned[1].indexOf("{") + 1);
                default -> System.out.println("No match");
            }
        }

        scan.close();

        System.out.println("-----------------------Productions---------------------------");
        for (Map.Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();

            System.out.println("Key = " + key);
            System.out.println("Values = " + value);
        }

        System.out.println("-----------------------------------------------------------");
        System.out.println("Nonterminals: " + nonterminals);
        System.out.println("Terminals: " + terminals);
        System.out.println("Start: " + start);

        TablesGenerator tablesGenerator = new TablesGenerator(nonterminals, terminals, productions, start);
        tablesGenerator.generateTables();

        String[][] matrix = tablesGenerator.combineTables();

        File inputSir = new File("inputEntryString");
        Scanner scanner = null;
        try {
            scanner = new Scanner(inputSir);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String[] scanned = scanner.nextLine().split("");
        scanner.close();

        ArrayList<String> sir = new ArrayList<>();
        Collections.addAll(sir, scanned);

        System.out.println("-----------------------------------------------------------");

        System.out.println("Entry string: " + sir);

        Stack PDAStack = new Stack();
        PDAStack.push("$");
        PDAStack.push(0);

        int i;
        String letter;
        String numberAsString;

        System.out.println("-----------------------------------------------------------");
        System.out.println(PDAStack);

        label:
        while (sir.size() != 0) {
            for (i = 0; i < 9; i++) {
                if (matrix[0][i].equals(sir.get(0))) {
                    break;
                }
            }

            letter = matrix[(Integer.parseInt(PDAStack.peek().toString())) + 1][i].substring(0,1);
            numberAsString = matrix[(Integer.parseInt(PDAStack.peek().toString())) + 1][i].substring(1);

            switch (letter) {
                case "d":

                    if (sir.get(0).equals("a")) {
                        PDAStack.push(sir.get(0) + sir.get(1));
                        sir.remove(1);
                        sir.remove(0);
                    } else {
                        PDAStack.push(sir.get(0));
                        sir.remove(0);
                    }

                    PDAStack.push(numberAsString);

                    System.out.println("Entry string: " + sir);
                    System.out.println("Stack: " + PDAStack + "\n");

                    break;
                case "r":

                    int valueInTS = 0;
                    int number = Integer.parseInt(numberAsString);
                    String key = null;
                    int productionLength = 0;

                    for (Map.Entry<String, ArrayList<String>> entry : productions.entrySet()) {
                        if (entry.getValue().size() < number) {
                            number = number - entry.getValue().size();
                        } else {
                            key = entry.getKey();
                            productionLength = productions.get(key).get(number - 1).length();
                            String production = productions.get(key).get(number - 1).replaceAll("\\s+", "");

                            if (production.length() > 1 && !(production.charAt(0) == '(')) {
                                attributeStack.push(newTemp(productions.get(key).get(number - 1)));
                            }

                            break;
                        }
                    }

                    for (int j = 0; j < 2 * productionLength; j++) {
                        PDAStack.pop();

                        if (PDAStack.peek().toString().contains("a")) {
                            attributeStack.push(PDAStack.pop());
                            j++;
                        }
                    }

                    for (int j = 0; j < 9; j++) {
                        if (matrix[0][j].equals(key)) {
                            valueInTS = Integer.parseInt(matrix[Integer.parseInt(PDAStack.peek().toString()) + 1][j]);

                            break;
                        }
                    }

                    PDAStack.push(key);
                    PDAStack.push(valueInTS);

                    System.out.println("Entry string: " + sir);
                    System.out.println("Stack: " + PDAStack + "\n");

                    break;
                case "a":

                    System.out.println("Accepting: " + PDAStack);
                    break label;
                default:

                    System.out.println("Error!");
                    break label;
            }
        }
    }

    public static void emit(String temporaryCode) {
        System.out.println("Generated code: " + temporaryCode);
    }

    public static String newTemp(String production){
        temporaryCounter++;

        StringBuilder varTemp = new StringBuilder();

        for(int i=production.length()-1; i>=0; i--){
            if(nonterminals.contains(production.charAt(i))) {
                varTemp.insert(0, attributeStack.pop().toString());
            }
            else if (!(production.charAt(i) == '(' || production.charAt(i) == ')')) {
                varTemp.insert(0, production.charAt(i));
            }
        }

        varTemp.insert(0, "t" + temporaryCounter + " = ");

        emit(varTemp.toString());

        return "t" + temporaryCounter;
    }
}