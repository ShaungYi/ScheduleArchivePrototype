package scheduleArchive;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scheduleArchive.Scenes.Loader;
import scheduleArchive.Scenes.ScheduleCreator;
import scheduleArchive.Utility.Activity;
import scheduleArchive.Utility.EditLog;
import scheduleArchive.Utility.PastActivity;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

public class Main extends Application {

    static UrlFetcher urlFetcher = new UrlFetcher();
    public static final String ARCHIVE_URL = urlFetcher.fetchURL();


    public static String selectedDay;
    public static String lastDay;
    public static String currentDay = LocalDate.now().toString();


    public static EditLog editLog = new EditLog();


    public static ArrayList<PastActivity> pastActivities = new ArrayList<>();


    static Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    public static final double screenWidth = screenSize.getWidth()-1;
    public static final double screenHeight = screenSize.getHeight()-1;


    public static Scene scheduleCreator, stats, bars, table, piChart, loadScreen;
    public static ArrayList<Activity> archive = new ArrayList<Activity>();



    @Override
    public void start(Stage primaryStage) throws Exception{


        loadScreen = new Scene(FXMLLoader.load(getClass().getResource("Scenes/loadScreen.fxml")), screenWidth, screenHeight);
        Thread.sleep(100);
        scheduleCreator = new Scene(FXMLLoader.load(getClass().getResource("Scenes/scheduleCreator.fxml")), screenWidth, screenHeight);
        stats = new Scene(FXMLLoader.load(getClass().getResource("Scenes/stats.fxml")));
        bars = new Scene(FXMLLoader.load(getClass().getResource("Scenes/barDisplay.fxml")), screenWidth, screenHeight);
        table = new Scene(FXMLLoader.load(getClass().getResource("Scenes/table.fxml")), screenWidth, screenHeight);
        piChart = new Scene(FXMLLoader.load(getClass().getResource("Scenes/piChart.fxml")), screenWidth, screenHeight);


        primaryStage.setTitle("Schedule Archive");
        primaryStage.setScene(scheduleCreator);
        primaryStage.show();

    }



    public void stop(){

        backupData();
    }

    public static void backupData(){

        System.out.println("data backed up");

        if (archive.isEmpty()){
            return;
        }


        if (Loader.resumeMode || Loader.loadMode || !(selectedDay == null)){

            saveArchive(Main.toTableName(selectedDay));
//            System.out.println("archive saved at table "+ "y" + selectedDay.replaceAll("-", "_"));
        }
        else {
            String newDayID = saveDay();
            saveArchive(newDayID);


        }
    }



    public static void saveArchive(String tableID){

//        System.out.println("saveArchive called");


        try {
//            System.out.println(tableID);


            Connection conn = DriverManager.getConnection(ARCHIVE_URL);
            Statement statement = conn.createStatement();


            statement.execute("CREATE TABLE IF NOT EXISTS "+tableID+" (name TEXT, category TEXT, duration INTEGER, startTime INTEGER, endTime INTEGER, date TEXT)");

            statement.execute("DELETE FROM " + tableID);
            statement.execute("VACUUM");

            for (Activity activity : archive) {

                String name = activity.getName();

                if (name.contains("\'")) {
                    name = name.replaceAll("\'", "\u2019");
                    //System.out.println("name: "+name);
                }

                //System.out.println("INSERT INTO "+tableID+" (name, category, duration, startTime, endTime, date) VALUES (\'" + name + "\'" + "," + "\'" + activity.getCategory() + "\'" + "," + activity.getDurationSeconds() + "," + activity.getStartTimeSecs() + "," + activity.getEndTimeSecs() + "," + "\'" + activity.getDate() + "\'" + ")");

                statement.execute("INSERT INTO " + tableID + " (name, category, duration, startTime, endTime, date) VALUES (\'" + name + "\'" + "," + "\'" + activity.getCategory() + "\'" + "," + activity.getDurationSeconds() + "," + activity.getStartTimeSecs() + "," + activity.getEndTimeSecs() + "," + "\'" + activity.getDate() + "\'" + ")");
            }

            System.out.println("\n\n\n\n");


            statement.close();
            conn.close();





        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static String saveDay(){

        //System.out.println("saveDay called");

        try {
            Connection annalConn = DriverManager.getConnection(ARCHIVE_URL);
            Statement annalStatement = annalConn.createStatement();


            annalStatement.execute("CREATE TABLE IF NOT EXISTS annals (date text, duplicates INTEGER)");


            ResultSet annalSet = annalStatement.executeQuery("SELECT * FROM annals");

            String lastDate = "";
            int duplicateNum = 0;

            while (annalSet.next()){
                lastDate = annalSet.getString("date");
                duplicateNum = annalSet.getInt("duplicates");
            }




            String newDayName = generateDayName();




            if (newDayName.equals(lastDate)){
                duplicateNum += 2;
                newDayName = newDayName + "_Part_2";

            }
            else {
                if (!lastDate.contains(newDayName)){
                    duplicateNum = 0;

                }
                else {
                    duplicateNum ++;
                    newDayName = newDayName + "_Part_" + duplicateNum;

                }

            }


            annalStatement.execute("INSERT INTO annals VALUES (" +"\'"+newDayName+"\'"+","+duplicateNum+")");



            selectedDay = newDayName;


            return  toTableName(newDayName);


        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }





    public static String toTableName(String day){
        return "y" + day.replaceAll("-", "_");
    }

    public static String toDay(String tableName){
        return tableName.substring(1).replaceAll("_", "-");
    }




    public static String generateDayName(){

        String currentDate = archive.get(0).getDate();

        return currentDate;
    }




    public static void main(String[] args) throws InterruptedException {
        Application.launch(args);
    }


    public static Scene getScheduleCreator() {
        return scheduleCreator;
    }

    public static Scene getStats() {
        return stats;
    }

    public static Scene getBars() {
        return bars;
    }

    public static Scene getTable() {
        return table;
    }

    public static Scene getPiChart() {
        return piChart;
    }

    public static double getScreenWidth() {
        return screenWidth;
    }

    public static double getScreenHeight() {
        return screenHeight;
    }

    public static void setScheduleCreator(Scene scheduleCreator) {
        Main.scheduleCreator = scheduleCreator;
    }
}
