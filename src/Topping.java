/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class Topping {
    
    private String item_id;
    private String comment;
    private String half;
    private float price;

    /**
     * @return the item_id
     */
    public String getItem_id() {
        return item_id;
    }

    /**
     * @param item_id the item_id to set
     */
    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the half
     */
    public String getHalf() {
        return half;
    }

    /**
     * @param half the half to set
     */
    public void setHalf(String half) {
        this.half = half;
    }

    /**
     * @return the price
     */
    public float getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(String price) {
        try{
            this.price = Float.parseFloat(price);
        }catch(Exception e){
            this.price = 0.00f;
        }
    }
    
    
}
