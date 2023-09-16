package cn.xeblog.plugin.tools;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.xeblog.commons.util.ThreadUtils;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.ui.MainWindow;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anlingyi
 * @date 2022/8/5 6:18 上午
 */
public abstract class AbstractPanelComponent {

    protected final JPanel mainPanel;

    private JFrame window;

    private int opacity = 30;

    public AbstractPanelComponent(boolean initialized) {
        this.mainPanel = MainWindow.getInstance().getRightPanel();
        if (initialized) {
            initialized();
        }
    }

    private void initialized() {
        initMainPanel();
        init();
        this.showMainPanel();

        ConsoleAction.gotoConsoleLow(true);
    }

    /**
     * 初始化
     */
    protected abstract void init();

    protected JComponent getComponent() {
        return null;
    }

    protected final void showMainPanel() {
        this.closeWindow();
        this.initMainPanel();

        this.mainPanel.setLayout(new BorderLayout());
        JComponent component = this.getComponent();
        if (component != null) {
            Dimension minimumSize = this.getComponent().getMinimumSize();
            this.mainPanel.setMinimumSize(new Dimension((int) minimumSize.getWidth(), (int) (minimumSize.getHeight() + 50)));
            this.mainPanel.add(this.getComponent(), BorderLayout.CENTER);
        }

        JPanel panel = new JPanel();
        JButton switchButton = new JButton("Free!");
        switchButton.addActionListener(e -> openWindow());
        panel.add(switchButton);
        this.mainPanel.add(panel, BorderLayout.SOUTH);

        this.mainPanel.updateUI();
    }

    protected final void openWindow() {
        this.initWindow();
        this.addWindowListener();

        JPanel buttonPanel = this.getWindowButtonPanel();
        this.window.add(buttonPanel, BorderLayout.SOUTH);

        JComponent component = this.getComponent();
        if (component != null) {
            Dimension minimumSize = component.getMinimumSize();
            this.window.setSize((int) minimumSize.getWidth(), (int) (minimumSize.getHeight() + 50));
            this.window.add(component, BorderLayout.CENTER);
        }

        this.window.setOpacity(opacity / 100.0f);
        this.window.setVisible(true);
    }

    private void initWindow() {
        this.close();

        this.window = new JFrame();
        this.window.setLocationRelativeTo(null);
        this.window.setUndecorated(true);
        this.window.setLayout(new BorderLayout());
        this.window.setAlwaysOnTop(true);
        this.window.setResizable(true);
    }

    private JPanel getWindowButtonPanel() {
        JPanel panel = new JPanel();
        panel.setSize(200, 80);

        JSlider transparencySlider = new JSlider(JSlider.HORIZONTAL, 10, 100, 100);
        transparencySlider.setPreferredSize(new Dimension(100, 20));
        transparencySlider.setMajorTickSpacing(10);
        transparencySlider.setMinorTickSpacing(1);
        transparencySlider.setPaintTicks(false);
        transparencySlider.setPaintLabels(false);
        transparencySlider.setValue(opacity);

        transparencySlider.addChangeListener(e -> {
            int value = transparencySlider.getValue();
            opacity = value;
            window.setOpacity(value / 100.0f);
            window.setVisible(true);
        });

        JButton switchButton = new JButton("Default");
        switchButton.addActionListener(e -> showMainPanel());

        panel.add(transparencySlider);
        panel.add(switchButton);

        return panel;
    }

