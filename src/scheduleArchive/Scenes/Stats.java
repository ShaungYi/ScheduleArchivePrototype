package scheduleArchive.Scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scheduleArchive.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Stats {


    public static final ArrayList<String> categorySequence = new ArrayList<>();

    public static final ArrayList<String> colorSequence = new ArrayList<>();

    public static final HashMap<String, String> colorMap = new HashMap<>();

    public static Scene timeLine;

    public static boolean currentTimelineLayoutInitialized = false;


    public static Comparator<Table.chartDataUnit> chartDataUnitComparator = new Comparator<Table.chartDataUnit>() {
        @Override
        public int compare(Table.chartDataUnit u1, Table.chartDataUnit u2) {
            return ((Integer)u2.getDurationSecs()).compareTo((Integer) u1.getDurationSecs());
        }
    };


    public void initialize(){

        makeCategorySequence();
        makeColorSequence();
        makeColorMap();
    }

    private void makeCategorySequence(){
        categorySequence.add("Study");
        categorySequence.add("Entertainment");
        categorySequence.add("Spirituality");
        categorySequence.add("Exercise");
        categorySequence.add("Rest");
        categorySequence.add("Reading");
        categorySequence.add("Writing");
        categorySequence.add("Arts");
        categorySequence.add("Social");
        categorySequence.add("Media");
        categorySequence.add("Service");
        categorySequence.add("Miscellaneous");
    }

    private void makeColorSequence(){
        colorSequence.add("aqua");
        colorSequence.add("fuchsia");
        colorSequence.add("gold");
        colorSequence.add("chartreuse");
        colorSequence.add("bisque");
        colorSequence.add("chocolate");
        colorSequence.add("deepskyblue");
        colorSequence.add("crimson");
        colorSequence.add("olive");
        colorSequence.add("green");
        colorSequence.add("coral");
        colorSequence.add("blueviolet");
    }

    public void makeColorMap(){
        colorMap.put("Study", "aqua");
        colorMap.put("Entertainment","fuchsia");
        colorMap.put("Spirituality","gold");
        colorMap.put("Exercise","chartreuse");
        colorMap.put("Rest","bisque");
        colorMap.put("Reading","chocolate");
        colorMap.put("Writing","deepskyblue");
        colorMap.put("Arts","crimson");
        colorMap.put("Social","olive");
        colorMap.put("Media","green");
        colorMap.put("Service","coral");
        colorMap.put("Miscellaneous","blueviolet");
        colorMap.put("NoData","grey");
    }


    public void goToCreator(){
        Stage stage = (Stage) Main.getStats().getWindow();
        stage.setScene(Main.getScheduleCreator());
    }

    public void goToBarChart(){
        BarDisplay.updateBarChart();
        Stage stage = (Stage) Main.getStats().getWindow();
        stage.setScene(Main.getBars());
    }

    public void goToTable(){
        Stage stage = (Stage) Main.getStats().getWindow();
        stage.setScene(Main.getTable());
    }

    public void goToPiChart(){
        if (!Main.archive.isEmpty()){
            PiChart.updatePi();
        }

        Stage stage = (Stage) Main.getStats().getWindow();
        stage.setScene(Main.getPiChart());
    }

    public void goToLoader() throws IOException {
        Stage stage = (Stage) Main.getStats().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("loader.fxml"))));


        if (Loader.resumeMode || Loader.loadMode){

            Main.saveArchive(Main.toTableName(Main.selectedDay));
        }
    }

    public void goToTimeLine() throws IOException {
        timeLine = new Scene(FXMLLoader.load(getClass().getResource("timeLine.fxml")),1300,700);
        Stage stage = (Stage) Main.getStats().getWindow();
        stage.setScene(timeLine);
    }




    static String parseDuration(int durationSecs){
        int template = durationSecs;

        int hh = ((Integer)(template / 3600));

        template = template % 3600;

        int mm = ((Integer)(template / 60));

        template = template % 60;

        int ss = ((Integer) template);



        String hours = hh + (hh == 1 ? " hour " : " hours ");
        String minuts = mm + (mm == 1 ? " minute " : " minutes ");
        String seconds = ss + (ss == 1 ? " second" : " seconds");

        if (hh == 0 && mm == 0){
            return seconds;
        }
        else if (hh == 0){
            return minuts + seconds;
        }
        else if (mm == 0){
            return hours + seconds;
        }
        else {
            return  hours + minuts + seconds;
        }
    }

    static double calculatePercentage(int total, int value){
        return (double) value / (double)total * 100;
    }


}
