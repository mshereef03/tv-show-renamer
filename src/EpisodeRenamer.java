import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;


public class EpisodeRenamer {

    final static String apiKey = "16edf408";

    public static void renameEpisodes(String folderPath,String extension,String showName,String seasonNum) throws InterruptedException {

        //Requirements: Have all episodes in the folder and specify the 4 parameters below.

//         folderPath = "/Users/mshereef/Desktop/South.Park.S14.1080p.BluRay.x264-UNTOUCHABLES[rartv]"; // Update this to the path of your folder
//         extension = ".mkv"; // Type of file being changed
//         showName = "South Park";// Update this to the name of your TV show
//         seasonNum = "14"; // Season of show

        File folder = new File(folderPath);
        folder = new File(folderPath);
        File[] files = folder.listFiles();
        Arrays.sort(files);

        int counter = 1;

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(extension)) {
                String fileName = file.getName();

                try {
                    URL url = new URL("http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + showName + "&season=" + seasonNum + "&episode=" + counter);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    int responseCode = conn.getResponseCode();

                    if (responseCode == 200) {
                        Scanner scanner = new Scanner(url.openStream());
                        String response = scanner.useDelimiter("\\Z").next();
                        scanner.close();

                        String episodeName = response.substring(response.indexOf("Title\":\"") + 8, response.indexOf("\",\"Year\""));
                        String newFileName = "S"+seasonNum+"E"+(counter<10?"0"+counter:counter) + " - " + episodeName + extension;
                        File newFile = new File(folderPath +"/"+ newFileName);

                        if (file.renameTo(newFile)) {
                            System.out.println(fileName + " renamed to " + newFileName);
                        } else {
                            System.out.println("Failed to rename " + fileName);
                        }
                    } else {
                        System.out.println("Failed to get episode name for " + fileName);
                    }
                    counter++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
