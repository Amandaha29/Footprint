import java.util.*;

public class Country {

    public final String name;
    public final double emissions;

    public Country(String name, double emissions){
        this.name = name;
        this.emissions = emissions;
    }

    public double getEmissions (){
        return emissions;
    }

    public String getName (){
        return name;
    }

    public static Country fromCsv (String input){
        return fromCsv(input.split(","));
    }

    public static Country fromCsv(String... args) {
        return new Country(args[0], Double.parseDouble(args[1]));
    }

    public boolean equals(Country other) {
        if (this.name == other.name) {
            return true;
        } 
        return false;
    }
}

