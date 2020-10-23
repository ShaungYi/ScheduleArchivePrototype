package scheduleArchive.Utility;

public class PastActivity {

    String name;
    String category;

    String mostRecentDate;
    int frequency;
    int netDuration;

    public PastActivity(String name, String category, String mostRecentDate, int frequency, int netDuration) {
        this.name = name;
        this.category = category;
        this.mostRecentDate = mostRecentDate;
        this.frequency = frequency;
        this.netDuration = netDuration;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getMostRecentDate() {
        return mostRecentDate;
    }

    public void setMostRecentDate(String mostRecentDate) {
        this.mostRecentDate = mostRecentDate;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getNetDuration() {
        return netDuration;
    }

    public void setNetDuration(int netDuration) {
        this.netDuration = netDuration;
    }

    public String toString(){
        return "Activity: "+"name = "+name+", category = "+category+", netDuration = "+netDuration+" seconds, "+"frequency: "+frequency+", mostRecentDate: "+mostRecentDate;
    }
}
