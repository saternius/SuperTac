import java.io.*;

/**
 * Created by jason on 6/2/16.
 * Used to write to a file easily
 */
public class FileWriter {
    public static void out(String filename,String output) throws Exception{
        Writer writer = null;
        writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), "utf-8"));
        writer.write(output);
        writer.close();
    }

    public static void saveObject(Object object, String filename)throws Exception{
        File fileOne=new File(filename);
        FileOutputStream fos=new FileOutputStream(fileOne);
        ObjectOutputStream oos=new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        fos.close();
    }
}
