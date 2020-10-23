package scheduleArchive.Scenes;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduleArchive.Utility.Activity;
import scheduleArchive.Main;
import scheduleArchive.Utility.stopWatch;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;


public class ScheduleCreator {

    scheduleArchive.Utility.stopWatch stopWatch;


    @FXML
    BorderPane UniversalCommonAncestor;
    
    @FXML
    private Label stopWatchDisplay;
    @FXML



    private Label universalClockDisplay;
    @FXML
    Button saveButton;
    static boolean isAM;



    @FXML
    private Label startTime;
    @FXML
    private Label startTimeAMPM;
    @FXML
    private Label endTime;
    @FXML
    private Label endTimeAMPM;


    @FXML
    Label noonRelationMarker;
    @FXML
    Label date;

//    @FXML
//    TextField submitField;

    @FXML
    ToggleGroup ActivityTypes;

    @FXML
    RadioButton Study;
    @FXML
    RadioButton Entertainment;
    @FXML
    RadioButton Spirituality;
    @FXML
    RadioButton Exercise;
    @FXML
    RadioButton Rest;
    @FXML
    RadioButton Reading;
    @FXML
    RadioButton Writing;
    @FXML
    RadioButton Arts;
    @FXML
    RadioButton Social;
    @FXML
    RadioButton Media;
    @FXML
    RadioButton Service;
    @FXML
    RadioButton Miscellaneous;

    public void initialize(){


        stopWatch = new stopWatch(0);

//        archive.add(new Activity("환상의 투자기업","Study",1200,1200,2400,"2020-8-30"));
//        archive.add(new Activity("한국렌탈 사우디 법인","Arts",1200, 2401,3601,"2020-9-1"));
//        archive.add(new Activity("Respiro","Rest",500,3602,4102,"2020-8-30"));
//        archive.add(new Activity("legio","Reading",50,4102,4152,"2020-8-30"));



        new Thread(runClock).start();

        if (Loader.loadMode){
            Study.setDisable(true);
            Entertainment.setDisable(true);
            Spirituality.setDisable(true);
            Exercise.setDisable(true);
            Rest.setDisable(true);
            Reading.setDisable(true);
            Writing.setDisable(true);
            Arts.setDisable(true);
            Miscellaneous.setDisable(true);

            saveButton.setDisable(true);

        }
        else {
            startWatch();
        }


    }




