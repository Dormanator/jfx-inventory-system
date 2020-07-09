package info.ryandorman.inventorysystem.model;

/*
 *   Ryan Dorman
 *   ID: 001002824
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

public class Inventory {

    private ObservableList<Part> allParts;
    private ObservableList<Product> allProducts;


    public Inventory() {
        this.allParts = FXCollections.observableArrayList();
        this.allProducts = FXCollections.observableArrayList();
    }

    public void addPart(Part newPart) {
        allParts.add(newPart);
    }

    public void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    public Part lookupPart(int partId) {
        return allParts.stream()
                .filter(part -> partId == part.getId())
                .findAny()
                .orElse(null);
    }

    public Product lookupProduct(int productId) {
        return allProducts.stream()
                .filter(product -> productId == product.getId())
                .findAny()
                .orElse(null);
    }

    public ObservableList<Part> lookupPart(String partName) {
        return allParts.stream()
                .filter(part -> part.getName().toLowerCase().contains(partName.toLowerCase()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<Product> lookupProduct(String productName) {
        return allProducts.stream()
                .filter(product -> product.getName().toLowerCase().contains(productName.toLowerCase()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public void updatePart(int index, Part selectedPart) {
        allParts.set(index, selectedPart);
    }

    public void updateProduct(int index, Product newProduct) {
        allProducts.set(index, newProduct);
    }

    public boolean deletePart(Part selectedPart) {
        return allParts.remove(selectedPart);
    }

    public boolean deleteProduct(Product selectedProduct) {
        return allProducts.remove(selectedProduct);
    }

    public ObservableList<Part> getAllParts() {
        return allParts;
    }

    public ObservableList<Product> getAllProducts() {
        return allProducts;
    }
}
