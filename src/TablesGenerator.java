import java.util.*;

public class TablesGenerator {

    public static List<Character> neterminale = new ArrayList<>();
    public static List<Character> terminale = new ArrayList<>();
    public static Map<String, ArrayList<String>> productii = new LinkedHashMap<String, ArrayList<String>>();
    public static String start = null;
    public static HashMap<String, ArrayList<String>> caracterePRIM = new LinkedHashMap<>();
    public static HashMap<String, ArrayList<String>> caractereURM = new LinkedHashMap<>();
    public static HashMap<String, ArrayList<String>> multimeDeStari = new LinkedHashMap<>();
    public static HashMap<String, ArrayList<String>> salturi = new LinkedHashMap<>();
    public static String[][] TA;
    public static String[][] TS;
    public static int nrI = 0;

    public TablesGenerator(List<Character> neterminale, List<Character> terminale, Map<String, ArrayList<String>> productii, String start) {
        this.neterminale = neterminale;
        this.terminale = terminale;
        this.productii = productii;
        this.start = start;
    }

    public void generateTables(){
        PRIM();

        URM();

        ArrayList<String> productiiModificate = new ArrayList<>();

        multimeDeStari.put("I" + nrI, new ArrayList<>());
        multimeDeStari.get("I" + nrI).add("." + start);
        INC(start, productiiModificate, "I" + nrI);

        multimeDeStari.get("I" + nrI).addAll(productiiModificate);
        nrI++;

        colectie();

        constructieTabele();

        showTables();
    }

    public static String findKey(String valoare) {
        for (Map.Entry<String, ArrayList<String>> entry : productii.entrySet()) {
            if (entry.getValue().contains(valoare))
                return entry.getKey();
        }
        return "";
    }

