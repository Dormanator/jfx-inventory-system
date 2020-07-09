package info.ryandorman.inventorysystem.controller;

/*
 *   Ryan Dorman
 *   ID: 001002824
 */

import info.ryandorman.inventorysystem.model.InHouse;
import info.ryandorman.inventorysystem.model.Inventory;
import info.ryandorman.inventorysystem.model.Outsourced;
import info.ryandorman.inventorysystem.model.Part;
import info.ryandorman.inventorysystem.utilities.JavaFXUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class PartViewController implements Initializable {

    // State
    private Inventory inventory;
    private Part selectedPart;

    // View Title
    @FXML
    private Label title;

    // Source Toggle Group
    @FXML
    private ToggleGroup sourceToggleGroup;
    @FXML
    private RadioButton inHouseRadioButton;
    @FXML
    private RadioButton outsourcedRadioButton;

    // Default Form fields
    @FXML
    private TextField idTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField stockTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private TextField maxTextField;
    @FXML
    private TextField minTextField;

    // Dynamic Form fields
    @FXML
    private Label sourceFieldLabel;
    @FXML
    private TextField sourceTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup Radio Buttons and set to In-House as default
        sourceToggleGroup = new ToggleGroup();
        inHouseRadioButton.setToggleGroup(sourceToggleGroup);
        outsourcedRadioButton.setToggleGroup(sourceToggleGroup);
        inHouseRadioButton.fire();

        // Setup Text Fields that should only accept numeric values
        stockTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, JavaFXUtilities.isInteger));
        minTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, JavaFXUtilities.isInteger));
        maxTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, JavaFXUtilities.isInteger));
        priceTextField.setTextFormatter(new TextFormatter<>(JavaFXUtilities.truncatePrice, null, JavaFXUtilities.isDouble));
    }

    public void initData(Inventory inventory, Part selectedPart) {
        this.inventory = inventory;
        this.selectedPart = selectedPart;

        // Determine if the user is performing an Add or Modify
        if (selectedPart != null) {
            // Setup UI
            title.setText("Modify Part");

            // Load existing data into form
            idTextField.setText(String.valueOf(selectedPart.getId()));
            nameTextField.setText(selectedPart.getName());
            stockTextField.setText(String.valueOf(selectedPart.getStock()));
            priceTextField.setText(String.valueOf(selectedPart.getPrice()));
            minTextField.setText(String.valueOf(selectedPart.getMin()));
            maxTextField.setText(String.valueOf(selectedPart.getMax()));

            // Determine subclass to select Radio Button and set proper source data
            if (selectedPart.getClass().getName().equals("info.ryandorman.inventorysystem.model.InHouse")) {
                InHouse inHousePart = (InHouse) selectedPart;
                sourceTextField.setText(String.valueOf(inHousePart.getMachineId()));
            } else if (selectedPart.getClass().getName().equals("info.ryandorman.inventorysystem.model.Outsourced")) {
                outsourcedRadioButton.fire();
                Outsourced outsourcedPart = (Outsourced) selectedPart;
                sourceTextField.setText(outsourcedPart.getCompanyName());
            }
        }
    }

    public void onSourceToggleGroupUpdate() {
        // Update the View based on the Source Radio Button selected
        if (sourceToggleGroup.getSelectedToggle().equals(inHouseRadioButton)) {
            sourceFieldLabel.setText("Machine ID");
            sourceTextField.setPromptText("Mach ID");
            sourceTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, change -> {
                if (change.getControlText().matches("(([1-9][0-9]*)|0)?")) {
                    return change;
                }
                return null;
            }));
        } else if (sourceToggleGroup.getSelectedToggle().equals(outsourcedRadioButton)) {
            sourceFieldLabel.setText("Company Name");
            sourceTextField.setPromptText("Comp Nm");
            sourceTextField.setTextFormatter(new TextFormatter<>(TextFormatter.Change::clone));
        }
    }

    public void onSave(ActionEvent actionEvent) {
        try {
            Part newPart = null;
            boolean isUpdate = selectedPart != null;

            // Set default inventory value of 0 if none was given
            if (stockTextField.getText().isEmpty()) {
                stockTextField.setText("0");
            }

            // Get field values
            int id = isUpdate ? selectedPart.getId() : Part.getCount() + 1;
            String name = nameTextField.getText().trim();
            int stock = Integer.parseInt(stockTextField.getText());
            double price = Double.parseDouble(priceTextField.getText());
            int max = Integer.parseInt(maxTextField.getText());
            int min = Integer.parseInt(minTextField.getText());

            // Validate the stock, min, and max are logical and if not warn the User and stop the save
            if (max < min) {
                JavaFXUtilities.warnUser("Warning", "Invalid Min and Max Inputs",
                        "Please make sure the maximum is greater or equal to the minimum.");
                maxTextField.clear();
                return;
            } else if (stock < min || stock > max) {
                JavaFXUtilities.warnUser("Warning", "Invalid Inventory Input",
                        "Please make sure the number in stock is within the minimum and maximum.");
                stockTextField.clear();
                return;
            }

            // Save correct Part type based on source Radio Button selection
            if (sourceToggleGroup.getSelectedToggle().equals(inHouseRadioButton)) {
                int machineId = Integer.parseInt(sourceTextField.getText());
                newPart = new InHouse(id, name, price, stock, min, max, machineId);
            } else if (sourceToggleGroup.getSelectedToggle().equals(outsourcedRadioButton)) {
                String companyName = sourceTextField.getText();
                newPart = new Outsourced(id, name, price, stock, min, max, companyName);
            }

            // Update or Save the part accordingly
            if (isUpdate) {
                int existingIndex = inventory.getAllParts().indexOf(selectedPart);
                inventory.updatePart(existingIndex, newPart);
            } else {
                inventory.addPart(newPart);
            }

            // Close the Modal
            Stage partStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            partStage.close();
        } catch (NumberFormatException nfe) {
            JavaFXUtilities.warnUser("Warning", "Invalid Input", "Please fill in the form.");
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        // Confirm cancel before closing the associated Modal
        boolean userConfirmed = JavaFXUtilities.confirmAction("Cancel", "Cancel Changes",
                "Are you sure you want to return to the Inventory?");

        if (userConfirmed) {
            Stage partStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            partStage.close();
        }
    }
}
