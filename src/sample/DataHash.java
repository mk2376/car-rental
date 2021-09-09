package sample;


import javafx.scene.Node;
import javafx.scene.control.*;

import java.time.LocalDate;

public class DataHash {
    Object node;
    private String value;

    DataHash(Object node) {
        this.node = node;
    }

    @SuppressWarnings("unchecked")
    public void updateValue() {
        String nodeName = String.valueOf(node);

        //System.out.println("updateValue "+nodeName);

        String nodeType = Tools.getNodeType(node);

        String value = null;
        switch (nodeType.toLowerCase()) {       // .toLowerCase() to eliminate errors
            case "textfield":
                value = ((TextField) node).getText();
                break;
            case "datepicker":
                try {
                    value = ((DatePicker) node).getValue().format(Controller.dateFormater);
                } catch (NullPointerException e) {
                    value = "null";
                }
                break;
            case "combobox":
                value = String.valueOf(((ComboBox<String>) node).getValue());
                break;
            case "checkbox":
                value = String.valueOf(((CheckBox) node).isSelected());
                break;
            case "togglegroup":
                value = Tools.getToggleGroupSelection((ToggleGroup) node);
                break;
            case "listview":
                value = String.valueOf(((ListView<String>) node).getSelectionModel().getSelectedItems());
                break;
            case "spinner":
                value = String.valueOf(((Spinner<Integer>) node).getValue());
                break;
            default:
                System.out.println(nodeName+" value hasn't been updated, because type did not match any predefined one in switch...case, you will have to add it by yourself!");
        }

        if (value != null)
            this.value = value;
    }

    @SuppressWarnings("unchecked")
    public void setFromValue() {
        String nodeName = String.valueOf(node);

        String nodeType = Tools.getNodeType(node);

        String value = null;
        switch (nodeType.toLowerCase()) {       // .toLowerCase() to eliminate errors
            case "textfield":
                ((TextField) node).setText(value);
                break;
            case "datepicker":
                    ((DatePicker) node).setValue(LocalDate.parse(value, Controller.dateFormater));
                break;
            case "combobox":
                ((ComboBox<String>) node).setValue(value);
                break;
            case "checkbox":
                ((CheckBox) node).setSelected(Boolean.parseBoolean(value));
                break;
            case "togglegroup":
                //value = Tools.getToggleGroupSelection((ToggleGroup) node);
                break;
            case "listview":
                //value = String.valueOf(((ListView<String>) node).getSelectionModel().getSelectedItems());
                break;
            default:
                System.out.println(nodeName+" value hasn't been set, because type did not match any predefined one in switch...case, you will have to add it by yourself!");
        }

        if (value != null)
            this.value = value;
    }

    @SuppressWarnings("unchecked")
    public void resetValue() {
        String nodeName = String.valueOf(node);

        //System.out.println("resetValue "+nodeName);

        String nodeType = Tools.getNodeType(node);

        switch (nodeType.toLowerCase()) {       // .toLowerCase() to eliminate errors
            case "textfield":
                TextField textfield = ((TextField) node);
                textfield.setText("");
                Tools.removeStyleClass((Node) node, Controller.badBackground);
                textfield.setTooltip(null);
                textfield.setPromptText(null);
                break;
            case "datepicker":
                DatePicker datepicker = (DatePicker) node;
                datepicker.setValue(null);
                Tools.removeStyleClass((Node) node, Controller.badBackgroundFieldOnly);
                Tools.setDatePickerDefaultTooltip(datepicker);
                Tools.setDatePickerFormatter(datepicker);
                break;
            case "checkbox":
                CheckBox checkbox = (CheckBox) node;
                checkbox.setSelected(false);
                break;
            case "combobox":
                Tools.removeStyleClass((Node) node, Controller.badBackgroundFieldOnly);
                ComboBox<String> combobox = (ComboBox<String>) node;
                combobox.valueProperty().set(null);
                break;
            case "togglegroup":
                ToggleGroup togglegroup = (ToggleGroup) node;
                switch (nodeName) {
                    case "group_osnovno_zavarovanje_togglegroup":
                        togglegroup.selectToggle((Toggle) Data.toogels.get("toggle_to_select_osnovno_zavarovanje"));
                        break;
                    case "group_kasko_togglegroup":
                        togglegroup.selectToggle((Toggle) Data.toogels.get("toggle_to_select_kasko"));
                        break;
                }
                break;
            case "listview":
                ListView<String> listview = (ListView<String>) node;
                listview.getSelectionModel().clearSelection();
                break;
            default:
                //System.out.println(nodeName+" hasn't been reseted, because type did not match any predefined one in switch...case, you will have to add it by yourself!");
        }

        this.value = null;
    }

    public String getValue() {
        return this.value;
    }
}
