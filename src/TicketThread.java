
/*
   This is the core functionality of a Java desktop application that gets food orders 
   (JSON) from an API call to a web application and subsequently prints the orders to 
   drive the food production process.
*/

import JSON.JSONArray;
import JSON.JSONException;
import JSON.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class TicketThread extends Thread{
   int location = 1;
   JTextArea tf;
   JList jl;
   JLabel iconLabel;
   String folder;
   String nl = System.getProperty("line.separator");
   
    public TicketThread(int location, JTextArea tf, JList jl, JLabel iconLabel, String folder){
        this.location = location;
        this.tf = tf;
        this.jl = jl;
        this.iconLabel = iconLabel;
        File f = new File(folder);
        if (!f.exists()){
            f.mkdir();
        }
        
        this.folder = folder;
    }

    public void run(){
     
     while(true){
         
         /* populate ticket window */
     File f = new File(folder);
     String [] l = f.list();
     if (l != null) jl.setListData(l);
         
     try { 
         
         HttpClient httpclient = new DefaultHttpClient();
         HttpGet httpget = new HttpGet("https://www.haven-ethic.com/pizza/index.php/order/get_one_ticket?key=3c6e0b8a9c15224a8228b9a98ca1531d&location="+location); 
  
            iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/sync.png")));
            HttpResponse response = httpclient.execute(httpget);
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                            response.getEntity().getContent()));
            String line = "";
            Order ticket;
            JSONObject myjson;
            String stat = "";
            
            while ((line = rd.readLine()) != null) {
                    //System.out.println(line);
                    if (line.equals("Encoding Error")){
                        stat = "Encoding Error - Contact support."+nl;
                        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/Error.png")));
                        tf.append(stat);
                        break;
                    }
                    if (line.equals("*")){ //no orders
                        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/OK.png")));
                        break;
                    }
                                                            
                    ticket = new Order(false);
                    
                    try{
                        
                        myjson = new JSONObject(line);
                        
                        JSONObject order = myjson.getJSONObject("order");
                        ticket.setOrder_id(order.getString("id"));
                        ticket.setPhone(order.getString("phone"));
                        ticket.setCarrier(order.getString("carrier"));
                        ticket.setCustomer_name(order.getString("name"));
                        ticket.setDelivery(order.getInt("delivery"));
                        ticket.setSubmitted(order.getString("submitted"));
                        ticket.setDelivery_time(order.getString("delivery_time"));
                        
                        stat = ticket.getOrder_id() +" | "+ ticket.getCustomer_name() +" | "+  ticket.getPhone() +" | "+ticket.getDelivery_time()+System.getProperty("line.separator");
                        
                        //record ticket to a file so the user can reprint
                        String file = folder+"/"+ticket.getOrder_id()+"_"+ticket.getPhone()+"_"+ticket.getCustomer_name()+".myp";
                        this.writeFile(file, line, false, tf);
                        
                        //Send verification that we received
                        HttpGet verify = new HttpGet("https://www.haven-ethic.com/pizza/index.php/order/mark_as_printed?key=3c6e0b8a9c15224a8228b9a98ca1531d&order_id="+ticket.getOrder_id()); 
                        httpclient.execute(verify);

                        
                        /* Get the main food items */
                        JSONArray order_items = myjson.getJSONArray("items");
                        int size = order_items.length();                            
                        Item item;
                        for (int i = 0; i < size; i++) {
                            item = new Item();
                            JSONObject tmpJSON = order_items.getJSONObject(i);
                            item.setId(tmpJSON.getString("id"));
                            item.setName(tmpJSON.getString("name"));
                            item.setPrice(tmpJSON.getString("price"));                                
                            ticket.addItem(item);
                        }
                        
                        /* Get the toppings */
                        JSONArray item_toppings = myjson.getJSONArray("toppings");
                        size = item_toppings.length();                            
                        Topping topping;
                        for (int i = 0; i < size; i++) {
                            topping = new Topping();
                            JSONObject tmpJSON = item_toppings.getJSONObject(i);
                            topping.setItem_id(tmpJSON.getString("item_id"));
                            topping.setComment(tmpJSON.getString("comment"));
                            topping.setPrice(tmpJSON.getString("price"));                                
                            ticket.addTopping(topping);
                        }                        
                        
                    }catch(JSONException e){
                        tf.append("Error reading ticket information"+nl);
                        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/Error.png")));
                        e.printStackTrace();
                    }
                  
                    ticket.go(); //print the ticket
                    tf.append(stat);
                    httpclient.getConnectionManager().shutdown();
                    iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/OK.png")));
            }
            
      } catch(IOException ex){ 
          iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/Error.png")));
          tf.append("Internet Connection Error"+nl);
          ex.printStackTrace(); }

      try {
         Thread.sleep(20000);	  // 1000 == 1 second
      }
        catch (Exception e) {
            iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/Error.png")));
            tf.append("Sleep Error"+nl);
            e.printStackTrace();}	   // this never happen... nobody check for it
    }
     
    }
 
    
     private boolean writeFile (String filename, String text, boolean append, JTextArea tf){
        
        
        try {
                
            File data = new File (filename);
            
            PrintWriter out = new PrintWriter(
                    new BufferedWriter( 
                     new FileWriter (data, append ) ) );
                //write text
            
            
                out.write( text );
                 
            out.close();
            out = null;
            return true;
            
        } catch (Exception e) {
            
            tf.append("Error recording ticket history to: " + filename + System.getProperty("line.separator"));
            
            return false;}        
              
    }
    
    
   
}
