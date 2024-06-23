package ebookline.notepad.Util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ebookline.notepad.Model.SPCModel;

public class SPCRecognizer
{
    public static final String HASHTAG ="(^|\\s+)#(\\w+)";
    public static final String MENTION ="(^|\\s+)@(\\w+)";
    //private static final String URL     ="(https:\\/\\/www\\.|http:\\/\\/www\\.|https:\\/\\/|http:\\/\\/)?[a-zA-Z0-9]{2,}(\\.[a-zA-Z0-9]{2,})(\\.[a-zA-Z0-9]{2,})?";
    public static final String URL     = Patterns.WEB_URL.toString();
    public static final String EMAIL   ="([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9._-]+)";
    public static final String PHONE   ="\\d{13}|\\d{12}|\\d{11}|\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";

    public static final String SYMBOL_HASHTAG ="hashtag";
    public static final String SYMBOL_MENTION ="mention";
    public static final String SYMBOL_URL     ="url";
    public static final String SYMBOL_EMAIL   ="email";
    public static final String SYMBOL_PHONE   ="phone";
    public static final String SYMBOL_CUSTOM  ="custom";

    private int colorHashtag         =Color.parseColor("#69F288");
    private int colorMENTION         =Color.parseColor("#6895F2");
    private int colorURL             =Color.BLUE;
    private int colorMAIL            =Color.parseColor("#69F1F2");
    private int colorPHONE           =Color.parseColor("#7869F2");
    private int CustomRegexColor     =Color.parseColor("#FB742D");
    private int colorFindWord       =Color.parseColor("#F74524");
    private int backgroundColorFindWord  =Color.parseColor("#A6FAFC28");

    private boolean findHashtag =false;
    private boolean findMention =false;
    private boolean findUrl     =false;
    private boolean findMail    =false;
    private boolean findPhone   =false;

    private boolean clickHashtag =false;
    private boolean clickMention =false;
    private boolean clickUrl     =false;
    private boolean clickMail    =false;
    private boolean clickPhone   =false;
    private boolean clickCustom  =false;

    private String customRegex = null;

    private ClickListener clickListener;

    private TextView tv;
    private Spannable ws;

    private ArrayList<SPCModel>list;

