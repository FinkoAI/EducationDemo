package ru.sibhtc.educationdemo.models;

/**
 * Created by Антон on 17.09.2015.
 **/
public class Student {
    public String Name;
    public String Surname;
    public String Patronimity;
    public String Qualification;
    public int    Age;

    public Student(String name, String surname, String patronimity, String qualification, int age){
        Name = name;
        Surname = surname;
        Patronimity = patronimity;
        Qualification = qualification;
        Age = age;
    }

    public String GetShortName(){
        return Surname + " " + Name.toCharArray()[0] + "." + Patronimity.toCharArray()[0] + ".";
    }

    public Student(){

    }
}
