package de.eww.bibapp.fragment.account;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.BaseActivity;
import de.eww.bibapp.adapter.BorrowedAdapter;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.fragment.dialog.InsufficentRightsDialogFragment;
import de.eww.bibapp.fragment.dialog.PaiaActionDialogFragment;
import de.eww.bibapp.listener.RecyclerViewOnGestureListener;
import de.eww.bibapp.model.PaiaItem;
import de.eww.bibapp.tasks.paia.BorrowedJsonLoader;
import de.eww.bibapp.tasks.paia.PaiaRenewTask;

/**
 * Created by christoph on 05.11.14.
 */
public class AccountBorrowedFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<PaiaItem>>,
        RecyclerViewOnGestureListener.OnGestureListener,
        PaiaHelper.PaiaListener,
        PaiaActionDialogFragment.PaiaActionDialogListener,
        ActionMode.Callback,
        AsyncCanceledInterface {

    PaiaActionDialogFragment mPaiaActionDialog;

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    TextView mEmptyView;

    private BorrowedAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<PaiaItem> mPaiaItemList = new ArrayList<PaiaItem>();

    private ActionMode mActionMode;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Improve performance for RecyclerView by setting it to a fixed size,
        // since we now that changes in content do not change the layout size
        // of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        RecyclerViewOnGestureListener gestureListener = new RecyclerViewOnGestureListener(getActivity(), mRecyclerView);
        gestureListener.setOnGestureListener(this);
        mRecyclerView.addOnItemTouchListener(gestureListener);

        // Destroy loader and ensure paia connection
        getLoaderManager().destroyLoader(0);
        PaiaHelper.getInstance().ensureConnection(this, getActivity(), this);
        mEmptyView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPaiaConnected() {
        if (PaiaHelper.getInstance().hasScope(PaiaHelper.SCOPES.READ_ITEMS)) {
            // Force recreation of loader
            this.getLoaderManager().initLoader(0, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);

            InsufficentRightsDialogFragment dialog = new InsufficentRightsDialogFragment();
            dialog.show(this.getChildFragmentManager(), "load_rights");
        }
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_account_borrowed, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mEmptyView = (TextView) view.findViewById(R.id.empty);

        return view;
	}

	@Override
	public Loader<List<PaiaItem>> onCreateLoader(int id, Bundle args) {
		Loader<List<PaiaItem>> loader = new BorrowedJsonLoader(getActivity(), this);

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<PaiaItem>> loader, List<PaiaItem> paiaItemList) {
        getActivity().setProgressBarVisibility(false);

        mPaiaItemList.clear();

        // sort by end date
        Collections.sort(paiaItemList, (PaiaItem firstItem, PaiaItem secondItem) -> {
            if (firstItem.getEndDate() == null || secondItem.getEndDate() == null) {
                return 0;
            }

            return firstItem.getEndDate().compareTo(secondItem.getEndDate());
        });

        mPaiaItemList.addAll(paiaItemList);

        mProgressBar.setVisibility(View.GONE);
        if (paiaItemList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        }

        mAdapter = new BorrowedAdapter(paiaItemList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onLoaderReset(Loader<List<PaiaItem>> arg0) {
        // empty
	}

    @Override
	public void onActionDialogPositiveClick(DialogFragment dialog) {
		// close dialog
        mPaiaActionDialog.dismiss();
	}

    @Override
	public void onAsyncCanceled() {
        Toast toast = Toast.makeText(getActivity(), R.string.toast_account_error, Toast.LENGTH_LONG);
        toast.show();

        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);

        mActionMode.finish();
	}

	public void onRenew(JSONObject response) {
		// determ text to display
		String responseText = "";

		Resources resources = getActivity().getResources();

		try {
            if (response.has("doc")) {
                JSONArray docArray = response.getJSONArray("doc");

                int docArrayLength = docArray.length();
                String failedResponseText = "";
                int numFailedItems = 0;

                for (int i=0; i < docArrayLength; i++) {
                    JSONObject docEntry = docArray.getJSONObject(i);

                    if (docEntry.has("error")) {
                        failedResponseText += "\n";
                        failedResponseText += mAdapter.getPaiaItem(i).getAbout() + ": ";
                        failedResponseText += docEntry.get("error");

                        numFailedItems++;
                        continue;
                    }
                }

                if (numFailedItems == 0) {
                    responseText = resources.getQuantityString(R.plurals.paiadialog_renew_success, docArrayLength);
                } else {
                    if (docArrayLength == numFailedItems) {
                        responseText = resources.getString(R.string.paiadialog_renew_failure);
                    } else {
                        responseText = resources.getString(R.string.paiadialog_renew_partial);
                    }

                    responseText += "\n";
                    responseText += failedResponseText;
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		mPaiaActionDialog.paiaActionDone(responseText);

		// reload list
        mProgressBar.setVisibility(View.VISIBLE);
		this.getLoaderManager().getLoader(0).forceLoad();
	}

    @Override
    public void onClick(View view, int position) {
        if (view.getId() == R.id.borrowed_item) {
            // Are we in action mode?
            if (mActionMode != null) {
                toggleSelection(position);
                return;
            }
        }
    }

    @Override
    public void onLongPress(View view, int position) {
        // ActionMode already active?
        if (mActionMode != null) {
            return;
        }

        if (!PaiaHelper.getInstance().hasScope(PaiaHelper.SCOPES.WRITE_ITEMS)) {
            InsufficentRightsDialogFragment dialog = new InsufficentRightsDialogFragment();
            dialog.show(this.getChildFragmentManager(), "load_rights");
            return;
        }

        // Start the CAB
        mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
        int childPosition = mRecyclerView.getChildLayoutPosition(view);
        toggleSelection(childPosition);
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.account_borrowed_fragment_mode_actions, menu);

        mAdapter.setSelectionMode(true);
        mAdapter.notifyDataSetChanged();

        ((BaseActivity) getActivity()).showToolbar(false);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_account_borrowed_extend:
                sendPaiaExtendRequest();

                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mActionMode = null;
        mAdapter.clearSelection();

        mAdapter.setSelectionMode(false);
        mAdapter.notifyDataSetChanged();

        ((BaseActivity) getActivity()).showToolbar(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser && mActionMode != null) {
            mActionMode.finish();
        }
    }

    private void toggleSelection(int position) {
        // Check if the item is renewable
        if (mAdapter.getPaiaItem(position).isCanRenew()) {
            mAdapter.toggleSelection(position);
        }

        String title = getString(R.string.menu_selected_count, mAdapter.getSelectedItemCount());
        mActionMode.setTitle(title);
    }

    private void sendPaiaExtendRequest() {
        // start async task to send paia request
        JSONObject jsonRequest = new JSONObject();

        try {
            JSONArray jsonArray = new JSONArray();

            List<Integer> checkItems = mAdapter.getSelectedItems();
            for (int i : checkItems) {
                PaiaItem checkedItem = mAdapter.getPaiaItem(i);

                JSONObject checkedItemObject = new JSONObject();
                checkedItemObject.put("item", checkedItem.getItem());
                if (!checkedItem.getEdition().equals("")) {
                    checkedItemObject.put("edition", checkedItem.getEdition());
                }

                jsonArray.put(checkedItemObject);
            }

            jsonRequest.put("doc", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AsyncTask<String, Void, JSONObject> renewTask = new PaiaRenewTask(this, getActivity(), this);
        renewTask.execute(jsonRequest.toString());

        // show the action dialog
        mPaiaActionDialog = new PaiaActionDialogFragment();
        mPaiaActionDialog.show(this.getChildFragmentManager(), "paia_action");

        mActionMode.finish();
    }
}
