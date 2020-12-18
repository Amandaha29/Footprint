import java.util.*;

public class Output{
 
    private static List<Country> countries;
    private static Queue<Country> pQueue; 
    
    //Constructs a list of all countries sorted from least to greatest emissions 
    public Output(List<Country> countries) { 
        this.countries = countries;
        this.pQueue = new PriorityQueue<>(200, co2Comparator);
        for (Country eachCountry : this.countries){
            this.pQueue.add(eachCountry);
        }    
    }
    
    // Returns longest combination of counries in which their total CO2 output equates to a given country's CO2 output
    public Set<Country> maxLenCombo(Country inputCountry) { 
        return maxLenComboHelper(inputCountry, inputCountry.getEmissions(), 0, new HashSet<Country>());
    }

    private Set<Country> maxLenComboHelper(Country inputCountry, double inputEmissions, int index, Set<Country> maxLenSet) {
        Country currentCountry;
        if (inputEmissions == pQueue.peek().emissions){    
            return null;
        }
        if (inputEmissions <= 0){
            return maxLenSet; 
        } else {
            currentCountry = pQueue.poll(); 
            double currEmissions = 0;
            if (!currentCountry.equals(inputCountry)){ 
                maxLenSet.add(currentCountry);
                currEmissions = currentCountry.emissions;
            }
            return maxLenComboHelper(inputCountry, inputEmissions - currEmissions, index + 1, maxLenSet);
        }  
    }   

    // Compares country by emissions
    public static Comparator<Country> co2Comparator = new Comparator<Country>(){
		@Override
	    public int compare(Country c1, Country c2) {
            return (int) (c1.getEmissions() - c2.getEmissions());
        }
    };
}

