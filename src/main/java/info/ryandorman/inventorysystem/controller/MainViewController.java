package info.ryandorman.inventorysystem.controller;

/*
 *   Ryan Dorman
 *   ID: 001002824
 */

import info.ryandorman.inventorysystem.model.*;
import info.ryandorman.inventorysystem.utilities.JavaFXUtilities;
import javafx.application.Platform;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    // State
    private Inventory inventory;

    // Part Table
    @FXML
    private TextField partSearchField;
    @FXML
    private TableView<Part> partTableView;
    @FXML
    private TableColumn<Part, Integer> partIdColumn;
    @FXML
    private TableColumn<Part, String> partNameColumn;
    @FXML
    private TableColumn<Part, Integer> partStockColumn;
    @FXML
    private TableColumn<Part, Double> partPriceColumn;

    // Product Table
    @FXML
    private TextField productSearchField;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, Integer> productIdColumn;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, Integer> productStockColumn;
    @FXML
    private TableColumn<Product, Double> productPriceColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup Part Table View Columns
        partIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Setup Product Table View Columns
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Create in-memory Store to hold inventory
        initData(new Inventory());
    }

    public void initData(Inventory inventory) {
        // Allow store to be passed back after being altered in other Views
        this.inventory = inventory;

        // Create Test Data if none
//        if (inventory.getAllParts().isEmpty() && inventory.getAllProducts().isEmpty()) {
//            initTestData();
//        }

        // Load data into Table Views
        partTableView.setItems(inventory.getAllParts());
        productTableView.setItems(inventory.getAllProducts());
    }

    private void initTestData() {
        Part testPart1 = new InHouse(Part.getCount() + 1, "Test Part 1", 10.25, 3, 2, 5, 2543);
        Part testPart2 = new Outsourced(Part.getCount() + 1, "Test Part 2", 0.10, 100, 50, 500, "Test Co");
        Part testPart3 = new InHouse(Part.getCount() + 1, "Test Part 3", 100.66, 5, 5, 50, 2112);
        Part testPart4 = new InHouse(Part.getCount() + 1, "Test Part 4", 1.52, 75, 50, 500, 2543);
        Part testPart5 = new InHouse(Part.getCount() + 1, "Test Part 5", 10.25, 3, 2, 5, 2543);
        Part testPart6 = new Outsourced(Part.getCount() + 1, "Test Part 6", 0.10, 100, 50, 500, "Test Co");
        Part testPart7 = new InHouse(Part.getCount() + 1, "Test Part 7", 100.66, 5, 5, 50, 2112);
        Part testPart8 = new InHouse(Part.getCount() + 1, "Test Part 8", 1.52, 75, 50, 500, 2543);

        inventory.addPart(testPart1);
        inventory.addPart(testPart2);
        inventory.addPart(testPart3);
        inventory.addPart(testPart4);
        inventory.addPart(testPart5);
        inventory.addPart(testPart6);
        inventory.addPart(testPart7);
        inventory.addPart(testPart8);

        Product testProduct1 = new Product(Product.getCount() + 1, "Test Product 1", 125.98, 55, 25, 55);
        testProduct1.addAssociatedPart(inventory.getAllParts().get(0));
        testProduct1.addAssociatedPart(inventory.getAllParts().get(1));

        Product testProduct2 = new Product(Product.getCount() + 1, "Test Product 2", 10725.98, 5, 5, 10);
        testProduct2.addAssociatedPart(inventory.getAllParts().get(2));
        testProduct2.addAssociatedPart(inventory.getAllParts().get(3));

        inventory.addProduct(testProduct1);
        inventory.addProduct(testProduct2);
    }

    public void onSearchParts() {
        // Get value in search input
        String searchValue = partSearchField.getText().trim();

        // Determine if we are clearing a search or starting a new one
        if (searchValue == null || searchValue.isEmpty()) {
            // If no value entered, clear search and selection
            partTableView.setItems(inventory.getAllParts());
            partTableView.getSelectionModel().clearSelection();
        } else {
            // Otherwise search using the id or name
            ObservableList<Part> foundParts = FXCollections.observableArrayList();

            // Assume we got an int, if that fails we will search by String
            try {
                int id = Integer.parseInt(searchValue);
                Part foundPart = inventory.lookupPart(id);

                // If we find a result add it to the list of search results
                if (foundPart != null) {
                    foundParts.add(foundPart);
                }

            } catch (NumberFormatException nfe) {
                foundParts = inventory.lookupPart(searchValue);
            }

            // Set the table with the found results and select the first entry
            partTableView.setItems(foundParts);
            partTableView.getSelectionModel().clearAndSelect(0);
        }
    }

    public void onSearchProducts() {
        // Get value in search input
        String searchValue = productSearchField.getText().trim();

        // Determine if we are clearing a search or starting a new one
        if (searchValue == null || searchValue.isEmpty()) {
            // If no value entered, clear search and selection
            productTableView.setItems(inventory.getAllProducts());
            productTableView.getSelectionModel().clearSelection();
        } else {
            // Otherwise search using the id or name
            ObservableList<Product> foundProducts = FXCollections.observableArrayList();

            // Assume we got an int, if that fails we will search by String
            try {
                int id = Integer.parseInt(searchValue);
                Product foundProduct = inventory.lookupProduct(id);

                // If we find a result add it to the list of search results
                if (foundProduct != null) {
                    foundProducts.add(foundProduct);
                }

            } catch (NumberFormatException nfe) {
                foundProducts = inventory.lookupProduct(searchValue);
            }

            // Set the table with the found results and select the first entry
            productTableView.setItems(foundProducts);
            productTableView.getSelectionModel().clearAndSelect(0);
        }
    }

    public void onAddPart(ActionEvent actionEvent) throws IOException {
        loadPartView(actionEvent, "Add Part", null);
    }

    public void onModifyPart(ActionEvent actionEvent) throws IOException {
        Part selectedPart = partTableView.getSelectionModel().getSelectedItem();

        if (selectedPart != null) {
            loadPartView(actionEvent, "Modify Part", selectedPart);
        }
    }

    private void loadPartView(ActionEvent actionEvent, String title, Part selectedPart) throws IOException {
        Stage inventoryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/PartView.fxml"));
        Parent partViewParent = loader.load();
        Stage partViewStage = new Stage();
        PartViewController controller = loader.getController();

        // Pass Store and Part ref to Controller
        controller.initData(inventory, selectedPart);

        // Init View
        partViewStage.setTitle(title);
        partViewStage.setScene(new Scene(partViewParent, 500, 450));
        partViewStage.initOwner(inventoryStage);
        partViewStage.initModality(Modality.APPLICATION_MODAL);
        partViewStage.showAndWait();
    }

    public void onDeletePart() {
        Part selectedPart = partTableView.getSelectionModel().getSelectedItem();

        boolean userConfirmed = JavaFXUtilities.confirmAction("Delete", selectedPart.getName(),
                "Are you sure you want to delete this Part?");

        if (selectedPart != null && userConfirmed) {
            inventory.deletePart(selectedPart);
        }
    }

    public void onAddProduct(ActionEvent actionEvent) throws IOException {
        loadProductView(actionEvent, "Add Product", null);
    }

    public void onModifyProduct(ActionEvent actionEvent) throws IOException {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            loadProductView(actionEvent, "Modify Product", selectedProduct);
        }
    }

    private void loadProductView(ActionEvent actionEvent, String title, Product selectedProduct) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/ProductView.fxml"));
        Parent productViewParent = loader.load();
        ProductViewController controller = loader.getController();


        // Pass Store and Part ref to Controller
        controller.initData(inventory, selectedProduct);

        // Set stage with Product View
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setTitle(window.getTitle() + " - " + title);
        window.setScene(new Scene(productViewParent, 1200, 800));
        window.show();
    }

    public void onDeleteProduct() {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        boolean userConfirmed = JavaFXUtilities.confirmAction("Delete", selectedProduct.getName(),
                "Are you sure you want to delete this Product?");

        if (selectedProduct != null && userConfirmed) {
            inventory.deleteProduct(selectedProduct);
        }
    }

    public void onExit() {
        Platform.exit();
    }
}
