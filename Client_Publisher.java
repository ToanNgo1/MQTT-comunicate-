//package MQTT.Client;

import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Client_Publisher {

    public static void main(String[] args) throws InterruptedException {

        String broker       = "tcp://broker.hivemq.com:1883";   //http://www.mqtt-dashboard.com/index.html
        String clientId     = "PublisherClient";
       // while(true){
        try {
            
            //boolean satict stop=false;
            
            MqttClient sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            
            sampleClient.connect(connOpts);                          //connecting to the broker
            System.out.println("Connected to broker: "+broker);
            sampleClient.subscribe("DS341/ResultsFrom/Ngo/B", 1);
            sampleClient.subscribe("DS341/ResultsFrom/Ngo/A", 2);

            String input = "";
            String cl="";
            Scanner in = new Scanner(System.in);
            System.out.print("enter your command her:");
            while(!input.equals("exit"))
            {
                
                input=in.nextLine();
                System.out.println("select the client A/B:");
                cl=in.nextLine();
                if(input.equals("exit"))
                    {
                        break;
                    }
                if(cl.toUpperCase().equals("A")) 
                {
                    System.out.println("Publishing message!");
                    String content      = input;
                    MqttMessage message = new MqttMessage(content.getBytes());    //build the message
                    message.setQos(1);                                            //set QoS level
                    sampleClient.publish("/DS341/TaskTo/Ngo/<A>", message);
                    System.out.println("Message published");
                   
                }
                else
                {
                    System.out.println("Publishing message!");
                    String content      = input;
                    MqttMessage message = new MqttMessage(content.getBytes());    //build the message
                    message.setQos(1);                                            //set QoS level
                    sampleClient.publish("/DS341/TaskTo/Ngo/<B>", message);
                    System.out.println("Message published");
                }
            
            sampleClient.setCallback(new MqttCallback()
            {   
            	public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("\nReceived a Message!" +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + new String(message.getPayload()) +
                            "\n\tQoS:     " + message.getQos() + "\n");
                    System.out.print("enter new command here:");	
                
                }
                public void connectionLost(Throwable cause) {
                }
                
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
                
            }
            );
            }
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) 
        {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}