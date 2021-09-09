package sample;

import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tools {

    public static void removeStyleClassAll(Map<String, Node> mapOfSceneElements, String className) {
        mapOfSceneElements.forEach((k, v) -> {      // key, value
            removeStyleClass(v, className);
        });
    }

    public static void removeStyleClassTabAll(Map<String, Tab> mapOfSceneElements, String className) {
        mapOfSceneElements.forEach((k, v) -> {      // key, value
            v.getStyleClass().remove(className);
        });
    }

    public static void removeStyleClass(Node node, String className) {
        node.getStyleClass().remove(className);
    }

    public static void setDatePickerFormatter(DatePicker dp) {     // https://stackoverflow.com/a/21498568
        dp.setConverter(new StringConverter<>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty())
                    return null;

                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        });
    }

    public static void setDatePickerDefaultTooltip(DatePicker datepicker) {
        datepicker.setTooltip(new Tooltip("DD/MM/LLLL : (dan/mesec/leto)"));
        datepicker.setPromptText("DD/MM/LLLL");
    }

    public static void setTextFieldTooltipWhenEmpty(TextField tf) {
        tf.setTooltip(new Tooltip("Polje ne sme biti prazno"));
        tf.setPromptText("Izpolni ...");
    }

    public static void setComboBoxTooltipWhenEmpty(ComboBox cb) {
        cb.setTooltip(new Tooltip("Polje ne sme biti prazno"));
        cb.setPromptText("Izberi ...");
    }

    public static String getToggleGroupSelection(ToggleGroup toggleGroup) {
        String value = toggleGroup.getSelectedToggle().toString()
                .split("styleClass=radio-button]'")[1];
        return value.substring(0, value.length()-1);
    }

    public static void ifEmpty(boolean beforeAllChecksPassed, Node node, Tab tabpane) {
        if (beforeAllChecksPassed)
            tabPaneFocusAsist(node, tabpane);
    }

    public static void tabPaneFocusAsist(Node node, Tab tabpane) {
        //System.out.println("Activated");
        Data.tabPane.getSelectionModel().select(tabpane);
        node.requestFocus();
    }
/*
    public static TitledPane getTitledPaneFromNode(Node node, Map<String, Node> titledpanes) {
        String pane = null;
        try {
            pane = node.getParent().getParent().parentProperty().getValue().idProperty().getValue();
            pane = pane.replaceAll("_titledpane", "");
        } catch (NullPointerException e) {      // RadioButton's are lower in tree
            pane = node.getParent().getParent().getParent().parentProperty().getValue().idProperty().getValue();
            pane = pane.replaceAll("_titledpane", "");
        }

        //System.out.println(pane);
        //System.out.println((TitledPane) titledpanes.get(pane));

        return (TitledPane) titledpanes.get(pane);
    }

 */
    public static void addStyleClassTabPaneParent(Tab tab, String className) {
        if (! tab.getStyleClass().contains(className))
            tab.getStyleClass().add(className);
    }

    public static void addStyleClass(Node node, String className) {
        if (! node.getStyleClass().contains(className))
            node.getStyleClass().add(className);
    }

    public static int getIntFromTextField(TextField tf, String txt) {
        String id = tf.getId();

        if (id.equals("postna_st_textfield"))
            return Integer.parseInt(txt);
        else
            return Integer.parseInt(txt.replaceAll(" ", "")); // allow " " spaces for easier formating when inputing
    }

    public static int getIntFromTextField(TextField tf) {
        String id = tf.getId();

        if (id.equals("postna_st_textfield"))
            return Integer.parseInt(tf.getText());
        else
            return Integer.parseInt(tf.getText().replaceAll(" ", "")); // allow " " spaces for easier formating when inputing
    }

    public static void invalidNodeInputWhenOutOfFocus(Node node, Object min, Object max) {
        String nodeType = getNodeType(node);

        if (nodeType.equals("datepicker")){
            node.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    DatePicker datePicker = ((DatePicker) node);
                    datePicker.setValue(LocalDate.parse(datePicker.getEditor().getText(), Controller.dateFormater));
                }
            });
        }

        node.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {   // out of focus
                if (nodeType.equals("textfield"))
                    checkInvalidTextFieldInput((TextField) node, (Integer) min, (Integer) max);
                else if (nodeType.equals("datepicker")) {
                    checkInvalidDatePickerInput((DatePicker) node, (LocalDate) min, (LocalDate) max);

                }
            }
        });
    }
