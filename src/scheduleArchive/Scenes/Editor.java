package scheduleArchive.Scenes;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import scheduleArchive.Utility.Activity;
import scheduleArchive.Utility.Cycler;
import scheduleArchive.Main;
import scheduleArchive.Utility.PastActivity;

import java.io.IOException;
import java.util.*;

public class Editor {

    private int startTimeSecs;
    private int endTimeSecs;


//    private int colorLineMargins = 200;
//    private int timeLineMargins = 140;


    private int colorLineHeight = 125;
    private int timeLineHeight = 17;


    private int nodeStartPointY = 6;
    private int nodeEndPointY = -6;


    public static final int WIDTH_OF_5_MINS = 120;
    public static final double WIDTH_OF_1_SEC = 0.4;
    public static final int FIVE_MINS_IN_SECONDS = 300;
    private double dateChangePoint = -1;
    public static final int SECONDS_IN_A_DAY = 86400;


    private String firstDate = "";
    private String lastDate = "";


    @FXML
    Pane grandmotherPane;

    @FXML
    VBox motherPane;


    @FXML
    Pane portalContainerPane;
    @FXML
    HBox portalPane;
    @FXML
    Text testText;

    @FXML
    GridPane allContainerPane;

    @FXML
    Pane yearPane;
    @FXML
    Label dateDisplay;


    @FXML
    HBox colorPane;

    @FXML
    HBox nodePane;

    @FXML
    HBox timePane;


    @FXML
    Pane detailContainerPane;
    @FXML
    BorderPane detailPane;
    @FXML
    TextField categoryTitlePicker;
    @FXML
    TextField specificField;
    @FXML
    Label durationLabel;
    @FXML
    Label beganAt;
    @FXML
    Label finishedAt;



    ObservableList suggestedNames = FXCollections.observableArrayList();
    @FXML
    ListView nameSuggestions;




    double scrollableWidth;

    HashMap colorMap = Stats.colorMap;


    int selectedIndex;


    public void initialize() {


        nameSuggestions.setPrefWidth(200);
        nameSuggestions.setItems(suggestedNames);
        grandmotherPane.getChildren().remove(nameSuggestions);


        detailPane.setOpacity(0);
        detailPane.setDisable(true);

        categoryTitlePicker.setPromptText("Category");


        startTimeSecs = 0;
        endTimeSecs = 0;


        dateDisplay.setText(firstDate);



        addActivities(Main.archive);
        computeAndSetScrollableWidth();
        resizePanes(scrollableWidth);
        makeTimeLine(startTimeSecs, endTimeSecs);
        makeNodes(getNumNodes());
        configureDateChangePoint(startTimeSecs - startTimeSecs % FIVE_MINS_IN_SECONDS);
        setPosition(TimeLine.currentLayout);

        suggestedNames.add("mama");
        suggestedNames.add("dada");

    }




    @FXML
    public void undo(){

        if (Main.editLog.canGoBack()){
            ArrayList<Activity> prevArchive = Main.editLog.mostRecentLog();

            Main.archive = prevArchive;

            colorPane.getChildren().clear();

            addActivities(Main.archive);

            detailPane.setOpacity(0);
            detailPane.setDisable(true);
        }
        else {
            flashCantUnOrRedo(true);
        }


        System.out.println(Main.editLog);

        Main.backupData();


    }




    @FXML
    public void redo(){


        if (Main.editLog.canGoForward()){
            ArrayList<Activity> nextArchive = Main.editLog.forwardLog();


            Main.archive = nextArchive;

            colorPane.getChildren().clear();

            addActivities(nextArchive);
        }
        else {
            flashCantUnOrRedo(false);
        }

        System.out.println(Main.editLog);

        Main.backupData();



    }


