package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.math.BigInteger;

public class Calculator extends Application {

    private TextField display;
    private ToggleGroup radixGroup;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calculator");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        display = new TextField();
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);

        gridPane.add(display, 0, 0, 5, 1);

        RadioButton binaryRadioButton = new RadioButton("Binary");
        RadioButton decimalRadioButton = new RadioButton("Decimal");
        RadioButton hexRadioButton = new RadioButton("Hexadecimal");

        radixGroup = new ToggleGroup();
        binaryRadioButton.setToggleGroup(radixGroup);
        decimalRadioButton.setToggleGroup(radixGroup);
        hexRadioButton.setToggleGroup(radixGroup);

        HBox radixBox = new HBox(10, binaryRadioButton, decimalRadioButton, hexRadioButton);
        gridPane.add(radixBox, 0, 1, 5, 1);

        setButton(gridPane, "7", 2, 0);
        setButton(gridPane, "8", 2, 1);
        setButton(gridPane, "9", 2, 2);
        setButton(gridPane, "/", 2, 3);

        setButton(gridPane, "4", 3, 0);
        setButton(gridPane, "5", 3, 1);
        setButton(gridPane, "6", 3, 2);
        setButton(gridPane, "*", 3, 3);

        setButton(gridPane, "1", 4, 0);
        setButton(gridPane, "2", 4, 1);
        setButton(gridPane, "3", 4, 2);
        setButton(gridPane, "-", 4, 3);

        setButton(gridPane, "0", 5, 0);
        setButton(gridPane, "A", 5, 1);
        setButton(gridPane, "B", 5, 2);
        setButton(gridPane, "+", 5, 3);

        setButton(gridPane, "C", 6, 0);
        setButton(gridPane, "D", 6, 1);
        setButton(gridPane, "E", 6, 2);
        setButton(gridPane, "=", 6, 3);

        setButton(gridPane, "F", 7, 0);
        setButton(gridPane, "Not", 7, 1);
        setButton(gridPane, "And", 7, 2);
        setButton(gridPane, "Or", 7, 3);

        setButton(gridPane, "<<", 8, 0);
        setButton(gridPane, ">>", 8, 1);
        setButton(gridPane, "Clr", 8, 2);
        setButton(gridPane, "Xor", 8, 3);

        Scene scene = new Scene(gridPane, 300, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setButton(GridPane gridPane, String label, int row, int col) {
        Button button = new Button(label);
        button.setMinSize(70, 70);
        button.setOnAction(e -> handleButtonClick(label));
        gridPane.add(button, col, row);


        button.setDisable(false);

        radixGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton selectedRadixButton = (RadioButton) newValue;
            String selectedRadix = selectedRadixButton.getText();
            updateButtonState(button, selectedRadix);
        });
    }

    private void updateButtonState(Button button, String radix) {
        String label = button.getText();



        if (isLogicalOperator(label) || isMathOperator(label) || label.equals("Clr") || label.equals("=")) {

            button.setDisable(false);
            return;
        }

        switch (radix) {
            case "Binary":
                button.setDisable(!(label.equals("0") || label.equals("1")));
                break;
            case "Decimal":
                button.setDisable(!label.matches("[0-9]"));
                break;
            case "Hexadecimal":
                button.setDisable(!label.matches("[0-9A-Fa-f]"));
                break;
        }
    }

    private boolean isLogicalOperator(String label) {
        return label.equals("Not") || label.equals("And") || label.equals("Or") || label.equals("Xor") || label.equals("<<") || label.equals(">>");

    }

    private boolean isMathOperator(String label) {
        return label.equals("+") || label.equals("-") || label.equals("*") || label.equals("/");
    }

    private void handleButtonClick(String value) {
        String currentText = display.getText();

        if (value.equals("Clr")) {
            display.clear();
        } else if (value.equals("=")) {
            try {
                String radix = getSelectedRadix();
                String result = performOperation(currentText, radix);
                display.setText(result);
            } catch (Exception e) {
                display.setText("Error");
            }
        } else {
            display.appendText(value);
        }
    }

    private String performOperation(String expression, String radix) {
        try {
            return switch (radix) {
                case "Binary" -> performBinaryOperation(expression);
                case "Decimal" -> performDecimalOperation(expression);
                case "Hexadecimal" -> performHexOperation(expression);
                default -> "Error";
            };
        } catch (Exception e) {
            return "Error";
        }
    }

    private String performBinaryOperation(String expression) {
        String[] operands = expression.split("[+\\-*/]");
        if (operands.length != 2) {
            return "Error";
        }
        String operator = expression.replaceAll("[0-1]+", "");
        if (operator.length() != 1) {
            return "Error";
        }

        BigInteger operand1 = new BigInteger(operands[0], 2);
        BigInteger operand2 = new BigInteger(operands[1], 2);

        BigInteger result;

        switch (operator) {
            case "+":
                result = operand1.add(operand2);
                break;
            case "-":
                result = operand1.subtract(operand2);
                break;
            case "*":
                result = operand1.multiply(operand2);
                break;
            case "/":
                if (!operand2.equals(BigInteger.ZERO)) {
                    result = operand1.divide(operand2);
                } else {
                    return "Error";
                }
                break;
            case "And":
                result = operand1.and(operand2);
                break;
            case "Or":
                result = operand1.or(operand2);
                break;
            case "Xor":
                result = operand1.xor(operand2);
                break;
            case "Not":
                result = performBinaryNot(operand1);
                break;
            default:
                return "Error";
        }

        return result.toString(2);
    }

    private BigInteger performBinaryNot(BigInteger value) {
        BigInteger allOnes = BigInteger.ONE.shiftLeft(value.bitLength()).subtract(BigInteger.ONE);
        return value.xor(allOnes);
    }

    private String performDecimalOperation(String expression) {
        String[] operands = expression.split("[+\\-*/]");
        if (operands.length != 2) {
            return "Error";
        }

        String operator = expression.replaceAll("[0-9]+", "");
        BigInteger operand1 = new BigInteger(operands[0]);
        BigInteger operand2 = new BigInteger(operands[1]);

        switch (operator) {
            case "+":
                return operand1.add(operand2).toString();
            case "-":
                return operand1.subtract(operand2).toString();
            case "*":
                return operand1.multiply(operand2).toString();
            case "/":
                if (!operand2.equals(BigInteger.ZERO)) {
                    return operand1.divide(operand2).toString();
                } else {
                    return "Error";
                }
            default:
                return "Error";
        }
    }

    private String performHexOperation(String expression) {
        String[] operands = expression.split("[+\\-*/]");
        if (operands.length != 2) {
            return "Error";
        }

        String operator = expression.replaceAll("[0-9A-Fa-f]+", "");
        BigInteger operand1 = new BigInteger(operands[0], 16);
        BigInteger operand2 = new BigInteger(operands[1], 16);

        switch (operator) {
            case "+":
                return operand1.add(operand2).toString(16).toUpperCase();
            case "-":
                return operand1.subtract(operand2).toString(16).toUpperCase();
            case "*":
                return operand1.multiply(operand2).toString(16).toUpperCase();
            case "/":
                if (!operand2.equals(BigInteger.ZERO)) {
                    return operand1.divide(operand2).toString(16).toUpperCase();
                } else {
                    return "Error";
                }
            default:
                return "Error";
        }
    }
    private String getSelectedRadix() {
        if (radixGroup.getSelectedToggle() != null) {
            RadioButton selectedRadixButton = (RadioButton) radixGroup.getSelectedToggle();
            return selectedRadixButton.getText();
        }
        return "Decimal";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
