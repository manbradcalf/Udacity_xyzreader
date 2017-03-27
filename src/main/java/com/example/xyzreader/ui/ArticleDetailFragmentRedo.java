package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.squareup.picasso.Callback;

import static com.example.xyzreader.ui.ArticleDetailFragment.ARG_ITEM_ID;

/**
 * Created by ben.medcalf on 3/26/17.
 */

public class ArticleDetailFragmentRedo extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String TAG = ArticleDetailFragmentRedo.class.getSimpleName();
    private static final Boolean DEBUG = false;

    private static final String ARG_ARTICLE_POSITION = "arg_article_position";
    private static final String ARG_STARTING_ARTICLE_IMAGE_POSITION = "arg_starting_article_image_position";
    private ImageView mArticleImage;
    private int mStartingPosition;
    private int mArticlePosition;
    private boolean mIsTransitioning;
    private long mBackgroundImageFadeMillis;
    private CoordinatorLayout mCoordinatorLayout;


    private final Callback mImageCallback = new Callback()
    {
        @Override
        public void onSuccess()
        {
            startPostponedEnterTransition();
        }

        @Override
        public void onError()
        {
            startPostponedEnterTransition();
        }
    };
    private View mPhotoContainerView;
    private long mItemId;
    private Cursor mCursor;

    public static ArticleDetailFragmentRedo newInstance(int position, int startingPosition)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICLE_POSITION, position);
        args.putInt(ARG_STARTING_ARTICLE_IMAGE_POSITION, startingPosition);
        ArticleDetailFragmentRedo fragment = new ArticleDetailFragmentRedo();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        mStartingPosition = getArguments().getInt(ARG_STARTING_ARTICLE_IMAGE_POSITION);
        mArticlePosition = getArguments().getInt(ARG_ARTICLE_POSITION);
        mIsTransitioning = savedInstanceState == null && mStartingPosition == mArticlePosition;
        mBackgroundImageFadeMillis = 1000;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_article_detail_redo, container, false);

        mArticleImage = (ImageView) rootView.findViewById(R.id.toolbar_article_image);
        mCoordinatorLayout = (CoordinatorLayout)
                rootView.findViewById(R.id.coordinator_layout_detail_rootview);
        TextView titleView = (TextView) rootView.findViewById(R.id.article_title);
        TextView bylineView = (TextView) rootView.findViewById(R.id.article_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        TextView bodyView = (TextView) rootView.findViewById(R.id.article_body);
        bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));


        return rootView;
    }

    private void startPostponedEnterTransition()
    {
        if (mArticlePosition == mStartingPosition)
        {
            mArticleImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onPreDraw()
                {
                    mArticleImage.getViewTreeObserver().removeOnPreDrawListener(this);
                    getActivity().startPostponedEnterTransition();
                    return true;
                }
            });
        }
    }

    /**
     * Section for Loader Callbacks
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if (!isAdded())
        {
            if (data != null)
            {
                data.close();
            }
            return;
        }

        mCursor = data;
        if (mCursor != null && mCursor.moveToFirst())
        {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }
}
