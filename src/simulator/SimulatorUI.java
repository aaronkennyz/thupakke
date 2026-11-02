package simulator;

import javax.swing.*;
import java.awt.*;

public class SimulatorUI extends JFrame {

    private final Simulator simulator = new Simulator();

    private final JTextArea trace = new JTextArea();
    private final JLabel state = new JLabel();

    public SimulatorUI() {

        setTitle("Thupakki - MS51FB9AE Simulator | CRC-6");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(8, 8));

        JPanel buttons = new JPanel();

        JButton load = new JButton("Load Demo");
        JButton queue = new JButton("Load Queue Validation");
        JButton crc = new JButton("Load CRC-6");
        JButton reset = new JButton("Reset");
        JButton step = new JButton("Step");
        JButton run = new JButton("Run");

        buttons.add(load);
        buttons.add(queue);
        buttons.add(crc);
        buttons.add(reset);
        buttons.add(step);
        buttons.add(run);

        add(buttons, BorderLayout.NORTH);

        trace.setEditable(false);
        trace.setFont(
            new Font(Font.MONOSPACED, Font.PLAIN, 14)
        );

        add(new JScrollPane(trace), BorderLayout.CENTER);
        add(state, BorderLayout.SOUTH);

        load.addActionListener(e -> {
            simulator.loadDemo();
            refresh("Demo loaded.");
        });

        queue.addActionListener(e -> {
            simulator.loadQueueValidation();
            refresh("Queue validation program loaded.");
        });

        crc.addActionListener(e -> {
            simulator.loadCRC6();
            refresh("CRC-6 program loaded.");
        });

        reset.addActionListener(e -> {
            simulator.getCPU().reset();
            refresh("CPU reset.");
        });

        step.addActionListener(e -> {
            try {
                simulator.getCPU().step();
                refresh(simulator.getCPU().getLastTrace());
            } catch (Exception ex) {
                showError(ex);
            }
        });

        run.addActionListener(e -> {
            try {
                simulator.getCPU().run();
                refresh(simulator.getCPU().getLastTrace());
            } catch (Exception ex) {
                showError(ex);
            }
        });

        refresh("Simulator ready.");
    }

    private void refresh(String message) {

        CPU c = simulator.getCPU();

        int crcValue =
            c.getMemory().read(0x30);

        trace.setText(
            message + "\n\n" +

            "CRC-6 RESULT [30H] = " +
            String.format("%02XH", crcValue) + "\n\n" +

            "MEMORY [00-1F]\n" +
            c.getMemory().dump(0, 31) + "\n" +

            "INPUT DATA [40-48]\n" +
            c.getMemory().dump(0x40, 0x48) + "\n" +

            "STACK SP=" +
            c.getStack().getSP() + "  " +
            c.getStack().display() + "\n" +

            "QUEUE front=" +
            c.getQueue().getFrontIndex() +
            " rear=" +
            c.getQueue().getRearIndex() +
            " size=" +
            c.getQueue().size() + "  " +
            c.getQueue().display()
        );

        state.setText(String.format(
            "PC=%02X   A=%02X   R0=%02X R1=%02X R2=%02X   Z=%s C=%s   Halted=%s",
            c.getPC(),
            c.getAccumulator(),
            c.getRegisters()[0],
            c.getRegisters()[1],
            c.getRegisters()[2],
            c.isZeroFlag(),
            c.isCarryFlag(),
            c.isHalted()
        ));
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Execution Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new SimulatorUI().setVisible(true);
        });
    }
}