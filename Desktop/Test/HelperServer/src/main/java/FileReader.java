import java.io.*;

/**
 * Created by jason on 5/22/16.
 * Used to read a file easily
 */
public class FileReader {
    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new java.io.FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            System.err.println("File not Found.");
        }
        return result;
    }


    public static Object readObject(String filename) throws IOException, ClassNotFoundException {
        File toRead=new File(filename);
        FileInputStream fis=new FileInputStream(toRead);
        ObjectInputStream ois=new ObjectInputStream(fis);
        Object object = ois.readObject();
        ois.close();
        fis.close();
        return object;
    }
}
