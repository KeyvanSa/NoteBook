package ir.ebookline.notebook;

import android.content.Context;
import android.widget.Toast;

public class PublicFunction
{

    Context con;

    public PublicFunction(Context con)
    {
        this.con = con;
    }


    public void SHOW_TOAST(String str,int Type)
    {
        // 1 > normal Toast
        // 2 > error Toast
        // 3 > success Toast

        Toast.makeText(con,str,Toast.LENGTH_SHORT).show();
    }


}