    Runnable executeTicks = new Runnable() {
        @Override
        public void run() {

            while(true){

                waitUntilSynchronizedWithUniverse();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {


                        int currentTime = getTimeInSeconds(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond());


                        if (currentTime - stopWatch.getStarTimeSec() != stopWatch.getMeasuredTime()){

                            stopWatch.recalibrate(currentTime);

                        }
                        else {
                            stopWatch.tick();
                        }


                        String display = stopWatch.makeDisplay();
                        stopWatchDisplay.setText(display);
                        endTime.setText(getCurrentAMPMTime());;
                        endTimeAMPM.setText(isAM ? "A.M." : "P.M.");
                    }
                });
            }
        }
    };


    Thread stopWatchProcess = new Thread(executeTicks);


    Runnable runClock = new Runnable() {
        @Override
        public void run() { 

            while(true){

                waitUntilSynchronizedWithUniverse();
                isAM = LocalTime.now().getHour() < 12 && LocalTime.now().getHour() != 0;
                String currentAMPMtime = getCurrentAMPMTime();
                String currentDate = LocalDate.now().toString();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        //System.out.println(LocalTime.now().getSecond() +"."+ LocalTime.now().getNano());
                        universalClockDisplay.setText(currentAMPMtime);
                        noonRelationMarker.setText(isAM ? "A.M." : "P.M.");
                        date.setText(currentDate);

                    }
                });
            }
        }
    };

    static String convertToAMPM(int hour, int minute, int second){
        if (!isAM ){
            hour = hour - 12;
        }

        return  (hour < 10 ? "0"+hour : hour) + ":" + (minute < 10 ? "0"+minute : minute) + ":" + (second < 10 ? "0"+second : second);

    }

    static String getCurrentAMPMTime(){
        int hour = LocalTime.now().getHour();
        int minutes = LocalTime.now().getMinute();
        int seconds = LocalTime.now().getSecond();
        int nanos = LocalTime.now().getNano();


        isAM = LocalTime.now().getHour() < 12 && LocalTime.now().getHour() != 0;

        if (isAM){
            if(nanos > 900000000){
                return parseToDisplay(hour,minutes,seconds + 1);
            }
            else {
                return parseToDisplay(hour, minutes, seconds);
            }
        }
        else{
            if (hour == 0){
                return parseToDisplay(12, minutes, seconds + 1);
            }
            if (hour == 12){
                isAM = true;
                return parseToDisplay(12, minutes, seconds + 1);
            }
            if(nanos > 900000000){
                return parseToDisplay(hour-12, minutes, seconds + 1);
            }
            else {
                return parseToDisplay(hour-12, minutes, seconds);
            }

        }
    }


    static String parseToDisplay(int hour, int minute, int second){
        return (hour < 10 ? "0"+hour : hour) + ":" + (minute < 10 ? "0"+minute : minute) + ":" + (second < 10 ? "0"+second : second);
    }


    static void waitUntilSynchronizedWithUniverse(){
        int currentNanos = LocalTime.now().getNano();
        int untilNextTickNanos = 1000000000 - currentNanos;
        long untilNextTickMilis = (long) (untilNextTickNanos / 1000000.0);
        if (untilNextTickMilis == 0 || untilNextTickMilis > 1000){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                Thread.sleep(untilNextTickMilis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(untilNextTickMilis);

    }


    public void startWatch() {
        stopWatch.setStarTimeSec(getTimeInSeconds(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond() ));
        startTime.setText(getCurrentAMPMTime());
        startTimeAMPM.setText(isAM ? "A.M." : "P.M.");
        stopWatchProcess.start();
    }

    public void resetStopWatch(){
        stopWatch.setStarTimeSec(getTimeInSeconds(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond() ));
        stopWatchDisplay.setText("00:00:00");
        startTime.setText(getCurrentAMPMTime());
        startTimeAMPM.setText(isAM ? "A.M." : "P.M.");
        stopWatch.setMeasuredSecs(0);
    }




    static int getTimeInSeconds(int hours, int minutes, int seconds){
        return hours * 3600 + minutes * 60 + seconds;
    }




    public void proclaimActivityType(ActionEvent e){
        RadioButton button = (RadioButton) e.getSource();
        String id = button.getId();
        switch (id){
            case "Study":
                UniversalCommonAncestor.setStyle("-fx-background-color:aqua ;");
                break;

            case "Entertainment":
                UniversalCommonAncestor.setStyle("-fx-background-color:fuchsia ;");
                break;

            case "Spirituality":
                UniversalCommonAncestor.setStyle("-fx-background-color:gold ;");
                break;

            case "Exercise":
                UniversalCommonAncestor.setStyle("-fx-background-color:chartreuse ;");
                break;

            case "Rest":
                UniversalCommonAncestor.setStyle("-fx-background-color:bisque ;");
                break;

            case "Reading":
                UniversalCommonAncestor.setStyle("-fx-background-color:chocolate ;");
                break;

            case "Writing":
                UniversalCommonAncestor.setStyle("-fx-background-color:deepskyblue ;");
                break;

            case "Arts":
                UniversalCommonAncestor.setStyle("-fx-background-color:crimson ;");
                break;

            case "Social":
                UniversalCommonAncestor.setStyle("-fx-background-color:olive ;");
                break;

            case "Media":
                UniversalCommonAncestor.setStyle("-fx-background-color:green ;");
                break;

            case "Service":
                UniversalCommonAncestor.setStyle("-fx-background-color:coral ;");
                break;

            case "Miscellaneous":
                UniversalCommonAncestor.setStyle("-fx-background-color:blueviolet ;");
                break;
        }

    }

    @FXML

    public void goToStats(){
        Stage stage = (Stage) Main.getScheduleCreator().getWindow();
        stage.setScene(Main.getStats());
    }

    public void goToSubmitField() throws IOException {
        Stage stage = (Stage) Main.getScheduleCreator().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("submitScreen.fxml")),Main.getScreenWidth(),Main.getScreenHeight()));
    }






    public void processSubmission() throws NullPointerException, IOException {
        String category;
        try {
            category = getSelectedActivityCategory(ActivityTypes);
        }catch (NullPointerException e){
            category = "null";
        }

        if (!category.equals("null") && stopWatch.getMeasuredTime() != 0){
            Main.archive.add(new Activity("undefined",getSelectedActivityCategory(ActivityTypes),stopWatch.getMeasuredTime(), stopWatch.getStarTimeSec(),getTimeInSeconds(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond()), LocalDate.now().toString()));
            resetStopWatch();
            ActivityTypes.getSelectedToggle().setSelected(false);
            UniversalCommonAncestor.setStyle("-fx-background-color: white");
            goToSubmitField();
        }


    }


    public String getSelectedActivityCategory(ToggleGroup group) {
        RadioButton selected = (RadioButton) group.getSelectedToggle();

        if (selected.equals(null)) {
            return null;
        } else if (selected.equals(Study)) {
            return "Study";
        } else if (selected.equals(Entertainment)) {
            return "Entertainment";
        } else if (selected.equals(Spirituality)) {
            return "Spirituality";
        } else if (selected.equals(Exercise)) {
            return "Exercise";
        } else if (selected.equals(Rest)) {
            return "Rest";
        } else if (selected.equals(Reading)) {
            return "Reading";
        } else if (selected.equals(Writing)) {
            return "Writing";
        } else if (selected.equals(Arts)) {
            return "Arts";
        } else if (selected.equals(Social)) {
            return "Social";
        } else if (selected.equals(Media)) {
            return "Media";
        } else if (selected.equals(Service)) {
            return "Service";
        } else if (selected.equals(Miscellaneous)) {
            return "Miscellaneous";
        }
        return "error: selected category does not exist";

    }

}
