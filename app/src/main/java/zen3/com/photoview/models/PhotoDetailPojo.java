package zen3.com.photoview.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by SantoshT on 12/5/2017.
 */

public class PhotoDetailPojo {

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    Photo photo;


}
