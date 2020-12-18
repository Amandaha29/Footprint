import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import com.sun.net.httpserver.*;

public class Server {
    // Port number used to connect to this server
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));
    // JSON endpoint structure
    private static final String QUERY_TEMPLATE = "  {\"items\":[%s], \"input\":[%s] , \"pOfTot\":\"%s\", \"length\":\"%s\"}";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("index.html"));
            send(t, "text/html; charset=utf-8", html);
        });
        server.createContext("/rectangle", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("rectangleView.html"));
            send(t, "text/html; charset=utf-8", html);
        });
        server.createContext("/circle", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("circleView.html"));
            send(t, "text/html; charset=utf-8", html);
        });
        


        server.createContext("/query", (HttpExchange t) -> { 
            File file = new File("co2.csv");  
            if (!file.isFile()) {
                send(t, "application/json", String.format(QUERY_TEMPLATE, "", ""));
                return;
            }
            
            Map<String, Country> countries = new HashMap<String, Country>();
            double totalCarbon = 0;
            try (Scanner input = new Scanner(file)) {    
                input.nextLine();
                Country world = Country.fromCsv(input.nextLine());
                totalCarbon = world.getEmissions();
                while (input.hasNextLine()) {
                    Country country = Country.fromCsv(input.nextLine());
                    countries.put(country.getName(), country);   
                }
            }

            String c = parse("country", t.getRequestURI().getQuery().split("&")); //country inputed by user
            if (c.equals("")) {
                send(t, "application/json", String.format(QUERY_TEMPLATE, ""));
                return;
            } 
            Country inputCountry = countries.get(c);
            ArrayList<Country> inpCountry = new ArrayList<Country>();
            inpCountry.add(inputCountry);
            ArrayList<Country> allCountriesList = new ArrayList<>(countries.values());
            Set<Country> result = new Output(allCountriesList).maxLenCombo(inputCountry);
            double percent = (inputCountry.getEmissions() / totalCarbon) * 100; 
            String pOfTot = String.format("%.1f%% of total world CO2 emissions", percent);
            String length = Integer.toString(result.size());

            send(t, "application/json", String.format(QUERY_TEMPLATE, json(result), json(inpCountry), pOfTot, length));
    
        });
        server.setExecutor(null);
        server.start();
    }

    private static String parse(String key, String... params) {
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return "";
    }

    private static void send(HttpExchange t, String contentType, String data)
            throws IOException, UnsupportedEncodingException {
        t.getResponseHeaders().set("Content-Type", contentType);
        byte[] response = data.getBytes("UTF-8");
        t.sendResponseHeaders(200, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }

    private static String json(Iterable<Country> matches) { 
        StringBuilder results = new StringBuilder();
        for (Country c : matches) {
            if (results.length() > 0) {
                results.append(',');
            }
            results.append('{')
                   .append("\"name\":")
                   .append('"').append(c.getName()).append('"')
                   .append(',')
                   .append("\"emissions\":")
                   .append('"').append(c.getEmissions()).append('"')
                   .append('}');
        }
        return results.toString();
    }
}
