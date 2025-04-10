package org.example;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Analizador Lexico ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());

        JTextArea inputArea = new JTextArea(15, 40);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JTextPane outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputPane.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("JavaScript Entrada"));

        JScrollPane outputScrollPane = new JScrollPane(outputPane);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Analizador Salida"));

        JButton analyzeButton = new JButton("Analizar Lexico");
        JButton loadFileButton = new JButton("Abrir archivo");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadFileButton);
        buttonPanel.add(analyzeButton);

        topPanel.add(inputScrollPane, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(outputScrollPane, BorderLayout.CENTER);

        frame.add(mainPanel);

        // cargar archivo
        loadFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // leer doc
                    StringBuilder content = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                    }
                    inputArea.setText(content.toString());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading file: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // boton analizar
        analyzeButton.addActionListener(e -> {
            String code = inputArea.getText();
            if (code.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter or load JavaScript code first.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // analizador de codigo
                highlightCode(code, outputPane);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error analyzing code: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void highlightCode(String code, JTextPane outputPane) {
        try {

            JSLexer lexer = new JSLexer(new StringReader(code));

            StyledDocument doc = outputPane.getStyledDocument();
            doc.remove(0, doc.getLength()); // limpiar

            // definir los estilos
            Style defaultStyle = outputPane.getStyle(StyleContext.DEFAULT_STYLE);

            Style keywordStyle = outputPane.addStyle("keyword", defaultStyle);
            StyleConstants.setForeground(keywordStyle, Color.RED);
            StyleConstants.setBold(keywordStyle, true);

            Style identifierStyle = outputPane.addStyle("identifier", defaultStyle);
            StyleConstants.setForeground(identifierStyle, Color.BLACK);

            Style stringStyle = outputPane.addStyle("string", defaultStyle);
            StyleConstants.setForeground(stringStyle, new Color(0, 128, 0)); // verde

            Style numberStyle = outputPane.addStyle("number", defaultStyle);
            StyleConstants.setForeground(numberStyle, new Color(0, 0, 128)); // azul

            Style commentStyle = outputPane.addStyle("comment", defaultStyle);
            StyleConstants.setForeground(commentStyle, Color.GRAY);
            StyleConstants.setItalic(commentStyle, true);

            Style operatorStyle = outputPane.addStyle("operator", defaultStyle);
            StyleConstants.setForeground(operatorStyle, Color.BLUE);

            Style punctuationStyle = outputPane.addStyle("punctuation", defaultStyle);
            StyleConstants.setForeground(punctuationStyle, new Color(128, 0, 128)); // morado

            Style whitespaceStyle = outputPane.addStyle("whitespace", defaultStyle);

            Style errorStyle = outputPane.addStyle("error", defaultStyle);
            StyleConstants.setForeground(errorStyle, Color.RED);
            StyleConstants.setBackground(errorStyle, Color.YELLOW);

            // procesar tokens
            JSLexer.Token token;
            while ((token = lexer.yylex()) != null) {
                String text = token.getLexeme();
                Style style;

                // tokens por tipo
                switch (token.getType()) {
                    case KEYWORD:
                        style = keywordStyle;
                        break;
                    case IDENTIFIER:
                        style = identifierStyle;
                        break;
                    case STRING:
                        style = stringStyle;
                        break;
                    case NUMBER:
                        style = numberStyle;
                        break;
                    case COMMENT:
                        style = commentStyle;
                        break;
                    case OPERATOR:
                        style = operatorStyle;
                        break;
                    case PUNCTUATION:
                        style = punctuationStyle;
                        break;
                    case WHITESPACE:
                        style = whitespaceStyle;
                        break;
                    case ERROR:
                    default:
                        style = errorStyle;
                        break;
                }

                doc.insertString(doc.getLength(), text, style);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar Error
            try {
                outputPane.setText("Error analyzing code: " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}