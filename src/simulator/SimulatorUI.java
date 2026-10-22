package simulator;

import javax.swing.*;
import java.awt.*;

public class SimulatorUI extends JFrame {
    private final Simulator simulator = new Simulator();
    private final JTextArea trace = new JTextArea();
    private final JLabel state = new JLabel();

    public SimulatorUI() {
        setTitle("Thupakki - MS51FB9AE Simulator | Week 3");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel buttons = new JPanel();
        JButton load = new JButton("Load Demo");
        JButton reset = new JButton("Reset");
        JButton step = new JButton("Step");
        JButton run = new JButton("Run");
        JButton queue = new JButton("Load Queue Validation");

        buttons.add(load); buttons.add(queue); buttons.add(reset); buttons.add(step); buttons.add(run);
        add(buttons, BorderLayout.NORTH);

        trace.setEditable(false);
        trace.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        add(new JScrollPane(trace), BorderLayout.CENTER);
        add(state, BorderLayout.SOUTH);

        load.addActionListener(e -> { simulator.loadDemo(); refresh("Demo loaded."); });
        queue.addActionListener(e -> { simulator.loadQueueValidation(); refresh("Queue validation program loaded."); });
        reset.addActionListener(e -> { simulator.getCPU().reset(); refresh("Reset."); });
        step.addActionListener(e -> { simulator.getCPU().step(); refresh(simulator.getCPU().getLastTrace()); });
        run.addActionListener(e -> { simulator.getCPU().run(); refresh(simulator.getCPU().getLastTrace()); });
    }

    private void refresh(String message) {
        CPU c = simulator.getCPU();
        trace.setText(message + "\n\n" +
            "MEMORY [00-1F]\n" + c.getMemory().dump(0, 31) + "\n" +
            "STACK SP=" + c.getStack().getSP() + "  " + c.getStack().display() + "\n" +
            "QUEUE front=" + c.getQueue().getFrontIndex() +
            " rear=" + c.getQueue().getRearIndex() +
            " size=" + c.getQueue().size() + "  " + c.getQueue().display());
        state.setText(String.format("PC=%02X   A=%02X   R0=%02X R1=%02X R2=%02X   Z=%s C=%s   Halted=%s",
            c.getPC(), c.getAccumulator(), c.getRegisters()[0], c.getRegisters()[1],
            c.getRegisters()[2], c.isZeroFlag(), c.isCarryFlag(), c.isHalted()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulatorUI().setVisible(true));
    }
}
