package net.integraa.read.dbhelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.integraa.read.R;
import net.integraa.read.scanditLayout.FullscreenScanFragmentContainerActivity;

public class DialogHelper {

    public static boolean alert(Context context, String title, String message) {
        return alertIfEmpty(context, null,title, message);
    }

    public static boolean alert(Context context, String title, List<String> messages) {
        return alertIfEmpty(context, null,title, messages);
    }

    public static boolean alertIfEmpty(Context context, List<?> list,String title, List<String> messages) {
        if(messages==null||messages.size()==0) {
            return false;
        }
        return alertIfEmpty(context, list,title, String.join("\n",messages));
    }

    public static boolean alertIfEmpty(Context context, List<?> list,String title, String message) {
        if(list!=null && !list.isEmpty()) {
            return false;
        }
        AlertDialog.Builder info = new AlertDialog.Builder(context);
        setTitleMessageOrCustomView(info, context, title, message);
        info.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        info.setCancelable(false);
        info.setIcon(context.getResources().getDrawable(android.R.drawable.ic_dialog_info));
        info.show();
        return true;
    }
    private static void setTitleMessageOrCustomView(AlertDialog.Builder info,Context context, String title, String message) {
        if(title==null || title.isEmpty()) {
            title=context.getString(R.string.alert);
        }
        info.setTitle(title);
        //if(!message.startsWith("<html>")||!message.endsWith("</html>")) {
        if(!message.matches("^\\s*<html>.*</html>\\s*")) {
            info.setMessage(message);
            return;
        }
        //message=message.replaceAll("^<html>(.*)</html>$","<p color=\"#000000\">$1</p>");
        message=message.replace("<html>","");
        message=message.replace("</html>","");
        message="<p color=\"#000000\">"+message+"</p>";
        Spanned html = Html.fromHtml(message,Html.FROM_HTML_MODE_COMPACT|Html.FROM_HTML_OPTION_USE_CSS_COLORS);
        TextView msg = new TextView(context);
        msg.setPadding(50,0,50,0);
        msg.setText(html);
        msg.setMovementMethod(LinkMovementMethod.getInstance());
        msg.setClickable(true);
        info.setView(msg);
    }
    public static boolean alertWithAction(Context context, String title, String message, DialogInterface.OnClickListener listerner) {
        AlertDialog.Builder info = new AlertDialog.Builder(context);
        setTitleMessageOrCustomView(info, context, title, message);
        info.setIcon(context.getResources().getDrawable(android.R.drawable.ic_dialog_info));
        info.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                listerner.onClick(dialog, i);
                dialog.cancel();
            }
        });
        info.setCancelable(false);
        info.show();
        return true;
    }
    public static boolean confirm(Context context, String title, String message, DialogInterface.OnClickListener listerner) {
        try {
            AlertDialog.Builder info = new AlertDialog.Builder(context);
            setTitleMessageOrCustomView(info, context, title, message);
            info.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    listerner.onClick(dialog, i);
                    dialog.cancel();
                }
            });
            info.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    listerner.onClick(dialog, i);
                    dialog.cancel();
                }
            });
            info.setCancelable(false);
            info.show();
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }

    public static boolean passwordInput(Context context, String title, String message, Map<String,Object> settings, DialogInputsInterface listerner) {
        if(settings==null) {
            settings=new HashMap<>();
        }
        settings.put("InputType", InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        return genericInputs(context, title, message, settings, listerner);
    }

    public static boolean integerInput(Context context, String title, String message, Map<String,Object> settings, DialogInputsInterface listerner) {
        if(settings==null) {
            settings=new HashMap<>();
        }
        settings.put("InputType", InputType.TYPE_CLASS_NUMBER);
        return genericInputs(context, title, message, settings, listerner);
    }

    public static boolean clientAccessInput(Context context, String title, String message, Map<String,Object> settings, DialogInputsInterface listerner) {
        if(settings==null) {
            settings=new HashMap<>();
        }
        settings.put("Alert", true);
        settings.put("ManualDismiss", true);
        settings.put("InputsCount", 2);
        settings.put("Hint0", context.getString(R.string.client_code));
        settings.put("InputType0", InputType.TYPE_CLASS_TEXT);
        settings.put("Hint1", context.getString(R.string.password));
        settings.put("InputType1", InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        return genericInputs(context, title, message, settings, new DialogInputsInterface() {
            @Override
            public void onClick(DialogInterface dialog, int which, String[] inputs) {
                String msg = "";
                if(which==DialogInterface.BUTTON_POSITIVE) {
                    if (TextUtils.isEmpty(inputs[0])) {
                        msg+=context.getString(R.string.invalid_client_code)+"\n";
                    }
                    if (TextUtils.isEmpty(inputs[1])) {
                        msg+=context.getString(R.string.invalid_password)+"\n";
                    }
                }
                if (msg.isEmpty()) {
                    listerner.onClick(dialog, which, inputs);
                }
                else {
                    DialogHelper.alert(context,"",msg);
                }
            }
        });
    }

    public static boolean genericInputs(Context context, String title, String message, Map<String,Object> settings, DialogInputsInterface listerner) {
        if(settings==null) {
            settings=new HashMap<>();
        }
        if (!settings.containsKey("InputsCount")) {
            settings.put("InputsCount","1");
        }
        AlertDialog.Builder info;
        if (settings.containsKey("FullScreen")) {
            info = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        }
        if (settings.containsKey("Alert")) {
            info = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }
        else {
            info = new AlertDialog.Builder(context);
        }
        info.setTitle(title);
        final TextView viewtext = new TextView(context);
        viewtext.setText(message);
        viewtext.setPadding(50,0,50,0);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(viewtext);

        int count = Integer.parseInt(settings.get("InputsCount").toString());
        EditText[] edittexts = new EditText[count];
        for(int i=0;i<count;i++){
            edittexts[i]=new EditText(context);
            for(String value:new String[]{"InputType"+i,"InputType"}) {
                if(settings.containsKey(value)) {
                    edittexts[i].setInputType((Integer)settings.get(value));
                    break;
                }
            }
            for(String value:new String[]{"MaxLength"+i,"MaxLength"}) {
                if(settings.containsKey(value)) {
                    edittexts[i].setFilters(new InputFilter[] { new InputFilter.LengthFilter((Integer)settings.get(value)) });
                    break;
                }
            }
            for(String value:new String[]{"Hint"+i,"Hint"}) {
                if(settings.containsKey(value)) {
                    edittexts[i].setHint(settings.get(value).toString());
                    break;
                }
            }
            for(String value:new String[]{"Text"+i,"Text"}) {
                if(settings.containsKey(value)) {
                    edittexts[i].setText(settings.get(value).toString());
                    break;
                }
            }
            linearLayout.addView(edittexts[i]);
        }

        info.setView(linearLayout);
        Map<String, Object> finalSettings = settings;
        info.setPositiveButton(android.R.string.yes, null);
        info.setNegativeButton(android.R.string.no, null);
        info.setCancelable(false);
        AlertDialog alertDialog = info.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] inputs = new String[count];
                        for(int i=0;i<count;i++){
                            inputs[i]=edittexts[i].getText().toString();
                        }
                        listerner.onClick(dialog, DialogInterface.BUTTON_POSITIVE, inputs);
                        if(!finalSettings.containsKey("ManualDismiss")) {
                            dialog.cancel();
                        }
                    }
                });
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listerner.onClick(dialog, DialogInterface.BUTTON_NEGATIVE, null);
                        if(!finalSettings.containsKey("ManualDismiss")) {
                            dialog.cancel();
                        }
                    }
                });
                if(finalSettings.containsKey("Timeout")) {
                    final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    final CharSequence negativeButtonText = defaultButton.getText();
                    new CountDownTimer((Long)finalSettings.get("Timeout")*1000, 100) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            defaultButton.setText(String.format(
                                    Locale.getDefault(), "%s (%d)",
                                    negativeButtonText,
                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                            ));
                        }
                        @Override
                        public void onFinish() {
                            if (((AlertDialog) dialog).isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }.start();
                }
            }
        });
        alertDialog.show();
        return true;
    }

    public static <K,V extends Object> boolean singleSelect(Context context, String title, Map<K,V> map, EntryInterface<K,V> listener) {
        AlertDialog.Builder info = new AlertDialog.Builder(context);
        info.setTitle(title);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        /*final TextView viewtext = new TextView(context);
        viewtext.setText(message);
        viewtext.setPadding(50,0,50,0);*/

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setPadding(50,0,50,0);
        for(Map.Entry<K,V> entry:map.entrySet()) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(entry.getValue().toString());
            radioButton.setPadding(0,50,0,50);
            radioButton.setTag(entry);
            radioGroup.addView(radioButton);
        }

        //linearLayout.addView(viewtext);
        linearLayout.addView(radioGroup);
        info.setView(linearLayout);
        info.setCancelable(false);
        AlertDialog dialog = info.show();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                dialog.cancel();
                listener.onSelect((Map.Entry<K,V>)radioButton.getTag());
            }
        });
        return true;
    }

    public interface DialogInputInterface {
        void onClick(DialogInterface dialog, int which, String input);
    }

    public interface DialogInputsInterface {
        void onClick(DialogInterface dialog, int which, String[] inputs);
    }

    public interface EntryInterface<K,V extends Object> {
        void onSelect(Map.Entry<K,V> entry);
    }

}
