package dobbleproject.dobble;

import android.os.Bundle;
import android.os.Message;

public final class MessageHelper {
    public static Message createDebugMessage(String text) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("msg", text);
        msg.what = MessageType.DEBUG;
        msg.setData(bundle);
        return msg;
    }
}
