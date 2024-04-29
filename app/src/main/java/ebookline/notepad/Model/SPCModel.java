package ebookline.notepad.Model;

public class SPCModel
{
    String text;
    String symbol;
    boolean clickable;
    int start;
    int end;
    int color;

    public SPCModel(String text, String symbol,boolean clickable, int start, int end, int color)
    {
        this.text = text;
        this.symbol = symbol;
        this.clickable=clickable;
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public SPCModel()
    {}

    public void setClickable(boolean clickable)
    {
        this.clickable = clickable;
    }

    public boolean isClickable()
    {
        return clickable;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setStart(int start)
    {
        this.start = start;
    }

    public int getStart()
    {
        return start;
    }

    public void setEnd(int end)
    {
        this.end = end;
    }

    public int getEnd()
    {
        return end;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public int getColor()
    {
        return color;
    }

}