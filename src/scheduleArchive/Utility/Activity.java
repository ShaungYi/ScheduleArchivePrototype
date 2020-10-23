package scheduleArchive.Utility;

public class Activity implements Cloneable{
    private String name;
    private String category;
    private int durationSeconds;
    int startTimeSecs;
    int endTimeSecs;
    String date;

    public Activity (String name, String category, int duration, int startTime, int endTime, String date){
        this.name = name;
        this.category = category;
        this.durationSeconds = duration;
        this.startTimeSecs = startTime;
        this.endTimeSecs = endTime;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getStartTimeSecs() {
        return startTimeSecs;
    }



    public int getEndTimeSecs() {
        return endTimeSecs;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void setStartTimeSecs(int startTimeSecs) {
        this.startTimeSecs = startTimeSecs;
    }

    public void setEndTimeSecs(int endTimeSecs) {
        this.endTimeSecs = endTimeSecs;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String toString(){
        return "Activity: "+"name = "+name+", category = "+category+", duration = "+durationSeconds+" seconds, "+"startTimeSecs: "+startTimeSecs+", endTimeSecs: "+getEndTimeSecs();
    }

}
