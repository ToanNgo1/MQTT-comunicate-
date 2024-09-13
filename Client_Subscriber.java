//package MQTT.Client;

//import java.util.ArrayList;
import java.util.*;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Client_Subscriber {

    public static void main(String[] args) throws InterruptedException {
        //int term_track=1;
        //String return_val="instruction ";
        ArrayList<Integer> inputValues=new ArrayList<>();
        HashMap<String, Integer> command_track = new HashMap<String,Integer>();
        /*command_track.put("subs",1);
        System.out.println(command_track.containsKey("ADD"));
        System.out.println(command_track.containsKey("amsimsicma"));
        System.out.println(command_track);*/
        //String s="Add:542,1231 remove:6542,564 get_summation Review_Old_Commands";
        //String s="Add:542,1231 ";
        //String root=getting_start(s,inputValues,command_track);
        //System.out.println(command_track+"the end");
        //System.out.println(root);
    
        String broker       = "tcp://broker.hivemq.com:1883";   //http://www.mqtt-dashboard.com/index.html
        String clientId     = "SubscriberClient1";

        try {
        	MqttClient sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            
            sampleClient.connect(connOpts);                          //connecting to the broker
            System.out.println("Connected to broker: "+broker);
            
        	
            sampleClient.setCallback(new MqttCallback() {
                
            	public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("\nReceived a Message!" +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + new String(message.getPayload()) +
                            "\n\tQoS:     " + message.getQos() + "\n");	
                   
                    String conver=String.valueOf(message);
                    
                    String root=getting_start(conver,inputValues,command_track);
                    
                    messageSend(topic, root);
                }
                public void messageSend(String topic, String message) throws InterruptedException
                {
                  try{    
                    String content      = message;
                    MqttMessage message_send = new MqttMessage(content.getBytes());
                    message_send.setQos(2);
                    sampleClient.publish("DS341/ResultsFrom/Ngo/A", message_send);
                    System.out.println("Message published");
                  } 
                  catch(MqttException me) {
                    System.out.println("reason "+me.getReasonCode());
                    System.out.println("msg "+me.getMessage());
                    System.out.println("loc "+me.getLocalizedMessage());
                    System.out.println("cause "+me.getCause());
                    System.out.println("excep "+me);
                    me.printStackTrace();
                  }
               }
                
                
                public void connectionLost(Throwable cause) {
                }
                
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
                
            });

                  System.out.println("Message published");
        	
        	
            sampleClient.subscribe("/DS341/TaskTo/Ngo/<A>", 1);    //subscribe to certain topic using QoS 1
            
    		System.out.println("Subscribed");
    		
    		System.out.println("Listening");
            
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    public static String getting_start(String message,ArrayList<Integer> containner,HashMap <String,Integer> command_track)
    {
      //you should have this:ADD:5,4,654,654,56 or ADD:5,5 substract:5,2
      String re=message.replace(":"," ");
      re=re.strip();
      String[] track=re.split(" ");      //ADD|5,4,654.... or ADD|5,5| subtract|5,2
      ArrayList<String> packet=new ArrayList<String>(); 
      int count=1; 
      String output="";
      int counter=0;
      
      if(track.length>=1)
      {
          for(String a: track)
          {
            
            if(count==2)
            {
              packet.add(a);
              count=1;
              if(packet.get(0).toUpperCase().equals("ADD"))
              {
                String[] conver=packet.get(1).split(",");
                counter+=1;
                for(String element: conver)
                {
                  add(element, containner);
                  
                }
                if (counter>=track.length)
                {
                  
                  output+="Added Successfull";
                  update_command_track(packet.get(0),command_track);
                  
                  packet.clear();

                }
                else
                {
                  output+="Added Successfull,";
                  update_command_track(packet.get(0),command_track);
                  
                  packet.clear();
                }
              }
              else if(packet.get(0).toUpperCase().equals("REMOVE"))
              {
                
                String[] conver=packet.get(1).split(",");
                counter+=1;
                for(String element: conver)
                {
                  
                  remove_val(element, containner);
                
                }
                if (counter>=track.length)
                {
                  output+="Remove Successfull";
                  update_command_track(packet.get(0),command_track);
                  packet.clear();
                }
                else
                {
                  output+="Remove Successfull, ";
                  update_command_track(packet.get(0),command_track);
                  packet.clear();
                }
              }
              else
              {
                if (counter>=track.length)
                {
                  counter+=1;
                  output+="no command recognizable";
                  packet.clear();
                }
                else
                {
                  counter+=1;
                  output+="no command recognizable, ";
                  packet.clear();
                }
                
              }
            }
            
            else
            {
              packet.add(a);
             
              count+=1;
              
             

              if(packet.get(0).toUpperCase().equals("GET_SUMMATION"))
              {
                
                String sum=summation(containner);
                counter+=1;
               
                count=1;
                if (counter>=track.length)
                {
                 
                  output+="The summation is "+sum;
                  update_command_track(packet.get(0),command_track);
                  packet.clear();
                }
                else
                {
                  output+="The summation is "+sum+", ";
                  update_command_track(packet.get(0),command_track);
                  packet.clear();
                }
                
              }
              else if (packet.get(0).toUpperCase().equals("Review_Old_Commands".toUpperCase()))
              {
                int maptracker=0;
                counter+=1;
                String out="the folowing is the command been track so far"+"[";
                for(Map.Entry<String,Integer> entry : command_track.entrySet())
                {
                
                  if(maptracker>=1) 
                  {
                  
                    if(maptracker+1<command_track.size())
                    {
                      String key1=entry.getKey();
                      Integer value=entry.getValue();
                      out+=", ";
                      out+=key1+": "+value;
                      maptracker+=1;
                    }
                    else
                    {
                      String key1=entry.getKey();
                      Integer value=entry.getValue();
                      out+=", ";
                      out+=key1+": "+value;
                      maptracker+=1;
                    }
                  }
                  else
                  { 
                    String key1=entry.getKey();
                    Integer value=entry.getValue();
                    out+=key1+": "+value;
                    maptracker+=1;
                  }
                }
                out+="]";
                if (counter>=track.length)
                {
                  
                  output+= out;
                  packet.clear();
                }
                else
                {
               
                  output+= out+", ";
                  packet.clear();
                }
              }
              else if(!(packet.get(0).toUpperCase().equals("ADD"))&!(packet.get(0).toUpperCase().equals("REMOVE")))
              {
                counter+=1;
                output+="there are no such command. ";
                packet.clear();
              }
              counter+=1;
            }
          }
      }
      else if((track.length<1))
      {
        counter+=1;
        return "no command recognizable";
      } 
      return output;
    }
    
    public static void update_command_track(String command,HashMap <String,Integer> command_track)
    {
      if(command_track.containsKey(command))
      {
        Integer extract=command_track.get(command);
        extract+=1;
        command_track.put(command,extract);
      }
      else
      {
        command_track.put(command,1);
      }
     
    }
    public static String summation(ArrayList <Integer> array)
    { 
        if(array.size()==0)
        {
            return "{null}";
        }
        Integer value=0;
        for(Integer i : array)
        {
            value+=i;
        }
        String output=String.valueOf(value);
        return output;
    }

    public static void add(String value,ArrayList <Integer> array)
    {
        int value_int=Integer.parseInt(value);
        array.add(value_int);
      
    }

    public static void remove_val(String value, ArrayList <Integer> array)
    {
     
        Integer val=Integer.parseInt(value);
        while(true)
        {
            if(array.contains(val))
            {
                array.remove(val);
            }
            else
            {
               break;
            }
        }
    }
}