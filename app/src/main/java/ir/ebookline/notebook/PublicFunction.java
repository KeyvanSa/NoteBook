package ir.ebookline.notebook;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PublicFunction
{

    Context con;

    public PublicFunction(Context con)
    {
        this.con = con;
    }


    public void showTOAST(String str,int Type)
    {
        // 1 > normal Toast
        // 2 > error Toast
        // 3 > success Toast

        Toast.makeText(con,str,Toast.LENGTH_SHORT).show();
    }

    public void shareText (String text)
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text );
        shareIntent.putExtra(Intent.EXTRA_SUBJECT , "ShareText" );
        con.startActivity(shareIntent);
    }


}
