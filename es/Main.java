package de.ude.es;

import de.ude.es.comm.HivemqBroker;
import de.ude.es.TwinEnV5;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalTime;
import static java.util.concurrent.TimeUnit.*;
//import de.ude.es.exampleTwins.TwinWithHeartbeat;
//IntegrationTest
//import de.ude.es.exampleTwins.IntegrationTestTwinForEnV5;
//ENv5TwinStub
//import de.ude.es.exampleTwins.ENv5TwinStub;

public class Main {
    private static final String DOMAIN = "eip://uni-due.de/es";
    private static final String IP = "localhost";
    private static final int PORT = 1883;
    private static HivemqBroker broker;

    public static void main(String[] args) throws InterruptedException {
        broker = new HivemqBroker(DOMAIN, IP, PORT);
        TwinEnV5 twin = new TwinEnV5("sub");
        Timer timer = new Timer();
        LocalTime now = LocalTime.now();
        LocalTime limit = LocalTime.parse("20:00");
        LocalTime limit2 = LocalTime.parse("08:00");
        // Your code here
        twin.bind(broker);
        twin.Subscribing("Temperature");
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                   LocalTime now = LocalTime.now();
                   if(now.isAfter(limit)||now.isBefore(limit2)) {
                       twin.Publishing("Delay", 1800000);
                   }else{
                       twin.Publishing("Delay", 60000);
                   }
                }
            }, 2 * 1000, 30*60*1000);
    }

    }
     /*   if (now.isBefore(limit)) {
            twin.Publishing("Delay", 1000);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    LocalTime now = LocalTime.now();
                    if (now.isAfter(limit)) {
                        twin.Publishing("Delay", 10000);
                    }
                }
            }, 2 * 1000, 5 * 1000);
        }else{
            twin.Publishing("Delay", 10000);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    LocalTime now = LocalTime.now();
                    if (now.isAfter(limit2)) {
                        twin.Publishing("Delay", 1000);
                    }
                }
            }, 2 * 1000, 5* 1000);*/

//IntegrationTest
//twin.startSubscribing("Temperature");
//twin.startPublishing(1000);
//ENv5TwinStub
//ENv5TwinStub twin = new ENv5TwinStub ("sub");
//twin.activateLED(1);

        //Timer timer = new Timer();
        //LocalTime now = LocalTime.now();
        //LocalTime limit = LocalTime.parse("20:00");
        //LocalTime limit2 = LocalTime.parse("08:00");


