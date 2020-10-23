package scheduleArchive.Utility;

import scheduleArchive.Main;

import java.util.ArrayList;

public class EditLog {

    ArrayList<ArrayList<Activity>> logs = new ArrayList<>();

    int cursorPos = -1;


    public void addLog(ArrayList<Activity> archive){
        if (cursorPos < logs.size()){
            cursorPos ++;
            archive = cloneActivityList(archive);
            logs.add(cursorPos, archive);
        }
        else {
            logs.add(archive);
        }


    }

    public void wipe(){
        cursorPos = 0;
        logs.clear();
    }

    public ArrayList<Activity> mostRecentLog(){

        if (canGoBack()){
            cursorPos --;
            return cloneActivityList(logs.get(cursorPos));

        }
        else {
            return null;
        }

    }

    public ArrayList<Activity> forwardLog(){
        if (canGoForward()){
            cursorPos ++;
            return cloneActivityList(logs.get(cursorPos));
        }
        else {
            return null;
        }
    }


    public boolean canGoBack(){
        return cursorPos > 0;
    }

    public boolean canGoForward(){
        return cursorPos < logs.size() - 1 && cursorPos >= 0;
    }


    public String toString(){

        String allEdits = "";


        int count = 0;
        for (ArrayList<Activity> log : logs){

            count ++;

            String editSummary = "";

            for (Activity loggedActivity : log){

                boolean edited = true;

                for (Activity activity : Main.archive){
                    if (activity.getDurationSeconds() == loggedActivity.getDurationSeconds()
                            && activity.getCategory().equals(loggedActivity.getCategory())
                            && activity.getName().equals(loggedActivity.getName())
                            && activity.getStartTimeSecs() == loggedActivity.getStartTimeSecs()
                            && activity.getEndTimeSecs() == loggedActivity.getEndTimeSecs()){
                        edited = false;
                    }
                }

                if (edited){
                    editSummary += "log"+count+"\'s changes: {" + loggedActivity.getName() + ", ";
                }
            }
            allEdits += editSummary + "} \n";
        }


        String logState = "";

        for (int i = 0; i < logs.size(); i++){

            if (i == cursorPos){
                logState += "|" + i + "|" + " ";
            }
            else {
                logState += i + " ";
            }

        }

        return  allEdits + "\n\n" + "number of logs: " + count + "\n\n" + logState;
    }

    public static ArrayList<Activity> cloneActivityList(ArrayList<Activity> list){

        ArrayList<Activity> cloneList = new ArrayList();

        for (Activity activity : list){
            Activity cloneActivity = new Activity(activity.getName(),
                    activity.getCategory(),
                    activity.getDurationSeconds(),
                    activity.getStartTimeSecs(),
                    activity.getEndTimeSecs(),
                    activity.getDate());

            cloneList.add(cloneActivity);
        }


        return cloneList;
    }


}
