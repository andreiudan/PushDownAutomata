import java.util.*;

public class ProductionsReader {
    public LinkedHashMap<String, ArrayList<String>> readProductions(String[] scannedString){
        LinkedHashMap<String, ArrayList<String>> productions = new LinkedHashMap<>();

        String ecuations = scannedString[1].substring(scannedString[1].indexOf("{") + 1);
        String[] production = ecuations.split(",");

        for (int k = 0; k < production.length; k++) {
            production[k] = production[k].replaceAll("\\s+", "");
            String key = production[k].substring(0, production[k].indexOf("-"));

            productions.put(key, new ArrayList<>());

            production[k] = production[k].substring(production[k].indexOf(">") + 1);

            if (production[k].contains("|")) {
                String[] splitProduction = production[k].split("\\|");

                for (String a : splitProduction) {
                    productions.get(key).add(a);
                }

            } else {
                productions.get(key).add(production[k]);
            }
        }

        return productions;
    }

    public List<Character> readNonterminals(String[] scannedString){
        List<Character> nonterminals = new ArrayList<>();

        String nonterminalsRaw = scannedString[1].substring(scannedString[1].indexOf("{") + 1);

        for (int i = 0; i < nonterminalsRaw.length(); i++) {
            if (Character.isUpperCase(nonterminalsRaw.charAt(i))) {
                nonterminals.add(nonterminalsRaw.charAt(i));
            }
        }

        return nonterminals;
    }

    public List<Character> readTerminals(String[] scannedString){
        List<Character> terminals = new ArrayList<>();

        String terminalsRaw = scannedString[1].substring(scannedString[1].indexOf("{") + 1);

        for (int i = 0; i < terminalsRaw.length(); i++) {
            if (terminalsRaw.charAt(i) != ',') {
                terminals.add(terminalsRaw.charAt(i));
            }
        }

        return terminals;
    }
}
