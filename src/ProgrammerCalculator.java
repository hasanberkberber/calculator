import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;

public class ProgrammerCalculator {

    /* These calculations are all based 10. */

    public static int add(int a, int b) {
    /* Performs the addition operation */
        return a + b;
    }

    public static int subtract(int a, int b) {
    /* Performs the subtraction operation */
        return a - b;
    }

    public static int multiply(int a, int b) {
    /* Performs the multiplication operation */
        return a * b;
    }

    public static int divide(int a, int b) {
    /* Performs the division operation */
        if (b == 0) {
            return Integer.MAX_VALUE; // Return a max value to indicate error (divide by zero)
        }
        return a / b;
    }

    public static int and(int a, int b) {
        /* Performs and operation */
        return a & b;
    }

    public static int or(int a, int b) {
        /* Performs or operation */
        return a | b;
    }

    public static int xor(int a, int b) {
        /* Performs xor operation */
        return a ^ b;
    }

    public static int rightShift(int a) {
    /* Converts the input to it's right shift */
        int result = a >> 2; // Shift right by 2
        return result;
    }

    public static int leftShift(int a) {
    /* Converts the input to it's left shift */
        int result = a << 2;
        return result;
    }

    public static int not(int a) {
    /* Converts the input to its not */
        int result = ~a;
        return result;
    }

    public static Integer parse(String input, String calculationMode) {
        /*
            Parses the input depending on the calculationMode.
            If calculation mode is `HEX`, that means, the input is based HEX, and we parse it radix 16.
        */

        return switch (calculationMode) {
            case "hex" -> Integer.parseInt(input, 16);
            case "dec" -> Integer.parseInt(input, 10);
            case "bin" -> Integer.parseInt(input, 2);
            default -> 0;
        };

    }

    public static String[] split(String inputEquation) {
        /*
        * The input equation consist of operands and operators.
        * The operands are separated by operators. However, sometimes we have only one operands with an operator.
        *
        * Example with two operands with an operator.
        *
        *               2           +          2
        *           [operand]  [operator]  [operand]
        *
        *
        * Example with one operand with an operator.
        *
        *               5          NOT
        *           [operand]  [operator]
        *
         */
        // Given equation string consist of operators and operands, here we parse and split them using regex.
        // Regular expression to match operators (longer terms first), hex numbers, standalone hex digits, and decimal numbers
        String regex = "(0x[0-9A-Fa-f]+|AND|OR|XOR|NOT|>>|<<|[A-Fa-f]|\\d+|\\+|-|\\*|/)";

        // Compile the pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputEquation);

        // We store each split part (tokens) in a list.
        ArrayList<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        // Convert the list to an array and return
        return tokens.toArray(new String[0]);
    }


    // Method to handle the calculation logic
    public static String calculate(JTextField display, String calculationMode) {
        // Get the textfield string
        String input = display.getText();

        // Check if the input is empty
        if (input.isEmpty()) {
            return "";
        }

        // Call our helper split function to parse the equation string.
        String[] tokens = split(input);
        // Prints out the tokens in a list. E.g., [0, +, 1], [7, AND, 3] or [1, >>]
        System.out.println(Arrays.toString(tokens));

        // Perform the operations
        // The first token will be the result for now. We will keep performing operation and re-assign on it.
        int result = parse(tokens[0], calculationMode);

        //                                                       i=1                i=3
        // We will increment by two because list has [operand, operator, operand, operator ...] pattern.
        for (int i = 1; i < tokens.length; i += 2) {
            String operator = tokens[i];
            Integer nextOperand;
            if (operator.equals("<<") || operator.equals(">>") || operator.equals("NOT")) {
                /* If we have any of these operators, then we will not need second operand */
                nextOperand = null;
            } else {
                // Otherwise, we get the next operand.
                nextOperand = parse(tokens[i + 1], calculationMode);
            }

            // Perform the corresponding operation based on the operator
            switch (operator) {
                /* Operations that require nextOperand (two operands) */
                case "+":
                    result = add(result, nextOperand);
                    break;
                case "-":
                    result = subtract(result, nextOperand);
                    break;
                case "*":
                    result = multiply(result, nextOperand);
                    break;
                case "/":
                    result = divide(result, nextOperand);
                    break;
                case "AND":
                    result = and(result, nextOperand);
                    break;
                case "OR":
                    result = or(result, nextOperand);
                    break;
                case "XOR":
                    result = xor(result, nextOperand);
                    break;
                /* Single operators */
                case ">>":
                    result = rightShift(result);
                    break;
                case "<<":
                    result = leftShift(result);
                    break;
                case "NOT":
                    result = not(result);
                    break;
                default:
                    return "Error"; // Invalid operator
            }
        }

        /* So far, the result is in base 10. We will convert into _desired_ base based on calculationMode,
        and returns the string. */
        return switch (calculationMode) {
            case "dec" -> Integer.toString(result);
            case "hex" -> Integer.toHexString(result).toUpperCase();
            case "bin" -> Integer.toBinaryString(result);
            default -> throw new IllegalArgumentException("Invalid calculation mode: " + calculationMode);
        };
    }

