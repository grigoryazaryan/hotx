package app.hotx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Grigory Azaryan on 10/7/18.
 */

@Entity
public class Category  implements Serializable {
    @Id
    @SerializedName("_idd")
    public long id;
    @SerializedName("id")
    String catId;
    String name;
    String imageUrl;

    public String getId() {
        return catId;
    }

    public void setId(String id) {
        this.catId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
