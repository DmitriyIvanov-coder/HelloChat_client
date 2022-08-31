package client.client;

import javafx.scene.control.TextArea;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class WriterHistory {
    File messagesHistory = new File("messagesHistory.txt");
    List linesOfHistoryFile;

    public void writeMessagesHistory(String messages) throws IOException {
        if (messages.length()>=1 & !messages.startsWith("System:")){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(messagesHistory, true))){
            writer.write(messages);
            }
        }
    }

    private void clearPreviousLines() throws IOException {
            linesOfHistoryFile = Files.readAllLines(messagesHistory.toPath(), Charset.defaultCharset());
            Files.write(messagesHistory.toPath(), linesOfHistoryFile.subList(linesOfHistoryFile.size()-100,linesOfHistoryFile.size()), Charset.defaultCharset());
    }

    public int checkHistorySize() throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(messagesHistory))){
            int count = 0;
            while (reader.ready()){
                reader.readLine();
                count++;
            }
            return count;
        }
    }

    public void loadHistory(TextArea textArea) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(messagesHistory))){
            if (checkHistorySize()>100)
                clearPreviousLines();
            while (reader.ready()){
                textArea.appendText(reader.readLine()+"\n");
            }
        }
    }


}
