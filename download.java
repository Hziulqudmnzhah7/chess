import java.net.*;
import java.io.*;
public class download{
   public static void main(String[] args){
      try {
         URL url = new URL("https://raw.githubusercontent.com/Hziulqudmnzhah7/chess/changes/chess.java");
	URLConnection conn = url.openConnection();
	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         String response = new String();
         for (String line; (line = br.readLine()) != null; response += line);
            System.out.println(response);
      } catch(MalformedURLException e){
         System.out.println("something happened");
      }catch (IOException e){
         System.out.println("something happened");
      }
   }
}
