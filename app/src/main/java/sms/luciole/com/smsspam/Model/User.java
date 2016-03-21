package sms.luciole.com.smsspam.Model;

/**
 * Created by Luciole on 15/03/2016.
 */
public class User {
    private int id;
    private String Name;
    private String Number;

    public User(){}
    public User(int id, String name, String number){
        this.id = id;
        this.Name = name;
        this.Number = number;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return this.Name;
    }

    public void setName(String name){
        this.Name = name;
    }

    public String getNumber(){
        return this.Number;
    }

    public void setNumber(String number){
        this.Number = number;
    }
}
