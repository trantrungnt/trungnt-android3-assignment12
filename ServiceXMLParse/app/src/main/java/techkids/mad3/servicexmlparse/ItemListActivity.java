package techkids.mad3.servicexmlparse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {
    private List<VnExpressXmlParser.Item> items = null;
    private RecyclerView recyclerView;
    private Intent intent;
    private Bundle bundle;
    private String urlRSS = "http://vnexpress.net/rss/tin-moi-nhat.rss";
    private int startDescription;
    private String contentDescription;
    private int startURLDescriptionImage, endURLDescriptionImage;
    private String urlImage;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        initComponent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //download file XML va doc RSS
        loadXMLfromDownloadService();
        //sau khi doc xong RSS thi gan du lieu vao RecycleView
        setupRecyclerView();
    }

    //khoi tao cac thanh phan cua giao dien
    private void initComponent()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
    }


    //doc RSS XML dung Download Service
    private void loadXMLfromDownloadService()
    {
        //gui link URL RSS cho Service DownloadService xu ly
        intent = new Intent(ItemListActivity.this, DownloadXmlService.class);
        bundle = new Bundle();
        bundle.putString("URL_RSS", urlRSS);
        intent.putExtras(bundle);
        startService(intent);
    }

    //Service xu ly load chuoi URL va download XML ve xu ly (dung Intent va Bundle de day sang cho ItemListActivity)
    private void setupRecyclerView() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                bundle = intent.getExtras();
                items = (List<VnExpressXmlParser.Item>) bundle.getSerializable("GET_ITEMS");
                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(items));
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("FILTER_DOWNLOAD_XML_PARSE"));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<VnExpressXmlParser.Item> mValues;

        public SimpleItemRecyclerViewAdapter(List<VnExpressXmlParser.Item> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            if (null == view) {
                Log.d("XMLL", "view = null");
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            if (holder.title == null) {
                Log.d("XMLL", "" + mValues.get(position).title);
                Log.d("XMLL", "" + mValues.get(position).description);
                Log.d("XMLL", "" + mValues.get(position).pubDate);
            }

            description = mValues.get(position).description;

            holder.title.setText(mValues.get(position).title);

            startDescription = description.indexOf("</a></br>");
            contentDescription = description.substring(startDescription + 9);
            holder.description.setText(contentDescription);
            holder.pubDate.setText(mValues.get(position).pubDate);

            //tim link anh jpg va cat chuoi, sau do load anh jpg voi thu vien Picasso
            startURLDescriptionImage = description.indexOf("src=\"http://");
            endURLDescriptionImage = description.indexOf(".jpg");
            try {
                urlImage = description.substring(startURLDescriptionImage + 5, endURLDescriptionImage + 4);
                Picasso.with(holder.image.getContext()).load(urlImage).into(holder.image);
            } catch (Exception e)
            {
                System.out.println(e.toString());
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra("urlDescription", mValues.get(position).link);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView image;
            public final TextView title;
            public final TextView description;
            public final TextView pubDate;
            public VnExpressXmlParser.Item mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                image = (ImageView) view.findViewById(R.id.image);
                title = (TextView) view.findViewById(R.id.title);
                description = (TextView) view.findViewById(R.id.description);
                pubDate = (TextView) view.findViewById(R.id.pubDate);
            }
        }
    }

}
