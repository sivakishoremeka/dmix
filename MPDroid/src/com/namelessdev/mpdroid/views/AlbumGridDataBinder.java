package com.namelessdev.mpdroid.views;

import java.util.List;

import org.a0z.mpd.Album;
import org.a0z.mpd.Item;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import com.namelessdev.mpdroid.MPDApplication;
import com.namelessdev.mpdroid.R;
import com.namelessdev.mpdroid.helpers.AlbumCoverDownloadListener;
import com.namelessdev.mpdroid.helpers.CoverAsyncHelper;
import com.namelessdev.mpdroid.views.holders.AbstractViewHolder;
import com.namelessdev.mpdroid.views.holders.AlbumViewHolder;

public class AlbumGridDataBinder extends AlbumDataBinder {

	public AlbumGridDataBinder(MPDApplication app, boolean isLightTheme) {
		super(app, null, isLightTheme);
	}

	@Override
	public void onDataBind(final Context context, final View targetView, final AbstractViewHolder viewHolder, List<? extends Item> items, Object item, int position) {
		AlbumViewHolder holder = (AlbumViewHolder) viewHolder;

		final Album album = (Album) item;

		// Caching must be switch on to use this view
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(app);
		coverHelper = new CoverAsyncHelper(app, settings);
		coverHelper.setCoverRetrieversFromPreferences();
		final int height = holder.albumCover.getHeight();
		// If the list is not displayed yet, the height is 0. This is a problem, so set a fallback one.
		coverHelper.setCoverMaxSize(height == 0 ? 256 : height);

		// display the album title
		holder.albumName.setText(album.getName());

		// listen for new artwork to be loaded
		final AlbumCoverDownloadListener acd = new AlbumCoverDownloadListener(context, holder.albumCover);
		final AlbumCoverDownloadListener oldAcd = (AlbumCoverDownloadListener) holder.albumCover.getTag(R.id.AlbumCoverDownloadListener);
		oldAcd.detach();
		holder.albumCover.setTag(R.id.AlbumCoverDownloadListener, acd);
		coverHelper.addCoverDownloadListener(acd);

		// Can't get artwork for missing album name
		if((!album.getName().equals("")) && (!album.getName().equals("-"))) {
			loadArtwork(null, album.getName());
		} else {
			loadPlaceholder();
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.album_grid_item;
	}

}