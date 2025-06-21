import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CalculatorGUI extends JFrame implements ActionListener, KeyListener {

    JTextField expressionField, resultField;
    JPanel panel;

    StringBuilder expression = new StringBuilder();

    public CalculatorGUI() {
        setTitle("Smart Calculator");
        setSize(420, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        // Expression Field
        expressionField = new JTextField();
        expressionField.setBounds(30, 30, 340, 40);
        expressionField.setFont(new Font("Arial", Font.PLAIN, 20));
        expressionField.setEditable(false);
        add(expressionField);

        // Result Field
        resultField = new JTextField();
        resultField.setBounds(30, 80, 340, 40);
        resultField.setFont(new Font("Arial", Font.BOLD, 24));
        resultField.setEditable(false);
        resultField.setHorizontalAlignment(JTextField.RIGHT);
        add(resultField);

        // Buttons
        String[] buttons = {
                "C", "DEL", "%", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                ".", "0", "√", "="
        };

        panel = new JPanel();
        panel.setBounds(30, 140, 340, 450);
        panel.setLayout(new GridLayout(5, 4, 10, 10));
        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.addActionListener(this);
            panel.add(btn);
        }

        add(panel);
        setFocusable(true);
        addKeyListener(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "=" -> evaluate();
            case "C" -> {
                expression.setLength(0);
                expressionField.setText("");
                resultField.setText("");
            }
            case "DEL" -> {
                if (expression.length() > 0) {
                    expression.deleteCharAt(expression.length() - 1);
                    expressionField.setText(expression.toString());
                }
            }
            case "√" -> {
                try {
                    double value = Double.parseDouble(expression.toString());
                    double result = Math.sqrt(value);
                    resultField.setText(String.valueOf(result));
                    expression.setLength(0);
                    expression.append(result);
                    expressionField.setText(expression.toString());
                } catch (Exception ex) {
                    resultField.setText("Error");
                }
            }
            case "%" -> {
                expression.append("/100");
                expressionField.setText(expression.toString());
            }
            default -> {
                expression.append(cmd);
                expressionField.setText(expression.toString());
            }
        }
    }

    void evaluate() {
        try {
            double result = eval(expression.toString());
            resultField.setText(String.valueOf(result));
            expression.setLength(0);
            expression.append(result);
            expressionField.setText(expression.toString());
        } catch (Exception ex) {
            resultField.setText("Error");
        }
    }

    // Simple expression evaluator
    public double eval(String expr) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    // Keyboard Support
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if (Character.isDigit(c) || "+-*/.^".indexOf(c) >= 0) {
            expression.append(c);
            expressionField.setText(expression.toString());
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            evaluate();
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
                expressionField.setText(expression.toString());
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            expression.setLength(0);
            expressionField.setText("");
            resultField.setText("");
        } else if (c == 'r' || c == 'R') {
            try {
                double value = Double.parseDouble(expression.toString());
                double result = Math.sqrt(value);
                resultField.setText(String.valueOf(result));
                expression.setLength(0);
                expression.append(result);
                expressionField.setText(expression.toString());
            } catch (Exception ex) {
                resultField.setText("Error");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorGUI::new);
    }
}
