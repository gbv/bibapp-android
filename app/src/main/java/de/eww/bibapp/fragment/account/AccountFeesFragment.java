package de.eww.bibapp.fragment.account;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.FeeAdapter;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.fragment.dialog.InsufficentRightsDialogFragment;
import de.eww.bibapp.model.FeeItem;
import de.eww.bibapp.tasks.paia.FeeJsonLoader;

/**
 * Created by christoph on 07.11.14.
 */
public class AccountFeesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<FeeItem>>,
        PaiaHelper.PaiaListener,
        AsyncCanceledInterface {

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    TextView mEmptyView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
        View view = inflater.inflate(R.layout.fragment_account_fees, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mEmptyView = (TextView) view.findViewById(R.id.empty);

        return view;
    }

    @Override
    public Loader<List<FeeItem>> onCreateLoader(int id, Bundle args) {
        Loader<List<FeeItem>> loader = new FeeJsonLoader(getActivity(), this);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<FeeItem>> loader, List<FeeItem> feeItemList) {
        getActivity().setProgressBarVisibility(false);

        mAdapter = new FeeAdapter(feeItemList);
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar.setVisibility(View.GONE);
        if (feeItemList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        }

        if (!feeItemList.isEmpty()) {
            TextView sumView = (TextView) getView().findViewById(R.id.sum);
            Resources resources = getActivity().getResources();
            sumView.setText(resources.getString(R.string.account_fees_amount) + " " + feeItemList.get(0).sum);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<FeeItem>> arg0) {
        // empty
    }

    @Override
    public void onAsyncCanceled() {
        Toast toast = Toast.makeText(getActivity(), R.string.toast_account_error, Toast.LENGTH_LONG);
        toast.show();

        mEmptyView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
}