    public static ArrayList<String> getAllValues(String cheie, Map<String, ArrayList<String>> map) {
        ArrayList<String> valori = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            if (entry.getKey().equals(cheie))
                valori = entry.getValue();
        }
        return valori;
    }

    public static ArrayList<String> findLast(String cheie, ArrayList<String> last) {
        ArrayList<String> valori = getAllValues(cheie, productii);
        for (String valoare : valori) {
            if (Character.isUpperCase(valoare.charAt(valoare.length() - 1))) {
                findLast(valoare, last);
            } else {
                last.add(String.valueOf(valoare.charAt(0)));
            }
        }
        return last;
    }

    public static ArrayList<String> findURM(String cheie, ArrayList<String> urm) {
        for (Map.Entry<String, ArrayList<String>> entry : productii.entrySet()) {
            String cheie2 = entry.getKey();
            ArrayList<String> valori = entry.getValue();

            for (String valoare : valori) {
                if (valoare.contains(cheie) && valoare.length() > 1 && valoare.indexOf(cheie) + 1 < valoare.length()) {
                    if (terminale.contains(valoare.charAt(valoare.indexOf(cheie) + 1))) {
                        urm.add(String.valueOf(valoare.charAt(valoare.indexOf(cheie) + 1)));
                    }
                } else if (valoare.equals(cheie) && !cheie.equals(cheie2)) {
                    urm.addAll(caractereURM.get(cheie2));
                }
            }
        }
        return urm;
    }

    public static void PRIM() {
        Set<String> chei = productii.keySet();

        for (String cheie : chei) {
            if (!caracterePRIM.containsKey(cheie)) {
                caracterePRIM.put(cheie, new ArrayList<>());
            }
            ArrayList<String> valori = findLast(cheie, new ArrayList<>());
            caracterePRIM.get(cheie).addAll(valori);
        }
    }

    public static void URM() {
        Set<String> chei = productii.keySet();

        for (String cheie : chei) {
            if (!caractereURM.containsKey(cheie)) {
                caractereURM.put(cheie, new ArrayList<>());
            }
            ArrayList<String> valori = findURM(cheie, new ArrayList<>());
            caractereURM.get(cheie).addAll(valori);
            if (cheie.equals(start)) {
                caractereURM.get(cheie).add("$");
            }
        }
    }

    public static void SALT(String cheie, String simbol, int indexI) {
        ArrayList<String> valori = getAllValues(cheie, multimeDeStari);
        HashMap<String, ArrayList<String>> productiiModificate = new HashMap<>();
        ArrayList<String> productiiModificate2 = new ArrayList<>();
        HashMap<String, ArrayList<String>> temp = new HashMap<>();
        productiiModificate.put("I" + nrI, new ArrayList<>());
        int ok = 0;
        String characterINC = "";
        String cheieEgal = "";

        for (String valoare : valori) {
            if (ok == 1) {
                break;
            }
            ok = 0;
            char[] valoareChar = valoare.toCharArray();
            for (int i = 0; i < valoareChar.length; i++) {
                if (valoareChar[i] == '.') {
                    if (i + 1 < valoareChar.length && valoareChar[i + 1] == simbol.charAt(0)) {
                        valoareChar[i] = simbol.charAt(0);
                        valoareChar[i + 1] = '.';
                        productiiModificate.get("I" + nrI).add(String.valueOf(valoareChar));
                        if (valoareChar.length > i + 2) {
                            if (neterminale.contains(valoareChar[i + 2])) {
                                ok = 1;
                                characterINC = String.valueOf(valoareChar[i + 2]);
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (productiiModificate.isEmpty()) {
            System.out.println("Eoare in salt");
            return;
        }

        if (!checkIfSame("I" + nrI, productiiModificate, indexI, simbol)) {
            if (!checkIfContainsFirst("I" + nrI, productiiModificate, indexI, simbol)) {
                multimeDeStari.putAll(productiiModificate);
                temp.putAll(productiiModificate);
                if(!salturi.containsKey(nrI + "")){
                    salturi.put(nrI + "", new ArrayList<>());
                }
                salturi.get(nrI + "").add("" + indexI + simbol);
            } else {
                nrI--;
                return;
            }
        } else {
            nrI--;
        }

        if (ok == 1) {
            INC(characterINC, productiiModificate2, "I" + nrI);
            productiiModificate.put("I" + nrI, productiiModificate2);

            if (!checkIfSame("I" + nrI, productiiModificate, indexI, cheieEgal)) {
                temp.get("I" + nrI).addAll(productiiModificate2);
                if(!salturi.containsKey(nrI + "")){
                    salturi.put(nrI + "", new ArrayList<>());
                }
                salturi.get(nrI + "").add("" + indexI + simbol);
            } else {
                nrI--;
            }
        }
    }

    public static boolean checkIfSame(String cheie, HashMap<String, ArrayList<String>> productiiModificate, int indexI, String simbol) {
        ArrayList<String> valoriModificate = getAllValues(cheie, productiiModificate);
        Set<String> chei = multimeDeStari.keySet();

        for (String cheie2 : chei) {
            ArrayList<String> valori = getAllValues(cheie2, multimeDeStari);
            if (valoriModificate.equals(valori)) {
                salturi.get(cheie2.charAt(1) + "").add("" + indexI + simbol);
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfContainsFirst(String cheie, HashMap<String, ArrayList<String>> productiiModificate, int indexI, String simbol) {
        String valMod = productiiModificate.get(cheie).get(0);
        String val = "";
        Set<String> chei = multimeDeStari.keySet();

        for (String cheie2 : chei) {
            val = multimeDeStari.get(cheie2).get(0);
            if (valMod.equals(val)) {
                salturi.get(cheie2.charAt(1) + "").add("" + indexI + simbol);
                return true;
            }
        }
        return false;
    }

    public static void INC(String cheie, ArrayList<String> productiiModificate, String cheieStare) {
        ArrayList<String> valori = getAllValues(cheieStare, multimeDeStari);
        ArrayList<String> valoriNoi = new ArrayList<>();

        for (String valoare : valori) {
            if (valoare.contains(".") && valoare.indexOf('.') + 1 <= valoare.length()) {
                if (neterminale.contains(valoare.charAt(valoare.indexOf('.') + 1))) {
                    for (String valoareNoua : getAllValues(cheie, productii)) {
                        if (getAllValues(cheie, productii).contains("a")) {
                            for (String val : getAllValues(cheie, productii)) {
                                valoriNoi.add("." + val);
                            }
                            productiiModificate.addAll(valoriNoi);
                            return;
                        } else if (!productiiModificate.contains(valoareNoua)) {
                            valoriNoi.add("." + valoareNoua);
                            if (!(valoareNoua.charAt(valoareNoua.indexOf('.') + 1) == cheie.charAt(cheie.length() - 1))) {
                                productiiModificate.addAll(valoriNoi);

                                INC(valoareNoua.charAt(valoareNoua.indexOf('.') + 1) + "", productiiModificate, cheieStare);
                                return;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public static void colectie() {
        Set<String> chei = multimeDeStari.keySet();
        for (int i = 0; i < chei.size(); i++) {
            ArrayList<String> valori = getAllValues("I" + i, multimeDeStari);
            ArrayList<String> cheiFolosite = new ArrayList<>();
            for (String valoare : valori) {
                if (valoare.contains(".")) {
                    if (valoare.indexOf('.') + 1 < valoare.length()) {
                        if (!cheiFolosite.contains(valoare.charAt(valoare.indexOf('.') + 1) + "")) {
                            if (!salturi.containsKey(nrI + "")) {
                                salturi.put(nrI + "", new ArrayList<>());
                            }
                            SALT("I" + i, valoare.charAt(valoare.indexOf('.') + 1) + "", i);
                            salturi.get(nrI + "").add("" + i + valoare.charAt(valoare.indexOf('.') + 1));
                            nrI++;
                        }
                        cheiFolosite.add(valoare.charAt(valoare.indexOf('.') + 1) + "");
                    }
                }
            }
        }
    }

    public static void initTables() {
        Set<String> chei = multimeDeStari.keySet();

        TA = new String[chei.size() + 1][terminale.size() + 1];
        for (int j = 0; j < terminale.size(); j++) {
            TA[0][j] = terminale.get(j) + "";
        }

        TA[0][terminale.size()] = "$";
        for (int i = 1; i < chei.size() + 1; i++) {
            for (int j = 0; j < terminale.size() + 1; j++) {
                TA[i][j] = "00";
            }
        }

        TS = new String[chei.size() + 1][neterminale.size()];
        for (int j = 0; j < neterminale.size(); j++) {
            TS[0][j] = neterminale.get(j) + "";
        }

        for (int i = 1; i < chei.size() + 1; i++) {
            for (int j = 0; j < neterminale.size(); j++) {
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

    public static void constructieTabele() {
        Set<String> chei = multimeDeStari.keySet();
        initTables();
        String indexSalt = null;

        for (int i = 0; i < chei.size(); i++) {
            ArrayList<String> valori = getAllValues("I" + i, multimeDeStari);
            for (String valoare : valori) {
                if (valoare.contains(".")) {
                    if (valoare.indexOf('.') + 1 < valoare.length()) {

                        indexSalt = findSalt("" + i + valoare.charAt(valoare.indexOf('.') + 1));

                        if (neterminale.contains(valoare.charAt(valoare.indexOf('.') + 1))) {
                            TS[i + 1][neterminale.indexOf(valoare.charAt(valoare.indexOf('.') + 1))] = indexSalt + "";
                        } else if (terminale.contains(valoare.charAt(valoare.indexOf('.') + 1))) {
                            TA[i + 1][terminale.indexOf(valoare.charAt(valoare.indexOf('.') + 1))] = "d" + indexSalt;
                        }
                    } else if (valoare.indexOf('.') == valoare.length() - 1) {
                        if (valoare.charAt(valoare.indexOf('.') - 1) == start.charAt(0)) {
                            TA[i + 1][terminale.size()] = "acc";
                        } else if (neterminale.contains(valoare.charAt(valoare.indexOf('.') - 1))) {
                            String vallueWithoutDot = valoare.substring(0, valoare.indexOf("."));
                            for (int j = 0; j < caractereURM.get(findKey(vallueWithoutDot)).size(); j++) {
                                for (int k = 0; k < terminale.size() + 1; k++) {
                                    if (caractereURM.get(findKey(vallueWithoutDot)).contains(TA[0][k])) {
                                        TA[i + 1][k] = "r" + findIndexProduction(valoare);
                                    }
                                }
                            }
                        } else if (terminale.contains(valoare.charAt(valoare.indexOf('.') - 1))){
                            String vallueWithoutDot = valoare.substring(0, valoare.indexOf("."));
                            for (int j = 0; j < caractereURM.get(findKey(vallueWithoutDot)).size(); j++) {
                                for (int k = 0; k < terminale.size() + 1; k++) {
                                    if (caractereURM.get(findKey(vallueWithoutDot)).contains(TA[0][k])) {
                                        TA[i + 1][k] = "r" + findIndexProduction(valoare);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static String findSalt(String valoare) {
        Set<String> chei = salturi.keySet();

        for (String cheie : chei) {
            if (salturi.get(cheie).contains(valoare)) {
                return cheie;
            }
        }
        return "";
    }

    public static int findIndexProduction(String valoare) {
        int counter = 1;
        String valoareFaraPunct = valoare.substring(0, valoare.indexOf("."));

        for (Map.Entry<String, ArrayList<String>> entry : productii.entrySet()) {
            for(String productie : entry.getValue()){
                if(productie.equals(valoareFaraPunct)){
                    return counter;
                }
                counter++;
            }
        }
        return 0;
    }

    public static String[][] combineTables(){
        String[][] matrix = new String[TA.length][neterminale.size()+terminale.size()+1];

        for(int i=0;i<TA.length;i++){
            for(int j=0;j<(neterminale.size()+terminale.size())+1;j++){
                if(j<=terminale.size()){
                    matrix[i][j] = TA[i][j];
                }else{
                    matrix[i][j] = TS[i][j-terminale.size()-1];
                }
            }
        }

        return matrix;
    }
}
