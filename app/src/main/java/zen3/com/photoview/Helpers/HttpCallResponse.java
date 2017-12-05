package zen3.com.photoview.Helpers;

/**
 * Created by tskva on 12/5/2017.
 */

public interface HttpCallResponse {

    void OnSuccess(Object obj);

    void OnFailure(int ErrorCode);

}
