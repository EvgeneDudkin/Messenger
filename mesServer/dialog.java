import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by Kirill2 on 16.11.2015.
 */
public class dialog {
    public int Id;
    public String Name;

    public dialog(JSONObject fr) {
        Name = fr.isNull("name") ? "" : fr.getString("name");
        Id = fr.getInt("id");
    }
}
