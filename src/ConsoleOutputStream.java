import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class ConsoleOutputStream extends OutputStream {
    private JTextArea textArea;

    public ConsoleOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // Redirect the byte to the JTextArea
        textArea.append(String.valueOf((char) b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // Redirect the byte array to the JTextArea
        textArea.append(new String(b, off, len));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