    private void addWindowListener() {
        final AtomicInteger cursorRegion = new AtomicInteger();
        final Point clickPoint = new Point();
        final AtomicBoolean isResizing = new AtomicBoolean();

        this.window.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int cursorType = getCursorRegion(e.getPoint());
                isResizing.set(cursorType > 0);
                cursorRegion.set(cursorType);
                clickPoint.setLocation(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isResizing.set(false);
            }
        });

        this.window.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (isResizing.get()){
                    resizeWindow(e.getPoint(), clickPoint, cursorRegion.get());
                } else {
                    moveWindow(e.getLocationOnScreen(), clickPoint);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int newCursorRegion = getCursorRegion(e.getPoint());
                window.setCursor(Cursor.getPredefinedCursor(newCursorRegion));
            }
        });
    }

    private void moveWindow(Point screenPoint, Point clickPoint) {
        int x = screenPoint.x - clickPoint.x;
        int y = screenPoint.y - clickPoint.y;
        window.setLocation(x, y);
    }

    private void resizeWindow(Point currentPoint, Point clickPoint, int cursorRegion) {
        int xOffset = currentPoint.x - clickPoint.x;
        int yOffset = currentPoint.y - clickPoint.y;

        int newX = window.getX();
        int newY = window.getY();
        int newWidth = window.getWidth();
        int newHeight = window.getHeight();

        switch (cursorRegion) {
            case Cursor.NW_RESIZE_CURSOR:
                // 左上角
                newWidth -= xOffset;
                newHeight -= yOffset;
                newX += xOffset;
                newY += yOffset;
                break;
            case Cursor.SW_RESIZE_CURSOR:
                // 左下角
                newWidth -= xOffset;
                newHeight += yOffset;
                newX += xOffset;
                break;
            case Cursor.NE_RESIZE_CURSOR:
                // 右上角
                newWidth += xOffset;
                newHeight -= yOffset;
                newY += yOffset;
                break;
            case Cursor.SE_RESIZE_CURSOR:
                // 右下角
                newWidth += xOffset;
                newHeight += yOffset;
                break;
            case Cursor.W_RESIZE_CURSOR:
                // 左
                newWidth -= xOffset;
                newX += xOffset;
                break;
            case Cursor.E_RESIZE_CURSOR:
                // 右
                newWidth += xOffset;
                break;
            case Cursor.N_RESIZE_CURSOR:
                // 上
                newHeight -= yOffset;
                newY += yOffset;
                break;
            case Cursor.S_RESIZE_CURSOR:
                // 下
                newHeight += yOffset;
                break;
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();

        if (newWidth < 100) {
            newWidth = 100;
        }
        if (newHeight < 100) {
            newHeight = 100;
        }
        if (newWidth > screenWidth - 100) {
            newWidth = screenWidth - 100;
        }
        if (newHeight > screenHeight - 100) {
            newHeight = screenHeight - 100;
        }

        window.setSize(newWidth, newHeight);
        window.setLocation(newX, newY);
        clickPoint.setLocation(currentPoint);
    }

    private int getCursorRegion(Point point) {
        int border = 5;
        int x = point.x;
        int y = point.y;
        int width = window.getWidth();
        int height = window.getHeight();

        if (x < border) {
            return (y < border) ? Cursor.NW_RESIZE_CURSOR : (y > height - border) ? Cursor.SW_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR;
        }

        if (x > width - border) {
            return (y < border) ? Cursor.NE_RESIZE_CURSOR : (y > height - border) ? Cursor.SE_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR;
        }

        if (y < border) {
            return Cursor.N_RESIZE_CURSOR;
        }

        if (y > height - border) {
            return Cursor.S_RESIZE_CURSOR;
        }

        return 0;
    }

    /**
     * 结束
     */
    public void over() {
        invoke(this::close);
    }

    protected JButton getExitButton() {
        JButton exitButton = new JButton("退出");
        exitButton.addActionListener(e -> Command.OVER.exec());
        return exitButton;
    }

    protected final void invoke(Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(runnable);
    }

    protected final void invoke(Runnable runnable, long millis) {
        GlobalThreadPool.execute(() -> {
            spinMoment(millis);
            invoke(runnable);
        });
    }

    protected void spinMoment(long millis) {
        ThreadUtils.spinMoment(millis);
    }

    protected void initMainPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
    }

    private void closeMainPanel() {
        mainPanel.setVisible(false);
        mainPanel.removeAll();
        mainPanel.updateUI();
    }

    private void closeWindow() {
        if (this.window != null) {
            this.window.dispose();
            this.window = null;
        }
    }

    private void close() {
        closeWindow();
        closeMainPanel();
    }

}