    public void flashCantUnOrRedo(boolean isUndo){

        Bounds referenceBounds = categoryTitlePicker.localToScreen(categoryTitlePicker.getLayoutBounds());

        Label notification = new Label();

        notification.setFont(new Font(50));
        notification.setStyle("-fx-text-fill: red;");
        notification.setOpacity(0.3);

        if (isUndo){
            notification.setText("this is your first edit");
        }
        else {
            notification.setText("this is your last edit");
        }

        new Thread( new Runnable() {
            @Override
            public void run() {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        grandmotherPane.getChildren().add(notification);

                        notification.setLayoutX(referenceBounds.getMinX());
                        notification.setLayoutY(referenceBounds.getMinY() - 400);
                    }
                });


                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        grandmotherPane.getChildren().remove(notification);
                    }
                });

            }
        }).start();


    }





    Cycler cycler = new Cycler();


    boolean switchModeOn = false;
    @FXML
    public void handleCategoryEdit(KeyEvent event){

        String input = event.getCharacter();

        if (event.getCharacter().equals(" ")){
            if (cycler.getItems().isEmpty()){
                categoryTitlePicker.setText("");
            }
            else{
                editCategory(cycler.nextItem());
            }
        }
        else{
            updateCycler(input);
            editCategory(cycler.nextItem());
        }


        Main.editLog.addLog(Main.archive);

        Main.backupData();

    }




    private void updateCycler(String input){

        input = input.toUpperCase();


        cycler.clear();

        for (String category : Stats.categorySequence){
            if (category.startsWith(input)){
                cycler.addNewItem(category);
                System.out.println(cycler.getItems());
            }
        }

        if (cycler.getItems().isEmpty()){
            cycler.setItems((ArrayList<String>) Stats.categorySequence.clone());
            System.out.println("default");
        }

    }



    private void editCategory(String newCat){
        String newColor = Stats.colorMap.get(newCat);

        colorPane.getChildren().get(selectedIndex).setStyle("-fx-background-color:" + newColor + "; -fx-alignment: center; -fx-border-color: white; fx-border-width: 1;");

        if (newColor != null){
            categoryTitlePicker.setStyle("-fx-alignment: center; -fx-text-fill:" + newColor);
        }
        else {
            categoryTitlePicker.setStyle("-fx-alignment: center; -fx-text-fill: black");
        }

        categoryTitlePicker.setText(newCat);



        Main.archive.get(selectedIndex).setCategory(newCat);

        Main.editLog.addLog(Main.archive);

        Main.backupData();
    }


    @FXML
    public void handleNameEdit(KeyEvent event){
        String newName = specificField.getText();

        changeName(newName);

        if (!grandmotherPane.getChildren().contains(nameSuggestions)){
            grandmotherPane.getChildren().add(nameSuggestions);

            Bounds fieldBounds = specificField.localToScreen(specificField.getLayoutBounds());



            nameSuggestions.setLayoutX(fieldBounds.getMinX() - 1);
            nameSuggestions.setLayoutY(fieldBounds.getMinY() - 444);
        }

        updateSuggestedNames(newName);


        if (suggestedNamesIsEmpty()){
            hideSuggestedNames();
            System.out.println("suggestions hidden");
        }

//        String command = event.getCharacter();
//
//        switch (command){
//            case "w":
//                nameSuggestions.setLayoutY(nameSuggestions.getLayoutY() - 1);
//
//                break;
//            case "s":
//                nameSuggestions.setLayoutY(nameSuggestions.getLayoutY() + 1);
//
//                break;
//            case "a":
//                nameSuggestions.setLayoutX(nameSuggestions.getLayoutX() - 1);
//
//                break;
//            case "d":
//                nameSuggestions.setLayoutX(nameSuggestions.getLayoutX() + 1);
//
//                break;
//
//        }
//
//        System.out.println("x: "+nameSuggestions.getLayoutX());
//        System.out.println("y: "+nameSuggestions.getLayoutY());




    }

    private boolean suggestedNamesIsEmpty(){
        for (Object name : suggestedNames){

            String[] chars = ((String) name).split("");

            for (String ch : chars){

                if (!ch.equals(" ") && !ch.isEmpty()){

                    return false;
                }
            }
        }
        return true;
    }


    private void updateSuggestedNames(String newName){

        suggestedNames.clear();

        suggestedNames.addAll(LoadScreen.browsPastActivities(newName));


        if (!suggestedNames.isEmpty() && suggestedNames.size() < 16){
            int Nspaces = 16 - suggestedNames.size();
            for (int i = 0; i < Nspaces; i++){

                suggestedNames.add(0, "");

            }
        }

        if (!suggestedNames.isEmpty()){
            nameSuggestions.scrollTo(suggestedNames.get(suggestedNames.size() - 1));
        }


        suggestedNames.remove(newName);




    }




    @FXML
    public void autoCompleteName() throws ConcurrentModificationException {


        String selectedName = (String)nameSuggestions.getSelectionModel().getSelectedItem();
        changeName(selectedName);
        specificField.setText(selectedName);
        System.out.println((String)nameSuggestions.getSelectionModel().getSelectedItem());
        hideSuggestedNames();
    }





    @FXML
    public void tellMousePos(MouseEvent e){

    }

    private void changeName(String newName){
        Label givenEpoch = (Label)colorPane.getChildren().get(selectedIndex);
        String oldName = givenEpoch.getText();

        givenEpoch.setText(newName);


        Activity givenActivity = Main.archive.get(selectedIndex);


        givenActivity.setName(newName);


        changeFontUntilTextFitsLabel(givenEpoch);




        LoadScreen.loadActivity(givenActivity.getName(), givenActivity.getCategory(), givenActivity.getDurationSeconds(), givenActivity.getDate());
        removePastActivity(oldName);



        Main.editLog.addLog(Main.archive);



        Main.backupData();
    }



    public static void removePastActivity(String name){
        for (Iterator<PastActivity> iterator = Main.pastActivities.iterator(); iterator.hasNext();){

            PastActivity pastActivity = iterator.next();

            if (pastActivity.getName().equals(name) && pastActivity.getFrequency() == 1){
                iterator.remove();

            }
        }
    }




    public void expandLeft(double deltaX){

        if (selectedIndex != 0){
            deltaX = Math.abs(deltaX);
            int deltaSecs = (int)(deltaX / WIDTH_OF_1_SEC);

            int neighborIndex = selectedIndex - 1;

            Activity selected = Main.archive.get(selectedIndex);
            Activity neighbor = Main.archive.get(neighborIndex);


            if (neighbor.getDurationSeconds() > 1){
                int selectedStartTime = selected.getStartTimeSecs();
                int selectedDuration = selected.getDurationSeconds();


                int neighborEndTime = neighbor.getEndTimeSecs();
                int neighborDuration = neighbor.getDurationSeconds();


                selectedDuration += deltaSecs;
                neighborDuration -= deltaSecs;

                selectedStartTime -= deltaSecs;
                neighborEndTime -= deltaSecs;

                selected.setStartTimeSecs(selectedStartTime);
                selected.setDurationSeconds(selectedDuration);

                neighbor.setEndTimeSecs(neighborEndTime);
                neighbor.setDurationSeconds(neighborDuration);



                Label selectedDisplay = (Label)colorPane.getChildren().get(selectedIndex);
                Label neighborDisplay = (Label) colorPane.getChildren().get(neighborIndex);

                selectedDisplay.setPrefWidth(selectedDuration * WIDTH_OF_1_SEC);
                selectedDisplay.setMaxWidth(selectedDuration * WIDTH_OF_1_SEC);
                selectedDisplay.setMinWidth(selectedDuration * WIDTH_OF_1_SEC);

                neighborDisplay.setPrefWidth(neighborDuration * WIDTH_OF_1_SEC);
                neighborDisplay.setMaxWidth(neighborDuration * WIDTH_OF_1_SEC);
                neighborDisplay.setMinWidth(neighborDuration * WIDTH_OF_1_SEC);


                changeFontUntilTextFitsLabel(selectedDisplay);
                changeFontUntilTextFitsLabel(neighborDisplay);


                upDateInfoPanel(selectedStartTime, selected.getEndTimeSecs(), selectedDuration);
            }







        }

    }



    //check
    public void expandRight(double deltaX){

        if (selectedIndex != Main.archive.size() - 1){
            deltaX = Math.abs(deltaX);
            int deltaSecs = (int)(deltaX / WIDTH_OF_1_SEC);

            int neighborIndex = selectedIndex + 1;

            Activity selected = Main.archive.get(selectedIndex);
            Activity neighbor = Main.archive.get(neighborIndex);



            if (neighbor.getDurationSeconds() > 1){
                int selectedEndTime = selected.getEndTimeSecs();
                int selectedDuration = selected.getDurationSeconds();


                int neighborStartTime = neighbor.getStartTimeSecs();
                int neighborDuration = neighbor.getDurationSeconds();


                selectedDuration += deltaSecs;
                neighborDuration -= deltaSecs;

                selectedEndTime += deltaSecs;
                neighborStartTime += deltaSecs;

                selected.setEndTimeSecs(selectedEndTime);
                selected.setDurationSeconds(selectedDuration);

                neighbor.setStartTimeSecs(neighborStartTime);
                neighbor.setDurationSeconds(neighborDuration);



                Label selectedDisplay = (Label)colorPane.getChildren().get(selectedIndex);
                Label neighborDisplay = (Label) colorPane.getChildren().get(neighborIndex);

                selectedDisplay.setPrefWidth(selectedDuration * WIDTH_OF_1_SEC);
                selectedDisplay.setMaxWidth(selectedDuration * WIDTH_OF_1_SEC);
                selectedDisplay.setMinWidth(selectedDuration * WIDTH_OF_1_SEC);

                neighborDisplay.setPrefWidth(neighborDuration * WIDTH_OF_1_SEC);
                neighborDisplay.setMaxWidth(neighborDuration * WIDTH_OF_1_SEC);
                neighborDisplay.setMinWidth(neighborDuration * WIDTH_OF_1_SEC);

                changeFontUntilTextFitsLabel(selectedDisplay);
                changeFontUntilTextFitsLabel(neighborDisplay);

                upDateInfoPanel(selected.getStartTimeSecs(), selectedEndTime, selectedDuration);
            }







        }

    }


    //check
    public void shrinkRight(double deltaX){


            deltaX = Math.abs(deltaX);
            int deltaSecs = (int)(deltaX / WIDTH_OF_1_SEC);

            int neighborIndex = selectedIndex + 1;

            Activity selected = Main.archive.get(selectedIndex);
            Activity neighbor = Main.archive.get(neighborIndex);


            if (selected.getDurationSeconds() > 1){
                int selectedEndTime = selected.getEndTimeSecs();
                int selectedDuration = selected.getDurationSeconds();


                int neighborStartTime = neighbor.getStartTimeSecs();
                int neighborDuration = neighbor.getDurationSeconds();


                selectedDuration -= deltaSecs;
                neighborDuration += deltaSecs;

                selectedEndTime -= deltaSecs;
                neighborStartTime -= deltaSecs;

                selected.setEndTimeSecs(selectedEndTime);
                selected.setDurationSeconds(selectedDuration);

                neighbor.setStartTimeSecs(neighborStartTime);
                neighbor.setDurationSeconds(neighborDuration);



                Label selectedDisplay = (Label)colorPane.getChildren().get(selectedIndex);
                Label neighborDisplay = (Label) colorPane.getChildren().get(neighborIndex);

                selectedDisplay.setPrefWidth(selectedDuration * WIDTH_OF_1_SEC);
                selectedDisplay.setMaxWidth(selectedDuration * WIDTH_OF_1_SEC);
                selectedDisplay.setMinWidth(selectedDuration * WIDTH_OF_1_SEC);

                neighborDisplay.setPrefWidth(neighborDuration * WIDTH_OF_1_SEC);
                neighborDisplay.setMaxWidth(neighborDuration * WIDTH_OF_1_SEC);
                neighborDisplay.setMinWidth(neighborDuration * WIDTH_OF_1_SEC);



                changeFontUntilTextFitsLabel(selectedDisplay);
                changeFontUntilTextFitsLabel(neighborDisplay);



                upDateInfoPanel(selected.getStartTimeSecs(), selectedEndTime, selectedDuration);
            }








    }



    public void shrinkLeft(double deltaX){


            deltaX = Math.abs(deltaX);
            int deltaSecs = (int)(deltaX / WIDTH_OF_1_SEC);

            int neighborIndex = selectedIndex - 1;

            Activity selected = Main.archive.get(selectedIndex);
            Activity neighbor = Main.archive.get(neighborIndex);


            if (selected.getDurationSeconds() > 1){
                int selectedStartTime = selected.getStartTimeSecs();
                int selectedDuration = selected.getDurationSeconds();


                int neighborEndTime = neighbor.getEndTimeSecs();
                int neighborDuration = neighbor.getDurationSeconds();


                selectedDuration -= deltaSecs;
                neighborDuration += deltaSecs;

                selectedStartTime += deltaSecs;
                neighborEndTime += deltaSecs;

                selected.setStartTimeSecs(selectedStartTime);
                selected.setDurationSeconds(selectedDuration);

                neighbor.setEndTimeSecs(neighborEndTime);
                neighbor.setDurationSeconds(neighborDuration);



                Label selectedDisplay = (Label)colorPane.getChildren().get(selectedIndex);
                Label neighborDisplay = (Label) colorPane.getChildren().get(neighborIndex);

                selectedDisplay.setPrefWidth(selectedDuration * WIDTH_OF_1_SEC);
                selectedDisplay.setMaxWidth(selectedDuration * WIDTH_OF_1_SEC);
                selectedDisplay.setMinWidth(selectedDuration * WIDTH_OF_1_SEC);

                neighborDisplay.setPrefWidth(neighborDuration * WIDTH_OF_1_SEC);
                neighborDisplay.setMaxWidth(neighborDuration * WIDTH_OF_1_SEC);
                neighborDisplay.setMinWidth(neighborDuration * WIDTH_OF_1_SEC);



                changeFontUntilTextFitsLabel(selectedDisplay);
                changeFontUntilTextFitsLabel(neighborDisplay);



                upDateInfoPanel(selectedStartTime, selected.getEndTimeSecs(), selectedDuration);
            }








    }


    private void upDateInfoPanel(int newStartTimeSecs, int newEndTimeSecs, int newDuration){
        beganAt.setText( getAMPMTimeFromSeconds(newStartTimeSecs));
        finishedAt.setText(getAMPMTimeFromSeconds(newEndTimeSecs));
        durationLabel.setText(Stats.parseDuration(newDuration));
    }




    double prevX = 0;
    boolean isNewStart = true;
    private void addDragHandler(Label activity) {
        activity.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                selectedIndex = colorPane.getChildren().indexOf(activity);

                double mousePos = event.getScreenX();
                Bounds activityBounds = activity.localToScreen(activity.getBoundsInLocal());
                double activityPos = (activityBounds.getMinX() + activityBounds.getMaxX()) / 2;


                double deltaX;
                if (isNewStart) {
                    deltaX = 0;
                } else {
                    deltaX = mousePos - prevX;
                }
                isNewStart = false;

                double calibratedDeltaX = ((int)(deltaX / WIDTH_OF_1_SEC)) * WIDTH_OF_1_SEC;

                if (mousePos < activityPos) {
                    if (deltaX < 0){
                        expandLeft(calibratedDeltaX);
                    }
                    else if (deltaX > 0){
                        shrinkLeft(calibratedDeltaX);
                    }
                }
                else if (mousePos > activityPos){
                    if(deltaX < 0){
                        shrinkRight(calibratedDeltaX);
                    }
                    else if (deltaX > 0){
                        expandRight(calibratedDeltaX);
                    }
                }


                prevX = event.getScreenX();
            }
        });


        activity.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isNewStart = true;

                Main.editLog.addLog(Main.archive);

                Main.backupData();
            }
        });

    }





    @FXML
    public void insertActivity(){

            Activity selectedActivity = Main.archive.get(selectedIndex);
            Label selectedLabel = (Label) colorPane.getChildren().get(selectedIndex);


            int currentSecs = selectedActivity.getDurationSeconds();
            int existingSecs = currentSecs / 2;
            int newSecs = currentSecs - existingSecs;

            int existingStartTime = selectedActivity.getStartTimeSecs();
            int existingEndtime = selectedActivity.getEndTimeSecs();

            int newStartTime = existingEndtime - newSecs;
            int newEndTime = existingEndtime;


            Activity newActivity = new Activity("new","", newSecs, newStartTime , newEndTime, selectedActivity.getDate());

            selectedActivity.setDurationSeconds(existingSecs);

            existingEndtime = newStartTime - 1;
            selectedActivity.setEndTimeSecs(existingEndtime);

        Main.archive.add(selectedIndex+1, newActivity);

            selectedLabel.setPrefWidth(existingSecs * WIDTH_OF_1_SEC);
            selectedLabel.setMaxWidth(existingSecs * WIDTH_OF_1_SEC);
            selectedLabel.setMinWidth(existingSecs * WIDTH_OF_1_SEC);


            Label newLabel = new Label("new");
            newLabel.setStyle("-fx-alignment: center; -fx-border-color: white; fx-border-width: 1;");
            newLabel.setFont(new Font(30));
            newLabel.setPrefWidth(newSecs * WIDTH_OF_1_SEC);
            newLabel.setPrefHeight(colorLineHeight);
            addClickHandler(newLabel);
            addDragHandler(newLabel);
            colorPane.getChildren().add(selectedIndex+1, newLabel);
            changeFontUntilTextFitsLabel(newLabel);


            changeFontUntilTextFitsLabel(selectedLabel);
            changeFontUntilTextFitsLabel(newLabel);


            updateSelectedEpoch(newActivity);


            selectedIndex = Main.archive.indexOf(selectedActivity);

            Main.editLog.addLog(Main.archive);

            Main.backupData();


    }





    @FXML
    public void deleteActivity(){
        if (selectedIndex != 0){

                int mergerIndex = selectedIndex - 1;

                Activity merger = Main.archive.get(mergerIndex);
                Activity engulfed = Main.archive.get(selectedIndex);


                removePastActivity(engulfed.getName());


                int mergerEndTime = merger.getEndTimeSecs();
                int mergerDuration = merger.getDurationSeconds();


                int engulfedEndTime = engulfed.getEndTimeSecs();
                int engulfedDuration = engulfed.getDurationSeconds();

            Main.archive.remove(engulfed);

                mergerEndTime = engulfedEndTime;
                mergerDuration += engulfedDuration;


                merger.setEndTimeSecs(mergerEndTime);
                merger.setDurationSeconds(mergerDuration);


                Label mergerLabel = (Label) colorPane.getChildren().get(mergerIndex);
                Label engulfedLabel = (Label)colorPane.getChildren().get(selectedIndex);

                colorPane.getChildren().remove(engulfedLabel);

                mergerLabel.setPrefWidth(mergerDuration * WIDTH_OF_1_SEC);
                mergerLabel.setMaxWidth(mergerDuration * WIDTH_OF_1_SEC);
                mergerLabel.setMinWidth(mergerDuration * WIDTH_OF_1_SEC);

                changeFontUntilTextFitsLabel(mergerLabel);

                updateSelectedEpoch(merger);

                selectedIndex = mergerIndex;


                Main.editLog.addLog(Main.archive);

                Main.backupData();

        }
    }


    private void updateSelectedEpoch(Activity epoch){

        String mergerCat = epoch.getCategory();

        categoryTitlePicker.setStyle("-fx-alignment:center; -fx-text-fill:" + Stats.colorMap.get(mergerCat) + ";");
        categoryTitlePicker.setText(mergerCat);
        specificField.setText(epoch.getName());

        upDateInfoPanel(epoch.getStartTimeSecs(), epoch.getEndTimeSecs(), epoch.getDurationSeconds());

    }






    public void computeAndSetScrollableWidth() {
        scrollableWidth = (endTimeSecs - startTimeSecs) * WIDTH_OF_1_SEC + 600;
        if (scrollableWidth < 1280) {
            scrollableWidth = 1280;
        }
    }





    private void makeTimeLine(int startTiempo, int endTiempo) {
        int beginTime = startTiempo - startTiempo % FIVE_MINS_IN_SECONDS;
        allContainerPane.setMargin(colorPane, new Insets(0, 0, 0, 200 + (startTiempo - beginTime) * WIDTH_OF_1_SEC));
        int finishTime = FIVE_MINS_IN_SECONDS * (endTiempo / FIVE_MINS_IN_SECONDS) + FIVE_MINS_IN_SECONDS;

        if (!Main.archive.isEmpty()) {
            for (int currentTime = beginTime; currentTime <= finishTime; currentTime += FIVE_MINS_IN_SECONDS) {
                Label timeLabel = new Label(getAMPMTimeFromSeconds(currentTime));
                timeLabel.setPrefWidth(WIDTH_OF_5_MINS);
                timeLabel.setPrefHeight(timeLineHeight);
                timeLabel.setStyle("-fx-alignment: center");
                timePane.getChildren().add(timeLabel);

            }
        }

    }


    public void addActivities(ArrayList<Activity> activities) {

        if (! activities.isEmpty()){
            Activity firstAct = activities.get(0);
            Activity lastAct = activities.get(activities.size()-1);
            if (Main.archive.size() == 1) {
                this.startTimeSecs = firstAct.getStartTimeSecs();
                this.endTimeSecs = firstAct.getEndTimeSecs();
                firstDate = firstAct.getDate();
                lastDate = decrementDate(firstDate);
            } else if (!Main.archive.isEmpty()) {
                this.startTimeSecs = firstAct.getStartTimeSecs();
                this.endTimeSecs = Main.archive.get(Main.archive.size() - 1).getEndTimeSecs();
                firstDate = firstAct.getDate();
                lastDate = Main.archive.get(Main.archive.size() - 1).getDate();
            }
//        System.out.println("starttime: "+this.startTimeSecs);
//        System.out.println("endTime: "+this.endTimeSecs);

            if (endTimeSecs - startTimeSecs < FIVE_MINS_IN_SECONDS) {
                endTimeSecs += FIVE_MINS_IN_SECONDS;
            }

            for (Activity activity : activities) {
                Label newActivity = new Label();
                newActivity.setStyle("-fx-background-color:" + Stats.colorMap.get(activity.getCategory()) + "; -fx-alignment: center; -fx-border-color: white; fx-border-width: 1;");
                newActivity.setPrefHeight(colorLineHeight);
                newActivity.setPrefWidth(activity.getDurationSeconds() * WIDTH_OF_1_SEC);
                newActivity.setText(activity.getName());
                //newActivity.setFont(new Font(40));
                addClickHandler(newActivity);
                addDragHandler(newActivity);
                colorPane.getChildren().add(newActivity);
                changeFontUntilTextFitsLabel(newActivity);


                if (activity.getEndTimeSecs() > endTimeSecs){
                    endTimeSecs += SECONDS_IN_A_DAY;
                }
            }
        }




    }


    public static String decrementDate(String date) {
        return date.substring(0, date.length() - 2) + (Integer.parseInt(date.substring(date.length() - 2)) - 1);
    }



    private void addClickHandler(Label activity) {
        activity.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (!Main.archive.isEmpty()){
                    Activity represented = Main.archive.get(colorPane.getChildren().indexOf(activity));

                    String activityCategory = represented.getCategory();

                    String color = "white";
                    try {
                        color = (String) colorMap.get(activityCategory);

                    }
                    catch (NullPointerException e){

                    }
                    categoryTitlePicker.setStyle("-fx-alignment:center; -fx-text-fill:" + color + ";");
                    categoryTitlePicker.setText(activityCategory);



                    durationLabel.setText(Stats.parseDuration(represented.getDurationSeconds()));

                    specificField.setText(represented.getName());

                    beganAt.setText(getAMPMTimeFromSeconds(represented.getStartTimeSecs()));

                    finishedAt.setText(getAMPMTimeFromSeconds(represented.getEndTimeSecs()));

                    selectedIndex = Main.archive.indexOf(represented);

                    detailPane.setOpacity(1);
                    detailPane.setDisable(false);

                    hideSuggestedNames();





                    System.out.println(Main.editLog);



                }


            }
        });
    }

    @FXML
    private void hideSuggestedNames(){

        grandmotherPane.getChildren().remove(nameSuggestions);
        System.out.println("suggestedNames Removed");
    }


