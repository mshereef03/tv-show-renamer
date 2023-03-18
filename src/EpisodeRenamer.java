import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;

// basic and complex subs break if more than 26 episodes due to lexicrophaical sorting of files
// didnt test basic subs but gg


public class EpisodeRenamer {

    final static String apiKey = "16edf408";

    public static void renameEpisodes(String folderPath, String extension, String showName, String seasonNum, JTextArea outputArea, boolean isBasicSubs, boolean isComplexSubs) throws InterruptedException {


        File folder = new File(folderPath);
        if(isBasicSubs)mergeFoldersOneSubtitle(folder);
        if(isComplexSubs)mergeFoldersMultipleLangauges(folder);
        folder = new File(folderPath);
        File[] files = folder.listFiles();
        Arrays.sort(files);
        System.out.println(Arrays.toString(files));

        int episodeCounter = 1;

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(extension)) {
                String fileName = file.getName();

                try {
                    URL url = new URL("http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + showName + "&season=" + seasonNum + "&episode=" + episodeCounter);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    int responseCode = conn.getResponseCode();

                    if (responseCode == 200) {
                        Scanner scanner = new Scanner(url.openStream());
                        String response = scanner.useDelimiter("\\Z").next();
                        scanner.close();

                        String episodeName = response.substring(response.indexOf("Title\":\"") + 8, response.indexOf("\",\"Year\""));
                        String newFileName = showName+" - S"+seasonNum+"E"+(episodeCounter<10?"0"+episodeCounter:episodeCounter) + " - " + episodeName + extension;
                        File newFile = new File(folderPath +"/"+ newFileName);

                        if (file.renameTo(newFile)) {
                            System.out.println(fileName + " renamed to " + newFileName);
                        } else {
                            System.out.println("Failed to rename " + fileName);
                            break;
                        }
                    } else {
                        System.out.println("Failed to get episode name for " + fileName);
                        break;
                    }
                    outputArea.update(outputArea.getGraphics());
                    episodeCounter++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public static void mergeFoldersOneSubtitle(File folder) {
        if (!folder.isDirectory()) {
            return;
        }
        int c = 'a';
        File[] subfolders = folder.listFiles(File::isDirectory);
        Arrays.sort(subfolders);
        for (File subfolder : subfolders) {
            File[] files = subfolder.listFiles();
            for (File file : files) {
                String fileName = file.getName();
                String extension = fileName.substring(fileName.lastIndexOf('.'));
                File destFile = new File(folder, "English_"+(char)c+extension);

                try {
                    Files.move(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                c++;
            }
            // Delete the subfolder after moving its files
            subfolder.delete();
        }
    }

    public static void mergeFoldersMultipleLangauges(File folder) {
        if (!folder.isDirectory()) {
            return;
        }
        int c = 'a';
        File[] subfolders = folder.listFiles(File::isDirectory);
        Arrays.sort(subfolders);
        for (File subfolder : subfolders) {
            File[] files = subfolder.listFiles();
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.contains("English")) {
                    String extension = fileName.substring(fileName.lastIndexOf('.'));
                    File destFile = new File(folder, "English_"+(char)c+extension);
                    try {
                        Files.move(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    c++;
                    break;
                } else {
                    file.delete();
                }
            }

            deleteDirectory(subfolder);

        }
    }

    private static void deleteDirectory(File directory) {
        if (!directory.isDirectory()) {
            return;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        directory.delete();
    }

}