/*
    @SuppressWarnings("unchecked")
    public static void restrictComboBoxToOptions(ComboBox<String> combobox, ObservableList<String> comboBoxLinkedItems) {
        System.out.println(combobox+ " "+ comboBoxLinkedItems);

        combobox.getItems().setAll(comboBoxLinkedItems);
        new AutoCompleteComboBoxListener<>(combobox);

        invalidNodeInputWhenOutOfFocus(combobox, comboBoxLinkedItems);
    }
*/

    @SuppressWarnings("unchecked")
    public static void invalidNodeInputWhenOutOfFocus(ComboBox<String> combobox, ObservableList<String> comboBoxLinkedItems) {
        combobox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {   // out of focus
                checkInvalidComboBoxInput(combobox, comboBoxLinkedItems);
            }
        });
    }

    public static void checkInvalidComboBoxInput(ComboBox<String> cb,  ObservableList<String> comboBoxLinkedItems) {
        try {
            String value = String.valueOf(cb.getEditor().getText());

            if (value.equals("")) // if user accedently clicks and doesn't enter anything it won't mark red
                return;

            //System.out.println(n);

            if (! comboBoxLinkedItems.contains(value)) {
                cb.setTooltip(new Tooltip(value+" ni med možnostmi izbire!"));
                addStyleClass(cb, Controller.badBackgroundFieldOnly);
            } else {
                cb.setValue(value);
                resetComboBox(cb, Controller.badBackgroundFieldOnly);
            }

        } catch (Exception e) {
            cb.setTooltip(new Tooltip("Izbira ni veljavna!"));
            addStyleClass(cb, Controller.badBackgroundFieldOnly);
        }
    }


    public static void checkInvalidTextFieldInput(TextField tf, Integer min, Integer max) {
        try {
            if (tf.getText().equals("")) // if user accedently clicks and doesn't enter anything it won't mark red
                return;

            int n = getIntFromTextField(tf);
            //System.out.println(n);

            if (min != null && max != null) {
                if (!(min <= n && n <= max)) {
                    tf.setTooltip(new Tooltip(n+" je manjše od "+min+" ali večje od "+max+"!"));
                    addStyleClass(tf, Controller.badBackground);
                } else {
                    resetTextField(tf, Controller.badBackground);
                }
            } else if (min != null) {
                if (!(min <= n)) {
                    tf.setTooltip(new Tooltip(n+" je manjše od "+min+"!"));
                    addStyleClass(tf, Controller.badBackground);
                } else {
                    resetTextField(tf, Controller.badBackground);
                }
            } else if (max != null) {
                if (!(n <= max)) {
                    tf.setTooltip(new Tooltip(n+" je večje od "+max+"!"));
                    addStyleClass(tf, Controller.badBackground);
                } else {
                    resetTextField(tf, Controller.badBackground);
                }
            }
        } catch (Exception e) {
            tf.setTooltip(new Tooltip("Številka mora biti večja ali enako "+min+" oz. manjša ali enako "+max+"!"));
            addStyleClass(tf, Controller.badBackground);
        }
    }

    public static void checkInvalidDatePickerInput(DatePicker dp, LocalDate min, LocalDate max) {
        DateTimeFormatter dateTimeFormatter = Controller.dateFormater;

        try {
            if (dp.getEditor().getText().equals("")) // if user accedently clicks and doesn't enter anything it won't mark red
                return;

            LocalDate n = LocalDate.parse(dp.getEditor().getText(), dateTimeFormatter);
            //System.out.println(n);

            if (min != null && max != null) {
                if (!(min.compareTo(n) < 1 && n.compareTo(max) < 1)) {
                    dp.setTooltip(new Tooltip(n.format(dateTimeFormatter)+" je manjše od "+min.format(dateTimeFormatter)+" ali večje od "+max.format(dateTimeFormatter)+"!"));
                    addStyleClass(dp, Controller.badBackgroundFieldOnly);
                } else {
                    resetDatePicker(dp, Controller.badBackgroundFieldOnly, n);
                }
            } else if (min != null) {
                if (!(min.compareTo(n) < 1)) {
                    dp.setTooltip(new Tooltip(n+" je manjše od "+min.format(dateTimeFormatter)+"!"));
                    Tools.addStyleClass(dp, Controller.badBackgroundFieldOnly);
                } else {
                    resetDatePicker(dp, Controller.badBackgroundFieldOnly, n);
                }
            } else if (max != null) {
                if (!(n.compareTo(max) < 1)) {
                    dp.setTooltip(new Tooltip(n+" je večje od "+max.format(dateTimeFormatter)+"!"));
                    addStyleClass(dp, Controller.badBackgroundFieldOnly);
                } else {
                    resetDatePicker(dp, Controller.badBackgroundFieldOnly, n);
                }
            }
        } catch (Exception e) {
            try {
                assert min != null;
                assert max != null;
                dp.setTooltip(new Tooltip("Datum mora biti večji ali enak "+min.format(dateTimeFormatter)+" oz. manjši ali enak "+max.format(dateTimeFormatter)+"!"));
                Tools.addStyleClass(dp, Controller.badBackgroundFieldOnly);
            } catch (NullPointerException q) {
                System.out.println("min and max DatePicker produced NullPointerException");
            }
        }
    }

    public static void resetDatePicker(DatePicker datePicker, String className, LocalDate n) {
        setDatePickerDefaultTooltip(datePicker);
        removeStyleClass(datePicker, className);
        datePicker.setValue(n);
    }

    public static void resetTextField(TextField textField, String className) {
        textField.setTooltip(null);
        removeStyleClass(textField, className);
    }

    public static void resetComboBox(ComboBox comboBox, String className) {
        comboBox.setTooltip(null);
        removeStyleClass(comboBox, className);
    }

    public static void restrictLength(TextField tf, int length){
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.startsWith(" ") ||  newValue.length() > length) {
                tf.setText(oldValue);
            } else if (newValue.replaceAll(" ", "").length()>=1) {
                tf.setTooltip(null);
                removeStyleClass(tf, Controller.badBackground);
            }
        });
    }

    public static void restrictDatePickerMinMaxDate(DatePicker dp, String min, String max) { //https://stackoverflow.com/questions/35907325/how-to-set-minimum-and-maximum-date-in-datepicker-calander-in-javafx8
        LocalDate minDate = null;
        LocalDate maxDate = null;

        if (min.startsWith("now")) {
            if (min.equals("now"))
                minDate = LocalDate.now();
            else {
                try {
                    minDate = LocalDate.now().minusYears(Integer.parseInt(min.substring(4)));
                } catch (Exception e) {
                    System.out.println("Something went wrong when subtracting years from LocalDate.now()");
                }
            }
        } else
            minDate = LocalDate.parse(min);

        if (max.startsWith("now")) {
            if (max.equals("now"))
                maxDate = LocalDate.now();
            else {
                try {
                    maxDate = LocalDate.now().plusYears(Integer.parseInt(max.substring(4)));
                } catch (Exception e) {
                    System.out.println("Something went wrong when adding years from LocalDate.now()");
                }
            }
        } else
            maxDate = LocalDate.parse(max);

        LocalDate finalMaxDate = maxDate;
        LocalDate finalMinDate = minDate;
        dp.setDayCellFactory(d ->
                new DateCell() {
                    @Override public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(item.isAfter(finalMaxDate) || item.isBefore(finalMinDate));
                    }});

        // Check when out of focus
        invalidNodeInputWhenOutOfFocus(dp, minDate, maxDate);
    }

    public static void restrictNumbersOnly(TextField tf, Integer min, Integer max){
        invalidNodeInputWhenOutOfFocus(tf, min, max);

        tf.setTextFormatter(new TextFormatter<Integer>(change -> {
            String txt = change.getControlNewText();

            /*if (change.isDeleted()) {
                return change;
            }*/
            //System.out.println("\""+txt+"\"");

            if (txt.equals(""))
                return change;

            if (txt.matches("0\\d+")) { // Delete leading zeros
                //System.out.println("Deleted leading zeros.");
                tf.setText(txt.substring(1, txt.length()));
                return null;
            }

            if (txt.matches(".*\\s{2,}.*")) { // Delete if 2 or more spaces
                //System.out.println("Deleted spaces.");
                return null;
            }

            try {
                int n;

                try {
                    n = Tools.getIntFromTextField(tf, txt);
                } catch (NumberFormatException e) {
                    return null;
                }

                //System.out.println("\""+n+"\"");
                String n2 = String.valueOf(n);
                String min2;

                try {
                    min2 = String.valueOf(min.intValue()).substring(0, n2.length());
                } catch (IndexOutOfBoundsException e) {
                    min2 = String.valueOf(min.intValue());
                }

                if (min != null && max != null) {
                    try {
                        //System.out.println(Integer.parseInt(min2)+" <= "+n +" "+ (Integer.parseInt(min2) <= n));
                        return n <= max ? change : null;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else if (min != null) {
                    try {
                        return Integer.parseInt(min2) <= n ? change : null;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else if (max != null) {
                    try {
                        return n <= max.intValue() ? change : null;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else {
                    return change;
                }
            } catch (Exception e) {
                System.out.println("Something bad happened in RestrictNumbersOnly when creating min2 from min, but probably bad n (input).\n"+e);
                return null;
            }
        }));
    }

    public static String getNodeType(Object node) {
        String nodeName = null;
        try {
            nodeName = String.valueOf(((Node) node).getId());
            String[] nodeNameArray;

            try {
                nodeNameArray = nodeName.split("_");
            } catch (Error e) {
                System.out.println("Could not split on "+nodeName+". Wrong formating of number used: <name>_<type>"+e);
                return "";
            }

            nodeName = nodeNameArray[nodeNameArray.length-1].toLowerCase();
        } catch (ClassCastException e) {
            String[] temp = String.valueOf(node).split("@")[0].split("\\.");
            nodeName = temp[temp.length-1].toLowerCase();
        }

        //System.out.println("getNodeType "+nodeName);

        return nodeName;
    }

    public static void labelNodeBind(Node label, Object node) {
        //System.out.println(label+" "+ node);
        String nodeType = getNodeType(node);

        if (nodeType.equals("togglegroup")) {
            if (label.getId().equals("osnovno_zavarovanje_label"))
                node = Data.toogels.get("toggle_to_select_osnovno_zavarovanje");
            else if (label.getId().equals("kasko_label"))
                node = Data.toogels.get("toggle_to_select_kasko");
        }

        bind(label, node);
    }

    private static void bind(Node label, Object node) {
        label.setOnMouseClicked(e -> {
            ((Node) node).requestFocus();
        });
    }

    public static void bindWidth(Object node, BorderPane titledPane, double multiplier) {
        String nodeType = getNodeType(node);

        try {
            DoubleBinding doubleBinding = titledPane.widthProperty().multiply(multiplier);

            switch (nodeType.toLowerCase()) {       // .toLowerCase() to eliminate errors
                case "textfield":
                    ((TextField) node).prefWidthProperty().bind(doubleBinding);
                    ((TextField) node).minWidthProperty().bind(doubleBinding);
                    ((TextField) node).maxWidthProperty().bind(doubleBinding);
                    break;
                case "datepicker":
                    ((DatePicker) node).prefWidthProperty().bind(doubleBinding);
                    ((DatePicker) node).minWidthProperty().bind(doubleBinding);
                    ((DatePicker) node).maxWidthProperty().bind(doubleBinding);
                    break;
                case "combobox":
                    ((ComboBox<String>) node).prefWidthProperty().bind(doubleBinding);
                    ((ComboBox<String>) node).minWidthProperty().bind(doubleBinding);
                    ((ComboBox<String>) node).maxWidthProperty().bind(doubleBinding);
                    break;
                case "listview":
                    ((ListView<String>) node).prefWidthProperty().bind(doubleBinding);
                    ((ListView<String>) node).minWidthProperty().bind(doubleBinding);
                    ((ListView<String>) node).maxWidthProperty().bind(doubleBinding);
                    break;
                case "checkbox":
                    ((CheckBox) node).prefWidthProperty().bind(doubleBinding);
                    ((CheckBox) node).minWidthProperty().bind(doubleBinding);
                    ((CheckBox) node).maxWidthProperty().bind(doubleBinding);
                    break;
                case "spinner":
                    ((Spinner<Integer>) node).prefWidthProperty().bind(doubleBinding);
                    ((Spinner<Integer>) node).minWidthProperty().bind(doubleBinding);
                    ((Spinner<Integer>) node).maxWidthProperty().bind(doubleBinding);
                    break;
                case "label":
                    DoubleBinding min = new DoubleBinding() {
                        @Override
                        protected double computeValue() {
                            return 70;
                        }
                    };

                    //System.out.println(doubleBinding.getValue());

                    if (!(doubleBinding.getValue() > 70))
                        doubleBinding = min;

                    ((Label) node).prefWidthProperty().bind(doubleBinding);
                    ((Label) node).minWidthProperty().bind(doubleBinding);
                    ((Label) node).maxWidthProperty().bind(doubleBinding);
                default:
                    //System.out.println(node.getId()+" width hasn't been, because type did not match any predefined one in switch...case, you will have to add it by yourself!");
            }
        } catch (NullPointerException e) {
            System.out.println("Not yet initialized.");
        }
    }

    public static void bindListViewToToggle(ListView<String > listView, Toggle toggle) {
        listView.setDisable(true);

        toggle.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            //System.out.println(newValue +" | "+ oldValue);
            listView.setDisable(!newValue);
        }));
    }
/*
    public static void onEnterNext(String ndK, Object nd, String nkK, Object nx) {
        Node node = (Node) toogleGroupToRadioButton(ndK, nd);
        Node next = (Node) toogleGroupToRadioButton(nkK, nx);

        //System.out.println(node);
        //System.out.println(next);

        if (node != null && next != null) {
            node.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    tabPaneFocusAsist(next, Data.tabpanes);
                }
            });
        }
    }
*/
    private static Object toogleGroupToRadioButton(String id, Object nd) {

        String nodeType = getNodeType(nd);

        if (nodeType.equals("togglegroup")) {
            //System.out.println(id);
            if (id.equals("osnovno_zavarovanje"))
                return Data.toogels.get("toggle_to_select_osnovno_zavarovanje");
            else if (id.equals("kasko"))
                return Data.toogels.get("toggle_to_select_kasko");
        }

        return nd;
    }
}
