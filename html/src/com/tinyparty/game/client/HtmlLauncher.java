package com.tinyparty.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.github.czyzby.websocket.GwtWebSockets;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;

public class HtmlLauncher extends GwtApplication {

        // USE THIS CODE FOR A FIXED SIZE APPLICATION
        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        }
        // END CODE FOR FIXED SIZE APPLICATION

        // UNCOMMENT THIS CODE FOR A RESIZABLE APPLICATION
        // PADDING is to avoid scrolling in iframes, set to 20 if you have problems
        // private static final int PADDING = 0;
        // private GwtApplicationConfiguration cfg;
        //
        // @Override
        // public GwtApplicationConfiguration getConfig() {
        //     int w = Window.getClientWidth() - PADDING;
        //     int h = Window.getClientHeight() - PADDING;
        //     cfg = new GwtApplicationConfiguration(w, h);
        //     Window.enableScrolling(false);
        //     Window.setMargin("0");
        //     Window.addResizeHandler(new ResizeListener());
        //     cfg.preferFlash = false;
        //     return cfg;
        // }
        //
        // class ResizeListener implements ResizeHandler {
        //     @Override
        //     public void onResize(ResizeEvent event) {
        //         int width = event.getWidth() - PADDING;
        //         int height = event.getHeight() - PADDING;
        //         getRootPanel().setWidth("" + width + "px");
        //         getRootPanel().setHeight("" + height + "px");
        //         getApplicationListener().resize(width, height);
        //         Gdx.graphics.setWindowedMode(width, height);
        //     }
        // }
        // END OF CODE FOR RESIZABLE APPLICATION

        @Override
        public ApplicationListener createApplicationListener () {
                // Initiating web sockets module
                GwtWebSockets.initiate();
                return new TinyParty();
        }
}