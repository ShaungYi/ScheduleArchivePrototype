package scheduleArchive.Scenes;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scheduleArchive.Main;
import scheduleArchive.Utility.PastActivity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;

public class LoadScreen {

    @FXML
    AnchorPane motherPane;

    static boolean loadingComplete = false;


    public void initialize(){
        loadAllPastActivities();

    }

    @FXML
    private void goToCreator(){

        if (loadingComplete){
            Stage stage = (Stage) motherPane.getScene().getWindow();
            stage.setScene(Main.scheduleCreator);
        }

    }


    public static Comparator<PastActivity> pastActivityComparator = new Comparator<PastActivity>() {
        @Override
        public int compare(PastActivity p1, PastActivity p2) {
            int relevanceScore1 = p1.getFrequency();
            int relevanceScore2 = p2.getFrequency();

            if (p1.getMostRecentDate().equals(Main.selectedDay)){
                relevanceScore1 += 50;
            }
            if (p2.getMostRecentDate().equals(Main.selectedDay)){
                relevanceScore2 += 50;
            }

            if (relevanceScore1 > relevanceScore2){
                return 1;
            }
            else if (relevanceScore1 < relevanceScore2){
                return -1;
            }
            else {
                return 0;
            }
        }
    };


    public static void loadAllPastActivities(){

        try {
//
            Connection conn = DriverManager.getConnection(Main.ARCHIVE_URL);
            Statement statement = conn.createStatement();


            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            ResultSet days = metaData.getTables(null, null, "%", types);


            days.next();
            while (days.next()) {
                String tableID = days.getString("TABLE_NAME");
                System.out.println(tableID);

                ResultSet activities = statement.executeQuery("SELECT * FROM " + tableID);

                while (activities.next()){

                    String activityName = activities.getString("name");
                    String activityCategory = activities.getString("category");
                    int activityDuration = activities.getInt("duration");
                    String activityDate = activities.getString("date");

                    loadActivity(activityName, activityCategory, activityDuration, activityDate);


                }
            }

            Main.pastActivities.sort(pastActivityComparator);

            for (PastActivity pastActivity : Main.pastActivities){
                System.out.println(pastActivity.getName() + ": " + pastActivity.getFrequency());
            }


            loadingComplete = true;

            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }




    public static void loadActivity(String activityName, String activityCategory, int activityDuration, String activityDate){


        boolean activityAlreadyExists = false;

        if (!Main.pastActivities.isEmpty()){
            for (PastActivity pastActivity : Main.pastActivities){
                if (pastActivity.getName().toLowerCase().equals(activityName.toLowerCase())){

                    pastActivity.setFrequency(pastActivity.getFrequency() + 1);
                    pastActivity.setMostRecentDate(activityDate);
                    pastActivity.setNetDuration(pastActivity.getNetDuration() + activityDuration);
                    activityAlreadyExists = true;

                    break;
                }
            }
        }


        if (!activityAlreadyExists){
            Main.pastActivities.add(new PastActivity(activityName, activityCategory, activityDate, 1, activityDuration));

        }
    }




    public static ArrayList<String> browsPastActivities(String query){

        ArrayList<String> results = new ArrayList<>();

        for (PastActivity pastActivity : Main.pastActivities){
            String name = pastActivity.getName();

            name = name.trim();

            if (name.toLowerCase().contains(query.toLowerCase().trim())
            && !name.equals("undefined")
            && !name.equals("new")
            && !name.equals("no data")
            && !results.contains(name)){
                results.add(name);
            }
        }

        return results;
    }








}
