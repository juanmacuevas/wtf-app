package nl.wtf.app;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorSet;

public class WtfMain extends FragmentActivity {

	static PostsFragmentAdapter mAdapter;
	static ViewPager mPager;
	private ImageView logo;
	private float posfinal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_fragment_pager);

		mAdapter = new PostsFragmentAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pager);
		// mPager.setAdapter(mAdapter);
		mPager.setVisibility(View.INVISIBLE);

		logo = (ImageView) findViewById(R.id.logoView);
		WtfData.getInstance().requestNews(mHandler);
	}

	final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mPager.setAdapter(mAdapter);
						// mPager.getAdapter().notifyDataSetChanged();
					}
				});
				break;
			}
			super.handleMessage(msg);
		}
	};

	// @SuppressLint("NewApi")
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		View p = (View) logo.getParent();
		posfinal = (logo.getTop() - (p.getHeight() / 2 - logo.getHeight() / 2));
		animate(logo).alpha(1).setDuration(1000);
		animate(logo).translationY(-posfinal).setDuration(1000)
				.setInterpolator(new AnticipateOvershootInterpolator())
				.setListener(new AnimatorListener() {

					@Override
					public void onAnimationStart(Animator animation) {
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						AnimatorSet set = (AnimatorSet) AnimatorInflater
								.loadAnimator(WtfMain.this,
										R.animator.loading_beat);
						set.setTarget(logo);
						set.start();
						logo.postDelayed(new Runnable() {

							@Override
							public void run() {
								animate(logo).cancel();
								View p = (View) logo.getParent();

								animate(logo).translationY(p.getHeight())
										.setDuration(1000);
								animate(logo).setInterpolator(
										new AccelerateDecelerateInterpolator());
								mPager.setVisibility(View.VISIBLE);

							}
						}, 1000);
						// logo.setOnClickListener(logoListener);
					}

					@Override
					public void onAnimationCancel(Animator animation) {
					}
				});

		// animate(logo).withEndAction(loadingLoopAnimation);
	}

	private OnClickListener logoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i("MainActivity", "Click!");
			animate(logo).cancel();
			View p = (View) logo.getParent();

			animate(logo).translationY(p.getHeight()).setDuration(1000);
			// animate(logo).alpha(0).setDuration(1000);
			animate(logo);
			// mPager.setVisibility(View.VISIBLE);
			// mAdapter.notifyDataSetChanged();

		}
	};

	public static class PostsFragmentAdapter extends FragmentStatePagerAdapter {
		public PostsFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return WtfData.getInstance().getCount();
		}

		@Override
		public Fragment getItem(int position) {
			// TODO: change this!
			return WtfPostFragment.newInstance(position);
		}

		@Override
		public int getItemPosition(Object item) {

			WtfPostFragment fragment = (WtfPostFragment) item;
			int id = fragment.getArguments().getInt(WtfData.ID);

			return WtfData.getInstance().getPositionOf(id);
		}

	}

	public static class WtfPostFragment extends Fragment {
		int mNum;
		private ImageView mPostImageView;
		private TextView mTitle;
		private TextView mSubtitle;
		private TextView mHead;
		private TextView mBody;
		private WebView webView;

		/**
		 * Create a new instance of CountingFragment, providing "num" as an
		 * argument.
		 */
		static WtfPostFragment newInstance(int num) {
			WtfPostFragment f = new WtfPostFragment();
			Bundle b = WtfData.getInstance().getItemAt(num);
			b.putInt("position", num);
			f.setArguments(WtfData.getInstance().getItemAt(num));

			return f;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;
		}

		/**
		 * The Fragment's UI is just a simple text view showing its instance
		 * number.
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_pager_post, container,
					false);

			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			// mPostImageView.setImageBitmap(
			// decodeSampledBitmapFromResource(getResources(), R.id.myimage,
			// 100, 100));

			View v = getView();
			mTitle = (TextView) v.findViewById(R.id.postTitle);
			mSubtitle = (TextView) v.findViewById(R.id.postSubtitle);
			mHead = (TextView) v.findViewById(R.id.postHead);
			mBody = (TextView) v.findViewById(R.id.postBody);
//			webView = (WebView) v.findViewById(R.id.webView);

			mPostImageView = (ImageView) v.findViewById(R.id.postImage);

			// WtfClient.requestNews(null);
			if (getArguments() != null) {
				mTitle.setText(getArguments().getString(WtfData.TITLE));
				mSubtitle.setText(getArguments().getString(WtfData.SUBTITLE));
				mHead.setText(Html.fromHtml(getArguments().getString(
						WtfData.LEAD)));
				mBody.setText(Html.fromHtml(getArguments().getString(
						WtfData.BODY)));
				// webView.loadData(getArguments().getString(WtfData.LEAD)+"<br />"+getArguments().getString(WtfData.BODY),
				// "text/html", "utf-8");

				Linkify.addLinks(mHead, Linkify.ALL);
				Linkify.addLinks(mBody, Linkify.ALL);

				UrlImageViewHelper.setUrlDrawable(
						mPostImageView,
						"http://media.wtf.nl/m/"
								+ getArguments().getString(WtfData.MEDIA_ID)
								+ "_310x200.jpg", new UrlImageViewCallback() {

							@Override
							public void onLoaded(ImageView imageView,
									Drawable loadedDrawable, String url,
									boolean loadedFromCache) {
								imageView
										.setScaleType(ImageView.ScaleType.FIT_CENTER);

							}
						});
			}

			// new
			// DownloadImageTask(mPostImageView).execute("http://media.wtf.nl/m/m1nw8flpk481_310x200.jpg");

			// setListAdapter(new ArrayAdapter<String>(getActivity(),
			// android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
		}

	}

}
