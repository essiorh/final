package com.example.ilia.final_exercise.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


public class PageFragment extends Fragment {

    public static final String ID_RES = "idRes";
    public static final String TITLE = "title";
    private ProgressBar dialog;
    public static PageFragment newInstance(String title,int idRes) {

        PageFragment pageFragment = new PageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putInt(ID_RES,idRes);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.page_fragment, container, false);
        /*TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(getArguments().getString(TITLE));

        ImageView imageVersion=(ImageView) view.findViewById(R.id.imageVersion);
        imageVersion.setImageResource(getArguments().getInt(ID_RES));

        dialog = (ProgressBar) view.findViewById(R.id.progressBar);
        final WebView webView= (WebView) view.findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                dialog.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {

                dialog.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        final Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("http://vk.com");
            }
        });
*/

        return null;
    }
}