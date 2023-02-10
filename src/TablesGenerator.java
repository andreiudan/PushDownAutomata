import java.util.*;

public class TablesGenerator {
    private static List<Character> nonterminals = new ArrayList<>();

    private static List<Character> terminals = new ArrayList<>();

    private static LinkedHashMap<String, ArrayList<String>> productions = new LinkedHashMap<>();

    private static String start = null;

    private static HashMap<String, ArrayList<String>> PRIMCharacters = new LinkedHashMap<>();

    private static HashMap<String, ArrayList<String>> URMCharacters = new LinkedHashMap<>();

    private static HashMap<String, ArrayList<String>> jumpsCollection = new LinkedHashMap<>();

    private static HashMap<String, ArrayList<String>> jumps = new LinkedHashMap<>();

    public static String[][] TA;

    public static String[][] TS;

    private static int nrI = 0;

    public TablesGenerator(List<Character> nonterminals,
                           List<Character> terminals,
                           LinkedHashMap<String, ArrayList<String>> productions,
                           String start) {

        this.nonterminals = nonterminals;
        this.terminals = terminals;
        this.productions = productions;
        this.start = start;
    }

    public void generateTables(){
        PRIM();

        URM();

        ArrayList<String> modifiedProductions = new ArrayList<>();

        jumpsCollection.put("I" + nrI, new ArrayList<>());
        jumpsCollection.get("I" + nrI).add("." + start);
        INC(start, modifiedProductions, "I" + nrI);

        jumpsCollection.get("I" + nrI).addAll(modifiedProductions);
        nrI++;

        collection();

        TablesConstruction();

        showTables();
    }

    private static String findKey(String value) {
        for (Map.Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            if (entry.getValue().contains(value))
                return entry.getKey();
        }

        return "";
    }

    private static ArrayList<String> getAllValues(String key, Map<String, ArrayList<String>> map) {
        ArrayList<String> values = new ArrayList<>();

        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            if (entry.getKey().equals(key))
                values = entry.getValue();
        }

