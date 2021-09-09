package sample;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Data {
    static Map<String, Tab> tabpanes = new Hashtable<>();
    Map<String, Node> labels = new Hashtable<>();
    static Map<String, RadioButton> toogels = new Hashtable<>();
    Map<String, List<String>> comboBoxLinkedItems = new Hashtable<>();

    LinkedHashMap<String, DataHash> cas_najemaMap = new LinkedHashMap<>();
    LinkedHashMap<String, DataHash> izbira_vozilaMap = new LinkedHashMap<>();
    LinkedHashMap<String, DataHash> lokacija_prevzema_in_oddajeMap = new LinkedHashMap<>();
    LinkedHashMap<String, DataHash> podatki_strankeMap = new LinkedHashMap<>();
    LinkedHashMap<String, DataHash> placiloMap = new LinkedHashMap<>();

    public LinkedHashMap<String, DataHash> allInputFields = new LinkedHashMap<>();

    public BorderPane width;
    public static TabPane tabPane;

    Data() {
    }

    public void setMapAndWidth(BorderPane width, TabPane tabPane) {
        allInputFields.putAll(cas_najemaMap);
        allInputFields.putAll(izbira_vozilaMap);
        allInputFields.putAll(lokacija_prevzema_in_oddajeMap);
        allInputFields.putAll(podatki_strankeMap);
        allInputFields.putAll(placiloMap);
        this.width = width;
        Data.tabPane = tabPane;
    }

    public void dataUpdateValues() {
        allInputFields.forEach((k, v) -> {      // key, value
            v.updateValue();
        });
    }

    public void dataReset() {
        allInputFields.forEach((k, v) -> {      // key, value
            v.resetValue();
        });
    }

    public void bindWidthAll() {
        allInputFields.forEach((k, v) -> {      // key, value
            Tools.bindWidth(v.node, width, 0.2);
        });

        width.widthProperty().addListener((obs, oldVal, newVal) -> {
            labels.forEach((k, v) -> {      // key, value
                Tools.bindWidth(v, width, 0.15);
            });
        });
    }

    public void labelNodeBindAll() {
        labels.forEach((k, v) -> {      // key, value
            //System.out.println(k+" "+v);
            Tools.labelNodeBind(v, allInputFields.get(k).node);
        });
    }

    @Override
    public String toString() {
        return "\nČas najema:" +
                "\n   datum od: "+ allInputFields.get("datum_od").getValue() +
                "\n   datum do: "+ allInputFields.get("datum_do").getValue() +
                "\n   ura od: "+ allInputFields.get("ura_od").getValue() +
                "\n   ura do: "+ allInputFields.get("ura_do").getValue() +
                "\nIzbira vozila:" +
                "\n   vrsta vozila: "+ allInputFields.get("izbira_vozila").getValue() +
                "\n   velikost vozila: "+ allInputFields.get("velikost_vozila").getValue() +
                "\n   moznosti izbire vozila: "+ allInputFields.get("moznosti_izbire_vozila").getValue() +
                "\n   menjalnik: "+ allInputFields.get("menjalnik").getValue() +
                "\n   gorivo: "+ allInputFields.get("gorivo").getValue() +
                "\n   dodatno zavarovanje: "+ (allInputFields.get("dodatno_zavarovanje").getValue().equals("true")? "Da": "Ne") +
                "\nZavarovanje:" +
                "\n   lokacija prevzema: "+ allInputFields.get("lokacija_prevzema").getValue() +
                "\n   lokacija oddaje: "+ allInputFields.get("lokacija_oddaje").getValue() +
                "\nPodatki stranke:" +
                "\n   ime: "+ allInputFields.get("ime").getValue() +
                "\n   priimek: "+ allInputFields.get("priimek").getValue() +
                "\n   naslov: "+ allInputFields.get("naslov").getValue() +
                "\n   starost: "+ allInputFields.get("starost").getValue() +
                "\n   email: "+ allInputFields.get("email").getValue() +
                "\n   telefon: "+ allInputFields.get("telefon").getValue() +
                "\n   starost vozniškega izpita: "+ allInputFields.get("starost_vozniskega_izpita").getValue() +
                "\nPlačilo:" +
                "\n   izbira načina plačila: "+ allInputFields.get("izbira_nacina_placila").getValue();
    }
}
