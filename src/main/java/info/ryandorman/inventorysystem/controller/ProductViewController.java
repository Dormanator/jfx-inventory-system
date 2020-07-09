package info.ryandorman.inventorysystem.controller;

/*
 *   Ryan Dorman
 *   ID: 001002824
 */

import info.ryandorman.inventorysystem.model.*;
import info.ryandorman.inventorysystem.utilities.JavaFXUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProductViewController implements Initializable {

    // State
    private Inventory inventory;
    private Product selectedProduct;
    private ObservableList<Part> selectedParts;
    private ObservableList<Part> unselectedParts;

    // View Title
    @FXML
    private Label title;

    // Form Fields
    @FXML
    private TextField idTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField stockTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private TextField minTextField;
    @FXML
    private TextField maxTextField;

    // All Parts Table
    @FXML
    private TextField allPartsSearchField;
    @FXML
    private TableView<Part> allPartsTableView;
    @FXML
    private TableColumn<Part, Integer> allPartsIdColumn;
    @FXML
    private TableColumn<Part, String> allPartsNameColumn;
    @FXML
    private TableColumn<Part, Integer> allPartsStockColumn;
    @FXML
    private TableColumn<Part, Double> allPartsPriceColumn;

    // Selected Parts Table
    @FXML
    private TableView<Part> selectedPartsTableView;
    @FXML
    private TableColumn<Part, Integer> selectedPartsIdColumn;
    @FXML
    private TableColumn<Part, String> selectedPartsNameColumn;
    @FXML
    private TableColumn<Part, Integer> selectedPartsStockColumn;
    @FXML
    private TableColumn<Part, Double> selectedPartsPriceColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup Form Text Fields that should only accept numeric values
        stockTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, JavaFXUtilities.isInteger));
        minTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, JavaFXUtilities.isInteger));
        maxTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, JavaFXUtilities.isInteger));
        priceTextField.setTextFormatter(new TextFormatter<>(JavaFXUtilities.truncatePrice, null, JavaFXUtilities.isDouble));

        // Setup All Parts Table View Columns
        allPartsIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        allPartsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        allPartsStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        allPartsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Setup Selected Parts Table View Columns
        selectedPartsIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        selectedPartsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        selectedPartsStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        selectedPartsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    public void initData(Inventory inventory, Product selectedProduct) {
        this.inventory = inventory;
        this.selectedProduct = selectedProduct;

        // Determine if the user is performing an Add or Modify
        if (selectedProduct != null) {
            // Set correct data for Modify Table Views
            this.selectedParts = selectedProduct.getAllAssociatedParts();

            // Setup UI
            title.setText("Modify Product");

            // Load existing data into form
            idTextField.setText(String.valueOf(selectedProduct.getId()));
            nameTextField.setText(selectedProduct.getName());
            stockTextField.setText(String.valueOf(selectedProduct.getStock()));
            priceTextField.setText(String.valueOf(selectedProduct.getPrice()));
            minTextField.setText(String.valueOf(selectedProduct.getMin()));
            maxTextField.setText(String.valueOf(selectedProduct.getMax()));
        } else {
            // Set correct data for Add Table Views
            this.selectedParts = FXCollections.observableArrayList();
            this.unselectedParts = inventory.getAllParts();
        }

        // Load data into Table Views
        setUnselectedParts();
        selectedPartsTableView.setItems(selectedParts);
    }

    public void onSearchParts() {
        // Get value in search input
        String searchValue = allPartsSearchField.getText().trim();

        // Determine if we are clearing a search or starting a new one
        if (searchValue == null || searchValue.isEmpty()) {
            // If no value entered, clear search and selection
            allPartsTableView.setItems(unselectedParts);
            allPartsTableView.getSelectionModel().clearSelection();
        } else {
            // Otherwise search using the id or name
            ObservableList<Part> foundParts = FXCollections.observableArrayList();

            // Assume we got an int, if that fails we will search by String
            try {
                int id = Integer.parseInt(searchValue);
                Part foundPart = unselectedParts.stream()
                        .filter(part -> id == part.getId())
                        .findAny()
                        .orElse(null);

                // If we find a result add it to the list of search results
                if (foundPart != null) {
                    foundParts.add(foundPart);
                }

            } catch (NumberFormatException nfe) {
                foundParts = unselectedParts.stream()
                        .filter(part -> part.getName().toLowerCase().contains(searchValue.toLowerCase()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));;
            }

            // Set the table with the found results and select the first entry
            allPartsTableView.setItems(foundParts);
            allPartsTableView.getSelectionModel().clearAndSelect(0);
        }
    }

    public void onAddPart() {
        Part selectedPart = allPartsTableView.getSelectionModel().getSelectedItem();

        if (selectedPart != null) {
            selectedParts.add(selectedPart);
            setUnselectedParts();
        }
    }

    public void onDeletePart() {
        Part selectedPart = selectedPartsTableView.getSelectionModel().getSelectedItem();
        boolean userConfirmed = JavaFXUtilities.confirmAction("Delete", selectedPart.getName(),
                "Are you sure you want to delete this Part from this Product?");

        if (selectedPart != null && userConfirmed) {
            selectedParts.remove(selectedPart);
            setUnselectedParts();
        }
    }

    public void onSave(ActionEvent actionEvent) throws IOException {
        try {
            Product newProduct = null;
            boolean isUpdate = selectedProduct != null;

            // Set default inventory value of 0 if none was given
            if (stockTextField.getText().isEmpty()) {
                stockTextField.setText("0");
            }

            // Get field values
            int id = isUpdate ? selectedProduct.getId() : Product.getCount() + 1;
            String name = nameTextField.getText().trim();
            int stock = Integer.parseInt(stockTextField.getText());
            double price = Double.parseDouble(priceTextField.getText());
            int max = Integer.parseInt(maxTextField.getText());
            int min = Integer.parseInt(minTextField.getText());

            // Validate the a part was selected and the stock, min, max, and price are logical.
            // If not warn the User and stop the save
            double totalPriceOfParts = selectedParts.stream()
                    .reduce(0.0, (partialPrice, part) -> partialPrice + part.getPrice(), Double::sum);

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
            } else if (price < totalPriceOfParts) {
                JavaFXUtilities.warnUser("Warning", "Price Too Low",
                        "Please make sure the price of the Product is greater than the cost of its Parts.");
                return;
            } else if (selectedParts.isEmpty()) {
                JavaFXUtilities.warnUser("Warning", "No Parts Selected",
                        "Please make sure this Product is associated with at least one Part.");
                return;
            }

            // Create the Product and associate its Parts
            newProduct = new Product(id, name, price, stock, min, max);
            for (Part selectedPart : selectedParts) {
                newProduct.addAssociatedPart(selectedPart);
            }

            // Update or Save the part accordingly
            if (isUpdate) {
                int existingIndex = inventory.getAllProducts().indexOf(selectedProduct);
                inventory.updateProduct(existingIndex, newProduct);
            } else {
                inventory.addProduct(newProduct);
            }

            // Return to the Main View
            loadMainView(actionEvent);
        } catch (NumberFormatException nfe) {
            JavaFXUtilities.warnUser("Warning", "Invalid Input", "Please fill in the form.");
        }
    }

    public void onCancel(ActionEvent actionEvent) throws IOException {
        // Confirm cancel before returning to the Main View
        boolean userConfirmed = JavaFXUtilities.confirmAction("Cancel", "Cancel Changes",
                "Are you sure you want to return to the Inventory?");

        if (userConfirmed) {
            loadMainView(actionEvent);
        }
    }

    private void loadMainView(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/MainView.fxml"));
        Parent mainViewParent = loader.load();
        MainViewController controller = loader.getController();

        // Pass Store ref to Controller
        controller.initData(inventory);

        // Get the Main Stage info and set the Main View Scene
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setTitle(window.getTitle().substring(0, window.getTitle().indexOf('-')));
        window.setScene(new Scene(mainViewParent, 1200, 800));
        window.show();
    }

    private void setUnselectedParts() {
        this.unselectedParts = inventory.getAllParts().stream()
                .filter(part -> !selectedParts.contains(part))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        allPartsTableView.setItems(unselectedParts);
    }
}
