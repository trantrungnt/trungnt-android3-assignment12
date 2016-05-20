package techkids.mad3.servicexmlparse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by TrungNT on 5/21/2016.
 */
public class ItemDetailActivity extends AppCompatActivity {
    private WebView webViewNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        initComponent();
    }

    private void initComponent()
    {
        webViewNews = (WebView) this.findViewById(R.id.webViewNews);
        webViewNews.getSettings().setJavaScriptEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String urlDescription = bundle.getString("urlDescription");
        webViewNews.loadUrl(urlDescription);
    }
}