//    private void changeFontUntilTextFitsLabel(Label label, double deltaX) {
//
//        deltaX = Math.abs(deltaX);
//
//        String text = label.getText();
//        testText.setText(text);
//        testText.setFont(label.getFont());
//
//
//        double textWidth = testText.getLayoutBounds().getWidth();
//        double labelWidth = label.localToScreen(label.getBoundsInLocal()).getWidth();
//
//
//        if (labelWidth < textWidth){
//            label.setFont(new Font(label.getFont().getSize() * ((labelWidth - deltaX) / labelWidth)));
//        }
//        else if (label.getFont().getSize() < 30){
//            label.setFont(new Font(label.getFont().getSize() * ((labelWidth + deltaX) / labelWidth)));
//        }
//
//    }

    private void changeFontUntilTextFitsLabel(Label label) {

        label.setFont(new Font(100));

        String text = label.getText();
        testText.setText(text);
        testText.setFont(new Font(100));

        double textWidth = testText.getLayoutBounds().getWidth();
        double labelWidth = label.getPrefWidth();

        double resizeFactor = labelWidth / textWidth * 0.9;

        if (labelWidth < textWidth){
            label.setFont(new Font(label.getFont().getSize() * resizeFactor));
        }



    }


    private void configureDateChangePoint(int beginTime) {
        dateChangePoint = (double) (SECONDS_IN_A_DAY - beginTime) * WIDTH_OF_1_SEC + 200;

        Line upperBorder = new Line(0, 0, 0, 200);
        upperBorder.setLayoutY(0);
        upperBorder.setLayoutX(dateChangePoint);
        upperBorder.setStrokeWidth(3);


        Line marginBorder = new Line(0, 0, 0, 200);
        marginBorder.setLayoutY(0);
        marginBorder.setLayoutX(dateChangePoint);
        marginBorder.setStrokeWidth(3);


        Line bottomBorder = new Line(0, 0, 0, 350);
        bottomBorder.setLayoutY(0);
        bottomBorder.setLayoutX(dateChangePoint);
        bottomBorder.setStrokeWidth(3);


        portalContainerPane.getChildren().add(marginBorder);
        yearPane.getChildren().add(upperBorder);
        detailContainerPane.getChildren().add(bottomBorder);


    }


    private void makeNodes(int nodeNum){
        if (!Main.archive.isEmpty()){
            for (int i = 0; i <= nodeNum; i++){
                Line nodeMarker = new Line(0,nodeStartPointY,0,nodeEndPointY);
                nodeMarker.setScaleX(2);
                nodePane.getChildren().add(nodeMarker);
            }
        }

    }

    public void resizePanes(double newWidth) {

        allContainerPane.setPrefWidth(newWidth);
        allContainerPane.setMaxWidth(newWidth);
        allContainerPane.setMaxWidth(newWidth);

        yearPane.setPrefWidth(newWidth);
        yearPane.setMaxWidth(newWidth);
        yearPane.setMinWidth(newWidth);

        colorPane.setPrefWidth(newWidth);
        colorPane.setMaxWidth(newWidth);
        colorPane.setMinWidth(newWidth);

        nodePane.setPrefWidth(newWidth);
        nodePane.setMaxWidth(newWidth);
        nodePane.setMinWidth(newWidth);

        timePane.setPrefWidth(newWidth);
        timePane.setMaxWidth(newWidth);
        timePane.setMinWidth(newWidth);

        detailContainerPane.setPrefWidth(newWidth);
        detailContainerPane.setMaxWidth(newWidth);
        detailContainerPane.setMinWidth(newWidth);

        portalContainerPane.setPrefWidth(newWidth);
        portalContainerPane.setMaxWidth(newWidth);
        portalContainerPane.setMinWidth(newWidth);

    }


    private int getNumNodes() {
        return (FIVE_MINS_IN_SECONDS * (endTimeSecs / FIVE_MINS_IN_SECONDS) + FIVE_MINS_IN_SECONDS - (startTimeSecs - startTimeSecs % FIVE_MINS_IN_SECONDS)) / FIVE_MINS_IN_SECONDS;
    }


    public void goToScheduleCreator() {
        Table.updateData(Main.archive);
        Stage stage = (Stage) motherPane.getScene().getWindow();
        stage.setScene(Main.getScheduleCreator());

        Stats.currentTimelineLayoutInitialized = false;
    }

    public void goToMain() {
        Table.updateData(Main.archive);
        Stage stage = (Stage) motherPane.getScene().getWindow();
        stage.setScene(Main.getStats());

        Stats.currentTimelineLayoutInitialized = false;
    }

    public void goToTimeLine() throws IOException {
        Table.updateData(Main.archive);
        Stage stage = (Stage) motherPane.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("timeLine.fxml")), 1300, 700));
    }


    public void onScroll(ScrollEvent e) {

        double motherPaneLayoutX = motherPane.getLayoutX();
        double change = e.getDeltaX();

        if (motherPaneLayoutX + change < 10 && motherPaneLayoutX + change > -(scrollableWidth - TimeLine.screenWidth)) {
            double newLayoutX = motherPaneLayoutX + change;
            motherPane.setLayoutX(newLayoutX);
            portalPane.setLayoutX(newLayoutX);
            shiftPortals(-newLayoutX);
            shiftDateDisplay(-change);
            checkAndUpdateDate(dateDisplay.getLayoutX());
//            hammerDownUpperBorderOnMeetingPortals();
            shiftDetails(change);

            TimeLine.currentLayout = newLayoutX;

        }

    }


    private void setPosition(double layoutX){

        double change = layoutX - motherPane.getLayoutX();

        motherPane.setLayoutX(layoutX);
        portalPane.setLayoutX(layoutX);
        shiftPortals(-layoutX);
        shiftDateDisplay(-change);
        checkAndUpdateDate(dateDisplay.getLayoutX());
//            hammerDownUpperBorderOnMeetingPortals();
        shiftDetails(change);

    }

