package edu.najah.cap;

import edu.najah.cap.ex.EditorSaveException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class Editor extends JFrame implements ActionListener, DocumentListener {

	public static void main(String[] args) {
		new Editor();
	}

	private JEditorPane textPanel;// Text Panel
	private JMenuBar menu;// Menu
	private boolean changed = false;
	private File file;
	private final transient Logger logger;

	private String[] actions = { "Open", "Save", "New", "Edit", "Quit", "Save as..." };

	protected JMenu jmfile;
	private static final String FILE_CHANGE_MESSAGE = "The file has changed. You want to save it?";
	private static final String SAVE_FILE_MESSAGE = "Save file";
	private static final String CANT_WRITE_MESSAGE = "Cannot write file!";
	private static final String USER_HOME = "user.home";
	private static final String EXCEPTION_MESSAGE = "an exception was thrown";

	public JEditorPane getTextPanel() {
		return textPanel;
	}

	public void setTextPanel(JEditorPane newTextPanel) {
		this.textPanel = newTextPanel;
	}

	public Editor() {
		// Editor the name of our application
		super("Editor");
		textPanel = new JEditorPane();
		// center means middle of container.
		add(new JScrollPane(textPanel), "Center");
		textPanel.getDocument().addDocumentListener(this);
		logger = Logger.getLogger(Editor.class.getName());
		try {
			logger.addHandler(new FileHandler("./app.log"));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e, "Failed to add logger file handle: " + e.toString(), 0);
		}

		menu = new JMenuBar();
		setJMenuBar(menu);
		buildMenu();
		// The size of window
		setSize(500, 500);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private void buildMenu() {
		buildFileMenu();
		buildEditMenu();
	}

	private void buildFileMenu() {
		jmfile = new JMenu("File");
		jmfile.setMnemonic('F');
		menu.add(jmfile);
		JMenuItem n = new JMenuItem(actions[2]);
		n.setMnemonic('N');
		n.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		n.addActionListener(this);
		jmfile.add(n);
		JMenuItem open = new JMenuItem(actions[0]);
		jmfile.add(open);
		open.addActionListener(this);
		open.setMnemonic('O');
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		JMenuItem save = new JMenuItem(actions[1]);
		jmfile.add(save);
		save.setMnemonic('S');
		save.addActionListener(this);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		JMenuItem saveas = new JMenuItem(actions[5]);
		saveas
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		jmfile.add(saveas);
		saveas.addActionListener(this);
		JMenuItem quit = new JMenuItem(actions[4]);
		jmfile.add(quit);
		quit.addActionListener(this);
		quit.setMnemonic('Q');
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
	}

	private void buildEditMenu() {
		JMenu edit = new JMenu(actions[3]);
		menu.add(edit);
		edit.setMnemonic('E');
		// cut
		JMenuItem cut = new JMenuItem("Cut");
		cut.addActionListener(this);
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		cut.setMnemonic('T');
		edit.add(cut);
		// copy
		JMenuItem copy = new JMenuItem("Copy");
		copy.addActionListener(this);
		copy.setMnemonic('C');
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		edit.add(copy);
		// paste
		JMenuItem paste = new JMenuItem("Paste");
		paste.setMnemonic('P');
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		edit.add(paste);
		paste.addActionListener(this);
		// find
		JMenuItem find = new JMenuItem("Find");
		find.setMnemonic('F');
		find.addActionListener(this);
		edit.add(find);
		find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
		// select all
		JMenuItem sall = new JMenuItem("Select All");
		sall.setMnemonic('A');
		sall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		sall.addActionListener(this);
		edit.add(sall);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		try {
			if (action.equals(actions[4])) {
				System.exit(0);
			} else if (action.equals(actions[0])) {
				loadFile();
			} else if (action.equals(actions[1])) {
				save();
			} else if (action.equals(actions[2])) {
				createNewFile();
			} else if (action.equals(actions[5])) {
				saveAs(actions[5]);
			} else if (action.equals("Select All")) {
				textPanel.selectAll();
			} else if (action.equals("Copy")) {
				textPanel.copy();
			} else if (action.equals("Cut")) {
				textPanel.cut();
			} else if (action.equals("Paste")) {
				textPanel.paste();
			} else if (action.equals("Find")) {
				FindDialog find = new FindDialog(this, true);
				find.showDialog();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, e, "Error: " + ex.toString(), 0);
		}
	}

	private void loadFile() {
		JFileChooser dialog = new JFileChooser(System.getProperty(USER_HOME));
		dialog.setMultiSelectionEnabled(false);
		try {
			int result = dialog.showOpenDialog(this);

			if (result == 1)// 1 value if cancel is chosen.
				return;
			if (result == 0) {// value if approve (yes, ok) is chosen.
				loadFileAndSaveChanges(dialog);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			// 0 means show Error Dialog
			JOptionPane.showMessageDialog(null, e, "Error", 0);
		}
	}

	private void loadFileAndSaveChanges(JFileChooser dialog) {
		if (changed) {
			// Save file
			int ans = JOptionPane.showConfirmDialog(null, FILE_CHANGE_MESSAGE, SAVE_FILE_MESSAGE,
					0, 2);// 0 means yes and no question and 2 mean warning dialog
			if (ans == 1)// no option
				return;

			if (file == null) {
				saveAs(actions[1]);
				return;
			}

			writeToFile();
		}

		file = dialog.getSelectedFile();
		textPanel.setText(readFile());
		changed = false;
		setTitle("Editor - " + file.getName());
	}

	private String readFile() {
		StringBuilder rs = new StringBuilder();
		try (FileReader fr = new FileReader(file);
				BufferedReader reader = new BufferedReader(fr);) {
			String line;
			while ((line = reader.readLine()) != null) {
				rs.append(line + "\n");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			JOptionPane.showMessageDialog(null, "Cannot read file !", "Error !", 0);// 0 means show Error Dialog
		}

		return rs.toString();
	}

	private void saveAs(String dialogTitle) {
		dialogTitle = dialogTitle.toUpperCase();
		JFileChooser dialog = new JFileChooser(System.getProperty(USER_HOME));
		dialog.setDialogTitle(dialogTitle);
		int result = dialog.showSaveDialog(this);
		if (result != 0)// 0 value if approve (yes, ok) is chosen.
			return;
		file = dialog.getSelectedFile();
		writeToFile();
		changed = false;
		setTitle("Editor - " + file.getName());
	}

	private void save() {
		int ans = 0;
		if (changed) {
			// 0 means yes and no option, 2 Used for warning messages.
			ans = JOptionPane.showConfirmDialog(null, FILE_CHANGE_MESSAGE, SAVE_FILE_MESSAGE, 0, 2);
		}

		// 1 value from class method if NO is chosen.
		if (ans != 1) {
			if (file == null) {
				saveAs(actions[1]);
			} else {
				writeToFile();
			}
		}
	}

	private void writeToFile() {
		String text = textPanel.getText();
		logger.info(text);
		try (PrintWriter writer = new PrintWriter(file);) {
			if (!file.canWrite()) {
				throw new EditorSaveException(CANT_WRITE_MESSAGE);
			}
			writer.write(text);
			changed = false;
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			JOptionPane.showMessageDialog(null, "Failed to write to file: " + file.getName(), "Error !", 0);
		}
	}

	private void createNewFile() {
		if (changed) {
			// Save file
			// 0 means yes and no option, 2 Used for warning messages.
			int ans = JOptionPane.showConfirmDialog(null, FILE_CHANGE_MESSAGE, SAVE_FILE_MESSAGE, 0, 2);
			// 1 value from class method if NO is chosen.
			if (ans == 1)
				return;

			if (file == null) {
				saveAs(actions[1]);
				return;
			}

			String text = textPanel.getText();
			logger.info(text);
			writeToFile();
		}
		file = null;
		textPanel.setText("");
		changed = false;
		setTitle("Editor");
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		changed = true;
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changed = true;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		changed = true;
	}

}