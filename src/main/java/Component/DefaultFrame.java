package Component;

import javax.swing.*;
import java.awt.*;

public class DefaultFrame extends JFrame {

    private Dimension dimension;

    private Container container;

    /* **************************************************************************************************
     **
     **  Default Frame Init
     **
     ****************************************************************************************************/

    public DefaultFrame() {

        setLayout(null);;
        setDefaultCloseOperation (EXIT_ON_CLOSE);
        setResizable(false);

        //------------------------ Container ------------------------------------

        container = getContentPane();

    }

    /* **************************************************************************************************
     **
     **  Default Frame Init
     **
     ****************************************************************************************************/

    public void setDimension() {

        dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);

    }

    /* **************************************************************************************************
     **
     **  Getter And Setter
     **
     ****************************************************************************************************/

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

}
