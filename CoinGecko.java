
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.json.JsonReader;
import javax.json.JsonObject;
import javax.json.Json;

public class CoinGecko {
    public static void main(String[] args){
        String response = "";
        try {

            URL url = new URL("https://api.coingecko.com/api/v3/coins/list");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            response = FullResponseBuilder.getFullResponse(con);

        } catch(Exception e){
            e.printStackTrace();
        }

        //System.out.println(response.toString());
        
        JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
        JsonObject reply = jsonReader.readObject();
        System.out.println(reply.toString());

    }

    static class FullResponseBuilder {
        public static String getFullResponse(HttpURLConnection con) throws IOException {
            StringBuilder fullResponseBuilder = new StringBuilder();

            fullResponseBuilder.append(con.getResponseCode())
                .append(" ")
                .append(con.getResponseMessage())
                .append("\n");

            con.getHeaderFields()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey() != null)
                .forEach(entry -> {

                    fullResponseBuilder.append(entry.getKey())
                        .append(": ");

                    List<String> headerValues = entry.getValue();
                    Iterator<String> it = headerValues.iterator();
                    if (it.hasNext()) {
                        fullResponseBuilder.append(it.next());

                        while (it.hasNext()) {
                            fullResponseBuilder.append(", ")
                                .append(it.next());
                        }
                    }

                    fullResponseBuilder.append("\n");
                });

            Reader streamReader = null;

            if (con.getResponseCode() > 299) {
                streamReader = new InputStreamReader(con.getErrorStream());
            } else {
                streamReader = new InputStreamReader(con.getInputStream());
            }

            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            fullResponseBuilder.append("Response: ")
                .append(content);

            return fullResponseBuilder.toString();
        }
    }
}