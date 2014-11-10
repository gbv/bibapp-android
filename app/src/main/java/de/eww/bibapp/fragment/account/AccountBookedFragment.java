package de.eww.bibapp.fragment.account;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.BookedAdapter;
import de.eww.bibapp.fragment.dialog.InsufficentRightsDialogFragment;
import de.eww.bibapp.fragment.dialog.PaiaActionDialogFragment;
import de.eww.bibapp.listener.RecyclerItemClickListener;
import de.eww.bibapp.model.PaiaItem;
import de.eww.bibapp.tasks.paia.BookedJsonLoader;
import de.eww.bibapp.tasks.paia.PaiaCancelTask;

/**
 * Created by christoph on 07.11.14.
 */
public class AccountBookedFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<PaiaItem>>,
        PaiaHelper.PaiaListener,
        PaiaActionDialogFragment.PaiaActionDialogLisener,
        AsyncCanceledInterface {

    PaiaActionDialogFragment mPaiaActionDialog;

    MenuItem mMenuItem;

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<PaiaItem> mPaiaItemList = new ArrayList<PaiaItem>();
    private List<PaiaItem> mCheckedItems = new ArrayList<PaiaItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCheckedItems.clear();

        // Improve performance for RecyclerView by setting it to a fixed size,
        // since we now that changes in content do not change the layout size
        // of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Check / Uncheck the checkbox
                CheckBox checkBoxView = (CheckBox) view.findViewById(R.id.checkbox);
                if (checkBoxView.isEnabled()) {
                    checkBoxView.toggle();

                    PaiaItem item = (PaiaItem) mPaiaItemList.get(position);

                    // Update checked items
                    if (checkBoxView.isShown() && checkBoxView.isChecked()) {
                        mCheckedItems.add(item);
                    } else {
                        mCheckedItems.remove(item);
                    }

                    // Update menu action
                    getActivity().supportInvalidateOptionsMenu();
                }
            }
        }));

        // Destroy loader and ensure paia connection
        mProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().destroyLoader(0);
        PaiaHelper.getInstance().ensureConnection(this);
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
        View view = inflater.inflate(R.layout.fragment_account_booked, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.account_booked_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        return view;
    }

    @Override
    public Loader<List<PaiaItem>> onCreateLoader(int id, Bundle args) {
        Loader<List<PaiaItem>> loader = new BookedJsonLoader(getActivity(), this);

        return loader;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the toolbar
        inflater.inflate(R.menu.account_booked_fragment_actions, menu);

        // disable the extend action
        mMenuItem = menu.findItem(R.id.menu_account_booked_cancel);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Enable / Disable menu action
        mMenuItem.setEnabled(!mCheckedItems.isEmpty());

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onLoadFinished(Loader<List<PaiaItem>> loader, List<PaiaItem> paiaItemList) {
        getActivity().setProgressBarVisibility(false);

        mPaiaItemList.clear();
        mPaiaItemList.addAll(paiaItemList);

        mProgressBar.setVisibility(View.GONE);

        mAdapter = new BookedAdapter(paiaItemList, getActivity(), PaiaHelper.getInstance().hasScope(PaiaHelper.SCOPES.WRITE_ITEMS));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<PaiaItem>> arg0) {
        // empty
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.menu_account_booked_cancel:
                sendPaiaCancelRequest();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendPaiaCancelRequest() {
        //start async task to send paia request
        JSONObject jsonRequest = new JSONObject();

        try {
            JSONArray jsonArray = new JSONArray();
            Iterator<PaiaItem> it = mCheckedItems.iterator();

            while (it.hasNext()) {
                PaiaItem checkedItem = it.next();

                JSONObject checkedItemObject = new JSONObject();
                checkedItemObject.put("item", checkedItem.getItem());
                if (!checkedItem.getEdition().equals("")) {
                    checkedItemObject.put("edition", checkedItem.getEdition());
                }

                jsonArray.put(checkedItemObject);
            }

            jsonRequest.put("doc", jsonArray);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        AsyncTask<String, Void, JSONObject> cancelTask = new PaiaCancelTask(this);
        cancelTask.execute(jsonRequest.toString());

        // show the action dialog
        mPaiaActionDialog = new PaiaActionDialogFragment();
        mPaiaActionDialog.show(this.getChildFragmentManager(), "paia_action");
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
    }

    public void onRenew(JSONObject response) {
		// determ text to display
		String responseText = "";

		Resources resources = this.getActivity().getResources();

		try {
            if (response.has("doc")) {
                JSONArray docArray = response.getJSONArray("doc");

                int docArrayLength = docArray.length();
                int numFailedItems = 0;

                for (int i=0; i < docArrayLength; i++) {
                    JSONObject docEntry = docArray.getJSONObject(i);

                    if (docEntry.has("error")) {
                        numFailedItems++;
                        continue;
                    }
                }

                if (numFailedItems == mCheckedItems.size()) {
                    responseText = (String) resources.getText(R.string.paiadialog_cancel_failure);
                } else if (numFailedItems > 0) {
                    responseText = (String) resources.getText(R.string.paiadialog_cancel_partial);
                } else {
                    responseText = (String) resources.getText(R.string.paiadialog_cancel_success);
                }
            }
		} catch (Resources.NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mPaiaActionDialog.paiaActionDone(responseText);

		// reload list
        mProgressBar.setVisibility(View.VISIBLE);
		this.getLoaderManager().getLoader(0).forceLoad();

		// reset checked items
        mCheckedItems.clear();

        // Update menu actions
        getActivity().supportInvalidateOptionsMenu();
	}
}
