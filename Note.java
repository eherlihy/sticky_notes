import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Note {
    static LinkedList<Note> notes = new LinkedList<Note>();
    static int numberOfNotes;

    JFrame display;
    JTextArea text;
    int index;
    boolean pinned; // if the note is pinned to front of screen

    Note(int x, int y) {
        index = numberOfNotes;
        pinned = false;
        display = new JFrame();
        // allows the undecorated frame to be dragged
        ComponentMover cm = new ComponentMover();
        cm.registerComponent(display);

        JButton add = new JButton();
        JButton delete = new JButton();
        JButton pin = new JButton();
        JButton saveAndQuit = new JButton();
        text = new JTextArea(10, 50);
        JPanel greyBox = new JPanel();

        add.setBounds(230, 10, 25, 25);
        delete.setBounds(265, 10, 25, 25);
        pin.setBounds(10, 10, 25, 25);
        saveAndQuit.setBounds(45, 10, 25, 25);
        add.setBorderPainted(false);
        delete.setBorderPainted(false);
        pin.setBorderPainted(false);
        saveAndQuit.setBorderPainted(false);

        text.setBounds(10, 50, 280, 240);
        text.setLineWrap(true);
        text.setTabSize(4);
        greyBox.setBounds(0, 0, 300, 45);
        
        add.setIcon(new ImageIcon("icons/addButton.png"));
        delete.setIcon(new ImageIcon("icons/deleteButton.png"));
        pin.setIcon(new ImageIcon("icons/pinButton.png"));
        saveAndQuit.setIcon(new ImageIcon("icons/saveAndQuitButton.png"));
        greyBox.setBackground(new Color(223,223,223));
        text.setFont(new Font("Arial", Font.PLAIN, 14));

        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNote();
            }
        });

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                display.dispatchEvent(new WindowEvent(display, WindowEvent.WINDOW_CLOSING));
                deleteNote(index);
            }
        });

        pin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pinned = !pinned;
                if (pinned == true) {
                    pin.setIcon(new ImageIcon("icons/pinnedButton.png"));
                }
                else {
                    pin.setIcon(new ImageIcon("icons/pinButton.png"));
                }
                display.setAlwaysOnTop(pinned);
            }
        });

        saveAndQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveNotes();
            }
        });

        display.add(add);
        display.add(delete);
        display.add(pin);
        display.add(saveAndQuit);
        display.add(text);
        display.add(greyBox);

        display.setUndecorated(true);
        display.getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
        display.getContentPane().setBackground(Color.WHITE);
        display.setLayout(null);
        display.setBounds(x, y, 300, 300);
        display.setVisible(true);
    }

    void text(String t) {
        text.setText(t);
    }

    String getText() {
        return text.getText();
    }

    void setVisible() {
        display.setVisible(true);
    }

    int[] getLocation() {
        Point p = display.getLocationOnScreen();
        return new int[] {p.x, p.y};
    }

    // add a new sticky note
    static void addNote() {
        if (numberOfNotes < 10) {
            Note n = new Note(400, 400);
            notes.add(n);
            numberOfNotes++;
        }
    }

    // add a sticky note (on restore from save.txt)
    static void addNote(String t, int x, int y) {
        if (numberOfNotes < 10) {
            Note n = new Note(x, y);
            n.text.setText(t);
            notes.add(n);
            numberOfNotes++;
        }
    }

    // delete one sticky note
    static int deleteNote(int index) {
        notes.remove(index);
        numberOfNotes--;
        if (numberOfNotes == 0) {
            try {
                File file = new File("save.txt");
                PrintWriter pw = new PrintWriter(file);
                pw.write("");
                System.exit(0);
            }
            catch (FileNotFoundException e) {
                System.exit(0);
            }
        }
        updateIndeces();
        return 0;
    }

    // update indeces after deleting a note
    static void updateIndeces() {
        for (int i = 0; i < notes.size(); i++) {
            Note cur = (Note)notes.get(i);
            cur.index = i;
        }
    }

    // save notes and exit application
    void saveNotes() {
        try {
            File file = new File("save.txt");
            PrintWriter pw = new PrintWriter(file);
            for (int i = 0; i < numberOfNotes; i++) {
                Note cur = notes.get(i);
                if (cur != null) {
                    int[] coords = cur.getLocation();
                    String text = cur.getText() + "\n" + "█\n" + coords[0] + " " + coords[1] + "\n";
                    pw.write(text);
                }
            }
            pw.close();
            System.exit(0);
        }
        catch (IOException e) {
            System.exit(0);
        }
    }

    // restores all notes from save.txt and returns the number of notes
    static int restoreNotes() {
        int i = 0;
        try {
            Scanner file = new Scanner(new File("save.txt"));
            String s = "";
            while(file.hasNext()) {
                String line = file.nextLine();
                if (line.equals("█")) {
                    String[] coords = file.nextLine().split(" ");
                    addNote(s, Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                    i++;
                    s = "";
                }
                else {
                    s += line + "\n";
                }
            }
        }
        catch (FileNotFoundException e) {
            return 0;
        }
        return i;
    }

    public static void main(String args[]) {
        numberOfNotes = 0;
        numberOfNotes = restoreNotes();
        if (numberOfNotes == 0)
            addNote(); // create an initial note
    }
}