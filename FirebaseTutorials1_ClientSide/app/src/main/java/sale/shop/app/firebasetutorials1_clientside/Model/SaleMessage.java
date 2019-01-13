package sale.shop.app.firebasetutorials1_clientside.Model;

import java.util.Date;

/**
 * Created by farah.saeed on 6/15/2018.
 */

public class SaleMessage {

    private String message;
    private String sender;
    private Date date;

    public SaleMessage(String message, String sender, Date date) {
        this.message = message;
        String sendername = sender.substring(0, 1).toUpperCase() + sender.substring(1);
        this.sender = sendername;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public Date getDate() {
        return date;
    }
}
