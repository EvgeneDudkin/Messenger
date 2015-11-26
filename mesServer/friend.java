import org.json.JSONObject;

/**
 * Created by Kirill2 on 16.11.2015.
 */
public class friend {
    public int Id;
    public String Login;
    public String FirstName;
    public String LastName;

    public friend(JSONObject fr) {
        Login = fr.getString("login");
        FirstName = fr.getString("firstName");
        LastName = fr.getString("lastName");
        Id = fr.getInt("id");
    }
}
