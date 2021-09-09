package sample;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {
    private static final List<String> lokacije = List.of("Ljubljana", "Maribor", "Celje", "Kranj", "Velenje", "Koper", "Novo Mesto", "Murska Sobota", "Jesenice", "Portorož", "letališče Brnik", "letališče Maribor");
    public TabPane tabPane;

    Map<String, Object> vozilaMainMap =new HashMap<>();

    public static final String badBackground = "redBackground";
    public static final String badBackgroundPane = "redBackgroundPane";
    public static final String badBackgroundFieldOnly = "redBackgroundFieldOnly";

    public static final DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("d/M/y");
    public Integer cas_izposoje = null;
    public Integer cena_najema_na_dan = null;
    public Integer dodatno_zavarovanje = 2;

    private String karticaA = null;
    private String ccvA = null;

    public BorderPane width;
    public Tab cas_najema_tab;
    public Label datum_od_label;
    public Label datum_do_label;
    public DatePicker datum_od_datepicker;
    public DatePicker datum_do_datepicker;
    public Label ura_od_label;
    public Label ura_do_label;
    public Spinner<Integer> ura_od_spinner;
    public Spinner<Integer> ura_do_spinner;
    public Label cas_v_dnevih_label;


    public Tab izbira_vozila_tab;
    public Label vrsta_vozila_label;
    public ComboBox<String> vrsta_vozila_combobox;
    public Label izbira_vozila_label;
    public ComboBox<String> izbira_vozila_combobox;
    public Label velikost_vozila_label;
    public ComboBox<String> velikost_vozila_combobox;
    public Label menjalnik_label;
    public ComboBox<String> menjalnik_combobox;
    public Label gorivo_label;
    public ComboBox<String> gorivo_combobox;
    public Label dodatno_zavarovanje_label;
    public CheckBox dodatno_zavarovanje_checkbox;
    public Label cena_najema_na_dan_label;

    public Tab lokacija_prevzema_in_oddaje_tab;
    public Label lokacija_prevzema_label;
    public Label lokacija_oddaje_label;
    public ComboBox<String> lokacija_prevzema_combobox;
    public ComboBox<String> lokacija_oddaje_combobox;

    public Tab podatki_stranke_tab;
    public Label ime_label;
    public TextField ime_textfield;
    public Label email_label;
    public TextField email_textfield;
    public Label priimek_label;
    public TextField priimek_textfield;
    public Label telefon_label;
    public TextField telefon_textfield;
    public Label naslov_label;
    public TextField naslov_textfield;
    public Label starost_label;
    public TextField starost_textfield;
    public Label starost_vozniskega_izpita_label;
    public TextField starost_vozniskega_izpita_textfield;

    public Tab placilo_tab;
    public Label izbira_nacina_placila_label;
    public ComboBox<String> izbira_nacina_placila_combobox;
    public Button ponastavi_button;
    public Label status_placila_label;

    public Label statusna_vrstica_label;


    public Data data = new Data();

    public void pobrisi() {
        Tools.removeStyleClassTabAll(Data.tabpanes, badBackgroundPane);
        data.dataReset();

        status_placila_label.setText("Status plačila:");
        statusna_vrstica_label.setText("Izračun:");
        cas_v_dnevih_label.setText("Izposoja:");
    }

    public void izdaj_racun() {      // Action event
        if (checkData()) {
            data.dataUpdateValues();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pogovorno okno z informacijami");
            alert.setHeaderText("Račun:");
            alert.setContentText(data.toString()+
                    "\n"+ (karticaA != null? ("   uporabljena bančna kartica: xxxx-xxxx-xxxx-"+ karticaA.replaceAll("[\\- ]?", "").substring(12)): "") +
                    "\n\nČas izposoje (v dnevih): "+ cas_izposoje+
                    "\nZnesek: "+ cas_izposoje * (cena_najema_na_dan + (dodatno_zavarovanje!=null? dodatno_zavarovanje: 0)) + "€");
            alert.setWidth(500);
            alert.setHeight(700);
            alert.showAndWait();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean checkData() {
        AtomicBoolean allChecksPassed = new AtomicBoolean(true);
        ponastavi_button.requestFocus();

        Tools.removeStyleClassTabAll(Data.tabpanes, badBackgroundPane);
        //System.out.println(data.allInputFields);

        data.cas_najemaMap.forEach((k, v) -> {
            individualChecker(k, v, allChecksPassed, cas_najema_tab);
        });
        data.izbira_vozilaMap.forEach((k, v) -> {
            individualChecker(k, v, allChecksPassed, izbira_vozila_tab);
        });
        data.lokacija_prevzema_in_oddajeMap.forEach((k, v) -> {
            individualChecker(k, v, allChecksPassed, lokacija_prevzema_in_oddaje_tab);
        });
        data.podatki_strankeMap.forEach((k, v) -> {
            individualChecker(k, v, allChecksPassed, podatki_stranke_tab);
        });
        data.placiloMap.forEach((k, v) -> {
            individualChecker(k, v, allChecksPassed, placilo_tab);
        });

        return allChecksPassed.get();
    }

    public void individualChecker(String k, DataHash v, AtomicBoolean allChecksPassed, Tab currentTab) {
        String nodetype = Tools.getNodeType(v.node).toLowerCase();
        if (nodetype.equals("textfield") || nodetype.equals("datepicker") || nodetype.equals("combobox")) {
            boolean beforeAllChecksPassed = allChecksPassed.get();
            Node node = (Node) v.node;
            if (node.getStyleClass().contains(badBackground)) {
                allChecksPassed.set(false);
                Tools.ifEmpty(beforeAllChecksPassed, node, currentTab);
                Tools.addStyleClassTabPaneParent(currentTab, badBackgroundPane);
            } else {
                v.updateValue();
                String temp = v.getValue();
                if (temp.equals("null") || temp.equals("")) {
                    if (nodetype.equals("textfield")) {
                        Tools.addStyleClass(node, badBackground);
                        Tools.setTextFieldTooltipWhenEmpty((TextField) node);
                    } else if (nodetype.equals("datepicker") || nodetype.equals("combobox")) {
                        Tools.addStyleClass(node, badBackgroundFieldOnly);
                        if (nodetype.equals("combobox"))
                            Tools.setComboBoxTooltipWhenEmpty((ComboBox<String>) node);
                    } else
                        Tools.addStyleClass(node, badBackground);
                    allChecksPassed.set(false);
                    Tools.ifEmpty(beforeAllChecksPassed, node, currentTab);
                    Tools.addStyleClassTabPaneParent(currentTab, badBackgroundPane);
                }
            }
        }
    }

    public void zapri() {
        if (checkIfEmpty())
            System.exit(0);
        else {
            ButtonType close = new ButtonType("Zapri", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Prekliči", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", close, cancel);
            alert.setTitle("Upozorilno okno!");
            alert.setHeaderText("Zapiranje programa:");
            alert.setContentText("Imate podatke v programu, ali ga še vedno želite zapreti (s tem izgubite te podatke)?");
            alert.setHeight(500);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.orElse(close).equals(close)) {
                System.exit(0);
            }
        }

    }

    public boolean checkIfEmpty() {
        AtomicBoolean allChecksPassed = new AtomicBoolean(true);

        data.allInputFields.forEach((k, v) -> {
            String nodetype = Tools.getNodeType(v.node).toLowerCase();

            if (nodetype.equals("textfield") || nodetype.equals("datepicker") || nodetype.equals("combobox")) {
                v.updateValue();
                String temp = v.getValue();

                if (! (temp.equals("null") || temp.equals("")))
                    allChecksPassed.set(false);
            }
        });

        return allChecksPassed.get();
    }

    public void avtor() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pogovorno okno z informacijami");
        alert.setHeaderText("Avtor:");
        alert.setContentText("Miha Krumpestar");
        alert.showAndWait();
    }

    public void pomoc() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pogovorno okno z informacijami");
        alert.setHeaderText("Dodatna pomoč:");
        alert.setContentText("Za dodatne informacije o polju samo pustite miško vsaj 2 sekundi nad njim.");
        alert.setHeight(500);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeData();
        data.setMapAndWidth(width, tabPane);
        setDefault();
        data.bindWidthAll();
        data.labelNodeBindAll();
    }



    public void setDefault() {
        data.dataReset();

        Tools.restrictDatePickerMinMaxDate(datum_od_datepicker, "now", "now+2");

        datum_od_datepicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                Tools.restrictDatePickerMinMaxDate(datum_do_datepicker, String.valueOf(newValue), "now+2");
        });

        Tools.restrictDatePickerMinMaxDate(datum_do_datepicker, "now", "now+2");

        datum_do_datepicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                LocalDateTime min = datum_od_datepicker.getValue().atStartOfDay();
                LocalDateTime max = datum_do_datepicker.getValue().atStartOfDay();
                cas_izposoje = Math.toIntExact(Duration.between(min, max).toDays());
                cas_izposoje += 1;
                cas_v_dnevih_label.setText("Izposoja: " + cas_izposoje + " " + (cas_izposoje > 1 ? "dni" : "dan"));

                if (cena_najema_na_dan != null) {                                                                           // recalculate statusna_vrstica_label
                    if (dodatno_zavarovanje_checkbox.isSelected())
                        statusna_vrstica_label.setText("Izračun: " + cas_izposoje + " " + (cas_izposoje > 1 ? "dni" : "dan") + " x (" + cena_najema_na_dan + "€ najem + " + dodatno_zavarovanje + "€ dodatno zavarovanje) = " + cas_izposoje * (cena_najema_na_dan + dodatno_zavarovanje) + "€");
                    else
                        statusna_vrstica_label.setText("Izračun: " + cas_izposoje + " " + (cas_izposoje > 1 ? "dni" : "dan") + " x " + cena_najema_na_dan + "€ najem = " + cas_izposoje * cena_najema_na_dan + "€");
                }
            } catch (NullPointerException e) {
                // error
            }
        });

        ura_od_spinner.setEditable(true);
        ura_od_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 20, 6));
        ura_od_spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (datum_od_datepicker.getValue().equals(datum_do_datepicker.getValue())) {
                    ura_do_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(newValue + 1, 20, ura_do_spinner.getValue() <= newValue + 1 ? newValue + 1 : ura_do_spinner.getValue())); // minimum lease for one hour
                    ura_od_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 19, newValue));
                }
            } catch (NullPointerException e) {
                // error
            }
        });

        ura_do_spinner.setEditable(true);
        ura_do_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 20, 7));

        setVrstaVozila();

        vrsta_vozila_combobox.getItems().setAll(vozilaMainMap.keySet());
        Tools.invalidNodeInputWhenOutOfFocus(vrsta_vozila_combobox, FXCollections.observableArrayList(vozilaMainMap.keySet()));
        vrsta_vozila_combobox.setOnAction( (ActionEvent e) -> {
            String key = vrsta_vozila_combobox.getSelectionModel().getSelectedItem();
            try {
                velikost_vozila_combobox.setItems(FXCollections.observableArrayList(((Map<String,Object>) vozilaMainMap.get(key)).keySet()));
                Tools.invalidNodeInputWhenOutOfFocus(velikost_vozila_combobox, FXCollections.observableArrayList(((Map<String,Object>) vozilaMainMap.get(key)).keySet()));
            } catch (NullPointerException n) {
                velikost_vozila_combobox.setItems(null);
                vrsta_vozila_combobox.setValue(vrsta_vozila_combobox.getValue());
            }

            izbira_vozila_combobox.setItems(null);
            menjalnik_combobox.setItems(null);
            gorivo_combobox.setItems(null);
        });

        velikost_vozila_combobox.setOnAction( (ActionEvent e) -> {
            String key1 = vrsta_vozila_combobox.getSelectionModel().getSelectedItem();
            String key2 = velikost_vozila_combobox.getSelectionModel().getSelectedItem();
            try {
                izbira_vozila_combobox.setItems(FXCollections.observableArrayList(((Map<String,Object>) ((Map<String,Object>) vozilaMainMap.get(key1)).get(key2)).keySet()));
                Tools.invalidNodeInputWhenOutOfFocus(izbira_vozila_combobox, FXCollections.observableArrayList(((Map<String,Object>) ((Map<String,Object>) vozilaMainMap.get(key1)).get(key2)).keySet()));
            } catch (NullPointerException n) {
                izbira_vozila_combobox.setItems(null);
                velikost_vozila_combobox.setValue(velikost_vozila_combobox.getValue());
            }
            menjalnik_combobox.setItems(null);
            gorivo_combobox.setItems(null);
        });

        izbira_vozila_combobox.setOnAction( (ActionEvent e) -> {
            String key1 = vrsta_vozila_combobox.getSelectionModel().getSelectedItem();
            String key2 = velikost_vozila_combobox.getSelectionModel().getSelectedItem();
            String key3 = izbira_vozila_combobox.getSelectionModel().getSelectedItem();
            try {
                menjalnik_combobox.setItems(FXCollections.observableArrayList(((Map<String,Object>) ((Map<String,Object>) ((Map<String,Object>) vozilaMainMap.get(key1)).get(key2)).get(key3)).keySet()));
                Tools.invalidNodeInputWhenOutOfFocus(menjalnik_combobox, FXCollections.observableArrayList(((Map<String,Object>) ((Map<String,Object>) ((Map<String,Object>) vozilaMainMap.get(key1)).get(key2)).get(key3)).keySet()));
            } catch (NullPointerException n) {
                menjalnik_combobox.setItems(null);
                izbira_vozila_combobox.setValue(izbira_vozila_combobox.getValue());
            }
            gorivo_combobox.setItems(null);
        });

        menjalnik_combobox.setOnAction( (ActionEvent e) -> {
            String key1 = vrsta_vozila_combobox.getSelectionModel().getSelectedItem();
            String key2 = velikost_vozila_combobox.getSelectionModel().getSelectedItem();
            String key3 = izbira_vozila_combobox.getSelectionModel().getSelectedItem();
            String key4 = menjalnik_combobox.getSelectionModel().getSelectedItem();
            try {
                gorivo_combobox.setItems(FXCollections.observableArrayList(((Map<String,Object>) ((Map<String,Object>) ((Map<String,Object>) ((Map<String,Object>) vozilaMainMap.get(key1)).get(key2)).get(key3)).get(key4)).keySet()));
                Tools.invalidNodeInputWhenOutOfFocus(gorivo_combobox, FXCollections.observableArrayList(((Map<String,Object>) ((Map<String,Object>) ((Map<String,Object>) ((Map<String,Object>) vozilaMainMap.get(key1)).get(key2)).get(key3)).get(key4)).keySet()));
            } catch (NullPointerException n) {
                gorivo_combobox.setItems(null);
                menjalnik_combobox.setValue(menjalnik_combobox.getValue());
            }
        });

        gorivo_combobox.setOnAction( (ActionEvent e) -> {
            String key1 = vrsta_vozila_combobox.getSelectionModel().getSelectedItem();
            String key2 = velikost_vozila_combobox.getSelectionModel().getSelectedItem();
            String key3 = izbira_vozila_combobox.getSelectionModel().getSelectedItem();
            String key4 = menjalnik_combobox.getSelectionModel().getSelectedItem();
            String key5 = gorivo_combobox.getSelectionModel().getSelectedItem();
            try {
                cena_najema_na_dan = (int) ((Map<String,Object>) ((Map<String,Object>) ((Map<String,Object>) ((Map<String,Object>) vozilaMainMap.get(key1)).get(key2)).get(key3)).get(key4)).get(key5);
                cena_najema_na_dan_label.setText("Cena najema na dan: "+ cena_najema_na_dan+ "€");

                if (cas_izposoje != null) {                                                                             // recalculate statusna_vrstica_label
                    if (dodatno_zavarovanje_checkbox.isSelected())
                        statusna_vrstica_label.setText("Izračun: "+cas_izposoje +" "+ (cas_izposoje > 1 ? "dni": "dan") +" x ("+ cena_najema_na_dan+"€ najem + "+ dodatno_zavarovanje +"€ dodatno zavarovanje) = "+ cas_izposoje*(cena_najema_na_dan+dodatno_zavarovanje)+"€");
                    else
                        statusna_vrstica_label.setText("Izračun: "+cas_izposoje +" "+ (cas_izposoje > 1 ? "dni": "dan") +" x "+ cena_najema_na_dan+"€ najem = "+ cas_izposoje*cena_najema_na_dan+"€");
                }

            } catch (NullPointerException n) {
                cena_najema_na_dan = 0;
                cena_najema_na_dan_label.setText("Cena najema na dan:");
            }
        });

        dodatno_zavarovanje_checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (cas_izposoje != null && cena_najema_na_dan != null) {
                if (dodatno_zavarovanje_checkbox.isSelected())
                    statusna_vrstica_label.setText("Izračun: " + cas_izposoje + " " + (cas_izposoje > 1 ? "dni" : "dan") + " x (" + cena_najema_na_dan + "€ najem + " + dodatno_zavarovanje + "€ dodatno zavarovanje) = " + cas_izposoje * (cena_najema_na_dan + dodatno_zavarovanje) + "€");
                else
                    statusna_vrstica_label.setText("Izračun: " + cas_izposoje + " " + (cas_izposoje > 1 ? "dni" : "dan") + " x " + cena_najema_na_dan + "€ najem = " + cas_izposoje * cena_najema_na_dan + "€");
            }
        });

        new AutoCompleteComboBoxListener<>(vrsta_vozila_combobox);
        new AutoCompleteComboBoxListener<>(velikost_vozila_combobox);
        new AutoCompleteComboBoxListener<>(izbira_vozila_combobox);
        new AutoCompleteComboBoxListener<>(menjalnik_combobox);
        new AutoCompleteComboBoxListener<>(gorivo_combobox);

        lokacija_prevzema_combobox.getItems().setAll(lokacije);
        new AutoCompleteComboBoxListener<>(lokacija_prevzema_combobox);
        Tools.invalidNodeInputWhenOutOfFocus(gorivo_combobox, FXCollections.observableArrayList(lokacije));

        lokacija_oddaje_combobox.getItems().setAll(lokacije);
        new AutoCompleteComboBoxListener<>(lokacija_oddaje_combobox);
        Tools.invalidNodeInputWhenOutOfFocus(lokacija_oddaje_combobox, FXCollections.observableArrayList(lokacije));

        Tools.restrictLength(ime_textfield, 30);
        Tools.restrictLength(priimek_textfield, 40);
        Tools.restrictLength(naslov_textfield, 40);
        Tools.restrictNumbersOnly(starost_textfield, 18, 120);

        Tools.restrictLength(email_textfield, 60);
        email_textfield.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                String value = email_textfield.getText();
                String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(value);
                if (!matcher.matches() && !value.equals("")) {
                    email_textfield.setTooltip(new Tooltip("E-mail ni veljaven, nekje ste se zatipkali."));
                    Tools.addStyleClass(email_textfield, badBackground);
                } else {
                    email_textfield.setTooltip(null);
                    Tools.removeStyleClass(email_textfield, badBackground);
                }

            }
        });

        Tools.restrictLength(telefon_textfield, 16);
        telefon_textfield.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                String value = telefon_textfield.getText();
                if (!validatePhoneNumber(value) && !value.equals("")) {
                    telefon_textfield.setTooltip(new Tooltip("Telefonska številka ni veljaven, nekje ste se zatipkali."));
                    Tools.addStyleClass(telefon_textfield, badBackground);
                } else {
                    telefon_textfield.setTooltip(null);
                    Tools.removeStyleClass(telefon_textfield, badBackground);
                }
            }
        });


        Tools.restrictNumbersOnly(starost_vozniskega_izpita_textfield, 1, 102);

        izbira_nacina_placila_combobox.getItems().setAll(new ArrayList<>(Arrays.asList("gotovina", "kartica")));
        izbira_nacina_placila_combobox.setOnAction( (ActionEvent e) -> {
            String key = izbira_nacina_placila_combobox.getSelectionModel().getSelectedItem();
            try {
                if (key.equals("kartica")) {
                    dialog();
                } else if (key.equals("gotovina")) {
                    status_placila_label.setText("Status plačila: gotovina");
                    karticaA = null;
                    ccvA = null;
                }
            } catch (NullPointerException u) {
                // key not set
            }
        });
    }

    public void dialog() {
        // Create the custom dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Zavarovan bančni terminal");

        // Set the button types
        ButtonType potrdi = new ButtonType("Potrdi", ButtonBar.ButtonData.OK_DONE);
        ButtonType preklici = new ButtonType("Prekliči", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(potrdi, preklici);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField kartica = new TextField();
        kartica.setPromptText("xxxx xxxx xxxx xxxx");
        TextField ccv = new TextField();
        ccv.setPromptText("xxx");

        gridPane.add(new Label("Vnesite št. bančne kartice:"), 0, 0);
        gridPane.add(kartica, 1, 0);
        gridPane.add(new Label("Vnesite ccv kodo:"), 0, 1);
        gridPane.add(ccv, 1, 1);

        dialog.getDialogPane().setContent(gridPane);

        // Validate user input

        Button okButton = (Button) dialog.getDialogPane().lookupButton(potrdi);

        BooleanBinding isInvalid = Bindings.createBooleanBinding(() -> isInvalid(kartica.getText(), ccv.getText()), kartica.textProperty(), ccv.textProperty());

        okButton.disableProperty().bind(isInvalid);

        // Request focus on the credit card field by default.
        Platform.runLater(() -> kartica.requestFocus());

        // Convert the result to a card-ccv when the Confirm button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == potrdi) {
                return new Pair<>(kartica.getText(), ccv.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        AtomicBoolean notNull = new AtomicBoolean(false);
        result.ifPresent(pair -> {
            //System.out.println("From=" + pair.getKey() + ", To=" + pair.getValue());
            karticaA = pair.getKey();
            ccvA = pair.getValue();
            notNull.set(true);

            status_placila_label.setText("Status plačila: kartica xxxx-xxxx-xxxx-"+karticaA.replaceAll("[\\- ]?", "").substring(12) + " sprejeta");
        });

        if (! notNull.get()) {
            ponastavi();
        }

    }

    public void ponastavi() {
        Platform.runLater(() -> izbira_nacina_placila_combobox.getSelectionModel().select(null));
        karticaA = null;
        ccvA = null;
        status_placila_label.setText("Status plačila:");
    }

    private Boolean isInvalid(String text, String text2) {
        boolean kartica = ! text.matches("\\d{4}[\\- ]?\\d{4}[\\- ]?\\d{4}[\\- ]?\\d{4}[\\- ]?");
        boolean ccv = ! text2.matches("\\d{3}");
        //System.out.println("kartica: "+ kartica);
        //System.out.println("ccv: "+ ccv);

        boolean together = !(!kartica && !ccv);
        //System.out.println("together: "+together);

        return together;
    }

    private static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.matches("\\d{8,9}"))
            return true;
        else if (phoneNumber.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{3}"))
            return true;
        else if (phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
            return true;
        else if (phoneNumber.matches("\\(\\d{3}\\)-\\d{3}-\\d{3}"))
            return true;
        else
            return false;

    }

    public void setVrstaVozila() {
        Map<String,Object> vozilaAvto =new HashMap<>();

        Map<String,Object> vozilaAvtoMajhen =new HashMap<>();
        Map<String,Object> vozilaAvtoSrednji =new HashMap<>();
        Map<String,Object> vozilaAvtoVelik =new HashMap<>();

        // avto
        Map<String,Object> vozilaAvtoSrednjiAudi_A7 =new HashMap<>();

        Map<String,Object> vozilaAvtoSrednjiAudi_A7Rocni =new HashMap<>();
        vozilaAvtoSrednjiAudi_A7Rocni.put("diesel", 120);
        vozilaAvtoSrednjiAudi_A7Rocni.put("bencin", 120);

        // avto
        Map<String,Object> vozilaAvtoSrednjiTesla_Y =new HashMap<>();

        Map<String,Object> vozilaAvtoSrednjiTesla_YAutomatski =new HashMap<>();
        vozilaAvtoSrednjiTesla_YAutomatski.put("e-pogon", 110);
        
        vozilaAvtoSrednjiAudi_A7.put("Ročni", vozilaAvtoSrednjiAudi_A7Rocni);
        vozilaAvtoSrednjiTesla_Y.put("Automatski", vozilaAvtoSrednjiTesla_YAutomatski);

        vozilaAvtoSrednji.put("Audi_A7", vozilaAvtoSrednjiAudi_A7);
        vozilaAvtoSrednji.put("Tesla_Y", vozilaAvtoSrednjiTesla_Y);

        vozilaAvto.put("Srednji", vozilaAvtoSrednji);

        vozilaMainMap.put("Avto", vozilaAvto);

        Map<String,Object> vozilaMotor =new HashMap<>();
        vozilaMainMap.put("Motor", vozilaMotor);

        Map<String, Object> vozilaLimuzina =new HashMap<>();
        vozilaMainMap.put("Limuzina", vozilaLimuzina);

        Map<String, Object> vozilaSportni_avto =new HashMap<>();
        vozilaMainMap.put("Sportni avto", vozilaSportni_avto);
    }

    public void initializeData() {
        data.cas_najemaMap.put("datum_od", new DataHash(datum_od_datepicker));
        data.cas_najemaMap.put("datum_do", new DataHash(datum_do_datepicker));
        data.cas_najemaMap.put("ura_od", new DataHash(ura_od_spinner));
        data.cas_najemaMap.put("ura_do", new DataHash(ura_do_spinner));

        data.izbira_vozilaMap.put("izbira_vozila", new DataHash(vrsta_vozila_combobox));
        data.izbira_vozilaMap.put("velikost_vozila", new DataHash(velikost_vozila_combobox));
        data.izbira_vozilaMap.put("moznosti_izbire_vozila", new DataHash(izbira_vozila_combobox));
        data.izbira_vozilaMap.put("menjalnik", new DataHash(menjalnik_combobox));
        data.izbira_vozilaMap.put("gorivo", new DataHash(gorivo_combobox));
        data.izbira_vozilaMap.put("dodatno_zavarovanje", new DataHash(dodatno_zavarovanje_checkbox));

        data.lokacija_prevzema_in_oddajeMap.put("lokacija_prevzema", new DataHash(lokacija_prevzema_combobox));
        data.lokacija_prevzema_in_oddajeMap.put("lokacija_oddaje", new DataHash(lokacija_oddaje_combobox));

        data.podatki_strankeMap.put("ime", new DataHash(ime_textfield));
        data.podatki_strankeMap.put("priimek", new DataHash(priimek_textfield));
        data.podatki_strankeMap.put("naslov", new DataHash(naslov_textfield));
        data.podatki_strankeMap.put("starost", new DataHash(starost_textfield));
        data.podatki_strankeMap.put("email", new DataHash(email_textfield));
        data.podatki_strankeMap.put("telefon", new DataHash(telefon_textfield));
        data.podatki_strankeMap.put("starost_vozniskega_izpita", new DataHash(starost_vozniskega_izpita_textfield));

        data.placiloMap.put("izbira_nacina_placila", new DataHash(izbira_nacina_placila_combobox));

        Data.tabpanes.put("cas_najema_tab", cas_najema_tab);
        Data.tabpanes.put("vrsta_vozila_tab", izbira_vozila_tab);
        Data.tabpanes.put("lokacija_prevzema_in_oddaje_tab", lokacija_prevzema_in_oddaje_tab);
        Data.tabpanes.put("podatki_stranke_tab", podatki_stranke_tab);
        Data.tabpanes.put("placilo_tab", placilo_tab);

        // Labels
        data.labels.put("datum_od", datum_od_label);
        data.labels.put("datum_do", datum_do_label);
        data.labels.put("ura_od", ura_od_label);
        data.labels.put("ura_do", ura_do_label);

        data.labels.put("izbira_vozila", vrsta_vozila_label);
        data.labels.put("velikost_vozila", velikost_vozila_label);
        data.labels.put("moznosti_izbire_vozila", izbira_vozila_label);
        data.labels.put("menjalnik", menjalnik_label);
        data.labels.put("gorivo", gorivo_label);
        data.labels.put("dodatno_zavarovanje", dodatno_zavarovanje_label);

        data.labels.put("lokacija_prevzema", lokacija_prevzema_label);
        data.labels.put("lokacija_oddaje", lokacija_oddaje_label);

        data.labels.put("ime", ime_label);
        data.labels.put("priimek", priimek_label);
        data.labels.put("naslov", naslov_label);
        data.labels.put("starost", starost_label);
        data.labels.put("email", email_label);
        data.labels.put("telefon", telefon_label);
        data.labels.put("starost_vozniskega_izpita", starost_vozniskega_izpita_label);

        data.labels.put("izbira_nacina_placila", izbira_nacina_placila_label);
    }

}
