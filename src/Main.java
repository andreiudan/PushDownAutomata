import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static List<Character> neterminale = new ArrayList<>();
    public static List<Character> terminale = new ArrayList<>();
    public static Map<String, ArrayList<String>> productii = new LinkedHashMap<String, ArrayList<String>>();

    public static String[][] TA;

    public static String[][] TS;
    static int temporaryCounter = 0;
    static Stack attributeStack = new Stack();

    public static void emit(String s) {
        System.out.println("Cod generat: " + s);
    }
    public static String newtemp(String productie){

        temporaryCounter++;

        String varTemp = "";

        for(int i=productie.length()-1; i>=0; i--){
            if(neterminale.contains(productie.charAt(i)))
            {
                varTemp = attributeStack.pop().toString() + varTemp;
            }
            else if (!(productie.charAt(i) == '(' || productie.charAt(i) == ')'))
            {
                varTemp =  productie.charAt(i) + varTemp;
            }
        }
        
        varTemp = "t" + temporaryCounter + " = " + varTemp;

        emit(varTemp);

        return "t"+temporaryCounter;
    }

    public static void main(String[] args) {


        File input = new File("inputFile");
        Scanner scan = null;
        try {
            scan = new Scanner(input).useDelimiter("}");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String start = null;

        while (scan.hasNext()) {
            String[] sirCitit = scan.next().split("=");
            sirCitit[0] = sirCitit[0].replaceAll("\\s+", "");
            switch (sirCitit[0]) {
                case "P":
                    String substringEcuatii = sirCitit[1].substring(sirCitit[1].indexOf("{") + 1);
                    String[] productie = substringEcuatii.split(",");

                    for (int k = 0; k < productie.length; k++) {
                        productie[k] = productie[k].replaceAll("\\s+", "");
                        String cheie = productie[k].substring(0, productie[k].indexOf("-"));
                        //indexProductii.add(cheie);
                        productii.put(cheie, new ArrayList<>());
                        productie[k] = productie[k].substring(productie[k].indexOf(">") + 1);

                        if (productie[k].contains("|")) {
                            String[] splitProductie = productie[k].split("\\|");
                            for (String a : splitProductie) {
                                productii.get(cheie).add(a);
                            }
                        } else {
                            productii.get(cheie).add(productie[k]);
                        }
                    }
                    break;
                case "N":
                    String neterminal = sirCitit[1].substring(sirCitit[1].indexOf("{") + 1);
                    for (int i = 0; i < neterminal.length(); i++) {
                        if (Character.isUpperCase(neterminal.charAt(i))) {
                            neterminale.add(neterminal.charAt(i));
                        }
                    }
                    break;
                case "T":
                    String terminal = sirCitit[1].substring(sirCitit[1].indexOf("{") + 1);
                    for (int i = 0; i < terminal.length(); i++) {
                        if (terminal.charAt(i) != ',') {
                            terminale.add(terminal.charAt(i));
                        }
                    }
                    break;
                case "S":
                    start = sirCitit[1].substring(sirCitit[1].indexOf("{") + 1);
                    break;
                default:
                    System.out.println("No match");
            }
        }

        HashSet<String> TINI = new HashSet<>();
        HashSet<String> TFIN = new HashSet<>();

        System.out.println("-----------------------productii---------------------------");
        for (Map.Entry<String, ArrayList<String>> entry : productii.entrySet()) {

            String cheie = entry.getKey();
            ArrayList<String> valori = entry.getValue();

            for (String valoare : valori) {
                Character inceput = valoare.charAt(0);
                TINI.add(inceput.toString());
                TFIN.add(valoare.replaceAll("(.)(?!$)", ""));
            }

            System.out.println("Key = " + cheie);
            System.out.println("Values = " + valori);

        }

        System.out.println("-----------------------------------------------------------");
        System.out.println("TINI = " + TINI);
        System.out.println("TFIN = " + TFIN);
        System.out.println("Neterminale: " + neterminale);
        System.out.println("Terminale: " + terminale);
        System.out.println("Start: " + start);

        TablesGenerator tablesGenerator = new TablesGenerator(neterminale, terminale, productii, start);
        tablesGenerator.generateTables();

        String[][] matrix = tablesGenerator.combineTables();

        File inputMatrice = new File("inputSir");
        Scanner sc = null;
        try {
            sc = new Scanner(inputMatrice);
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

        //Stack attributeStack = new Stack();

        int i;
        String litera;
        String numar;

        System.out.println("-----------------------------------------------------------");
        System.out.println(myStack);

        while (sir.size() != 0) {
            for (i = 0; i < 9; i++) {
                if (matrix[0][i].equals(sir.get(0))) {
                    break;
                }
            }

            litera = matrix[(Integer.parseInt(myStack.peek().toString())) + 1][i].substring(0,1);
            numar = matrix[(Integer.parseInt(myStack.peek().toString())) + 1][i].substring(1);

            if (litera.equals("d")) {

                if(sir.get(0).equals("a")) {
                    myStack.push(sir.get(0).toString() + sir.get(1).toString());
                    sir.remove(1);
                    sir.remove(0);
                }
                else{
                    myStack.push(sir.get(0));
                    sir.remove(0);
                }

                myStack.push(numar);
                System.out.println("Sir de intrare: " + sir);
                System.out.println("Stiva: " + myStack + "\n");

            } else if (litera.equals("r")) {

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
                    if(myStack.peek().toString().contains("a")){
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

            } else if (litera.equals("a")) {

                System.out.println("Acceptare: " + myStack);
                break;
            } else {

                System.out.println("Sir eronat!");
                break;
            }
        }
    }
}