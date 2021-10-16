package main.ui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.*;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefMessageRouterHandlerAdapter;

public class UI extends JFrame {

    final String workingDirectory = System.getProperty("user.dir");
    final String url = "file:///" + workingDirectory + "/public/index.html";

    final CefBrowser browser;
    final JFrame _this;

    public UI(String title) {
        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(CefAppState state) {
                if (state == CefAppState.TERMINATED) System.exit(0);
            }
        });

        final boolean useOSR = false;
        final boolean isTransparent = false;
        final CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = false;

        final CefApp cefApp = CefApp.getInstance(settings);

        final CefClient client = cefApp.createClient();

        CefMessageRouter msgRouter = CefMessageRouter.create();
        msgRouter.addHandler(new MessageRouterHandler(), true);
        client.addMessageRouter(msgRouter);

        browser = client.createBrowser(url, useOSR, isTransparent);
        final Component browserUI = browser.getUIComponent();

        this.setTitle(title);
        this.getContentPane().add(browserUI, BorderLayout.CENTER);
        this.pack();
        this.setSize(1000, 600);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
            }
        });

        _this = this;
        // Wait until UI is loaded
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
                _this.setVisible(true);
                //String jscode = "document.getElementById(\"response\").innerHTML = \"Init done\"";
                //browser.executeJavaScript(jscode, browser.getURL(), 0);
            }
        }).start();

    }

    class MessageRouterHandler extends CefMessageRouterHandlerAdapter {
        @Override
        public boolean onQuery(CefBrowser browser, CefFrame frame, long query_id, String request, boolean persistent, CefQueryCallback callback) {
            //System.out.println(request);
            String result;
            try {
                Method classMethod = JavascriptInterface.class.getMethod("print", String.class);
                JavascriptInterface classInstance = new JavascriptInterface();
                result = (String) classMethod.invoke(classInstance, request);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | 
                        NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
                result = e.toString();
            }
            callback.success(result);
            return true;
        }
    }
    
}