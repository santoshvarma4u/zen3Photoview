package zen3.com.photoview.models;

import java.util.List;

/**
 * Created by tskva on 12/5/2017.
 */

public class PhotoResponse {
    String current_page;

    List<Photos> photos;

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }

    public String getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(String current_page) {
        this.current_page = current_page;
    }


}
