package com.github.shadyosrs.dropcalculator;

import net.runelite.client.ui.PluginPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DropCalculatorPanel extends PluginPanel {

    private JTextField kcInput;
    private JTextField dropRateInput;

    public DropCalculatorPanel() {
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 1, 0, 5));
        formPanel.setOpaque(false);

        JLabel title = new JLabel("Advanced Drop Calculator");
        title.setForeground(Color.WHITE);
        formPanel.add(title);

        JLabel kcLabel = new JLabel("Simulated KC:");
        kcLabel.setForeground(Color.LIGHT_GRAY);
        formPanel.add(kcLabel);

        kcInput = new JTextField();
        formPanel.add(kcInput);

        // 1. Atualizamos o texto de instrução da caixa
        JLabel dropRateLabel = new JLabel("Drop Rate (e.g., 3/408 or 300):");
        dropRateLabel.setForeground(Color.LIGHT_GRAY);
        formPanel.add(dropRateLabel);

        dropRateInput = new JTextField();
        formPanel.add(dropRateInput);

        JButton calculateButton = new JButton("Calculate Simulation");
        formPanel.add(calculateButton);

        JLabel resultProb = new JLabel("Probability: --");
        resultProb.setForeground(Color.GREEN);
        formPanel.add(resultProb);

        // 2. Adicionamos a nova linha de "Drops Esperados" em cor Ciano
        JLabel expectedDrops = new JLabel("Expected Drops: --");
        expectedDrops.setForeground(Color.CYAN);
        formPanel.add(expectedDrops);

        JLabel result50 = new JLabel("KC for 50% chance: --");
        result50.setForeground(Color.ORANGE);
        formPanel.add(result50);

        JLabel result99 = new JLabel("KC for 99% chance (Dry): --");
        result99.setForeground(Color.RED);
        formPanel.add(result99);

        this.add(formPanel, BorderLayout.NORTH);

        // 3. Atualizamos o Manual de Instruções
        String infoText = "<html><body style='width: 170px; color: #a5a5a5; font-family: sans-serif; font-size: 10px; margin-top: 15px;'>"
                + "<b style='color: white;'>How to use:</b><br>"
                + "Enter KC and Rate. You can use fractions (3/408) or just the denominator (300).<br><br>"
                + "<b style='color: white;'>What this means:</b><br>"
                + "• <i>Probability:</i> Chance of getting at least 1 drop.<br>"
                + "• <i>Expected:</i> Average number of drops you should have by this KC.<br>"
                + "• <i>50% chance:</i> KC threshold for a coin-flip.<br>"
                + "• <i>99% chance:</i> The 'Dry Limit'.<br><br><br>"
                + "<center>Developed by <b style='color: #00ff00;'>Shady</b></center>"
                + "</body></html>";

        JLabel infoLabel = new JLabel(infoText);
        this.add(infoLabel, BorderLayout.CENTER);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String kcText = kcInput.getText().replace(",", "").trim();
                    String dropText = dropRateInput.getText().replace(",", "").trim();

                    double kc = Double.parseDouble(kcText);

                    // 4. A Inteligência de Frações
                    double numerator = 1.0;
                    double denominator = 1.0;

                    if (dropText.contains("/")) {
                        String[] parts = dropText.split("/");
                        numerator = Double.parseDouble(parts[0]);
                        denominator = Double.parseDouble(parts[1]);
                    } else {
                        // Se não tiver '/', assumimos que o numerador é 1 e o usuário digitou o denominador
                        numerator = 1.0;
                        denominator = Double.parseDouble(dropText);
                    }

                    // Proteção contra dividir por zero ou rates inválidos
                    if (denominator <= 0 || numerator <= 0 || numerator >= denominator) {
                        resultProb.setText("Error: Invalid drop rate");
                        resultProb.setForeground(Color.RED);
                        return;
                    }

                    // 5. A Nova Matemática
                    double dropChance = numerator / denominator;
                    double expectedNumDrops = kc * dropChance;

                    double chanceOfNotDropping = 1.0 - dropChance;
                    double chanceNotDroppingInKc = Math.pow(chanceOfNotDropping, kc);
                    double finalProbability = (1.0 - chanceNotDroppingInKc) * 100;

                    double kcFor50 = Math.log(0.5) / Math.log(chanceOfNotDropping);
                    double kcFor99 = Math.log(0.01) / Math.log(chanceOfNotDropping);

                    // 6. Atualizando as linhas na tela
                    resultProb.setText(String.format("Probability: %.2f%%", finalProbability));
                    resultProb.setForeground(Color.GREEN);

                    expectedDrops.setText(String.format("Expected Drops: %.2f", expectedNumDrops));

                    result50.setText(String.format("KC for 50%% chance: %.0f", kcFor50));
                    result99.setText(String.format("KC for 99%% chance: %.0f", kcFor99));

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    resultProb.setText("Error: Use numbers or X/Y format");
                    resultProb.setForeground(Color.RED);
                }
            }
        });
    }
}