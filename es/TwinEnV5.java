package de.ude.es;

import de.ude.es.twin.JavaTwin;
import de.ude.es.twin.TwinStub;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import static java.lang.Thread.sleep;

public class TwinEnV5 extends JavaTwin {
    private final TwinStub enV5;
    public TwinEnV5(String identifier) {
        super(identifier);
        enV5 = new TwinStub("enV5");
    }

    @Override
    protected void executeOnBind() {
        enV5.bind(endpoint);
    }

    public void Subscribing(String topic) {
        enV5.subscribeForData(topic, posting -> System.out.println(posting.data()));
    }

    public void Publishing(String topic, int Delay) {
        String delay = String.valueOf(Delay);

        //this.publishData("Delay", Delaytime);
                    this.publishData(topic, delay);
    }
}

