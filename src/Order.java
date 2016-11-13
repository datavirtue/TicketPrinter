
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class Order implements Printable {

    
    private String order_id;
    private String submitted;
    private String delivery_time;
    private String customer_name;
    private String phone;
    private String carrier;
    private String delivery;
    
    private ArrayList items = new ArrayList();
    private ArrayList toppings = new ArrayList();    
        
    private PageFormat pgFormat = new PageFormat();
    private boolean debug = false;
    private Book book = new Book();
    private Paper p;
    private boolean prompt = true;
    private int lineSize = 0;
    private int Y_space = 0;
    private int W;
    private int H;
    private int usedLines = 0;
    private int availableLines=0;
    private  Font font = new Font("Helvetica", Font.PLAIN, 14);
    private float inches;
    
    private boolean printDialog;
    
    public Order(boolean printDialog){
        this.printDialog = printDialog;
    }
    
    public void setupPaper(){
        
        inches = 3.0f;
        W = (int)(inches * 72);

        BufferedImage bimage = new BufferedImage(W-4, 100, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = bimage.createGraphics();

        g2d.setFont(font);

        FontRenderContext fr = g2d.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics( "HijK", fr );

        lineSize = (int)lm.getHeight();


        //done with g2d
        g2d = null;


        Y_space = lineSize * usedLines;

        H = Y_space;
        p = new Paper();

        p.setSize(W, H);

        p.setImageableArea(2, 2, W-2 ,H-2);  //2 point margin (1/7th of an inch)

        pgFormat.setPaper(p);

    }
    
    
      public int print(Graphics g, PageFormat pageFormat, int page) {
          
          
          pageFormat = this.pgFormat;
          
      if (page > 0) {
                return Printable.NO_SUCH_PAGE;  //?
      }

      Graphics2D g2d = (Graphics2D) g;


      g2d.setPaint(Color.black);


      g2d.setFont(font);


      g2d.setClip(null);


      FontRenderContext fr = g2d.getFontRenderContext();
      LineMetrics lm = font.getLineMetrics( "HijK", fr );

      FontMetrics metrics = g2d.getFontMetrics(font);
      
      double X = pageFormat.getImageableX();


      double fh = (int)lm.getHeight();
      double Y = pageFormat.getImageableY();

            /* real new lines inside text strings are ignored */
      g2d.drawString ("-", (float) X , (float) Y);

      Y += fh;   // keeping track of how much was written
      
      g2d.drawString (this.getDelivery(), (float) X , (float) Y);

      Y += fh;   // keeping track of how much was written
      
      g2d.drawString ("ORDER ID: "+this.getOrder_id(), (float) X , (float) Y);

      Y += fh*2;   // keeping track of how much was written

      g2d.drawString (this.getPhone(), (float) X , (float) Y);

      Y += fh;   // keeping track of how much was written

      g2d.drawString (this.getCustomer_name(), (float) X , (float) Y);

      Y += fh*2;   // keeping track of how much was written

      g2d.drawString ("Ordered: "+this.getSubmitted(), (float) X , (float) Y);

      Y += fh;   // keeping track of how much was written

      g2d.drawString ("Due: "+this.getDelivery_time(), (float) X , (float) Y);

      Y += fh*2;   // keeping track of how much was written
    
      g2d.drawString ("Items/Toppings", (float) X , (float) Y);
      Y += fh;  
      
      g2d.drawString ("--------------------------------------", (float) X , (float) Y);
      Y += fh;  
      //items 0 = index
      //toppings 2 = item_id
      float total_bill = 0.00f;
      
      for (int i = 0; i < items.size(); i++){
          Item item = (Item)items.get(0);
          String item_id = item.getId();
          String item_name = item.getName();
          float item_price = item.getPrice();
          total_bill += item_price;
          g2d.drawString (item_name, (float) X , (float) Y);          
          try{
            //g2d.drawString (money(item_price), (float)(pageFormat.getWidth() - metrics.stringWidth(money(item_price))), (float) Y);
          }catch(Exception e){
               //ignore...yay!
          }
          Y += fh; 
          
          for (int t = 0; t < toppings.size(); t++){
              Topping topping = (Topping)toppings.get(t);
              String id = topping.getItem_id();
              
              if (id.equals(item_id)){
                  
                  String top_comment = topping.getComment();
                  String top_half = topping.getHalf();
                  float top_price = topping.getPrice();
                  total_bill += top_price;
                  g2d.drawString (top_comment, (float) X+7 , (float) Y);          
                  try{
                      g2d.drawString (top_half, (float)(pageFormat.getWidth() - (metrics.stringWidth(top_half))), (float) Y);
                      //g2d.drawString (money(top_price), (float)(pageFormat.getWidth() - metrics.stringWidth(money(top_price))), (float) Y);
                  }catch(Exception e){
                      //ignore...yay!
                  }
                  Y += fh; 
              }
              
          }
          g2d.drawString ("--------------------------------------", (float) X , (float) Y);
          Y += fh;  
          Y += fh;//an extra line
          
          g2d.drawString ("ORDER TOTAL:     $"+money(total_bill) , (float) X+7 , (float) Y);        
          
      }
      
      
      return (PAGE_EXISTS);
    }

      public void go(){
       this.setupPaper();
       
       PrinterJob printJob = PrinterJob.getPrinterJob();

       printJob.setPrintable(this);
       //printJob.setPageable(book);  //contains all pgFormats
  
       boolean doJob = true;

       if (printDialog){

           doJob = printJob.printDialog();

       }

       if (doJob) {

           try {


               printJob.print();

             }catch (Exception PrintException) {

                PrintException.printStackTrace();

             }

        }
          
      }
      
    /**
     * @return the order_id
     */
    public String getOrder_id() {
        return order_id;
    }

    /**
     * @param order_id the order_id to set
     */
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    /**
     * @return the submitted
     */
    public String getSubmitted() {
        return submitted;
    }

    /**
     * @param submitted the submitted to set
     */
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }

    /**
     * @return the delivery_time
     */
    public String getDelivery_time() {
        return delivery_time;
    }

    /**
     * @param delivery_time the delivery_time to set
     */
    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    /**
     * @return the customer_name
     */
    public String getCustomer_name() {
        return customer_name;
    }

    /**
     * @param customer_name the customer_name to set
     */
    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the carrier
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * @param carrier the carrier to set
     */
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    /**
     * @return the delivery
     */
    public String getDelivery() {
        return delivery;
    }

    /**
     * @param delivery the delivery to set
     */
    public void setDelivery(int d) {
        if (d == 1) {
            this.delivery = "DELIVERY";
        }else this.delivery = "PICK-UP";
    }

    /**
     * @return the items
     */
    public ArrayList getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void addItem(Item item) {
        this.items.add(item);
    }

    /**
     * @return the toppings
     */
    public ArrayList getToppings() {
        return toppings;
    }

    /**
     * @param toppings the toppings to set
     */
    public void addTopping(Topping topping) {
        this.toppings.add(topping);
    }
    
    public static String money (float money) {
        
        money = (float)(Math.round(money*100.00f)/100.00f);
        NumberFormat formatter = new DecimalFormat("#,###,##0.00");

        return formatter.format(money);
    }
    
}