    public ArrayList<SPCModel> check(View view)
    {
        tv=(TextView) view;

        String txt=tv.getText().toString();

        this.ws = new SpannableString(txt);

        list=new ArrayList<>();

        if(findHashtag){
            Matcher matcherHashtag = Pattern.compile(HASHTAG).matcher(txt);
            while(matcherHashtag.find()){
                SPCModel spc=new SPCModel();
                spc.setText(matcherHashtag.group());
                spc.setSymbol(SYMBOL_HASHTAG);
                spc.setClickable(clickHashtag);
                spc.setColor(colorHashtag);
                spc.setStart(matcherHashtag.start());
                spc.setEnd(matcherHashtag.end());
                list.add(spc);
                this.ws.setSpan(new SpanClick(spc),spc.getStart(),spc.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if(findMention){
            Matcher matcherMENTION = Pattern.compile(MENTION).matcher(txt);
            while(matcherMENTION.find()){
                SPCModel spc=new SPCModel();
                spc.setText(matcherMENTION.group());
                spc.setSymbol(SYMBOL_MENTION);
                spc.setClickable(clickMention);
                spc.setColor(colorMENTION);
                spc.setStart(matcherMENTION.start());
                spc.setEnd(matcherMENTION.end());
                list.add(spc);
                this.ws.setSpan(new SpanClick(spc),spc.getStart(),spc.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if(findUrl){
            Matcher matcherURL = Pattern.compile(URL).matcher(txt);
            while(matcherURL.find()){
                SPCModel spc=new SPCModel();
                spc.setText(matcherURL.group());
                spc.setSymbol(SYMBOL_URL);
                spc.setClickable(clickUrl);
                spc.setColor(colorURL);
                spc.setStart(matcherURL.start());
                spc.setEnd(matcherURL.end());
                list.add(spc);
                this.ws.setSpan(new SpanClick(spc),spc.getStart(),spc.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if(findMail){
            Matcher matcherMAIL = Pattern.compile(EMAIL).matcher(txt);
            while(matcherMAIL.find()){
                SPCModel spc=new SPCModel();
                spc.setText(matcherMAIL.group());
                spc.setSymbol(SYMBOL_EMAIL);
                spc.setClickable(clickMail);
                spc.setColor(colorMAIL);
                spc.setStart(matcherMAIL.start());
                spc.setEnd(matcherMAIL.end());
                list.add(spc);
                this.ws.setSpan(new SpanClick(spc),spc.getStart(),spc.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if(findPhone){
            Matcher matcherPHONE = Pattern.compile(PHONE).matcher(txt);
            while(matcherPHONE.find()){
                SPCModel spc=new SPCModel();
                spc.setText(matcherPHONE.group());
                spc.setSymbol(SYMBOL_PHONE);
                spc.setClickable(clickPhone);
                spc.setColor(colorPHONE);
                spc.setStart(matcherPHONE.start());
                spc.setEnd(matcherPHONE.end());
                list.add(spc);
                this.ws.setSpan(new SpanClick(spc),spc.getStart(),spc.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if(customRegex != null && !customRegex.isEmpty()) {
            Matcher matcherCUSTOM = Pattern.compile(customRegex).matcher(txt);
            while (matcherCUSTOM.find()) {
                SPCModel spc=new SPCModel();
                spc.setText(matcherCUSTOM.group());
                spc.setSymbol(SYMBOL_CUSTOM);
                spc.setClickable(clickCustom);
                spc.setColor(CustomRegexColor);
                spc.setStart(matcherCUSTOM.start());
                spc.setEnd(matcherCUSTOM.end());
                list.add(spc);
                this.ws.setSpan(new SpanClick(spc),spc.getStart(),spc.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        tv.setText(ws);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setHighlightColor(Color.TRANSPARENT);

        return list;
    }

    public int findWord(String word,int position)
    {
        this.ws = new SpannableString(tv.getText().toString());

        int currentPosition=1;

        Matcher matcher=null;
        if(!word.isEmpty()) {
            try{
                matcher = Pattern.compile("(?i)" + word.trim()).matcher(tv.getText().toString());
                while (matcher.find()) {
                    int st = matcher.start();
                    int en = st + Objects.requireNonNull(matcher.group(0)).length();
                    this.ws.setSpan(new ForegroundColorSpan(colorFindWord), st, en, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(currentPosition==position)
                        this.ws.setSpan(new BackgroundColorSpan(backgroundColorFindWord), st, en, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    currentPosition++;
                }
            }catch(Exception ignored){}
        }

        tv.setText(ws);
        return currentPosition-1;
    }

    private class SpanClick extends ClickableSpan {
        SPCModel spcModel;
        SpanClick(SPCModel spcModel){
            this.spcModel=spcModel;
        }

        @Override
        public void onClick(View textView) {
            if(clickListener==null) return;
            if(!spcModel.isClickable()) return;
            clickListener.onClick(spcModel);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(spcModel.getColor());
            ds.setUnderlineText(false);
        }
    }

    public SPCRecognizer(View textView){
        tv = (TextView) textView;
    }

    public void setCustomRegex(String customRegex){
        this.customRegex = customRegex;
    }

    public void setclickHashtag(boolean clickHashtag){
        this.clickHashtag=clickHashtag;
    }

    public void setclickMention(boolean clickMention){
        this.clickMention=clickMention;
    }

    public void setclickUrl(boolean clickUrl){
        this.clickUrl=clickUrl;
    }

    public void setclickMail(boolean clickMail){
        this.clickMail=clickMail;
    }

    public void setclickPhone(boolean clickPhone){
        this.clickPhone=clickPhone;
    }

    public void setclickCustom(boolean clickCustom){
        this.clickCustom=clickCustom;
    }

    public void setFindHashtag(boolean findHashtag){
        this.findHashtag=findHashtag;
    }

    public void setFindMention(boolean findMention){
        this.findMention=findMention;
    }

    public void setFindUrl(boolean findUrl){
        this.findUrl=findUrl;
    }

    public void setfindMail(boolean findMail){
        this.findMail=findMail;
    }

    public void setFindPhone(boolean findPhone){
        this.findPhone=findPhone;
    }

    public void setColorTAG(int colorHashtag){
        this.colorHashtag = colorHashtag;
    }

    public void setColorMENTION(int colorMENTION){
        this.colorMENTION = colorMENTION;
    }

    public void setColorURL(int colorURL){
        this.colorURL = colorURL;
    }

    public void setColorMAIL(int colorMAIL){
        this.colorMAIL = colorMAIL;
    }

    public void setColorPHONE(int colorPHONE){
        this.colorPHONE = colorPHONE;
    }

    public void setCustomRegexColor(int CustomRegexColor){
        this.CustomRegexColor = CustomRegexColor;
    }

    public void setColorFindWord(int colorFindWord){
        this.colorFindWord = colorFindWord;
    }

    public void setBackgroundColorFindWord(int backgroundColorFindWord){
        this.backgroundColorFindWord = backgroundColorFindWord;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onClick(SPCModel model);
    }

}
