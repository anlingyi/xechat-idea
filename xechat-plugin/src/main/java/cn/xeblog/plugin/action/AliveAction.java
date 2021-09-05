package cn.xeblog.plugin.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author anlingyi
 * @date 2021/9/4 12:39 下午
 */
public class AliveAction {

    /**
     * 是否正在运行
     */
    private static boolean running;

    /**
     * 是否开启该功能
     */
    private static boolean enabled;

    /**
     * 工作时间，单位：秒
     */
    private static int workTime = 1 * 60 * 60;

    /**
     * 休息时间，单位：秒
     */
    private static int restTime = 10 * 60;

    /**
     * 下一次提醒的时间（精确到秒的时间戳）
     */
    private static long nextStartTime;

    private static final String GAN = "我只想搞钱";

    public static void setWorkTime(int second) {
        if (second < 0) {
            return;
        }

        workTime = second;
    }

    public static void setRestTime(int second) {
        if (second < 0) {
            return;
        }

        restTime = second;
    }

    public static void setEnabled(boolean bool) {
        if (bool) {
            if (!enabled) {
                setNextStartTime();
                run();
            }
        } else {
            running = false;
        }

        enabled = bool;
    }

    private static long getNowTimeSecond() {
        return System.currentTimeMillis() / 1000;
    }

    public static boolean flushNextStartTime() {
        if (getNowTimeSecond() > nextStartTime) {
            setNextStartTime();
            return true;
        }

        return false;
    }

    private static void setNextStartTime() {
        nextStartTime = getNowTimeSecond() + workTime;
    }

    public static long getNextStartTime() {
        return nextStartTime;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isContinued() {
        return enabled && running;
    }

    private static void run() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!enabled) {
                    timer.cancel();
                    return;
                }

                if (getNowTimeSecond() == nextStartTime) {
                    ApplicationManager.getApplication().invokeLater(() -> areYouOk());
                }
            }
        }, 0, 1000);
    }

    private static void areYouOk() {
        if (running) {
            return;
        }

        AliveDialogWrapper alive = new AliveDialogWrapper(restTime);
        alive.show();
    }

    private static class AliveDialogWrapper extends DialogWrapper {

        JTextField input = new JTextField();
        JButton button = new JButton();
        JLabel timeLabel = new JLabel("00:00:00");

        int second;

        public AliveDialogWrapper(int second) {
            super(true);

            this.second = second;

            setTitle("Are You Ok ?");
            setOKActionEnabled(false);
            setResizable(false);
            setCrossClosesWindow(false);

            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            int width = 200;
            int height = 130;
            int widthHalf = width / 2;

            JPanel main = new JPanel();
            main.setLayout(null);
            main.setPreferredSize(new Dimension(width, height));

            JLabel tipsLabel = new JLabel("休息一下，马上回来...");
            tipsLabel.setFont(new Font(null, 0, 14));
            int tipsWidth = getFontWidth(tipsLabel.getFont(), tipsLabel.getText());
            tipsLabel.setBounds(widthHalf - tipsWidth / 2, 0, tipsWidth, 20);

            timeLabel.setFont(new Font(null, 1, 18));
            int timeWidth = getFontWidth(timeLabel.getFont(), timeLabel.getText());
            timeLabel.setBounds(widthHalf - timeWidth / 2, 20, timeWidth, 35);

            input.setBounds(10, 60, 180, 30);

            button.setBounds(60, 95, 80, 35);
            button.setText("肝！");
            button.setForeground(new Color(0xDB4141));
            button.addActionListener(e -> {
                if (GAN.equals(input.getText())) {
                    stop(true);
                }
            });

            main.add(tipsLabel);
            main.add(timeLabel);
            main.add(input);
            main.add(button);

            return main;
        }

        private final FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(),
                true, true);

        private int getFontWidth(Font font, String text) {
            return (int) Math.ceil(font.getStringBounds(text, fontRenderContext).getWidth());
        }

        @Override
        protected @NotNull Action[] createActions() {
            return new Action[]{};
        }

        private void countdown() {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int time = second;

                @Override
                public void run() {
                    if (--time < 0 || !isContinued()) {
                        timer.cancel();
                        stop(false);

                        input.setEnabled(false);
                        button.setEnabled(true);
                        button.setText("I'm Fine.");
                        button.setForeground(new Color(0x21BD21));
                        button.addActionListener(e -> stop(true));

                        return;
                    }

                    int hour = time / 60 / 60;
                    int minute = (time / 60 - hour * 60) % 60;
                    int second = time - hour * 60 * 60 - minute * 60;

                    StringBuilder sb = new StringBuilder();
                    if (hour < 10) {
                        sb.append("0").append(hour);
                    } else {
                        sb.append(hour);
                    }
                    sb.append(":");
                    if (minute < 10) {
                        sb.append("0").append(minute);
                    } else {
                        sb.append(minute);
                    }
                    sb.append(":");
                    if (second < 10) {
                        sb.append("0").append(second);
                    } else {
                        sb.append(second);
                    }

                    timeLabel.setText(sb.toString());
                }
            }, 0, 1000);
        }

        private void stop(boolean exit) {
            running = false;

            if (exit) {
                setNextStartTime();
                ApplicationManager.getApplication().invokeLater(() -> close(0));
            }
        }

        public void show() {
            if (running) {
                return;
            }

            running = true;
            countdown();
            super.show();
        }
    }

}
