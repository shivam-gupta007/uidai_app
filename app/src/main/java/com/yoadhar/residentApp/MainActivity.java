package com.yoadhar.residentApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import net.lingala.zip4j.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends AppCompatActivity {

    EditText uidEditText, captchaEditText, otpEditText;
    RequestQueue queue;
    String encodedCaptcha, txId;
    ImageView captchaImageView, userImg, refreshIc;
    String uidVal, userCaptchaVal, otpTxId, eKycXml, fileName;
    String encodedPht, userAddress, userName, userDob, dist, street, post, pinCode, state, subDist, village;
    Button sendBtn, submitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uidEditText = findViewById(R.id.inputUid);

        captchaImageView = findViewById(R.id.iv_cap);
        userImg = findViewById(R.id.iv_pic);
        refreshIc = findViewById(R.id.refreshIcon);

        captchaEditText = findViewById(R.id.inputCaptcha);

        otpEditText = findViewById(R.id.et_eotp);

        sendBtn = findViewById(R.id.sendOtpBtn);

        submitBtn = findViewById(R.id.btnSubmit);

        queue = Volley.newRequestQueue(MainActivity.this);
        callCaptcha();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateOTP(); //calling generateOtp() method for OTP
            }
        });

        refreshIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callCaptcha();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calleKyc();
            }
        });
    }


    private void openQrIntent() {
        Intent intent = new Intent(MainActivity.this, qr_activity.class);
        intent.putExtra("name", userName);
        intent.putExtra("addr", userAddress);
        intent.putExtra("dob", userDob);
        intent.setType("text/plain");
        startActivity(intent);
    }


    // Retrieving the uri to share
    private Uri getImageToShare(Bitmap bitmap) {
        File imageFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            boolean boo = imageFolder.mkdirs();
            File file = new File(imageFolder, "demoimg.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            uri = FileProvider.getUriForFile(this, "com.yoadhar.residentApp", file);
        } catch (Exception e) {

        }
        return uri;
    }

    private void printErrorMsg() {
        Toast.makeText(this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
    }

    private void generateOTP() {
        uidVal = uidEditText.getEditableText().toString();
        userCaptchaVal = captchaEditText.getEditableText().toString();
        callOtp();
    }


    private void callOtp() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uidNumber", uidVal);
        params.put("captchaTxnId", txId);
        params.put("captchaValue", userCaptchaVal);
        params.put("transactionId", "MYAADHAAR:59142477-3f57-465d-8b9a-75b28fe48725");

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Constants.generateOtp, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("Success")) {
                        otpTxId = response.getString("txnId");

                        Toast.makeText(MainActivity.this, "OTP sent to Your Registered Mobile NO", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();

                    } else {
                        Snackbar.make(findViewById(R.id.relativeLayout), "Check UID or Captcha Value", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMsg();
            }
        });
        queue.add(req);

    }

    //set captcha value to imageView
    private void setCaptcha() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        //String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        imageBytes = Base64.decode(encodedCaptcha, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        captchaImageView.setImageBitmap(decodedImage);

    }

    private void callCaptcha() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.langCode, "AbCdEfGh123456");
        params.put(Constants.captchaLength, "AbCdEfGh123456");
        params.put(Constants.captchaType, "AbCdEfGh123456");

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Constants.captchaUrl, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("Success")) {
                        encodedCaptcha = response.getString("captchaBase64String");
                        txId = response.getString("captchaTxnId");

                        setCaptcha();


                    } else {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMsg();
            }
        });
        queue.add(req);


    }


    public void calleKyc() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("txnNumber", otpTxId);
        params.put("otp", otpEditText.getText().toString());
        params.put("shareCode", "1234");
        params.put("uid", uidEditText.getText().toString());

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Constants.ekycOffline, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("Success")) {
                        openQrIntent();
                        doProcess(response.getString("eKycXML"), response.getString("fileName"));


                    } else {
                        Snackbar.make(findViewById(R.id.relativeLayout), "Incorrect OTP", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMsg();
            }
        });
        queue.add(req);

    }

    private void doProcess(String eKycXML, String filename) {
        //  file = getFilesDir();

        // data save
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            byte[] decoder = Base64.decode(eKycXML, 1);
            fos.write(decoder);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//     data save


        // open zip


        File path = getFilesDir();
        String fpath = path + "/" + filename;
        String n = path + "/dataUIDAI/";
        String withXml = filename.replaceAll("zip", "xml");
        String ExtractFilePath = n + "/" + withXml;
        Log.d(">>>>>", path.getPath());
        try {
            new ZipFile(fpath, "1234".toCharArray()).extractAll(path.getPath() + "/dataUIDAI");
            encodedPht = parseString(ExtractFilePath, "OfflinePaperlessKyc", "Pht");
            userName = parseStringByTag(ExtractFilePath, "Poi", "name");
            userDob = parseStringByTag(ExtractFilePath, "Poi", "dob");
            dist = parseStringByTag(ExtractFilePath, "Poa", "dist");
            street = parseStringByTag(ExtractFilePath, "Poa", "street");
            post = parseStringByTag(ExtractFilePath, "Poa", "po");
            state = parseStringByTag(ExtractFilePath, "Poa", "state");
            subDist = parseStringByTag(ExtractFilePath, "Poa", "subdist");
            village = parseStringByTag(ExtractFilePath, "Poa", "vtc");
            pinCode = parseStringByTag(ExtractFilePath, "Poa", "pc");
            userAddress = post + " " + street + " " + subDist + "\n" + dist + " " + state + " " + pinCode;

            Toast.makeText(this, userName + "\n" + userDob + "\n" + userAddress, Toast.LENGTH_SHORT).show();

            setPic(encodedPht, userImg);

            Log.d("<><><><", encodedPht);
        } catch (IOException e) {


        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


    }

    public void setPic(String dob, ImageView view) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byteArrayOutputStream.toByteArray();
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        ;
        imageBytes = Base64.decode(dob, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        view.setImageBitmap(decodedImage);
    }

    //String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

    public static String parseString(String filename, String tag, String subNode) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(filename);
        String text = null;
        //an instance of factory that gives a document builder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();
//        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        NodeList nodeList = doc.getElementsByTagName(tag);
// nodeList is not iterable, so we are using for loop
        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);
            System.out.println("\nNode Name :" + node.getNodeName());

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                text = element.getElementsByTagName(subNode).item(0).getTextContent();
                System.out.println(text);
//                text = element.getElementsByTagName(subNode).item(0).getTextContent();
            }

        }


        return text;
    }


    public static String parseStringByTag(String filename, String tag, String subNode) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(filename);
        String text = null;
//an instance of factory that gives a document builder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();
//        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        NodeList nodeList = doc.getElementsByTagName(tag);
// nodeList is not iterable, so we are using for loop
        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);
            System.out.println("\nNode Name :" + node.getNodeName());

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                text = element.getAttribute(subNode);
                System.out.println(text);
//                text = element.getElementsByTagName(subNode).item(0).getTextContent();


            }
        }


        return text + " ";
    }


}
