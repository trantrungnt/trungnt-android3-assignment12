package techkids.mad3.servicexmlparse;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrungNT on 5/21/2016.
 */
public class DownloadXmlService extends IntentService {
    private List<VnExpressXmlParser.Item> items = null;
    private Bundle bundleGetURL, bundleResult;
    private String urlRSS;
    private Intent intentResult;

    public DownloadXmlService() {
        super("DownloadXmlService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //nhan du lieu gui sang tu ItemListActivity gui duong dan RSS XML
        bundleGetURL = intent.getExtras();
        urlRSS = bundleGetURL.getString("URL_RSS");

        try {
            loadXmlFromNetwork(urlRSS);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        intentResult = new Intent();
        bundleResult = new Bundle();
        //items = new ArrayList<VnExpressXmlParser.Item>();
        bundleGetURL.putSerializable("GET_ITEMS", (Serializable) items);
        intentResult.putExtra("INTENT_ITEMS", bundleResult);
        intentResult.setAction("FILTER_DOWNLOAD_XML_PARSE");
        sendBroadcast(intentResult);
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        VnExpressXmlParser vnExpressXmlParser = new VnExpressXmlParser();

        try {
            stream = downloadUrl(urlString);
            items = vnExpressXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return null;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
