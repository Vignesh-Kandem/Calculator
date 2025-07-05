import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class SmartCalculator extends JFrame implements ActionListener, KeyListener {

    JTextField expressionField, resultField;
    JButton equalsButton;
    JPanel buttonPanel;
    JComboBox<String> themeSelector;
    StringBuilder expression = new StringBuilder();
    Color lightBG = new Color(245, 245, 245), darkBG = new Color(25, 25, 25);
    Color lightButton = new Color(220, 230, 245), darkButton = new Color(50, 50, 50);
    boolean isDarkMode = false;

    public SmartCalculator() {
        setTitle("Smart Calculator");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        JLabel titleLabel = new JLabel("Calculator");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        titleLabel.setBounds(20, 0, 300, 40);
        titleLabel.setForeground(new Color(70, 130, 180));
        add(titleLabel);

        themeSelector = new JComboBox<>(new String[]{"Light Mode", "Dark Mode"});
        themeSelector.setBounds(450, 5, 120, 30);
        themeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        themeSelector.addActionListener(e -> toggleTheme());
        add(themeSelector);

        expressionField = new JTextField();
        expressionField.setBounds(40, 50, 500, 40);
        expressionField.setFont(new Font("Times New Roman", Font.PLAIN, 22));
        expressionField.setEditable(false);
        expressionField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(expressionField);

        resultField = new JTextField();
        resultField.setBounds(40, 100, 500, 40);
        resultField.setFont(new Font("Times New Roman", Font.BOLD, 26));
        resultField.setEditable(false);
        resultField.setHorizontalAlignment(JTextField.RIGHT);
        resultField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(resultField);

        buttonPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBounds(40, 160, 500, 350);
        String[] buttons = {
            "C", "DEL", "%", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            ".", "0", "√", "^"
        };
        for (String text : buttons) {
            CircularButton btn = new CircularButton(text);
            btn.setFont(new Font("Times New Roman", Font.BOLD, 20));
            btn.addActionListener(this);
            buttonPanel.add(btn);
        }
        add(buttonPanel);

        equalsButton = new RoundedButton("=");
        equalsButton.setBounds(40, 530, 500, 60);
        equalsButton.setFont(new Font("Times New Roman", Font.BOLD, 30));
        equalsButton.addActionListener(this);
        add(equalsButton);

        applyTheme();

        setFocusable(true);
        addKeyListener(this);
        setVisible(true);
    }

    void toggleTheme() {
        isDarkMode = themeSelector.getSelectedIndex() == 1;
        applyTheme();
    }

    void applyTheme() {
        Color bg = isDarkMode ? darkBG : lightBG;
        Color fg = isDarkMode ? Color.WHITE : Color.BLACK;
        Color fieldBG = isDarkMode ? new Color(30, 30, 30) : Color.WHITE;
        Color btnColor = isDarkMode ? darkButton : lightButton;

        getContentPane().setBackground(bg);
        expressionField.setBackground(fieldBG);
        resultField.setBackground(fieldBG);
        expressionField.setForeground(fg);
        resultField.setForeground(fg);

        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton btn) {
                btn.setBackground(btnColor);
                btn.setForeground(fg);
                btn.setFont(new Font("Times New Roman", Font.BOLD, 20));
            }
        }
        equalsButton.setBackground(isDarkMode ? new Color(102, 102, 255) : new Color(180, 210, 250));
        equalsButton.setForeground(fg);
        equalsButton.setFont(new Font("Times New Roman", Font.BOLD, 30));

        repaint();
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
            case "^" -> {
                expression.append("^");
                expressionField.setText(expression.toString());
            }
            default -> {
                expression.append(cmd);
                expressionField.setText(expression.toString());
            }
        }

        requestFocusInWindow();
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
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "√"));
        } else if (c == 'p' || c == 'P') {
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "^"));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartCalculator::new);
    }
}

class CircularButton extends JButton {
    public CircularButton(String label) {
        super(label);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        g2.setColor(getBackground());
        g2.fill(shape);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {}
}

class RoundedButton extends JButton {
    public RoundedButton(String label) {
        super(label);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30);
        g2.setColor(getBackground());
        g2.fill(shape);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {}
}
