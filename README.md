# SimpleEditor

*********

## Problems detected by SonarQube by file

### Editor.java

* Code Smells

  * unused imports `edu.najah.cap.ex.EditorException`, `java.awt.BorderLayout`, `java.util.ArrayList`, `java.util.List`. Affects readability.
  * member `TP` is not camelCase. Affects readability and maintainability.
  * members `TP`, `menu`, `copy`, `paste`, `cut`, `move`, `changed` are public. Affects maintainability.
  * members `copy`, `paste`, `cut`, `move` should be declared on separate lines. Affects readability.
  * should replace `JFrame.EXIT_ON_CLOSE` with `WindowConstants.EXIT_ON_CLOSE`. Affects maintainability.
  * method `BuildMenu` is not camelCase. Affects readability and maintainability.
  * values `InputEvent.CTRL_MASK` and `InputEvent.SHIFT_MASK` are deprecated. Affects reliability.
  * block of commented out code should be removed. Affects readability.
  * methods `actionPerformed` and `loadFile` have too many nested statements and should be refactored. affects readability.
  * define constants for the duplicating strings literals `"The file has changed. You want to save it?"`, `"Save File"`, `"Cannot write file!"` and `"user.home"`. Affects reusability.
  * `System.out` or `System.err` should be replaced by a logger. Affects integrity.
  * using a generic exception on `line 199` and `line 256`. Affects readability.
  * nested try blocks on `line 253` and `line 266` should be extracted into a separate method. Affects maintainability.
  * unused private method `saveAsText`. Affects maintainability.

* Bugs

  * conditions in `line 181` and `line 238` will always evaluate to `true`. Affects readability and maintainability.
  * `PrintWriter` should be closed. Affects efficiency.
  * a `NullPointerException` could be thrown, because `writer` at `line 297` is nullable. Affects reliability.

* Security Hotspots

  * all the `.printStackTrace` should not be in production, because they might leak sensitive data. Affects integrity.

### FindDialog.java

* Code Smells

  * "parent" is the name of a field in "Component": `Editor parent;`. Affects readability and maintainability.

    Here is how to solve this issue, renamed the field in the "Editor" class:

    ```java
    private final Editor editor;
    ```

  * Declare `close` on a separate line: `JButton find, close;`. . Affects readability.

    Here is how to solve this issue:

    ```java
    JButton find;
    JButton close;
    ```

  * Make "matcher" transient or serializable: `Matcher matcher;`. Affects efficiency.
    To make the "matcher" field either transient in Java, you need to ensure that it can be properly serialized and deserialized.

    Here is how to solve this issue:

    ```java
    private transient Matcher matcher;
    ```

    This indicates that the field should not be included when the object is serialized.

  * This block of commented-out lines of code should be removed: `// closeDialog();`
  * command line
    Programmers should not comment out code as it bloats programs and reduces readability.
    Unused code should be deleted and can be retrieved from history

    ```java
    @Override
    public void keyReleased(KeyEvent e) { //Empty }
    ```

    ```java
    @Override
    public void keyTyped(KeyEvent e) { //Empty }
    ```

    There are several reasons why a method may not have a method body:
    It is an inadvertent omission, and should be fixed to prevent unexpected behavior in production.
    It is not supported yet, or it will never be supported. In this case, any object or command must be added to solve this case. The nested ampersand must be explained to not override a space.
    The method shall not be empty.

### EditorException.java

* Code Smells

  * The issue was solved by decreasing the inheritance, in this case by making EditorException inherit from the default Exception instead of NoSuchFileException.

    ```java
    public class EditorException extends Exception {
      public EditorException(String message) {
        super(message);
      }
    }
    ```

## SonarQube false positives and false negatives by file

### Editor.java

* False Negatives:
  * The `actions` string array should have been either constants or an enum, because a statement like `actions[0]` is not clear. <br />
    SonarQube didn't detect it because it doesn't see the meaning behind the array or why its used.

  * The if statements in method `actionPerformed` should have been a switch statement. <br />
    It may have failed to detect it because `.equals` has been used instead of equal operator `==`.

  * Not all actions compared in `actionPerformed` is in the `actions` array. <br />
    Most likely a combination of the previous two.

  * The duplicate statements
    * `new JFileChooser(System.getProperty(USER_HOME))` and <br />
    * `JOptionPane.showConfirmDialog(null, FILE_CHANGE_MESSAGE, SAVE_FILE_MESSAGE, 0, 2)` <br />

    should be moved to functions that initializes each because they use the same parameters every time and constants in them should be removed. <br />
    Maybe it couldn't see it because the variable behind the statements is different.

  * The magical numbers in `JOptionPane.showConfirmDialog(null, FILE_CHANGE_MESSAGE, SAVE_FILE_MESSAGE, 0, 2)` should be <br />
    replaced with constants. <br />
    It most likely was not detected because the method param itself takes integers, and it couldn't know its constants.

  * There's a lot of similar statements using `writer` to save file content, it could be generalized and reused. <br />
    The statements are a little different every time, so it couldn't see it.

  * Button creation in `buildEditMenu` and `buildFileMenu` like

    ```java
      JMenuItem cut = new JMenuItem("Cut");
      cut.addActionListener(this);
      cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
      cut.setMnemonic('T');
      edit.add(cut);
    ```

      can be generalized into a function/method:

    ```java
      JMenuItem createButton(String text, char mnemonic, int keyEvent, int inputEvent) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(this);
        item.setAccelerator(KeyStroke.getKeyStroke(keyEvent, inputEvent));
        item.setMnemonic(mnemonic);
        return item;
      }
    ```

    The analyzer couldn't see the pattern at all, maybe because the call order of each item wasn't the same and used across different methods.

* False Positives:
  * None

### FindDialog.java

* False Positives:
  * None

### EditorException.java

* False Negatives:
  * None

* False Positives:
  * None
  
### EditorFileException.java

* False Negatives:
  * None

* False Positives:
  * None
  
### EditorSaveAsException.java

* False Negatives:
  * None

* False Positives:
  * None
    
### EditorSaveException.java

* False Negatives:
  * None

* False Positives:
  * None
  
## Work Distribution

*  Editor.java
*  EditorException.java and EditorFileException.java
*  EditorSaveAsException.java and EditorSaveException.java
*  FindDialog.java
