package info.ryandorman.inventorysystem.utilities;

/*
 *   Ryan Dorman
 *   ID: 001002824
 */

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class JavaFXUtilities {
    public static boolean confirmAction(String title, String header, String content) {
        return alertUser(Alert.AlertType.CONFIRMATION, title, header, content);
    }

    public static boolean warnUser(String title, String header, String content) {
        return alertUser(Alert.AlertType.WARNING, title, header, content);
    }

    private static boolean alertUser(Alert.AlertType type, String title, String header, String content) {
        // Create Confirmation Alert and set the stylesheet on the pane
        Alert alert = new Alert(type);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(JavaFXUtilities.class.getResource("/view/theme.css").toExternalForm());

        // Set the Alert's type content
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Show the Alert and wait for a response, returning it to the calling method
        Optional<ButtonType> option = alert.showAndWait();

        return ButtonType.OK.equals(option.get());
    }

    public static UnaryOperator<TextFormatter.Change> isInteger = change -> {
        if (change.getControlText().matches("(([1-9][0-9]*)|0)?")) {
            return change;
        }
        return null;
    };

    public static UnaryOperator<TextFormatter.Change> isDouble = change -> {
        Pattern validEditingState = Pattern.compile("(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
        String text = change.getControlNewText();

        if (validEditingState.matcher(text).matches()) {
            return change;
        }
        return null;
    };

    public static StringConverter<Double> truncatePrice = new StringConverter<Double>() {
        @Override
        public Double fromString(String s) {
            if (s == null || s.isEmpty()) {
                return 0.0;
            } else {
                return Math.round(Double.valueOf(s) * 100.0) / 100.0;
            }
        }

        @Override
        public String toString(Double d) {
            if (d == null) {
                return "";
            } else {
                return d.toString();
            }
        }
    };
}
