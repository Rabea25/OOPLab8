package org.example;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class JsonDatabaseManager {
    private static final String usersFile = "users.json";
    private static final String coursesFile = "courses.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try(FileReader r = new FileReader(usersFile)){
            JsonArray arr = JsonParser.parseReader(r).getAsJsonArray();
            for(JsonElement j : arr){
                JsonObject o = j.getAsJsonObject();

                if(o.get("role").getAsString().equals("student")) users.add(gson.fromJson(o, Student.class));
                else if(o.get("role").getAsString().equals("instructor")) users.add(gson.fromJson(o, Instructor.class));
                else users.add(gson.fromJson(o, Admin.class));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void writeUsers(List<User> users){
        try(FileWriter w = new FileWriter(usersFile)){
            gson.toJson(users, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        try (FileReader r = new FileReader(coursesFile)) {
            JsonArray arr = JsonParser.parseReader(r).getAsJsonArray();
            for (JsonElement j : arr) {
                JsonObject o = j.getAsJsonObject();
                courses.add(gson.fromJson(o, Course.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static void writeCourses(List<Course> courses){
        try(FileWriter w = new FileWriter(coursesFile)){
            gson.toJson(courses, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
