package dobbleproject.dobble;


// https://stackoverflow.com/questions/14457711/android-listening-for-variable-changes/22561751
//
public class PlayAgainFlag {
    private boolean flag = false;
    private ChangeListener listener;

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
        if (listener != null && flag) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