        return values;
    }

    private static ArrayList<String> findLast(String key, ArrayList<String> last) {
        ArrayList<String> values = getAllValues(key, productions);

        for (String value : values) {
            if (Character.isUpperCase(value.charAt(value.length() - 1))) {
                findLast(value, last);
            } else {
                last.add(String.valueOf(value.charAt(0)));
            }
        }

        return last;
    }

    private static ArrayList<String> findURM(String key, ArrayList<String> urm) {
        for (Map.Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            String entryKey = entry.getKey();
            ArrayList<String> entryValues = entry.getValue();

            for (String entryValue : entryValues) {
                if (entryValue.contains(key) && entryValue.length() > 1 && entryValue.indexOf(key) + 1 < entryValue.length()) {
                    char nextTerminal = entryValue.charAt(entryValue.indexOf(key) + 1);
                    if (terminals.contains(nextTerminal)) {
                        urm.add(String.valueOf(nextTerminal));
                    }
                } else if (entryValue.equals(key) && !key.equals(entryKey)) {
                    urm.addAll(URMCharacters.get(entryKey));
                }
            }
        }

        return urm;
    }

    private static void PRIM() {
        Set<String> keys = productions.keySet();

        for (String key : keys) {
            if (!PRIMCharacters.containsKey(key)) {
                PRIMCharacters.put(key, new ArrayList<>());
            }
            ArrayList<String> values = findLast(key, new ArrayList<>());
            PRIMCharacters.get(key).addAll(values);
        }
    }

    private static void URM() {
        Set<String> keys = productions.keySet();

        for (String key : keys) {
            if (!URMCharacters.containsKey(key)) {
                URMCharacters.put(key, new ArrayList<>());
            }
            ArrayList<String> values = findURM(key, new ArrayList<>());
            URMCharacters.get(key).addAll(values);
            if (key.equals(start)) {
                URMCharacters.get(key).add("$");
            }
        }
    }

    private static void SALT(String key, String symbol, int indexI) {
        ArrayList<String> values = getAllValues(key, jumpsCollection);
        HashMap<String, ArrayList<String>> modifiedProductions = new HashMap<>();
        ArrayList<String> modifiedProductions2 = new ArrayList<>();
        HashMap<String, ArrayList<String>> temp = new HashMap<>();
        modifiedProductions.put("I" + nrI, new ArrayList<>());
        int ok = 0;
        String characterINC = "";
        String keyEqual = "";

        for (String value : values) {
            char[] valueAsChar = value.toCharArray();

            for (int i = 0; i < valueAsChar.length; i++) {
                if (valueAsChar[i] == '.') {
                    if (i + 1 < valueAsChar.length && valueAsChar[i + 1] == symbol.charAt(0)) {
                        valueAsChar[i] = symbol.charAt(0);
                        valueAsChar[i + 1] = '.';
                        modifiedProductions.get("I" + nrI).add(String.valueOf(valueAsChar));

                        if (valueAsChar.length > i + 2) {
                            if (nonterminals.contains(valueAsChar[i + 2])) {
                                ok = 1;
                                characterINC = String.valueOf(valueAsChar[i + 2]);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (modifiedProductions.isEmpty()) {
            System.out.println("Jump Error!");
            return;
        }

        if (!checkIfSame("I" + nrI, modifiedProductions, indexI, symbol)) {
            if (!checkIfContainsFirst("I" + nrI, modifiedProductions, indexI, symbol)) {
                jumpsCollection.putAll(modifiedProductions);
                temp.putAll(modifiedProductions);

                if(!jumps.containsKey(nrI + "")){
                    jumps.put(nrI + "", new ArrayList<>());
                }

                jumps.get(nrI + "").add("" + indexI + symbol);
            } else {
                nrI--;
                return;
            }
        } else {
            nrI--;
        }

        if (ok == 1) {
            INC(characterINC, modifiedProductions2, "I" + nrI);
            modifiedProductions.put("I" + nrI, modifiedProductions2);

            if (!checkIfSame("I" + nrI, modifiedProductions, indexI, keyEqual)) {
                temp.get("I" + nrI).addAll(modifiedProductions2);

                if(!jumps.containsKey(nrI + "")){
                    jumps.put(nrI + "", new ArrayList<>());
                }

                jumps.get(nrI + "").add("" + indexI + symbol);
            } else {
                nrI--;
            }
        }
    }

    private static boolean checkIfSame(String key, HashMap<String, ArrayList<String>> modifiedProductions, int indexI, String symbol) {
        ArrayList<String> modifiedValues = getAllValues(key, modifiedProductions);
        Set<String> keys = jumpsCollection.keySet();

        for (String keyInJumpsCollection : keys) {
            ArrayList<String> values = getAllValues(keyInJumpsCollection, jumpsCollection);
            if (modifiedValues.equals(values)) {
                jumps.get(keyInJumpsCollection.charAt(1) + "").add("" + indexI + symbol);

                return true;
            }
        }

        return false;
    }

    private static boolean checkIfContainsFirst(String key, HashMap<String, ArrayList<String>> modifiedProductions, int indexI, String symbol) {
        String valueModified = modifiedProductions.get(key).get(0);
        String originalValue = "";
        Set<String> keys = jumpsCollection.keySet();

        for (String keyInJumpsCollection : keys) {
            originalValue = jumpsCollection.get(keyInJumpsCollection).get(0);
            if (valueModified.equals(originalValue)) {
                jumps.get(keyInJumpsCollection.charAt(1) + "").add("" + indexI + symbol);

                return true;
            }
        }

        return false;
    }

    private static void INC(String key, ArrayList<String> modifiedProductions, String jumpCollectionKey) {
        ArrayList<String> values = getAllValues(jumpCollectionKey, jumpsCollection);
        ArrayList<String> newValues = new ArrayList<>();

        for (String value : values) {
            if (value.contains(".") && value.indexOf('.') + 1 <= value.length()) {
                if (nonterminals.contains(value.charAt(value.indexOf('.') + 1))) {
                    for (String newValue : getAllValues(key, productions)) {
                        if (getAllValues(key, productions).contains("a")) {
                            for (String val : getAllValues(key, productions)) {
                                newValues.add("." + val);
                            }

                            modifiedProductions.addAll(newValues);

                            return;
                        } else if (!modifiedProductions.contains(newValue)) {
                            newValues.add("." + newValue);

                            if (!(newValue.charAt(newValue.indexOf('.') + 1) == key.charAt(key.length() - 1))) {
                                modifiedProductions.addAll(newValues);

                                INC(newValue.charAt(newValue.indexOf('.') + 1) + "", modifiedProductions, jumpCollectionKey);

                                return;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private static void collection() {
        Set<String> keys = jumpsCollection.keySet();

        for (int i = 0; i < keys.size(); i++) {
            ArrayList<String> values = getAllValues("I" + i, jumpsCollection);
            ArrayList<String> usedKeys = new ArrayList<>();

            for (String value : values) {
                if (value.contains(".")) {
                    if (value.indexOf('.') + 1 < value.length()) {
                        String key = value.charAt(value.indexOf('.') + 1) + "";
                        
                        if (!usedKeys.contains(key)){
                            if (!jumps.containsKey(nrI + "")) {
                                jumps.put(nrI + "", new ArrayList<>());
                            }

                            SALT("I" + i, key, i);
                            jumps.get(nrI + "").add("" + i + value.charAt(value.indexOf('.') + 1));
                            nrI++;
                        }

                        usedKeys.add(key);
                    }
                }
            }
        }
    }

    private static void initTables() {
        Set<String> keys = jumpsCollection.keySet();

        TA = new String[keys.size() + 1][terminals.size() + 1];
        for (int j = 0; j < terminals.size(); j++) {
            TA[0][j] = terminals.get(j) + "";
        }

        TA[0][terminals.size()] = "$";
        for (int i = 1; i < keys.size() + 1; i++) {
            for (int j = 0; j < terminals.size() + 1; j++) {
                TA[i][j] = "00";
            }
        }

        TS = new String[keys.size() + 1][nonterminals.size()];
        for (int j = 0; j < nonterminals.size(); j++) {
            TS[0][j] = nonterminals.get(j) + "";
        }

        for (int i = 1; i < keys.size() + 1; i++) {
            for (int j = 0; j < nonterminals.size(); j++) {
                TS[i][j] = "00";
            }
        }
    }

    public static void showTables() {
        System.out.println("----------------------TA--------------------");
        for (int i = 0; i < TA.length; i++) {
            for (int j = 0; j < TA[0].length; j++) {
                System.out.print(TA[i][j] + "    ");
            }
            System.out.println();
        }

        System.out.println("--------------------TS------------------");
        for (int i = 0; i < TS.length; i++) {
            for (int j = 0; j < TS[0].length; j++) {
                System.out.print(TS[i][j] + "    ");
            }
            System.out.println();
        }
    }

    private static void TablesConstruction() {
        Set<String> keys = jumpsCollection.keySet();
        String jumpIndex = null;

        initTables();

        for (int i = 0; i < keys.size(); i++) {
            ArrayList<String> values = getAllValues("I" + i, jumpsCollection);

            for (String value : values) {
                if (value.contains(".")) {
                    if (value.indexOf('.') + 1 < value.length()) {
                        jumpIndex = findJump("" + i + value.charAt(value.indexOf('.') + 1));

                        if (nonterminals.contains(value.charAt(value.indexOf('.') + 1))) {
                            TS[i + 1][nonterminals.indexOf(value.charAt(value.indexOf('.') + 1))] = jumpIndex + "";
                        } else if (terminals.contains(value.charAt(value.indexOf('.') + 1))) {
                            TA[i + 1][terminals.indexOf(value.charAt(value.indexOf('.') + 1))] = "d" + jumpIndex;
                        }

                    } else if (value.indexOf('.') == value.length() - 1) {
                        if (value.charAt(value.indexOf('.') - 1) == start.charAt(0)) {
                            TA[i + 1][terminals.size()] = "acc";
                        } else if (nonterminals.contains(value.charAt(value.indexOf('.') - 1))) {
                            addReductionToTA(value, i);
                        } else if (terminals.contains(value.charAt(value.indexOf('.') - 1))){
                            addReductionToTA(value, i);
                        }
                    }
                }
            }
        }
    }

    private static void addReductionToTA(String value, int i){
        String valueWithoutDot = value.substring(0, value.indexOf("."));

        for (int j = 0; j < URMCharacters.get(findKey(valueWithoutDot)).size(); j++) {
            for (int k = 0; k < terminals.size() + 1; k++) {
                if (URMCharacters.get(findKey(valueWithoutDot)).contains(TA[0][k])) {
                    TA[i + 1][k] = "r" + findIndexProduction(value);
                }
            }
        }
    }

    private static String findJump(String value) {
        Set<String> keys = jumps.keySet();

        for (String key : keys) {
            if (jumps.get(key).contains(value)) {
                return key;
            }
        }

        return "";
    }

    private static int findIndexProduction(String value) {
        int counter = 1;
        String valueWithoutDot = value.substring(0, value.indexOf("."));

        for (Map.Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            for(String production : entry.getValue()){
                if(production.equals(valueWithoutDot)){
                    return counter;
                }
                counter++;
            }
        }

        return 0;
    }

    public String[][] combineTables(){
        String[][] matrix = new String[TA.length][nonterminals.size()+ terminals.size()+1];

        for(int i=0;i<TA.length;i++){
            for(int j = 0; j<(nonterminals.size()+ terminals.size())+1; j++){
                if(j<= terminals.size()){
                    matrix[i][j] = TA[i][j];
                }else{
                    matrix[i][j] = TS[i][j- terminals.size()-1];
                }
            }
        }

        return matrix;
    }
}
