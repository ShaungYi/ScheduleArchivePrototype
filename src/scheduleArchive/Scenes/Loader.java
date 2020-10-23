package scheduleArchive.Scenes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import scheduleArchive.Utility.Activity;
import scheduleArchive.Main;


import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Loader {




    @FXML
    ListView backups;
    ObservableList<String> backupData = FXCollections.observableArrayList();



    Connection conn;
    Statement statement;


    @FXML
    Button loadButton;
    @FXML
    Button resumeButton;



    public static boolean resumeMode = false;

    public static boolean loadMode = false;


    public static ResultSet pastDays;


    public void initialize(){

        try {
            conn = DriverManager.getConnection(Main.ARCHIVE_URL);
            statement = conn.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        setupBackups();

    }



    public void setupBackups(){

        backups.setItems(backupData);

        try {



            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            pastDays = metaData.getTables(null, null, "%", types);



            while (pastDays.next()){
                backupData.add(Main.toDay(pastDays.getString("TABLE_NAME")));
            }

            if (!backupData.isEmpty()){
                Main.lastDay = backupData.get(backupData.size() - 1);

                backups.scrollTo(backupData.size() - 1);
            }





        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    @FXML
    public void goToStats(){
        Stage stage = (Stage) backups.getScene().getWindow();
        stage.setScene(Main.getStats());
    }



    @FXML
    public void resumeCreation(){

        try {


            Main.selectedDay = (String)backups.getSelectionModel().getSelectedItem();


            System.out.println(Main.selectedDay);

            String tableID = Main.toTableName(Main.selectedDay);


            statement.execute("SELECT * FROM " + tableID);

            ResultSet DBdata = statement.getResultSet();

            Main.archive.clear();

            while (DBdata.next()){
                Main.archive.add(new Activity(DBdata.getString("name"), DBdata.getString("category"), DBdata.getInt("duration"), DBdata.getInt("startTime"), DBdata.getInt("endTime"), DBdata.getString("date")));
            }


            int gapStartTime = Main.archive.get(Main.archive.size()-1).getEndTimeSecs();
            int gapEndTime = ScheduleCreator.getTimeInSeconds(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond());

            if (gapEndTime < gapStartTime){
                gapEndTime += TimeLine.SECONDS_IN_A_DAY;
            }


            Main.archive.add(new Activity("no data", "NoData", gapEndTime - gapStartTime, gapStartTime, gapEndTime, LocalDate.now().toString()));

            resumeMode = true;
            loadMode = false;

            Main.currentDay = Main.archive.get(0).getDate();

            Table.updateData(Main.archive);


            wrapUpAndGoToCreator();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    public void loadDaysOfOld() {



        try {

            Main.selectedDay = (String)backups.getSelectionModel().getSelectedItem();

            System.out.println(Main.selectedDay);

            String tableID = Main.toTableName(Main.selectedDay);

            statement.execute("SELECT * FROM " + tableID);

            ResultSet DBdata = statement.getResultSet();






            Main.archive.clear();


            while (DBdata.next()){
                Main.archive.add(new Activity(DBdata.getString("name"), DBdata.getString("category"), DBdata.getInt("duration"), DBdata.getInt("startTime"), DBdata.getInt("endTime"), DBdata.getString("date")));
            }



            Table.updateData(Main.archive);



            disableAndGotoCreator();



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }



    }


    private void disableAndGotoCreator(){

        try {
            loadMode = true;
            Main.setScheduleCreator(new Scene(FXMLLoader.load(getClass().getResource("scheduleCreator.fxml")), 1300, 700));
            goToStats();
            Main.editLog.wipe();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void wrapUpAndGoToCreator(){

        try {
            Main.setScheduleCreator(new Scene(FXMLLoader.load(getClass().getResource("scheduleCreator.fxml")), 1300, 700));
            Stage stage = (Stage) backups.getScene().getWindow();
            stage.setScene(Main.getScheduleCreator());

            Main.editLog.wipe();

            Main.selectedDay = (String)backups.getSelectionModel().getSelectedItem();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private boolean canResume(){

        try {


           String currentSelected = (String)backups.getSelectionModel().getSelectedItem();

           statement.execute("SELECT * FROM "+Main.toTableName(Main.selectedDay));

           ResultSet testData = statement.getResultSet();

            int lastEndTime = 0;
            int totalDuration = 0;
            while (testData.next()) {
                totalDuration += testData.getInt("duration");
                lastEndTime = testData.getInt("endTime");
            }


            if ( totalDuration + (ScheduleCreator.getTimeInSeconds(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond()) - lastEndTime) < 86400){
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;

    }


    public void assessSelectedDay(){

        String selectedDayTemp = (String)backups.getSelectionModel().getSelectedItem();

        //System.out.println(selected);
        //System.out.println(lastDay);

        if (!(selectedDayTemp == null)){


                loadButton.setDisable(false);

                if (selectedDayTemp.equals(Main.lastDay) && canResume()){
                    resumeButton.setDisable(false);
                }
                else {
                    resumeButton.setDisable(true);
                }
                
        }
        else {
            loadButton.setDisable(true);
        }


    }


}