    public static void main(String[] args) {
        // Frame.
        JFrame frame = new JFrame("Programmer Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setResizable(false);
        frame.setLayout(null);

        // UI components and setup (same as before)
        JRadioButton hexButton = new JRadioButton("Hexadecimal");
        JRadioButton decButton = new JRadioButton("Decimal");
        JRadioButton binButton = new JRadioButton("Binary");

        // RadioButtons
        hexButton.setActionCommand("hex");
        decButton.setActionCommand("dec");
        binButton.setActionCommand("bin");

        hexButton.setSelected(true);  // Set one as selected by default.

        // Group the radio buttons
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(hexButton);
        modeGroup.add(decButton);
        modeGroup.add(binButton);

        // Size config of radio buttons.
        hexButton.setBounds(20, 20, 120, 30);
        decButton.setBounds(20, 50, 120, 30);
        binButton.setBounds(20, 80, 120, 30);
        // And add them to the frame.
        frame.add(hexButton);
        frame.add(decButton);
        frame.add(binButton);

        // The inputEquation field which is a text field.
        JTextField display = new JTextField();
        display.setBounds(20, 120, 340, 40);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        frame.add(display);

        // Add ActionListener to each radio button
        // With each selection, it will clear out the text automatically.
        ActionListener listener = e -> {
            JRadioButton selectedButton = (JRadioButton) e.getSource();
            System.out.println("Selected mode: " + selectedButton.getText());
            display.setText("");
        };

        // Add the listeners.
        hexButton.addActionListener(listener);
        decButton.addActionListener(listener);
        binButton.addActionListener(listener);

        // Button list.
        String[] buttons = {
                "A", "NOT", "<<", ">>", "CLEAR",
                "B", "AND", "OR", "XOR", "+",
                "C", "7", "8", "9", "-",
                "D", "4", "5", "6", "*",
                "E", "1", "2", "3", "/",
                "F", "", "0", "", "="
        };
        // Their sizes.
        JButton[] buttonComponents = new JButton[buttons.length];
        int x = 20, y = 180, width = 65, height = 40;
        // Iterate over each buttons.
        for (int i = 0; i < buttons.length; i++) {
            if (!buttons[i].trim().isEmpty()) {
                JButton button = new JButton(buttons[i]);
                buttonComponents[i] = button;
                // If button is clear, it will have different look.
                if (buttons[i].equals("CLEAR")) {
                    button.setBackground(Color.RED);
                    button.setForeground(Color.WHITE);
                }

                button.setBounds(x, y, width, height);
                frame.add(button);

                // Each clicked button will act with this action Listener.
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String currentText = display.getText();
                        String buttonText = button.getText();
                        // If clicked button is CLEAR, empty the text field.
                        if (buttonText.equals("CLEAR")) {
                            display.setText("");
                        // If clicked button is "=", then perform the calculations.
                        } else if (buttonText.equals("=")) {
                            String calculationMode = modeGroup.getSelection().getActionCommand();
                            String result = calculate(display, calculationMode);
                            display.setText(result); // Display the result
                        } else {
                            display.setText(currentText + buttonText); // Append text
                        }
                    }
                });
            }

            // Adjust position for next button
            x += width + 10;
            if ((i + 1) % 5 == 0) {
                x = 20;
                y += height + 10;
            }
        }

        // Add ActionListener to Binary Radio Button
        binButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Disable all number buttons except 1 and 0
                for (JButton button : buttonComponents) {
                    if (button != null) {
                        String text = button.getText();
                        if (text.matches("[2-9A-F]")) {
                            button.setEnabled(false);
                        } else if (text.equals("1") || text.equals("0")) {
                            button.setEnabled(true);
                        }
                    }
                }
            }
        });

        // Add ActionListener to Decimal Radio Button
        decButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Enable decimal number buttons (0-9), disable hexadecimal buttons (A-F)
                for (JButton button : buttonComponents) {
                    if (button != null) {
                        String text = button.getText();
                        if (text.matches("[A-F]")) {  // A - F is disabled.
                            button.setEnabled(false);
                        } else if (text.matches("[0-9]")) {  // we are allowing number from 0 to 9.
                            button.setEnabled(true);
                        }
                    }
                }
            }
        });

        // Add ActionListener to Hexadecimal Radio Button
        hexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Enable all buttons (0-9, A-F, and operators)
                for (JButton button : buttonComponents) {
                    if (button != null) {  // whatever buttonm it is (unless null) we set it enabled.
                        button.setEnabled(true);
                    }
                }
            }
        });

        // Set frame visibility
        frame.setVisible(true);
    }
}
