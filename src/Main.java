import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static List<Character> neterminale = new ArrayList<>();

    public static List<Character> terminale = new ArrayList<>();

    public static LinkedHashMap<String, ArrayList<String>> productii = new LinkedHashMap<>();

    public static String start = null;

    static int temporaryCounter = 0;

    static Stack attributeStack = new Stack();

    public static void main(String[] args) {

        File input = new File("inputFile");
        Scanner scan = null;
        try {
            scan = new Scanner(input).useDelimiter("}");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ProductionsReader productionsReader = new ProductionsReader();

        while (scan.hasNext()) {
            String[] sirCitit = scan.next().split("=");
            sirCitit[0] = sirCitit[0].replaceAll("\\s+", "");
            switch (sirCitit[0]) {
                case "P" -> productii = productionsReader.readProductions(sirCitit);
                case "N" -> neterminale = productionsReader.readNonterminals(sirCitit);
                case "T" -> terminale = productionsReader.readTerminals(sirCitit);
                case "S" -> start = sirCitit[1].substring(sirCitit[1].indexOf("{") + 1);
                default -> System.out.println("No match");
            }
        }

        scan.close();

        System.out.println("-----------------------Productions---------------------------");
        for (Map.Entry<String, ArrayList<String>> entry : productii.entrySet()) {
            String cheie = entry.getKey();
            ArrayList<String> valori = entry.getValue();

            System.out.println("Key = " + cheie);
            System.out.println("Values = " + valori);
        }

        System.out.println("-----------------------------------------------------------");
        System.out.println("Neterminale: " + neterminale);
        System.out.println("Terminale: " + terminale);
        System.out.println("Start: " + start);

        TablesGenerator tablesGenerator = new TablesGenerator(neterminale, terminale, productii, start);
        tablesGenerator.generateTables();

        String[][] matrix = tablesGenerator.combineTables();

        File inputSir = new File("inputSir");
        Scanner sc = null;
        try {
            sc = new Scanner(inputSir);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String[] sirIntrare = sc.nextLine().split("");
        sc.close();

        ArrayList<String> sir = new ArrayList<>();
        Collections.addAll(sir, sirIntrare);

        System.out.println("-----------------------------------------------------------");

        System.out.println("Sir de intrare: " + sir);

        Stack myStack = new Stack();
        myStack.push("$");
        myStack.push(0);

        int i;
        String litera;
        String numar;

        System.out.println("-----------------------------------------------------------");
        System.out.println(myStack);

        label:
        while (sir.size() != 0) {
            for (i = 0; i < 9; i++) {
                if (matrix[0][i].equals(sir.get(0))) {
                    break;
                }
            }

            litera = matrix[(Integer.parseInt(myStack.peek().toString())) + 1][i].substring(0,1);
            numar = matrix[(Integer.parseInt(myStack.peek().toString())) + 1][i].substring(1);

            switch (litera) {
                case "d":

                    if (sir.get(0).equals("a")) {
                        myStack.push(sir.get(0) + sir.get(1));
                        sir.remove(1);
                        sir.remove(0);
                    } else {
                        myStack.push(sir.get(0));
                        sir.remove(0);
                    }

                    myStack.push(numar);
                    System.out.println("Sir de intrare: " + sir);
                    System.out.println("Stiva: " + myStack + "\n");

                    break;
                case "r":

                    int valoareTS = 0;
                    int red = Integer.parseInt(numar);
                    String cheie = null;
                    int lungimeProductie = 0;

                    for (Map.Entry<String, ArrayList<String>> entry : productii.entrySet()) {
                        if (entry.getValue().size() < red) {
                            red = red - entry.getValue().size();
                        } else {
                            cheie = entry.getKey();
                            lungimeProductie = productii.get(cheie).get(red - 1).length();
                            String productie = productii.get(cheie).get(red - 1).replaceAll("\\s+", "");

                            if (productie.length() > 1 && !(productie.charAt(0) == '(')) {
                                attributeStack.push(newtemp(productii.get(cheie).get(red - 1)));
                            }

                            break;
                        }
                    }

                    for (int j = 0; j < 2 * lungimeProductie; j++) {
                        myStack.pop();

                        if (myStack.peek().toString().contains("a")) {
                            attributeStack.push(myStack.pop());
                            j++;
                        }
                    }

                    for (int j = 0; j < 9; j++) {
                        if (matrix[0][j].equals(cheie)) {
                            valoareTS = Integer.parseInt(matrix[Integer.parseInt(myStack.peek().toString()) + 1][j]);

                            break;
                        }
                    }

                    myStack.push(cheie);
                    myStack.push(valoareTS);

                    System.out.println("Sir de intrare: " + sir);
                    System.out.println("Stiva: " + myStack + "\n");

                    break;
                case "a":

                    System.out.println("Acceptare: " + myStack);
                    break label;
                default:

                    System.out.println("Sir eronat!");
                    break label;
            }
        }
    }

    public static void emit(String s) {
        System.out.println("Cod generat: " + s);
    }

    public static String newtemp(String productie){
        temporaryCounter++;

        StringBuilder varTemp = new StringBuilder();

        for(int i=productie.length()-1; i>=0; i--){
            if(neterminale.contains(productie.charAt(i))) {
                varTemp.insert(0, attributeStack.pop().toString());
            }
            else if (!(productie.charAt(i) == '(' || productie.charAt(i) == ')')) {
                varTemp.insert(0, productie.charAt(i));
            }
        }

        varTemp.insert(0, "t" + temporaryCounter + " = ");

        emit(varTemp.toString());

        return "t"+temporaryCounter;
    }
}