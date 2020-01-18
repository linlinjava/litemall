package org.linlinjava.litemall.db.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbUtil {

    public static void backup(File file, String user, String password, String db) {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = "mysqldump -u" + user + " -p" + password + " --set-charset=utf8 " + db;
            Process child = rt.exec(command);
            InputStream inputStream = child.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter( new FileOutputStream(file), StandardCharsets.UTF_8);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                outputStreamWriter.write(str + "\r\n");
            }
            outputStreamWriter.flush();
            inputStream.close();
            bufferedReader.close();
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(File file, String user, String password, String db) {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = "mysql -u" + user + " -p" + password + " --default-character-set=utf8 " + db;
            Process child = rt.exec(command);
            OutputStream outputStream = child.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                outputStreamWriter.write(str + "\r\n");
            }
            outputStreamWriter.flush();
            outputStream.close();
            bufferedReader.close();
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}