//    private void hammerDownUpperBorderOnMeetingPortals(){
//        double borderX = upperBorder.getLayoutX();
//        double portalX = portalPane.getLayoutX();
//        if (borderX > portalX && borderX < portalX + 393){
//            upperBorder.setStartY(0);
//        }
//        else {
//            upperBorder.setStartY(-86);
//        }
//    }


    private void checkAndUpdateDate(double currentLayoutX) {

        if (dateChangePoint != -1 && currentLayoutX < dateChangePoint) {
            dateDisplay.setText(firstDate);
        } else if (dateChangePoint != -1 && currentLayoutX > dateChangePoint) {
            dateDisplay.setText(lastDate);
        }
    }


    private void shiftPortals(double newLayoutX) {
        portalPane.setLayoutX(newLayoutX);
    }


    private void shiftDateDisplay(double change) {
        dateDisplay.setLayoutX(dateDisplay.getLayoutX() + change);
    }

    private void shiftDetails(double change) {
        detailPane.setLayoutX(detailPane.getLayoutX() - change);
    }


    public static String getAMPMTimeFromSeconds(int seconds) {

        int template;

        if (seconds > SECONDS_IN_A_DAY) {
            template = seconds - SECONDS_IN_A_DAY;
        } else {
            template = seconds;
        }


        int hh = template / 3600;

        template = template % 3600;

        int mm = (template / 60);

        template = template % 60;


        String minutes;
        if (mm == 0) {
            minutes = "00";
        } else if (mm < 10) {
            minutes = "0" + mm;
        } else {
            minutes = ((Integer) mm).toString();
        }


        if (hh == 0) {
            return "12" + ":" + minutes + " A.M.";
        } else if (hh == 12) {
            return hh + ":" + minutes + " P.M.";
        } else if (hh < 12) {
            return hh + ":" + minutes + " A.M.";
        }
        return hh - 12 + ":" + minutes + " P.M.";
    }


}



//

