package scheduleArchive.Scenes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import scheduleArchive.Utility.Activity;
import scheduleArchive.Main;

import java.util.ArrayList;

public class SubmitScreen {


    @FXML Label duration;


    @FXML
    Label startTime;
    @FXML
    Label endTime;


    @FXML
    TextField nameField;
    @FXML
    Button submitButton;




    @FXML
    ListView suggestions;
    ObservableList<String> suggestionsData = FXCollections.observableArrayList();
    @FXML
    GridPane suggestionsContainer;


    Activity activityInQuestion;


    public void initialize(){
        updateActivityInQuestion();
        setUpDisplays();
        setUpSuggestionsBar();
    }

    private void setUpSuggestionsBar(){
        suggestions.setItems(suggestionsData);
    }


    private void updateActivityInQuestion(){
        ;
        activityInQuestion = Main.archive.get(Main.archive.size() - 1);
    }

    public void setUpDisplays(){

        duration.setText(Stats.parseDuration(activityInQuestion.getDurationSeconds()));

        startTime.setText(parseSeconds(activityInQuestion.getStartTimeSecs()));
        endTime.setText(parseSeconds(activityInQuestion.getEndTimeSecs()));
    }


    public void submit(){

        Main.currentDay = Main.archive.get(0).getDate();

        activityInQuestion.setName(nameField.getText());



        mergeLikeActivities();



        Table.addNewActivityToData(activityInQuestion, Table.categoryData, "category");
        Table.categoryData.sort(Stats.chartDataUnitComparator);
        Table.moveTotalToBottom();


        Table.total.setDurationSecs(Table.total.getDurationSecs() + activityInQuestion.getDurationSeconds());


        ObservableList activityData;
        switch (activityInQuestion.getCategory()){
            case "Study":
                activityData = Table.studyData;
                break;
            case "Entertainment":
                activityData = Table.entertainmentData;
                break;
            case "Spirituality":
                activityData = Table.spiritualityData;
                break;
            case "Exercise":
                activityData = Table.exerciseData;
                break;
            case "Rest":
                activityData = Table.restData;
                break;
            case "Reading":
                activityData = Table.readingData;
                break;
            case "Writing":
                activityData = Table.writingData;
                break;
            case "Arts":
                activityData = Table.artsData;
                break;
            case "Social":
                activityData = Table.socialData;
                break;
            case "Media":
                activityData = Table.mediaData;
                break;
            case "Service":
                activityData = Table.serviceData;
                break;
            case "Miscellaneous":
                activityData = Table.miscellaneousData;
                break;
            default:
                activityData = null;
                break;
        }

        Table.addNewActivityToData(activityInQuestion,activityData, "name");
        activityData.sort(Stats.chartDataUnitComparator);


        LoadScreen.loadActivity(activityInQuestion.getName(), activityInQuestion.getCategory(), activityInQuestion.getDurationSeconds(), activityInQuestion.getDate());


        Main.backupData();



        Main.editLog.wipe();



        goToCreator();
    }


    public void mergeLikeActivities(){

        if (Main.archive.size() > 1){
            Activity prevActivity = Main.archive.get(Main.archive.indexOf(activityInQuestion) - 1);


            if (activityInQuestion.getName().equals(prevActivity.getName()) && activityInQuestion.getCategory().equals(prevActivity.getCategory())){

                activityInQuestion.setDurationSeconds(prevActivity.getDurationSeconds() + activityInQuestion.getDurationSeconds());
                activityInQuestion.setStartTimeSecs(prevActivity.getStartTimeSecs());

                Main.archive.remove(prevActivity);
            }
        }

        Main.backupData();

    }


    public void onEntryToNameField(){

        suggestionsData.clear();

        if (!nameField.getText().equals("")){

            submitButton.setDisable(false);


            searchActivityListForNameAndLoadToData(nameField.getText(), suggestionsData);



            if (suggestionsData.isEmpty()){
                hideSuggestions();
                suggestions.setDisable(true);
            }
            else{
                showSuggestions();
                suggestions.setDisable(false);

            }



        }
        else {
            hideSuggestions();
            submitButton.setDisable(true);
            suggestions.setDisable(true);
        }


    }


    public void completeNameField(){

        String suggestion = (String)suggestions.getSelectionModel().getSelectedItem();

        nameField.setText((String)suggestions.getSelectionModel().getSelectedItem());
        hideSuggestions();
    }

    public void showSuggestions(){
        suggestionsContainer.setOpacity(1);
    }

    public void hideSuggestions(){
        suggestionsContainer.setOpacity(0);
    }


    public ObservableList<String> searchActivityListForNameAndLoadToData(String targetFragment, ObservableList<String> data){

        ArrayList<String> result = LoadScreen.browsPastActivities(targetFragment);

        System.out.println(result);
        for (int i = result.size() - 1; i >= 0; i--){
            data.add(result.get(i));
        }


        return data;
    }


    public void goToCreator(){
        Stage stage = (Stage) suggestionsContainer.getScene().getWindow();
        stage.setScene(Main.getScheduleCreator());
    }
    private String parseSeconds(int seconds){
        int template;

        if (seconds > TimeLine.SECONDS_IN_A_DAY){
            template = seconds - TimeLine.SECONDS_IN_A_DAY;
        }
        else {
            template = seconds;
        }



        int hh = ((Integer)(template / 3600));

        template = template % 3600;

        int mm = ((Integer)(template / 60));

        int ss = template % 60;




        String minutes;
        if (mm == 0){
            minutes = "00";
        }
        else if (mm < 10){
            minutes = "0" + mm;
        }
        else {
            minutes = ((Integer) mm).toString();
        }


        if (hh == 0){
            return "12" + ":" + minutes + ":" + (ss < 10 || ss == 0 ? "0" + ss : ss) + " A.M.";
        }
        else if (hh == 12){
            return hh + ":" + minutes + ":" + (ss < 10 || ss == 0 ? "0" + ss : ss) +" P.M.";
        }
        else if (hh < 12){
            return hh + ":" + minutes + ":" + (ss < 10 || ss == 0 ? "0" + ss : ss) + " A.M.";
        }
        return hh - 12 + ":" + minutes + ":" + (ss < 10 || ss == 0 ? "0" + ss : ss) + " P.M.";
    }

}
