import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class WindowOpeningAdapter extends WindowAdapter {
    @Override
    public void windowOpened(WindowEvent e) {
            System.out.println("Window is opened");
    }
}