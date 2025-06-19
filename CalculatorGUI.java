import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class CalculatorGUI extends JFrame implements ActionListener
{
    JTextField textField;
    JTextField resultField;
    JButton[] numberButtons = new JButton[10];
    JButton addButton, subButton, mulButton, divButton, eqButton, clrButton, delButton;
    JButton dotButton, percentButton, powerButton, sqrtButton;
    JPanel panel;

    double num1 = 0, num2 = 0, result = 0;
    char operator;

    CalculatorGUI()
    {
        this.setTitle("Advanced Calculator");
        this.setSize(420, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);

        textField = new JTextField();
        textField.setBounds(30, 30, 340, 40);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setEditable(false);
        this.add(textField);

        resultField = new JTextField();
        resultField.setBounds(30, 80, 340, 40);
        resultField.setFont(new Font("Arial", Font.BOLD, 24));
        resultField.setEditable(false);
        resultField.setHorizontalAlignment(JTextField.RIGHT);
        this.add(resultField);

        for (int i = 0; i < 10; i++)
        {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].setFont(new Font("Arial", Font.BOLD, 20));
            numberButtons[i].addActionListener(this);
        }

        clrButton = new JButton("C");
        delButton = new JButton("DEL");
        divButton = new JButton("/");
        mulButton = new JButton("*");
        subButton = new JButton("-");
        addButton = new JButton("+");
        eqButton = new JButton("=");
        dotButton = new JButton(".");
        percentButton = new JButton("%");
        powerButton = new JButton("^");
        sqrtButton = new JButton("√");

        JButton[] functionButtons =
        {
            clrButton, delButton, divButton, mulButton,
            subButton, addButton, eqButton,
            dotButton, percentButton, powerButton, sqrtButton
        };

        for (JButton btn : functionButtons)
        {
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.addActionListener(this);
        }

        panel = new JPanel();
        panel.setBounds(30, 140, 340, 450);
        panel.setLayout(new GridLayout(5, 4, 10, 10));

        panel.add(clrButton);
        panel.add(delButton);
        panel.add(sqrtButton);
        panel.add(percentButton);

        panel.add(numberButtons[7]);
        panel.add(numberButtons[8]);
        panel.add(numberButtons[9]);
        panel.add(divButton);

        panel.add(numberButtons[4]);
        panel.add(numberButtons[5]);
        panel.add(numberButtons[6]);
        panel.add(mulButton);

        panel.add(numberButtons[1]);
        panel.add(numberButtons[2]);
        panel.add(numberButtons[3]);
        panel.add(subButton);

        panel.add(dotButton);
        panel.add(numberButtons[0]);
        panel.add(powerButton);
        panel.add(addButton);

        eqButton.setBounds(30, 600, 340, 40);
        this.add(eqButton);

        this.add(panel);
        this.setVisible(true);
    }

    public static void main(String[] args)
    {
        new CalculatorGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();

        for (int i = 0; i < 10; i++) {
            if (src == numberButtons[i]) {
                textField.setText(textField.getText() + i);
                return;
            }
        }

        if (src == dotButton) {
            textField.setText(textField.getText() + ".");
        } else if (src == clrButton) {
            textField.setText("");
            resultField.setText("");
        } else if (src == delButton) {
            String text = textField.getText();
            if (!text.isEmpty()) {
                textField.setText(text.substring(0, text.length() - 1));
            }
        } else if (src == addButton) {
            textField.setText(textField.getText() + " + ");
        } else if (src == subButton) {
            textField.setText(textField.getText() + " - ");
        } else if (src == mulButton) {
            textField.setText(textField.getText() + " * ");
        } else if (src == divButton) {
            textField.setText(textField.getText() + " / ");
        } else if (src == percentButton) {
            textField.setText(textField.getText() + " % ");
        } else if (src == powerButton) {
            textField.setText(textField.getText() + " ^ ");
        } else if (src == sqrtButton) {
            textField.setText(textField.getText() + " √ ");
        } else if (src == eqButton) {
            evaluateExpression();
        }
    }

    void evaluateExpression()
    {
        try {
            String expr = textField.getText().trim();
            String[] tokens = expr.split(" ");

            if (tokens.length == 1 && tokens[0].startsWith("√")) {
                double num = Double.parseDouble(tokens[0].substring(1));
                resultField.setText(String.valueOf(Math.sqrt(num)));
                return;
            }

            if (tokens.length == 2 && tokens[0].equals("√")) {
                double num = Double.parseDouble(tokens[1]);
                resultField.setText(String.valueOf(Math.sqrt(num)));
                return;
            }

            if (tokens.length != 3) {
                resultField.setText("Invalid");
                return;
            }

            double a = Double.parseDouble(tokens[0]);
            String op = tokens[1];
            double b = Double.parseDouble(tokens[2]);

            switch (op) {
                case "+":
                    result = a + b;
                    break;
                case "-":
                    result = a - b;
                    break;
                case "*":
                    result = a * b;
                    break;
                case "/":
                    if (b == 0) {
                        resultField.setText("Error");
                        return;
                    }
                    result = a / b;
                    break;
                case "%":
                    result = a % b;
                    break;
                case "^":
                    result = Math.pow(a, b);
                    break;
                default:
                    resultField.setText("Unknown Op");
                    return;
            }

            resultField.setText(String.valueOf(result));

        } catch (Exception ex) {
            resultField.setText("Error");
        }
    }
}
