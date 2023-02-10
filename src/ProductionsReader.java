import java.util.*;

public class ProductionsReader {
    public LinkedHashMap<String, ArrayList<String> >readProductions(String[] sirCitit){
        LinkedHashMap<String, ArrayList<String>> productions = new LinkedHashMap<>();

        String substringEcuatii = sirCitit[1].substring(sirCitit[1].indexOf("{") + 1);
        String[] productie = substringEcuatii.split(",");

        for (int k = 0; k < productie.length; k++) {
            productie[k] = productie[k].replaceAll("\\s+", "");
            String cheie = productie[k].substring(0, productie[k].indexOf("-"));

            productions.put(cheie, new ArrayList<>());

            productie[k] = productie[k].substring(productie[k].indexOf(">") + 1);

            if (productie[k].contains("|")) {
                String[] splitProductie = productie[k].split("\\|");

                for (String a : splitProductie) {
                    productions.get(cheie).add(a);
                }

            } else {
                productions.get(cheie).add(productie[k]);
            }
        }

        return productions;
    }

    public List<Character> readNonterminals(String[] sirCitit){
        List<Character> nonterminals = new ArrayList<>();

        String nonterminalsRaw = sirCitit[1].substring(sirCitit[1].indexOf("{") + 1);

        for (int i = 0; i < nonterminalsRaw.length(); i++) {
            if (Character.isUpperCase(nonterminalsRaw.charAt(i))) {
                nonterminals.add(nonterminalsRaw.charAt(i));
            }
        }

        return nonterminals;
    }

    public List<Character> readTerminals(String[] sirCitit){
        List<Character> terminals = new ArrayList<>();

        String terminalsRaw = sirCitit[1].substring(sirCitit[1].indexOf("{") + 1);

        for (int i = 0; i < terminalsRaw.length(); i++) {
            if (terminalsRaw.charAt(i) != ',') {
                terminals.add(terminalsRaw.charAt(i));
            }
        }

        return terminals;
    }
}
