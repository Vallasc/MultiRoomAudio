package main.ui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;

public class UI extends JFrame {

    final String workingDirectory = System.getProperty("user.dir");
    final String url = "file:///" + workingDirectory + "/public/index.html";

    final CefBrowser browser;
    //final JFrame _this;
    final BindingsHandler bindingsHandler;

    public UI(String title, Class<?> javascriptInterface) {
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
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_DEFAULT;

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
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
            }
        });

        //_this = this;
        // Wait until UI is loaded
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
                //_this.setVisible(true);
                //String jscode = "document.getElementById(\"response\").innerHTML = \"Init done\"";
                //browser.executeJavaScript(jscode, browser.getURL(), 0);
                bindingsHandler.injectJavascriptInterface(browser);
            }
        }).start();

        bindingsHandler = new BindingsHandler(javascriptInterface);
    }

    class MessageRouterHandler extends CefMessageRouterHandlerAdapter {
        final ObjectMapper objectMapper = new ObjectMapper();
        @Override
        public boolean onQuery(CefBrowser browser, CefFrame frame, long query_id, String request, boolean persistent, CefQueryCallback callback) {
            try {
                MethodDTO fun = objectMapper.readValue(request, MethodDTO.class);
                callback.success( objectMapper.writeValueAsString(bindingsHandler.callMethod(fun)) );
            } catch (IOException e) {
                e.printStackTrace();
                callback.success(e.toString());
            }
            return true;
        }
    }
    
}