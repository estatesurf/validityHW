import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class App {

    public static String FROM = "From:";
    public static String SUBJECT = "Subject:";
    public static String DATE = "Date:";
    public static String OUTPUTFILENAME = "results.txt";
    public static String INPUTFILENAME = "sampleEmails.tar.gz";

    public static void main(String[] args) {
        App app = new App("");
        File tarball = new File(INPUTFILENAME);
        try {
            String newTarFile = app.decompressGzip(tarball, app.getRootPath());
            File tarFile = new File(newTarFile);
            app.unTarFile(tarFile, app.getRootPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupOutputFile(OUTPUTFILENAME);

        app.iterateOverFiles();

        closeOutputFile();
    }

    private String rootPath;
    private static FileWriter writer;

    App() {

    }

    App(String rootPath) {
        setRootPath(rootPath);
    }


    public void iterateOverFiles() {
        String targetFile = "smallset\\sampleEmails\\smallset";
        File f = new File(targetFile);
        BufferedReader br = null;

        File[] files = f.listFiles();

        for(File file : files) {

            if(file.isFile()) {
                try {
                    br = new BufferedReader(new FileReader(file));
                    writeLineToOutputFile(file.getName() + " | " + processFile(br));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                finally {
                    if(br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public String processFile(BufferedReader input) {

        String from = "", subject = "", date = "";

        Pattern fromPattern = Pattern.compile("From: .*<(.*)>");
        Pattern subjectPattern = Pattern.compile("Subject: (.*)");
        Pattern datePattern = Pattern.compile("Date: (.*)");

        try {
            String line;

            while((line = input.readLine()) != null) {

                Matcher m = fromPattern.matcher(line);
                if(m.find()) {
                    from = m.group(1);
                }
                m = subjectPattern.matcher(line);
                if(m.find()) {
                    subject = m.group(1);
                }
                m = datePattern.matcher(line);
                if(m.find()) {
                    date = m.group(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format("%s | %s | %s ", from, subject, date);
    }


    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    private String decompressGzip(File input, String targetDir) throws IOException {
        String[] filenameParts = input.getName().split("\\.gz");
        String filenamePrefix = filenameParts[0];
        String slash = System.getProperty("file.separator");
        String targetFile2 = targetDir + slash + filenamePrefix;
        String targetFile = filenamePrefix;
        File output = new File(targetFile);
        try (GZIPInputStream in = new GZIPInputStream(new FileInputStream(input))) {
            try (FileOutputStream out = new FileOutputStream(output)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
        }
        return targetFile;
    }

    private void unTarFile(File tarFile, String targetDir) throws IOException {
        String[] filenameParts = tarFile.getName().split("\\.tar");
        String filenamePrefix = filenameParts[0];
        String slash = System.getProperty("file.separator");
        File output = new File(targetDir + slash + filenamePrefix);
        FileInputStream fis = new FileInputStream(tarFile);
        TarArchiveInputStream tis = new TarArchiveInputStream(fis);
        TarArchiveEntry tarEntry = null;

        while ((tarEntry = tis.getNextTarEntry()) != null) {
            File outputFile = new File(output + File.separator + tarEntry.getName());

            if (tarEntry.isDirectory()) {

                System.out.println("email Directory ---- "
                        + outputFile.getAbsolutePath());
                if (!outputFile.exists()) {
                    outputFile.mkdirs();
                }
            } else {
                System.out.println("email File created ---- " + outputFile.getAbsolutePath());
                outputFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(outputFile);
                IOUtils.copy(tis, fos);
                fos.close();
            }
        }
        tis.close();
    }

   private static void setupOutputFile(String filename) {
       try {
           writer = new FileWriter(filename, false);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   private static void writeLineToOutputFile(String line) {
       System.out.println("Writing to " + OUTPUTFILENAME + " ==> " + line + "\n"); 
       try {
           writer.write(line);
           writer.write("\r\n");   // write new line
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   private static void closeOutputFile() {
       try {
           writer.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

}
